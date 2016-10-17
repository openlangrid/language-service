package jp.go.nict.langrid.wrapper.common.db.service;

import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.COMPLETE;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PARTIAL;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PREFIX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.SUFFIX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.transformer.CodeStringToLanguageTransformer;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

public class Util {
	public static List<Language> toLanguages(String[] languageColumnNames){
		return Arrays.asList(ArrayUtil.collect(languageColumnNames, c2l));
	}

	public static List<LanguagePair> toRoundrobinPairs(Language[] languages){
		List<LanguagePair> pairs = new ArrayList<LanguagePair>();
		LanguagePairUtil.addBidirectionalRoundrobinformedPairs(pairs, languages);
		return pairs;
	}

	public static Set<MatchingMethod> toMatchingMethods(MatchingMethod[] methods){
		if(methods != null){
			return new HashSet<MatchingMethod>(Arrays.asList(methods));
		} else{
			return MINIMUM_MATCHINGMETHODS;
		}
	}

	private static final Transformer<String, Language> c2l = new CodeStringToLanguageTransformer();
	private static final Set<MatchingMethod> MINIMUM_MATCHINGMETHODS;
	static{
		MINIMUM_MATCHINGMETHODS = Collections.unmodifiableSet(new HashSet<MatchingMethod>(
				Arrays.asList(COMPLETE, PARTIAL, PREFIX, SUFFIX
				)));
	}
}
