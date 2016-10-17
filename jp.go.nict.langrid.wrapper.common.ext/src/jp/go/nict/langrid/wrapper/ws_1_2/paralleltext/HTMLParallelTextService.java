/*
 * $Id: HTMLParallelTextService.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.ws_1_2.paralleltext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.repository.Storage;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.cache.HTML_DataUpdater;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.paralleltext.HTML_ServiceDescription;
import jp.go.nict.langrid.wrapper.common.repository.WorkStorageFactory;

/**
 * HTMLで提供される用例対訳サービスに関するライブラリ
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class HTMLParallelTextService {
	
	/**
	 * search要求を処理する部分の実装
	 * @param sourceLang 対訳元言語
	 * @param targetLang 対訳先言語
	 * @param source 対訳を検索する文字列
	 * @param matchingMethod 検索方法
	 * @param aClass 呼び出しもとのクラス.キャッシュの作成に使用する
	 * @param sd iniファイルの内容を保持するオブジェクト
	 * @return 対訳結果
	 * @throws InvalidParameterException
	 * @throws ProcessFailedException
	 */
	public static Collection<ParallelText> doSearchImpl(Language sourceLang,
			Language targetLang, String source, MatchingMethod matchingMethod
			, Class<?> aClass
			,HTML_ServiceDescription sd)
			throws InvalidParameterException, ProcessFailedException
	{		
		
		//キャッシュのストレージを取得
		Storage s = WorkStorageFactory.createStorage(aClass.getPackage().getName());
		
		//利用可能な言語を格納します．
		Map<Language, Integer> languages = new HashMap<Language, Integer>();

		//対訳データを格納します．
		Collection<String[]> texts = new ArrayList<String[]>();
		
		//対訳データファイルのアップロードを試行
		//ページの取得で例外が生じた場合、既存のデータファイルを用いる
		
		try{
			// .dat、.rdfファイルを生成
			HTML_DataUpdater.update_Parallel(
					sd,s,aClass.getSimpleName());
		}catch(IOException e)
		{
			//ページの取得で例外が発生した場合の処理
			throw new ProcessFailedException("page not found");
		}
		
		//正常にdat,rdfファイルが更新された
				
		//textsを初期化
		texts = new ArrayList<String[]>();
		
		try{
			LocalParallelTextService.setValue(
				s.getInputStream(aClass.getSimpleName()+".dat"),
				languages, texts,getSupported(sd));
		} catch(IOException e){
			//datのエンティティの取得に失敗した場合
			throw new ProcessFailedException(e.toString());
		}
		
		return LocalParallelTextService.doSearch(
				sourceLang, targetLang, source, matchingMethod, languages,texts);
	}

	/**
	 * search要求を処理する部分の実装
	 * 呼び出し元がtextなどを保持するような実装
	 * @param sourceLang 対訳元言語
	 * @param targetLang 対訳先言語
	 * @param source 対訳を検索する文字列
	 * @param matchingMethod 検索方法
	 * @param aClass 呼び出しもとのクラス.キャッシュの作成に使用する
	 * @param sd iniファイルの内容を保持するオブジェクト
	 * @param texts 
	 * @param languages 
	 * @return 対訳結果
	 * @throws InvalidParameterException
	 * @throws ProcessFailedException
	 */
	public static Collection<ParallelText> doSearchImpl(Language sourceLang,
			Language targetLang, String source, MatchingMethod matchingMethod
			, Class<?> aClass
			,HTML_ServiceDescription sd,Collection<String[]> texts,Map<Language, Integer> languages)
			throws InvalidParameterException, ProcessFailedException
	{		
		
		//キャッシュのストレージを取得
		Storage s = WorkStorageFactory.createStorage(aClass.getPackage().toString());
				
		//対訳データファイルのアップロードを試行
		//ページの取得で例外が生じた場合、既存のデータファイルを用いる
		
		//更新処理の結果を表す
		//これが0となっていればdatは更新されておらず、setValueも不必要となる
		int flag = 1;
		try{
			// .dat、.rdfファイルを生成
			flag = HTML_DataUpdater.update_Parallel(
					sd,s,aClass.getSimpleName());
		}catch(IOException e)
		{
			//ページの取得で例外が発生した場合の処理
			throw new ProcessFailedException("page not found");
		}
		
		//正常にdat,rdfファイルが更新された
						
		if (flag != 0 || texts.size()==0) {
			try {
				LocalParallelTextService.setValue(s.getInputStream(aClass
						.getSimpleName()
						+ ".dat"), languages, texts, getSupported(sd));
			} catch (IOException e) {
				// datのエンティティの取得に失敗した場合
				throw new ProcessFailedException(e.toString());
			}
		}
		
		return LocalParallelTextService.doSearch(
				sourceLang, targetLang, source, matchingMethod, languages,texts);
	}

	/**
	 * iniファイルを読み込み、対応するオブジェクトを作成
	 * 用例対訳サービス用
	 * @param is iniファイルのInputStream
	 * @return iniファイルに対応するオブジェクト
	 * @throws ProcessFailedException 
	 */
	public static HTML_ServiceDescription getServiceDescription(InputStream is)
			throws ProcessFailedException {
		try {
			HTML_ServiceDescription sd = new HTML_ServiceDescription(is);
			return sd;
		} catch (Exception e) {
			throw new ProcessFailedException(e.toString());
		}
	}

	/**
	 * iniファイルを解釈し、対応言語対を返す
	 * 用例対訳サービス用
	 * @param sd iniファイルの内容を表すオブジェクト
	 * @return 対応する言語対
	 * @throws ProcessFailedException
	 */
	public static Collection<LanguagePair> getSupported(
			HTML_ServiceDescription sd) throws ProcessFailedException {

		try{
			ParallelTextServicePartIntegrater ssi = new ParallelTextServicePartIntegrater(sd);
			return ssi.getSupportedPairs();
		}catch(IOException e){
			//ページの読み込みで例外が発生した
			//ただし、ラッパー側でgetSuppoted実行前にページ読み込みのチェックを行っているので、
			//ここでIOEXCeptionが発生することは無い
			throw new ProcessFailedException(e.toString());
		}
	}
}
