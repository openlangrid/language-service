/*
 * $Id:AbstractBilingualDictionaryService.java 1495 2006-10-12 18:42:55 +0900 (木, 12 10 2006) sagawa $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 2.1 of the License, or (at 
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.language.util.LanguageUtil;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.AbstractBilingualDictionaryHeadwordsExtractionService;

/**
 * extractをサポートしたユーザ辞書の基底クラス。
 * @author $Author: sagawa $
 * @version $Revision: 3408 $
 */
public abstract class AbstractExtractableBilingualDbDictionaryService
extends AbstractBilingualDictionaryHeadwordsExtractionService
{
	/**
	 * コンストラクタ。
	 * 引数を取りません。
	 */
	public AbstractExtractableBilingualDbDictionaryService(){
	}

	//[Begin] Add 辞書名から対応する言語ペア・検査手法(全て)を取得する  by h.sagawa 2008/01/29
	/**
	 * コンストラクタ。
	 * @param dict 辞書テーブル名
	 */
	public AbstractExtractableBilingualDbDictionaryService(String dict)
	throws Exception
	{
		DictionaryService dictService = new DictionaryService();
		String[] slang = dictService.getSupportedLanguageByDictionary(dict);
		Language[] langs =LanguageUtil.codeArrayToArray(slang);
		List<LanguagePair> pairs = new ArrayList<LanguagePair>();
		int n = langs.length;
		for(int i = 0; i < (n - 1); i++){
			for(int j = i + 1; j < n; j++){
				LanguagePairUtil.addBidirectionalPair(pairs, langs[i], langs[j]);
			}
		}
		setSupportedLanguagePairs(pairs);
		setSupportedMethods();
	}	
	//[End]

	/**
	 * コンストラクタ。
	 * 対応する言語対の集合を引数にとります。
	 * @param supportedPairs 対応する言語対の集合
	 */
	public AbstractExtractableBilingualDbDictionaryService(
			Collection<LanguagePair> supportedPairs)
	{
		setSupportedLanguagePairs(supportedPairs);
		setSupportedMethods();
	}

	/**
	 * コンストラクタ。
	 * 対応する言語対の集合を引数にとります。
	 * @param context サービスコンテキスト
	 * @param supportedPairs 対応する言語対の集合
	 */
	public AbstractExtractableBilingualDbDictionaryService(
			ServiceContext context
			, Collection<LanguagePair> supportedPairs){
		setSupportedLanguagePairs(supportedPairs);
		setSupportedMethods();
	}

	private void setSupportedMethods(){
		Set<MatchingMethod> methods = new HashSet<MatchingMethod>(
				Arrays.asList(MatchingMethod.values()));
		methods.add(MatchingMethod.REGEX);
		setSupportedMatchingMethods(methods);		
	}
}
