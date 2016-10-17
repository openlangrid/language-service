/*
 * $Id:LocalBilingualDictionaryService.java 1495 2006-10-12 18:42:55 +0900 (木, 12 10 2006) nakaguchi $
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

/**
 * 対訳辞書（Lite）用のライブラリクラスです．
 * 対訳辞書データ（ローカルに保存）が，指定の形式になっている必要があります．
 * @author $Author:nakaguchi $
 * @version $Revision:1495 $
 */
public class LocalBilingualDictionaryService
{
	/**
	 * 対訳辞書の検索を行います．
	 * @param headLang 元言語(RFC3066準拠)
	 * @param targetLang 検索対象の言語(RFC3066準拠)
	 * @param headword 検索語
	 * @param matchingMethod 検索方法("string-exact", "prefix", "suffix", "substring", "regex"のいずれか)
	 * @param languages 利用可能な言語とそれに対応するインデックスの対を格納したハッシュ
	 * @param texts 見出し語に対応する対訳辞書データを格納した配列
	 * @param translationSplitter 対訳辞書データの区切り文字
	 * @return 検索結果が格納された配列．存在しない場合空配列．
	 */
	public static Collection<Translation> doSearch(
			Language headLang, Language targetLang, String headword,
			MatchingMethod matchingMethod, Map<Language, Integer> languages,
			Collection<String[]> texts, String translationSplitter)
	{
		Integer srcIdx = languages.get(headLang);
		Integer tgtIdx = languages.get(targetLang);
		Collection<Translation> ret = new ArrayList<Translation>();
		Iterator<String[]> iter = texts.iterator();
		// 全対訳データの中から検索
		while(iter.hasNext())
		{
			String[] lines = iter.next();
			
			// 対訳データのうち、検索元言語に対応する部分を
			// translationSplitter で分割
			String[] headwordCandidates = lines[srcIdx].split(translationSplitter);
			
			// 分割されたheadwordごとに検索条件と比較
			for(String headwordCandidate : headwordCandidates){
			if((matchingMethod.equals(MatchingMethod.COMPLETE) && headwordCandidate.equals(headword))
					|| (matchingMethod.equals(MatchingMethod.PREFIX) && headwordCandidate.startsWith(headword))
					|| (matchingMethod.equals(MatchingMethod.SUFFIX) && headwordCandidate.endsWith(headword))
					|| (matchingMethod.equals(MatchingMethod.PARTIAL) && headwordCandidate.contains(headword))
					|| (matchingMethod.equals(MatchingMethod.REGEX) && headwordCandidate.matches(headword)))
			{
				if(lines[tgtIdx].length()!=0)
				{
					// 条件にマッチするものを，返却用配列に追加
					ret.add(new Translation(headwordCandidate, lines[tgtIdx].split(translationSplitter)));
				}
			}
			}
		}
		return ret;
	}

	/**
	 * 対訳辞書データファイルから，対訳辞書データを取得します．
	 * 対訳ペアは，呼び出し元のコンストラクタで事前に書き下してください．
	 * @param is 対訳辞書データが格納されているファイルハンドラ
	 * @param languages 利用可能な言語とそれに対応するインデックスの対を格納したハッシュ
	 * @param texts 見出し語に対応する対訳辞書データを格納した配列
	 * @param supportedPairs 呼び出し元がサポートする言語ペア
	 * @return 対訳辞書データの区切り文字
	 * @throws ProcessFailedException サービスが適切に設定されていない
	 */
	public static String setValue(InputStream is, Map<Language, Integer> languages,
			Collection<String[]> texts, Collection<LanguagePair> supportedPairs)
	throws ProcessFailedException
	{
		String languageSplitter = "";
		String translationSplitter = "";
		try
		{
			BufferedReader r = new BufferedReader(StreamUtil.createUTF8Reader(is));
			String line = "";
			int line_num = 0;
			String[] lang = null;
			while((line=r.readLine()) != null)
			{

				// 最初の行がBOM(U+ffef)で始まっていた場合、これを除去
				if(++line_num==1 & line.matches("\\ufeff+.*"))
					line=line.replaceFirst("\\ufeff+", "");
								
				// "#"で始まる行と空行は，無視する
				if(line.startsWith("#") || line.trim().equals(""))
				{
				}
				// 最初の記述項目は，メタデータに関するもの
				else if(line.trim().matches("metadata(\\s)*=(\\s)*\".+\""))
				{
					// TODO メタデータ処理機能の実装
				}
				// 2番目の記述項目は，言語間の区切りに関するもの
				else if(line.trim().matches("languageSplitter(\\s)*=(\\s)*\".+\""))
				{
					languageSplitter = line.split("=")[1].split("\"")[1];
				}
				// 3番目の記述項目は，対訳の区切りに関するもの
				else if(line.trim().matches("translationSplitter(\\s)*=(\\s)*\".+\""))
				{
					translationSplitter = line.split("=")[1].split("\"")[1];
				}
				// 4番目の記述項目は，言語コードに関するもの
				else if(lang==null)
				{
					lang = line.split(languageSplitter);
					// SupportedPair内の言語コードを，dat内の<languageSplitter>で区切られたグループに割り当てる
					Iterator itr = supportedPairs.iterator();
					while(itr.hasNext())
					{
						LanguagePair lp = (LanguagePair)itr.next();
						// headLangに関する処理
						if(!languages.containsKey(lp.getSource()))
						{
							for(int k=0; k<lang.length; k++)
							{
								if(lang[k].equals(lp.getSource().getCode()))
								{
									languages.put(lp.getSource(), k);
								}
							}
						}
						// targetLangに関する処理
						if(!languages.containsKey(lp.getTarget()))
						{
							for(int k=0; k<lang.length; k++)
							{
								if(lang[k].equals(lp.getTarget().getCode()))
								{
									languages.put(lp.getTarget(), k);
								}
							}
						}
					}
				}
				// 5番目以降の記述項目は，対訳辞書データに関するもの
				else if(line.split(languageSplitter, languages.size()+1).length==languages.size())
				{
					texts.add(line.split(languageSplitter, languages.size()));
				}
				// どれにもマッチしない場合は，データ形式が異常
				else
				{
					System.out.println("error: invalid data format at line "+line_num);
					System.out.println(line.split(languageSplitter, languages.size()+1).length+" : "+languages.size());
					//throw new ProcessFailedException("error: invalid data format at line "+line_num);
				}
			}
		}
		catch(IOException e)
		{
			//throw new ProcessFailedException(e.toString());
		}
		return translationSplitter;
	}
}
