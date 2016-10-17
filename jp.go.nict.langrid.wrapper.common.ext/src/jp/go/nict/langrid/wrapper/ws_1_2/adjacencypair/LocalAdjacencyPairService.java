/*
 * $Id: LocalAdjacencyPairService.java 28172 2010-09-30 04:28:40Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.ws_1_2.adjacencypair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.util.LanguageFinder;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.service_1_2.adjacencypair.AdjacencyPair;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.service_1_2.util.LanguageParameterValidator;
import jp.go.nict.langrid.service_1_2.util.ParameterValidator;

/**
 * 隣接応答対用のライブラリクラスです．
 * 隣接応答対データ（ローカルに保存）が，指定の形式になっている必要があります．
 * 現在、カテゴリ情報を検索に含むインタフェースと、
 * 含まないインタフェースの双方に対応しています。
 * doSearch、setValue　は含まない場合に、
 * doSearchWithCategory、setValueWithCategory　は含む場合に呼び出されます。
 * 
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 28172 $
 */
public class LocalAdjacencyPairService
{
	/**
	 * 指定された質問文に対応する回答文の一覧を取得します．
	 * クエリにカテゴリを含むバージョンです
	 * @param category 発話のカテゴリ
	 * @param language 発話の言語
	 * @param firstPart 発話
	 * @param matchingMethod 検索方法
	 * @param texts 隣接応答対データを格納した配列（サポートする言語数分）
	 * @param answerSplitter 隣接応答対データの区切り文字
	 * @return 発話対の配列
	 * @throws InvalidParameterException カテゴリがnull
	 */
	public static Collection<AdjacencyPair> doSearch(
			String category, Language language, String firstPart, MatchingMethod matchingMethod,
			Map<Pair<Language, String>, Collection<String[]>> texts, String answerSplitter) 
			throws InvalidParameterException
	{

		// 変数の宣言
		Collection<AdjacencyPair> ret = 
			new ArrayList<AdjacencyPair>();
		Iterator<String[]> iter;
		
		// カテゴリがnullかどうかの判断
		if (category == null)
			throw new InvalidParameterException("category","Category can't be null;");

		// カテゴリが空文字列かどうかの判断
		if(category.length() == 0){
			// カテゴリが空文字列だった場合は全てのカテゴリについて検索する．
			Collection<String[]> tmpCol = new ArrayList<String[]>();
			Iterator<Pair<Language, String>> tmpItr = 
				texts.keySet().iterator();
			Pair<Language, String> aPair;
			while(tmpItr.hasNext()){
				// textsのキーのペアから，言語がクエリと一致するものを拾い出し，
				// そのキーに対応する応答データコレクションをtextsから取得する．
				aPair = tmpItr.next();
				if(aPair.getFirst().equals(language))
					tmpCol.addAll(texts.get(aPair));
			}
			iter = tmpCol.iterator();
			
		}else{
			// カテゴリが空文字ではなかった場合
			// 検索する言語・カテゴリの隣接応答対データを取得
			Pair<Language, String> linePair = new Pair<Language, String>(
					language, category);
			if (texts.containsKey(linePair))
				iter = texts.get(linePair).iterator();
			else
				return ret;
		}
		
		// 検索言語・カテゴリに対応する隣接応答対データの中から検索
		while(iter.hasNext())
		{
			String[] line = iter.next();
			if((matchingMethod.equals(MatchingMethod.COMPLETE) && line[0].equals(firstPart))
					|| (matchingMethod.equals(MatchingMethod.PREFIX) && line[0].startsWith(firstPart))
					|| (matchingMethod.equals(MatchingMethod.SUFFIX) && line[0].endsWith(firstPart))
					|| (matchingMethod.equals(MatchingMethod.PARTIAL) && line[0].contains(firstPart))
					|| (matchingMethod.equals(MatchingMethod.REGEX) && line[0].matches(firstPart)))
			{
				if(line[1].length()!=0)
				{
					// 条件にマッチするものを，返却用配列に追加
					ret.add(new AdjacencyPair(category
							, line[0], line[1].split(answerSplitter)));
				} else {
					// 対応する応答が無かった場合、空配列を返す
					ret.add(new AdjacencyPair(category
							, line[0], new String[0]));
				}
			}
		}
		return ret;
	}

