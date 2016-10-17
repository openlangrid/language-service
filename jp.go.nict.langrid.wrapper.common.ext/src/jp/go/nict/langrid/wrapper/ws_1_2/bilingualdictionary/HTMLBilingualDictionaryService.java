/*
 * $Id: HTMLBilingualDictionaryService.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.repository.Storage;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.cache.HTML_DataUpdater;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription;
import jp.go.nict.langrid.wrapper.common.repository.WorkStorageFactory;

/**
 * HTMLで提供される対訳辞書サービスに関する各種関数を記述。
 * テンプレートから呼び出せるよう。
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class HTMLBilingualDictionaryService {
	
	/**
	 * HTMLで提供される対訳辞書の、検索処理の実装
	 * @param headLang 対訳元の言語
	 * @param targetLang 対訳結果の言語
	 * @param headword 検索文字列
	 * @param matchingMethod 検索方法
	 * @param aClass datのファイル名決定に用いる、ラッパーのパッケージ・クラス名
	 * @param sd iniファイルに対応するクラス
	 * @return 検索結果
	 * @throws InvalidParameterException
	 * @throws ProcessFailedException
	 */
	public static Collection<Translation> doSearchImpl(Language headLang,
			Language targetLang, String headword, MatchingMethod matchingMethod,Class aClass
			,HTML_ServiceDescription sd)
			throws InvalidParameterException, ProcessFailedException
	{	
		//キャッシュのストレージを取得
		Storage s = WorkStorageFactory.createStorage(aClass.getPackage().toString());
		
		// 利用可能な言語を格納します.
		Map<Language, Integer> languages = new HashMap<Language, Integer>();

		//対訳データを格納します．
		Collection<String[]> texts = new TreeSet<String[]>();
		
		//対訳内のスプリッタ
		String bilingualSplitter = "";
		
		//対訳データファイルのアップロードを試行
		//ページの取得で例外が生じた場合、既存のデータファイルを用いる
		try{
			// .dat、.rdfファイルを生成
			HTML_DataUpdater.update_BilingDict(sd,s,aClass.getSimpleName());
		}catch(IOException e)
		{
			//ページの取得で例外が発生した場合の処理
			throw new ProcessFailedException("page not found");
		}
		
		//正常にdat,rdfファイルが更新された
		
		//textsを初期化
		texts = new ArrayList<String[]>();

		try {
			bilingualSplitter = LocalBilingualDictionaryService.setValue(s
					.getInputStream(aClass.getSimpleName() + ".dat"),
					languages, texts, getSupported(sd));

		} catch (IOException e) {
			// datのエンティティの取得に失敗した場合
			throw new ProcessFailedException(e.toString());
		}

		return LocalBilingualDictionaryService.doSearch(headLang, targetLang,
				headword, matchingMethod, languages, texts, bilingualSplitter);
	}
	
	/**
	 * HTMLで提供される対訳辞書の、検索処理の実装
	 * 呼び出し元がtextなどを保持するような実装
	 * @param headLang 対訳元の言語
	 * @param targetLang 対訳結果の言語
	 * @param headword 検索文字列
	 * @param matchingMethod 検索方法
	 * @param aClass datのファイル名決定に用いる、ラッパーのパッケージ・クラス名
	 * @param sd iniファイルに対応するクラス
	 * @return 検索結果
	 * @throws InvalidParameterException
	 * @throws ProcessFailedException
	 */
	public static Collection<Translation> doSearchImpl(Language headLang,
			Language targetLang, String headword, MatchingMethod matchingMethod,Class aClass
			,HTML_ServiceDescription sd,Collection<String[]> texts,Map<Language, Integer> languages
			,String[] bilingualSplitter)
			throws InvalidParameterException, ProcessFailedException
	{	
		//キャッシュのストレージを取得
		Storage s = WorkStorageFactory.createStorage(aClass.getPackage().toString());
		
		//対訳データファイルのアップロードを試行
		//ページの取得で例外が生じた場合、既存のデータファイルを用いる
		int flag = 1;
		try{
			// .dat、.rdfファイルを生成
			flag = HTML_DataUpdater.update_BilingDict(sd,s,aClass.getSimpleName());
		}catch(IOException e)
		{
			//ページの取得で例外が発生した場合の処理
			throw new ProcessFailedException("page not found");
		}
		
		// 正常にdat,rdfファイルが更新された

		if (flag != 0 || texts.size() == 0) {
			try {
				bilingualSplitter[0]  = LocalBilingualDictionaryService.setValue(s
						.getInputStream(aClass.getSimpleName() + ".dat"),
						languages, texts, getSupported(sd));

			} catch (IOException e) {
				// datのエンティティの取得に失敗した場合
				throw new ProcessFailedException(e.toString());
			}
		}

		return LocalBilingualDictionaryService.doSearch(headLang, targetLang,
				headword, matchingMethod, languages, texts, bilingualSplitter[0]);
		
	}
	
	/**
	 * iniファイルを読み込み、対応するオブジェクトを作成
	 * @param is iniファイルのInputStream
	 * @return iniファイルに対応するオブジェクト
	 * @throws ServiceConfigurationException 
	 */
	public static jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription
	getServiceDescription(InputStream is)
			throws ProcessFailedException {
		try {
			jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription
			sd = new jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription(is);
			return sd;
		} catch (Exception e) {
			throw new ProcessFailedException(e.toString());
		}
	}

	/**
	 * iniファイルを解釈し、対応言語対を返す
	 * 対訳辞書サービス用
	 * @param sd iniファイルの内容を表すオブジェクト
	 * @return 対応する言語対
	 * @throws ProcessFailedException
	 */
	public static Collection<LanguagePair> getSupported(
			jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription sd)
			throws ProcessFailedException {

		try{
			BilingualDictionaryServicePartIntegrater ssi = new BilingualDictionaryServicePartIntegrater(sd);
			
			return ssi.getSupportedPairs();
		}catch(IOException e){
			//ページの読み込みで例外が発生した
			//ただし、ラッパー側でgetSuppoted実行前にページ読み込みのチェックを行っているので、
			//ここでIOEXCeptionが発生することは無い
			throw new ProcessFailedException(e.toString());
		}
	}

}
