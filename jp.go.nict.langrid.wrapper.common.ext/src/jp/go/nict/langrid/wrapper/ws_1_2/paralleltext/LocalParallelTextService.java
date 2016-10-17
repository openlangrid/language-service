/*
 * $Id: LocalParallelTextService.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

/**
 * 用例対訳用のライブラリクラスです．
 * 用例対訳データ（ローカルに保存）が，指定の形式になっている必要があります．
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class LocalParallelTextService
{
	/**
	 * 対訳検索を行います．
	 * @param sourceLang 元言語．RFC3066準拠
	 * @param targetLang 対訳を探す言語．RFC3066準拠
	 * @param source 対訳を探す文章
	 * @param matchingMethod 検索方法（"string-exact", "prefix", "suffix", "substring", "regex"のいずれか）
	 * @param languages 利用可能な言語とそれに対応するインデックスの対を格納したハッシュ
	 * @param texts ある用例に対応する対訳データを格納した配列
	 * @return sourceパラメータの内容に対応する対訳の配列．存在しない場合空配列
	 */
	public static Collection<ParallelText> doSearch(
			Language sourceLang, Language targetLang,
			String source, MatchingMethod matchingMethod,
			Map<Language, Integer> languages, Collection<String[]> texts)
	{
		Integer srcIdx = languages.get(sourceLang);
		Integer tgtIdx = languages.get(targetLang);
		Collection<ParallelText> ret = new ArrayList<ParallelText>();
		Iterator<String[]> iter = texts.iterator();
		// 全対訳データの中から検索
		while(iter.hasNext())
		{
			String[] lines = iter.next();
			if((matchingMethod.equals(MatchingMethod.COMPLETE) && lines[srcIdx].equals(source))
					|| (matchingMethod.equals(MatchingMethod.PREFIX) && lines[srcIdx].startsWith(source))
					|| (matchingMethod.equals(MatchingMethod.SUFFIX) && lines[srcIdx].endsWith(source))
					|| (matchingMethod.equals(MatchingMethod.PARTIAL) && lines[srcIdx].contains(source))
					|| (matchingMethod.equals(MatchingMethod.REGEX) && lines[srcIdx].matches(source)))
			{
				if(lines[tgtIdx].length()!=0 && lines[srcIdx].length()!=0)
				{
					// 条件にマッチするものを，返却用配列に追加
					ret.add(new ParallelText(lines[srcIdx], lines[tgtIdx]));
				}
			}
		}
		return ret;
	}

	/**
	 * 用例対訳データファイルから，用例対訳データを取得します．
	 * 対訳ペアは，呼び出し元のコンストラクタで事前に書き下してください．
	 * @param is 用例対訳データが格納されているファイルハンドラ
	 * @param languages 利用可能な言語とそれに対応するインデックスの対を格納したハッシュ
	 * @param texts 用例に対応する用例対訳データを格納した配列
	 * @param supportedPairs 呼び出し元がサポートする言語ペア
	 * @throws ProcessFailedException サービスが適切に設定されていない
	 */
	public static void setValue(InputStream is, Map<Language, Integer> languages,
			Collection<String[]> texts, Collection<LanguagePair> supportedPairs)
	throws ProcessFailedException
	{
		String splitter = "";
		try
		{
			BufferedReader r = new BufferedReader(StreamUtil.createUTF8Reader(is));
			String line = "";
			int line_num=0;
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
				else if(line.trim().matches("splitter(\\s)*=(\\s)*\".+\""))
				{
					splitter = line.split("=")[1].split("\"")[1];
				}
				// 3番目の記述項目は，言語コードに関するもの
				else if(lang==null)
				{
					lang = line.split(splitter);
					// SupportedPair内の言語コードを，dat内の<splitter>で区切られたグループに割り当てる
					Iterator itr = supportedPairs.iterator();
					while(itr.hasNext())
					{
						LanguagePair lp = (LanguagePair)itr.next();
						// sourceLangに関する処理
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
				// 4番目以降の記述項目は，対訳辞書データに関するもの
				else if(line.split(splitter, languages.size()+1).length==languages.size())
				{
					texts.add(line.split(splitter, languages.size()));
				}
				// どれにもマッチしない場合は，データ形式が異常
				else
				{
					throw new ProcessFailedException("error: invalid data format at line "+line_num);
				}
			}
		}
		catch(IOException e)
		{
			throw new ProcessFailedException(e.toString());
		}
	}
}