	/**
	 * 指定された質問文に対応する回答文の一覧を取得します．
	 * クエリにカテゴリ情報を含むバージョンです．
	 * @param language 発話の言語
	 * @param firstPart 発話
	 * @param matchingMethod 検索方法
	 * @param texts 隣接応答対データを格納した配列（サポートする言語×カテゴリ数分）
	 * @param answerSplitter 隣接応答対データの区切り文字
	 * @return 発話対の配列
	 */
	public static String setValue(InputStream is, Map<Pair<Language, String>, Collection<String[]>> texts,
			Collection<Language> supportedLanguages) throws ProcessFailedException {
		String partSplitter = "";
		String answerSplitter = "";
		try
		{
			BufferedReader r = new BufferedReader(StreamUtil.createUTF8Reader(is));
			String line = "";
			int line_num = 0;
			while((line=r.readLine()) != null)
			{
				// 最初の行がBOM(U+ffef)で始まっていた場合、これを除去
				if(++line_num==1 & line.matches("\\ufeff+.*"))
					line=line.replaceFirst("\\ufeff+", "");
				
				// metafata記述行などに関する余計なmatches判断を行わないように，
				// 各要素の出現に対するフラグを設ける．
				int metadata_found = 0, partSplitter_found = 0, answerSplitter_found = 0;
				// "#"で始まる行と空行は，無視する
				if(line.startsWith("#") || line.trim().equals(""))
				{
				}
				// 最初の記述項目は，メタデータに関するもの
				else if(metadata_found == 0 && line.trim().matches("metadata(\\s)*=(\\s)*\".+\""))
				{
					// TODO メタデータ処理機能の実装
					metadata_found++;
				}
				// 2番目の記述項目は，言語・発話・応答の区切りに関するもの
				else if(partSplitter_found == 0 && line.trim().matches("partSplitter(\\s)*=(\\s)*\".+\""))
				{
					partSplitter = line.split("=")[1].split("\"")[1];
					partSplitter_found++;
				}
				// 3番目の記述項目は，応答の区切りに関するもの
				else if(answerSplitter_found == 0 && line.trim().matches("answerSplitter(\\s)*=(\\s)*\".+\""))
				{
					answerSplitter = line.split("=")[1].split("\"")[1];
					answerSplitter_found++;
				}
				// 4番目以降の記述項目は，隣接応答対データに関するもの
				// Add 
				// {"first_term","second_term1;second_term2..."}
				// to the map between the language and terms.
				// doSearch metohd splits the second_terms
				else if(line.split(partSplitter, 5).length==4)
				{
					String[] elem = line.split(partSplitter, 4);
					ParameterValidator.stringNotEmpty("language", elem[0]);
					Language ltemp = LanguageParameterValidator.getValidLanguage("language", elem[0]);
					Language l = LanguageParameterValidator.getUniqueLanguage("language", ltemp, LanguageFinder.find(supportedLanguages, ltemp));
					String category = elem[1];
					
					// Statement below means...
					// add(new String[]{category, first_turn, second_turns});
					Pair<Language, String> linePair = 
						new Pair<Language, String>(l, category);
					if(texts.containsKey(linePair))
						texts.get(linePair).add(new String[]{elem[2], elem[3]});
					else {
						texts.put(linePair, new ArrayList<String[]>());
						texts.get(linePair).add(new String[]{elem[2], elem[3]});
					}
				}
				// どれにもマッチしない場合は，データ形式が異常
				// （metadataなどの宣言が複数出現した場合も含む）
				else
				{
					throw new ServiceConfigurationException("error: invalid data format in line "+line_num);
				}
			}
		}
		catch(Exception e)
		{
			throw new ProcessFailedException(e.toString());
		}
				
		return answerSplitter;
	}
}
