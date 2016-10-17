/*
 * $Id: IniFunctions.java 28172 2010-09-30 04:28:40Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.service_1_2.util.LanguageParameterValidator;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.paralleltext.HTML_ServiceDescription;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.translation.Webapp_ServiceDescription;

/**
 * iniファイルを処理する際に使用する様々な関数をまとめたクラス.
 * 現在の機能は
 * -iniファイル内、langPathに書かれた文字列から、対応する言語ペアを作成
 * -iniファイル内、langPathに書かれた文字列から、対応する言語を作成
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 28172 $
 */
public class IniFunctions {
	
	/**
	 * iniファイル内、langPathに書かれた文字列から、対応する言語ペアを作成
	 * @param strings "en<>ja","en>tl" 等、対応言語対の文字列表現
	 * @return 対応言語対の、LanguagePair表現
	 * @throws ServiceConfigurationException 
	 */
	public static Collection<LanguagePair> stringsToLanguagePairs(String[] strings) 
	throws ServiceConfigurationException{
		ArrayList<LanguagePair> rtn = new ArrayList<LanguagePair>();
		
		for (int loop = 0; loop < strings.length; loop++) {
		
			String aPath = strings[loop];

			try{
			
			// rtnを追加
			if (aPath.matches(".+?<>.+?")) {
				// <>の場合
				String[] aPair = aPath.split("<>");
				Language lang1 = LanguageParameterValidator.getValidLanguage("lang1",
						aPair[0]);
				Language lang2 = LanguageParameterValidator.getValidLanguage("lang2",
						aPair[1]);
				LanguagePairUtil.addBidirectionalPair(rtn, lang1, lang2);
			} else if (aPath.matches(".+?>.+?")) {
				// >の場合
				String[] aPair = aPath.split(">");
				Language lang1 = LanguageParameterValidator.getValidLanguage("lang1",
						aPair[0]);
				Language lang2 = LanguageParameterValidator.getValidLanguage("lang2",
						aPair[1]);
				LanguagePairUtil.addPair(rtn, lang1, lang2);
			} else if (aPath.matches(".+?<.+?")) {
				// >の場合
				String[] aPair = aPath.split(">");
				Language lang1 = LanguageParameterValidator.getValidLanguage("lang1",
						aPair[0]);
				Language lang2 = LanguageParameterValidator.getValidLanguage("lang2",
						aPair[1]);
				LanguagePairUtil.addPair(rtn, lang2, lang1);
			} else {
				// langPathの内容が不正だった場合
				throw new ServiceConfigurationException(
						"usage: langPath:[\"ja>en\",\"ja<>tl\",...]");
			}
			
			}catch(InvalidParameterException e){
				throw new ServiceConfigurationException(e.toString());
			}
		}
		
		return rtn;
	}
	
	/**
	 * iniファイル内、langPathに書かれた文字列から、対応する言語のリストを作成
	 * 引数で登場する順に言語を保持するリストを返す
	 * @param strings "en<>ja","en>tl" 等、対応言語対の文字列表現のリスト
	 * @return 対応言語対に登場する言語の、String[]形式でのリスト
	 * @throws ServiceConfigurationException 
	 */
	public static Collection<Language> LanguagePairsStringsToLanguages(String[] strings) 
	throws ServiceConfigurationException{
		ArrayList<Language> rtn = new ArrayList<Language>();
		
		for (int loop = 0; loop < strings.length; loop++) {
		
			String aPath = strings[loop];

			try{
			
			// rtnを追加
			if (aPath.matches(".+?<>.+?")) {
				// <>の場合
				String[] aPair = aPath.split("<>");
				Language lang1 = LanguageParameterValidator.getValidLanguage("lang1",
						aPair[0]);
				Language lang2 = LanguageParameterValidator.getValidLanguage("lang2",
						aPair[1]);
				if(!rtn.contains(lang1))
					rtn.add(lang1);
				if(!rtn.contains(lang2))
					rtn.add(lang2);
			} else if (aPath.matches(".+?>.+?")) {
				// >の場合
				String[] aPair = aPath.split(">");
				Language lang1 = LanguageParameterValidator.getValidLanguage("lang1",
						aPair[0]);
				Language lang2 = LanguageParameterValidator.getValidLanguage("lang2",
						aPair[1]);
				if(!rtn.contains(lang1))
					rtn.add(lang1);
				if(!rtn.contains(lang2))
					rtn.add(lang2);
			} else if (aPath.matches(".+?<.+?")) {
				// >の場合
				String[] aPair = aPath.split(">");
				Language lang1 = LanguageParameterValidator.getValidLanguage("lang1",
						aPair[0]);
				Language lang2 = LanguageParameterValidator.getValidLanguage("lang2",
						aPair[1]);
				if(!rtn.contains(lang1))
					rtn.add(lang1);
				if(!rtn.contains(lang2))
					rtn.add(lang2);
			} else {
				// langPathの内容が不正だった場合
				throw new ServiceConfigurationException(
						"usage: langPath:[\"ja>en\",\"ja<>tl\",...]");
			}
			
			}catch(InvalidParameterException e){
				throw new ServiceConfigurationException(e.toString());
			}
		}
		
		return rtn;
	}
	
