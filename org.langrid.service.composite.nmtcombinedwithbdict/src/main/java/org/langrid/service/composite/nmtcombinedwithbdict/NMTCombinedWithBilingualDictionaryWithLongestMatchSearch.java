package org.langrid.service.composite.nmtcombinedwithbdict;

import java.util.ArrayList;
import java.util.Collection;

import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguagePairNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguagePairException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.BilingualDictionaryWithLongestMatchSearchService;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TranslationWithPosition;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.translation.TranslationService;
import jp.go.nict.langrid.service_1_2.translation.TranslationWithTemporalDictionaryService;
import jp.go.nict.langrid.service_1_2.util.validator.LanguagePairValidator;
import jp.go.nict.langrid.service_1_2.util.validator.LanguageValidator;
import jp.go.nict.langrid.service_1_2.util.validator.StringValidator;
import jp.go.nict.langrid.service_1_2.workflowsupport.SourceAndMorphemesAndCodes;
import jp.go.nict.langrid.servicecontainer.service.composite.AbstractCompositeService;
import jp.go.nict.langrid.servicecontainer.service.composite.Invocation;
import jp.go.nict.langrid.servicecontainer.service.composite.ServiceLoadingFailedException;
import jp.go.nict.langrid.wrapper.workflowsupport.ConstructSourceAndMorphemesAndCodes;
import jp.go.nict.langrid.wrapper.workflowsupport.DefaultMorphologicalAnalysis;
import jp.go.nict.langrid.wrapper.workflowsupport.ReplacementTerm;
import jp.go.nict.langrid.wrapper.workflowsupport.TemporalBilingualDictionaryWithLongestMatchSearch;

public class NMTCombinedWithBilingualDictionaryWithLongestMatchSearch
extends AbstractCompositeService
implements TranslationWithTemporalDictionaryService
{
	public static class Services{
		@Invocation(name="BilingualDictionaryWithLongestMatchSearchPL")
		private BilingualDictionaryWithLongestMatchSearchService bdict;
		@Invocation(name="MorphologicalAnalysisPL")
		private MorphologicalAnalysisService morph;
		@Invocation(name="TranslationPL")
		private TranslationService trans;
	}
	
	public String translate(String sourceLang, String targetLang, String source, Translation[] temporalDict,
			String dictTargetLang)
			throws AccessLimitExceededException, InvalidParameterException, LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, ProcessFailedException, NoValidEndpointsException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException, UnsupportedLanguagePairException {
		LanguagePair pair = new LanguagePairValidator(
				new LanguageValidator("sourceLang", sourceLang)
				, new LanguageValidator("targetLang", targetLang)
				).notNull().trim().notEmpty().getLanguagePair();
		String src = new StringValidator("source", source)
				.notNull().trim().notEmpty().getValue();
		Translation[] dict = temporalDict;
		if(dict == null){
			dict = new Translation[]{};
		}
		Language dictLang = null;
		if(dictTargetLang != null){
			dictLang = new LanguageValidator("dictTargetLang", dictTargetLang)
					.getLanguage();
		} else{
			dictLang = pair.getTarget();
		}

		String sl = pair.getSource().getCode();
		String tl = pair.getTarget().getCode();
		Translation[] tempDict = dict;
		String dl = dictLang.getCode();

		try{
			Services s = loadServices();
			TemporalBilingualDictionaryWithLongestMatchSearch tempDictMatch = new TemporalBilingualDictionaryWithLongestMatchSearch();
			ConstructSourceAndMorphemesAndCodes csmc = getConstructSourceAndMorphemesAndCodes();
			ReplacementTerm rt = new ReplacementTerm();

			Morpheme[] analyzeResult = null;
			{
				try{
					if(s.morph != null){
						analyzeResult = s.morph.analyze(sl, src);
					}
				} catch(ServiceNotActiveException e){
					if(!e.getServiceId().equals("AbstractService")){
						warning("service is not active: " + e.getServiceId());
					}
				} catch(Exception e){
					warning("exception occurred at morphologicalanalysis.", e);
				}
				if(analyzeResult == null){
					warning("analyze result is null. calling DefaultMorphologicalAnalysis.");
					analyzeResult = new DefaultMorphologicalAnalysis().analyze(sl, src);
				}
			}

			Collection<TranslationWithPosition> tempDictResult = new ArrayList<TranslationWithPosition>();
			try{
				tempDictResult = tempDictMatch.doSearchAllLongestMatchingTerms(
					pair.getSource(), analyzeResult, tempDict);
			} catch(Exception e){
				warning("exception occurred at tempdictmatch.", e);
			}
			SourceAndMorphemesAndCodes firstSmc = csmc.doConstructSMC(sl, analyzeResult, tempDictResult.toArray(emptyTranslation));
			TranslationWithPosition[] dictResult = new TranslationWithPosition[]{};
			try{
				if(s.bdict != null){
					dictResult = s.bdict.searchLongestMatchingTerms(sl, dl, firstSmc.getMorphemes());
				}
			} catch(ServiceNotActiveException e){
				if(!e.getServiceId().equals("AbstractService")){
					warning("service is not active: " + e.getServiceId());
				}
			} catch(Exception e){
				warning("exception occurred at bdict search.", e);
			}

			SourceAndMorphemesAndCodes secondSmc = csmc.doConstructSMC(sl, firstSmc.getMorphemes(), dictResult);

			SourceAndMorphemesAndCodes joinedSmc = new SourceAndMorphemesAndCodes(
					secondSmc.getSource()
					, secondSmc.getMorphemes()
					, ArrayUtil.append(firstSmc.getCodes(), secondSmc.getCodes())
					, ArrayUtil.append(firstSmc.getTargetWords(), secondSmc.getTargetWords())
			);

			String fwTranslationResult = null;
			if(s.trans == null){
				if(!sl.equals(tl)){
					throw new ProcessFailedException("TranslationPL not binded.");
				}
			} else{
				try{
					fwTranslationResult = s.trans.translate(sl, tl, joinedSmc.getSource());
				} catch(ServiceNotActiveException e){
					if(!e.getServiceId().equals("AbstractService") || !sl.equals(tl)){
						throw e;
					}
				}
			}
			if(fwTranslationResult == null){
				fwTranslationResult = joinedSmc.getSource();
			}
			return rt.doReplace(tl, fwTranslationResult
					, joinedSmc.getCodes(), joinedSmc.getTargetWords());
		} catch(InvalidParameterException e){
			throw e;
		} catch(ProcessFailedException e){
			warning("process failed.", e);
			throw e;
		} catch(ServiceLoadingFailedException e){
			throw new ServiceNotFoundException(e.getServiceId());
		} catch(Throwable t){
			warning("unknown error occurred.", t);
			throw new ProcessFailedException(t);
		}
	}

	protected ConstructSourceAndMorphemesAndCodes getConstructSourceAndMorphemesAndCodes(){
		return new ConstructSourceAndMorphemesAndCodes();
	}
	private static final TranslationWithPosition[] emptyTranslation = {};
}
