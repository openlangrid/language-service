package jp.go.nict.langrid.wrapper.juman;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.adjective;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.adverb;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun_common;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun_other;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun_proper;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.other;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.unknown;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.verb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.process.ProcessFacade;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

public class Juman extends AbstractMorphologicalAnalysisService {
	public Juman() {
		setSupportedLanguageCollection(Arrays.asList(ja));
		jumanPath = getInitParameterString("jumanPath", "/usr/local/bin/juman");
		jumanEncoding = getInitParameterString("jumanEncoding", "EUC_JP");
	}

	public void setJumanPath(String jumanPath) {
		this.jumanPath = jumanPath;
	}

	public void setJumanEncoding(String jumanEncoding) {
		this.jumanEncoding = jumanEncoding;
	}

	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		ProcessFacade p = new ProcessFacade(jumanPath, "-B","-e2");
		try{
			p.start();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					p.getOutputStream(), jumanEncoding));
			bw.write(text);
			bw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream(), jumanEncoding));

			ArrayList<Morpheme> morphemes = new ArrayList<Morpheme>();
			String str = null;
			while((str = br.readLine()) != null){
				if(str.startsWith("@") || str.equals("EOS")) continue;

				String[] analyzedMorpheme = str.split("\\s");
				String word = analyzedMorpheme[0];
				String lemma = analyzedMorpheme[2];
				PartOfSpeech partOfSpeech = determinePartOfSpeech(analyzedMorpheme[3], analyzedMorpheme[5]);
				morphemes.add(new Morpheme(word,lemma,partOfSpeech));
			}
			return morphemes;
		} catch (UnsupportedEncodingException e){
			logger.log(Level.SEVERE, "Wrapper Error: Unsupported system encoding, "+jumanEncoding, e);
			throw new ProcessFailedException(e);
		} catch (IOException e){
			logger.log(Level.SEVERE, "Wrapper Error: Process cannot start", e);
			throw new ProcessFailedException(e);
		} finally{
			try{
				p.close();
			} catch(InterruptedException e){
				throw new ProcessFailedException(e);
			} catch(IOException e){
				throw new ProcessFailedException(e);
			}
		}
	}

	private static PartOfSpeech determinePartOfSpeech(String pos, String detailedPos){
		PartOfSpeech ret = posMap.get(pos);
		if(ret != null) return ret;
		ret = posDetailMap.get(detailedPos);
		if(ret != null) return ret;
		ret = posOtherMap.get(pos);
		if(ret != null) return ret;
		return other;
	}

	private String jumanPath;
	private String jumanEncoding = "EUC-JP";
	private static Map<String, PartOfSpeech> posMap = new HashMap<String, PartOfSpeech>();
	private static Map<String, PartOfSpeech> posDetailMap = new HashMap<String, PartOfSpeech>();
	private static Map<String, PartOfSpeech> posOtherMap = new HashMap<String, PartOfSpeech>();
	static Logger logger = Logger.getLogger(Juman.class.getName());
	static{
		posMap.put("未定義語", unknown);
		posMap.put("動詞", verb);
		posMap.put("形容詞", adjective);
		posMap.put("副詞", adverb);
		posDetailMap.put("普通名詞", noun_common);
		posDetailMap.put("時相名詞", noun_common);
		posDetailMap.put("サ変名詞", noun_other);
		posDetailMap.put("人名", noun_proper);
		posDetailMap.put("地名", noun_proper);
		posDetailMap.put("数詞", noun_other);
		posDetailMap.put("形式名詞", noun_other);
		posDetailMap.put("名詞形態指示詞", noun_other);
		posDetailMap.put("副詞形態指示詞", adverb);
		posDetailMap.put("名詞性名詞助数辞", noun_other);
		posDetailMap.put("名詞性述語接尾辞", noun_other);
		posDetailMap.put("名詞性名詞接尾辞", noun_other);
		posDetailMap.put("名詞性特殊接尾辞", noun_other);
		posDetailMap.put("形容詞性述語接尾辞", adjective);
		posDetailMap.put("形容詞性名詞接尾辞", adjective);
		posDetailMap.put("動詞性接尾辞", verb);
		posOtherMap.put("名詞", noun_other);
		posOtherMap.put("指示詞", other);
		posOtherMap.put("接尾辞", other);
	}
}
