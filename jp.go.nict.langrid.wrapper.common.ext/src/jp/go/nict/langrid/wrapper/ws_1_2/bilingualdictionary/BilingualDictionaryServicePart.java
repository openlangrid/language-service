/*
 * $Id: BilingualDictionaryServicePart.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.common.ContentTracer;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_Resource;

/**
 * HTMLで提供される資源は、複数ページ群で構成されることが多い。
 * ページ群ごとに対訳データの切り出し方が異なる場合もあり、
 * それらを統合してひとつの資源として扱う必要が有る。
 * 
 * このクラスは、１つのページ群を独立したサブ資源として扱うためのもの。
 * 各サブ資源の統合は、SubServiceIntegraterクラスで行う。
 * 
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class BilingualDictionaryServicePart {
	
	//資源のサポートする言語対を保持
	protected Collection<LanguagePair> supported;
	
	//言語に整数を対応させる。GMAlibとの連携などに用いる
	protected Map<Language, Integer> languages;
	
	//langPathの内容を、CSV形式への変換用に保管
	protected Language[] languagesCSV;

	// 対訳データを格納
	protected ArrayList<String[]> texts;
	
	// スプリッタを格納
	protected String[] splitters;

	/**
	 * コンストラクタ
	 * .iniのresourcesからページ群のアドレスを取得し、
	 * それらから独立したサブ資源を作成する。
	 * @param res .iniの記述に対応するオブジェクト
	 * @throws ProcessFailedException
	 * @throws IOException 
	 */
	public BilingualDictionaryServicePart(HTML_Resource res) 
	throws ProcessFailedException, IOException{
		// 各メンバ変数の初期化
		languages = new HashMap<Language, Integer>();
		texts = new ArrayList<String[]>();
		ArrayList<String> refinedRegexes = new ArrayList<String>();
		
		//ローカル変数の初期化
		ArrayList<Language> alLanguageCSV = new ArrayList<Language>();
		
		// supported (対応する言語対の表記)を設定
		supported = res.getLangPath();
		
		// splittersを設定
		splitters = res.getContent().getScraper().getSplitters();
		
		// languages (LocalTextServiceとの連携のため、言語→インデックス を対応させる関数)
		// alLanguageCSV (LocalTextServiceとの連携のため ja:en:tl などの列を保管)
		// refinedRegexes (Lang0等を実際の言語コードに置換した正規表現)
		// を設定する
		
		// languages,languagesCSVを設定
		
		Iterator itr = supported.iterator();
		
		while (itr.hasNext()) {
			
			LanguagePair lanp = (LanguagePair) itr.next();

			if (languages.containsKey(lanp.getSource()) == false){
				languages.put(lanp.getSource(), languages.size());
				alLanguageCSV.add(lanp.getSource());
			}

			if (languages.containsKey(lanp.getTarget()) == false){
				languages.put(lanp.getTarget(), languages.size());
				alLanguageCSV.add(lanp.getTarget());
			}
		}
		
			
		languagesCSV = alLanguageCSV.toArray(new Language[0]);

		// iniのregex内の"lang1"などを,言語コードに変換し,
		// trimRegexで扱える形式にする.
		// (\\:Lang1:[^ <]+?[^<]*?) -> (\\:ja:[^ <]+?[^<]*?)など
		for (int i = 0; i < res.getContent().getScraper().getRegexes().length; i++) {

			String refinedRegex = new String(res.getContent().getScraper()
					.getRegexes()[i]);

			for (int j = 0; j < alLanguageCSV.size(); j++)
				refinedRegex = refinedRegex.replace("Lang" + j, alLanguageCSV
						.get(j).getCode());

			refinedRegexes.add(refinedRegex);

		}
				
				
		// 対訳を格納するHTMLtoData関数の呼び出し
		ContentTracer.trimRegexLangPair(res.getContent().getAdresses(),
				refinedRegexes,res.getContent().getScraper().getIgnores(),
				languages,texts,res.getContent().getCharSet());	
	}
	
	/**
	 * 保持しているTextsの内容を、CSV形式に対応した文字列で返す
	 * @param langIndex 言語コードの順序を保持
	 * @param splitter スプリッタとして使用する文字列
	 * @return 対訳ペアをスプリッタで区切ったもの
	 */
	public String getTextsAsCSV(Map<Language,Integer> langIndex, String splitter) {
		
		StringBuffer rtn = new StringBuffer();
		
		for(int i=0;i<texts.size();i++){
			
			//{"Hello","こんにちは","","",""}のように、対訳ペアを一旦格納する配列
			String[] buffPair = new String[langIndex.size()];
			for(int j=0;j<buffPair.length;j++){
				buffPair[j] = "";
			}

			//buffPairに、対訳ペアの内容を順次格納
			for(int j=0; j<languagesCSV.length;j++ ){
				buffPair[langIndex.get(languagesCSV[j])]
				         = texts.get(i)[j];				
			}

			//buffPairの内容を:で区切り文字列化
			StringBuffer buff = new StringBuffer();
			for(int j=0;j<buffPair.length;j++){
				buff.append(buffPair[j]);					
				if(j<buffPair.length-1){
					buff.append(splitter);
				}
			}
			rtn.append(buff);
			if(i<texts.size()-1){
				rtn.append("\n");
			}
		}
		
		return new String(rtn);
	}
	
	/**
	 * 保持しているTextsの内容を、CSV形式に対応した文字列の配列で返す
	 * 上のgetTextsAsCSVとこれの、どちらかを使用
	 * @param langIndex 言語コードの順序を保持
	 * @param splitter スプリッタとして使用する文字列
	 * @param subsplitter 対訳内の区切りに用いる
	 * @return 対訳ペアをスプリッタで区切ったもの
	 */
	public ArrayList<String> getTextsAsCSVList(Map<Language,Integer> langIndex,
			String splitter,String subsplitter){
		
		ArrayList<String> rtn = new ArrayList<String>();
		
		for(int i=0;i<texts.size();i++){
			
			//{"Hello","こんにちは","","",""}のように、対訳ペアを一旦格納する配列
			String[] buffPair = new String[langIndex.size()];
			for(int j=0;j<buffPair.length;j++){
				buffPair[j] = "";
			}

			//buffPairに、対訳ペアの内容を順次格納
			//splittersの内容をsubsplitterで置換する
			for(int j=0; j<languagesCSV.length;j++ ){
				String text=texts.get(i)[j];
				for(int k=0;k<splitters.length;k++){
					text = text.replaceAll(splitters[k],subsplitter);
				}
					buffPair[langIndex.get(languagesCSV[j])]
				         = text;			
			}

			//buffPairの内容を:で区切り文字列化
			StringBuffer buff = new StringBuffer();
			for(int j=0;j<buffPair.length;j++){
				buff.append(buffPair[j]);					
				if(j<buffPair.length-1){
					buff.append(splitter);
				}
			}
			rtn.add(buff.toString());
		}
		
		return rtn;
	}
	
	/**
	 * このサブ言語資源がサポートする対訳言語対を返す
	 * @return このサブ言語資源がサポートする対訳言語対
	 */
	public Collection<LanguagePair> getSupported(){
		return supported;
	}
}