	/**
	 * iniファイル内、langPathに書かれた文字列から、対応する言語を作成
	 * @param strings "en" 等、対応言語の文字列表現
	 * @return 対応言語の、Language表現
	 * @throws ServiceConfigurationException 
	 */
	public static Collection<Language> stringsToLanguages(String[] strings)
			throws ServiceConfigurationException {
		ArrayList<Language> rtn = new ArrayList<Language>();

		for (int loop = 0; loop < strings.length; loop++) {

			String aPath = strings[loop];

			try {
				// rtnを追加
				rtn.add(LanguageParameterValidator.getValidLanguage("lang1", aPath));
			} catch (InvalidParameterException e) {
				throw new ServiceConfigurationException(e.toString());
			}
		}

		return rtn;
	}
	
	/*
	 * TODO 関数の統一
	 * 以下のintegrateLanguagePathesは、
	 * 各ResourcesのLangPathから得られた対応言語対を統合して返す関数群
	 * 現在はオーバーロードで対処しているが、
	 * あまりに冗長になるようならば、
	 * "LangPathを持つResource"を持つServiceDescription
	 * を表すクッション的な抽象クラスを作成し、１つの関数にまとめるべきか。
	 */
	
	/**
	 * 各ResourcesのLangPathから得られた対応言語対を統合して返す
	 * Webアプリケーションで提供される翻訳サービスに対応
	 * @param wsd 各Resourcesの含まれるWebapp_ServiceDescription
	 * @return Collection<LanguagePair> pairs 統合された対応言語対
	 */
	public static Collection<LanguagePair> integrateLanguagePathes(Webapp_ServiceDescription wsd){
		
		Collection<LanguagePair> pairs = new HashSet<LanguagePair>();
		
		if(wsd == null)return pairs;
		
		int num = wsd.getResources().length;
		int i = 0;
		for(i = 0;i < num; i++){
			
			pairs.addAll(wsd.getResources()[i].getLangPath());
			
		}
		
		return pairs;
		
	}

	/**
	 * 各ResourcesのLangPathから得られた対応言語対を統合して返す
	 * Webアプリケーションで提供される翻訳サービスに対応
	 * @param hsd 各Resourcesの含まれるWebapp_ServiceDescription
	 * @return Collection<LanguagePair> pairs 統合された対応言語対
	 */
	public static Collection<LanguagePair> integrateLanguagePathes(HTML_ServiceDescription hsd){
		
		Collection<LanguagePair> pairs = new HashSet<LanguagePair>();
		
		int num = hsd.getResources().length;
		int i = 0;
		for(i = 0;i < num; i++){
			
			pairs.addAll(hsd.getResources()[i].getLangPath());
			
		}
		
		return pairs;
		
	}
	
	/**
	 * 各ResourcesのLangPathから得られた対応言語対を統合して返す
	 * Webアプリケーションで提供される対訳辞書サービスに対応
	 * @param bsd 各Resourcesの含まれるWebapp_ServiceDescription
	 * @return Collection<LanguagePair> pairs 統合された対応言語対
	 */
	public static Collection<LanguagePair> integrateLanguagePathes(
			jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.Webapp_ServiceDescription bsd){
		
		Collection<LanguagePair> pairs = new HashSet<LanguagePair>();
		
		int num = bsd.getResources().length;
		int i = 0;
		for(i = 0;i < num; i++){
			
			pairs.addAll(bsd.getResources()[i].getLangPath());
			
		}
		
		return pairs;
		
	}
	
	/**
	 * 各ResourcesのLangPathから得られた対応言語対を統合して返す
	 * HTMLで提供される対訳辞書サービスに対応
	 * @param hsd 各Resourcesの含まれるWebapp_ServiceDescription
	 * @return Collection<LanguagePair> pairs 統合された対応言語対
	 */
	public static Collection<LanguagePair> integrateLanguagePathes(
			jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription hsd){
		
		Collection<LanguagePair> pairs = new HashSet<LanguagePair>();
		
		int num = hsd.getResources().length;
		int i = 0;
		for(i = 0;i < num; i++){
			
			pairs.addAll(hsd.getResources()[i].getLangPath());
			
		}
		
		return pairs;
		
	}

	/**
	 * 各ResourcesのLangPathから得られた対応言語対を統合して返す
	 * Webアプリケーションで提供される用例対訳サービスに対応
	 * @param wsd 各Resourcesの含まれるWebapp_ServiceDescription
	 * @return Collection<LanguagePair> pairs 統合された対応言語対
	 */
	public static Collection<LanguagePair> integrateLanguagePathes(
			jp.go.nict.langrid.wrapper.common.iniFileUtil.paralleltext.Webapp_ServiceDescription wsd){
		
		Collection<LanguagePair> pairs = new HashSet<LanguagePair>();
		
		int num = wsd.getResources().length;
		int i = 0;
		for(i = 0;i < num; i++){
			
			pairs.addAll(wsd.getResources()[i].getLangPath());
			
		}
		
		return pairs;
	}
	
	/**
	 * 各ResourcesのLangPathから得られた対応言語を統合して返す
	 * Webアプリケーションで提供される形態素解析に対応
	 * @param wsd 各Resourcesの含まれるWebapp_ServiceDescription
	 * @return Collection<LanguagePair> pairs 統合された対応言語対
	 */
	public static Collection<Language> integrateLanguages(
			jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_ServiceDescription wsd){
		
		Collection<Language> langs = new HashSet<Language>();
		
		if(wsd == null)return langs;
		
		int num = wsd.getResources().length;
		int i = 0;
		for(i = 0;i < num; i++){
			
			langs.addAll(wsd.getResources()[i].getLangPath());
			
		}
		
		return langs;
		
	}
}
