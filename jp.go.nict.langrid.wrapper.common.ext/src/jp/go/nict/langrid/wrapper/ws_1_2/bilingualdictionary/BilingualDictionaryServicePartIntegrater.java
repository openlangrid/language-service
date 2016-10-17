/*
 * $Id: BilingualDictionaryServicePartIntegrater.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
import java.util.HashSet;
import java.util.Map;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription;

/**
 * HTMLで提供される資源は、複数ページ群で構成されることが多い。 ページ群ごとに対訳データの切り出し方が異なる場合もあり、
 * それらを統合してひとつの資源として扱う必要が有る。
 * 
 * 各ページ群はHTMLSubServiceクラスで表される。
 * 
 * このSubServiceIntegraterクラスは、 外部からの検索要求を、各HTMLSubServiceオブジェクトに対応した検索に分解し要求。
 * 結果を統合する
 * 
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class BilingualDictionaryServicePartIntegrater {

	// 各サブ資源へのリンク
	private ArrayList<BilingualDictionaryServicePart> services;

	// 総体として対応する言語
	private HashSet<LanguagePair> supportedPairs;

	/**
	 * コンストラクタ、初期化処理を行う
	 * ServiceDescriptionを参照し、resourcesの要素ごとにHTMLSubServiceを作成する。
	 * また、全体としての対応言語対を設定する。
	 * 
	 * @param sd
	 *            対象資源を記述する.iniファイルに対応するオブジェクト
	 * 
	 * @throws ProcessFailedException
	 *             各サブ言語資源の初期化処理に伴う問題
	 * @throws IOException 
	 */
	public BilingualDictionaryServicePartIntegrater(HTML_ServiceDescription sd)
			throws ProcessFailedException, IOException {

		// フィールドの初期化
		services = new ArrayList<BilingualDictionaryServicePart>();
		supportedPairs = new HashSet<LanguagePair>();

		// ServiceDescriptionを参照し、
		// resourcesの要素ごとにHTMLSubServiceを作成する。

		for (int i = 0; i < sd.getResources().length; i++) {
			
			BilingualDictionaryServicePart buff;
			buff = new BilingualDictionaryServicePart(sd.getResources()[i]);
			
			services.add(buff);
			supportedPairs.addAll(services.get(i).getSupported());
		}
	}
	
	/**
	 * サポートしている対訳言語対を返す
	 * 
	 * @return サポートしている対訳言語対
	 */
	public Collection<LanguagePair> getSupportedPairs() {
		return supportedPairs;
	}

	/**
	 * 全サブ資源が持つ対訳ペアをCSV形式で統合
	 * 
	 * @return CSV形式で対訳ペアが記述された文字列
	 * @throws ServiceConfigurationException
	 */
	public String getResultsAsCSV() throws ServiceConfigurationException {

		// デフォルトのスプリッタ
		String splitter = ":";

		// en:ja:tl... と、出力用の順番を決定するため、
		// supportedPairsから、サポート内の各言語とインデックスを対応させる
		Map<Language, Integer> langIndex = new HashMap<Language, Integer>();

		for (LanguagePair langPair : supportedPairs) {
			if (langIndex.containsKey(langPair.getSource()) == false)
				langIndex.put(langPair.getSource(), langIndex.size());
			if (langIndex.containsKey(langPair.getTarget()) == false)
				langIndex.put(langPair.getTarget(), langIndex.size());
		}

		// splitterを決定
		// デフォルトのスプリッタ":"が、対訳の中に含まれていた場合
		// スプリッタを":."とする。
		// それでも駄目ならば、有効なスプリッタとなるまで"."を末尾に追加する
		String testResult = "";

		for (int i = 0; i < services.size(); i++) {
			testResult = testResult.concat(services.get(i).getTextsAsCSV(
					langIndex, " "));

			if (i < services.size() - 1) {
				testResult = testResult.concat("\n");
			}
		}

		while (testResult.contains(splitter))
			splitter = splitter.concat("s");

		// langIndexに対応した順番で列記された、CSV形式の各サブ資源の内容を連結
		String totalResult = "";

		// まず先頭に、splitterを記述
		totalResult = totalResult.concat("splitter=\"" + splitter + "\"\n");

		// 言語コードの並びを表す行(en:ja:...)を付加する

		String[] buffIndex = new String[langIndex.size()];
		for (Map.Entry<Language, Integer> entry : langIndex.entrySet()) {
			buffIndex[entry.getValue()] = entry.getKey().getCode();
		}

		// buffIndexの内容をsplitterで区切り文字列化
		StringBuffer buff = new StringBuffer();
		for (int j = 0; j < buffIndex.length; j++) {
			buff.append(buffIndex[j]);
			if (j < buffIndex.length - 1) {
				buff.append(splitter);
			}
		}
		totalResult = totalResult.concat(new String(buff) + "\n");

		for (int i = 0; i < services.size(); i++) {

			totalResult = totalResult.concat(services.get(i).getTextsAsCSV(
					langIndex, splitter));

			if (i < services.size() - 1) {
				totalResult = totalResult.concat("\n");
			}
		}

		return totalResult;
	}

	/**
	 * 全サブ資源が持つ対訳ペアをCSV形式で統合 重複する対訳対を１つにまとめる。 getResultsAsCSVとこちらのいずれかを用いる
	 * 検索時に都合が良いが、.dat作成に掛かる時間が大きくなる。
	 * 
	 * ただし、対象HTMLを取得し、.datおよび.rdfを作成 という工程で、200対訳程度の資源において両者には
	 * (環境にも影響されるが)6%程度の時間差しか生じなかったため、 こちらをメインに用いることとする。
	 * 
	 * @return CSV形式で対訳ペアが記述された文字列
	 */
	public String getResultsAsCSV_single() {

		// デフォルトのスプリッタ
		String splitter = ":";
		
		// デフォルトの対訳内スプリッタ
		String subsplitter = ";";
		
		// en:ja:tl... と、出力用の順番を決定するため、
		// supportedPairsから、サポート内の各言語とインデックスを対応させる
		Map<Language, Integer> langIndex = new HashMap<Language, Integer>();

		for (LanguagePair langPair : supportedPairs) {
			if (langIndex.containsKey(langPair.getSource()) == false)
				langIndex.put(langPair.getSource(), langIndex.size());
			if (langIndex.containsKey(langPair.getTarget()) == false)
				langIndex.put(langPair.getTarget(), langIndex.size());
		}

		// splitterを決定
		// デフォルトのスプリッタ":"が、対訳の中に含まれていた場合
		// スプリッタを":."とする。
		// それでも駄目ならば、有効なスプリッタとなるまで"."を末尾に追加する
		// subsplitterも同様に決定する
		String testResult = "";

		for (int i = 0; i < services.size(); i++) {
			testResult = testResult.concat(services.get(i).getTextsAsCSV(
					langIndex, " "));

			if (i < services.size() - 1) {
				testResult = testResult.concat("\n");
			}
		}

		while (testResult.contains(splitter))
			splitter = splitter.concat("s");
		
		while (testResult.contains(subsplitter))
			subsplitter = subsplitter.concat("s");
		
		// langIndexに対応した順番で列記された、CSV形式の各サブ資源の内容を連結
		String totalResult = "";

		// まず先頭に、languageSplitter、subsplitterを記述
		totalResult = totalResult.concat("languageSplitter=\"" + splitter + "\"\n");
		totalResult = totalResult.concat("translationSplitter=\"" + subsplitter + "\"\n");

		// 言語コードの並びを表す行(en:ja:...)を付加する

		String[] buffIndex = new String[langIndex.size()];
		for (Map.Entry<Language, Integer> entry : langIndex.entrySet()) {
			buffIndex[entry.getValue()] = entry.getKey().getCode();
		}

		// buffIndexの内容をsplitterで区切り文字列化
		StringBuffer buff = new StringBuffer();
		for (int j = 0; j < buffIndex.length; j++) {
			buff.append(buffIndex[j]);
			if (j < buffIndex.length - 1) {
				buff.append(splitter);
			}
		}
		totalResult = totalResult.concat(new String(buff) + "\n");

		// 実際の対訳ペアを格納

		for (int i = 0; i < services.size(); i++) {

			ArrayList<String> al = services.get(i).getTextsAsCSVList(langIndex,
					splitter,subsplitter);

			// 既にtotalResultに同一の対訳ペアが存在しなければ、totalResultに追加

			for (int j = 0; j < al.size(); j++) {

				if (!totalResult.contains(al.get(j)))
					totalResult = totalResult.concat(al.get(j) + "\n");
			}

		}
		
		return totalResult;
	}
	
}
