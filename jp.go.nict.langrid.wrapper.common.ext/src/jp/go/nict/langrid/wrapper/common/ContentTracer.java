/*
 * $Id: ContentTracer.java 28172 2010-09-30 04:28:40Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.service_1_2.util.LanguageParameterValidator;

/**
 * Webページの取得、cgiの結果などにより得た、
 * HTMLなどのソースに対する処理をまとめたクラス。
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 28172 $
 */
public class ContentTracer {
	
	/***************************************************************************
	 資源全般で用いるもの
	 **************************************************************************/
	
	/**
	 * 与えられた文字列内で、与えられた正規表現をマッチする。
	 * グループ要素を全て含んだString配列を、マッチした部分ごとに作成。
	 * 最終的にArrayListに格納して返す
	 * @param content 走査する文字列
	 * @param regex 正規表現
	 * @return グループの文字列を、マッチした部分ごとに含んだコレクション
	 */
	public static ArrayList<String[]> trimRegex(String content, String regex) {
		
		ArrayList<String[]> rtn = new ArrayList<String[]>();
		
		if(content==null)
			return rtn;

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		int size = matcher.groupCount();
		
		while (matcher.find() == true) {
									
			String[] lngArray = new String[size];

			for (int i = 0; i < size; i++)
				lngArray[i]=matcher.group(i+1);
				
			rtn.add(lngArray);
		}
		return rtn;
	}
	
	/**
	 * 与えられた文字列から、正規表現にマッチする部分を消し去る
	 * 引数をコレクションで与えるもの
	 * 
	 * @param content
	 *            対象文字列
	 * @param ignores
	 *            無視したい部分の正規表現
	 * @return 処理結果の文字列
	 * @throws ProcessFailedException 
	 */
	public static String executeIgnore(String content,
			ArrayList<String> ignores) throws ProcessFailedException {
		for(String patternStr : ignores){
			try{
				Pattern pattern = Pattern.compile(patternStr);
				Matcher matcher = pattern.matcher(content);
				content = matcher.replaceAll("");
			}catch (PatternSyntaxException e){
				throw new ProcessFailedException("Invalid Regex Pattern : "+patternStr);
			}
		}
		return content;
	}
	
	/**
	 * 与えられた文字列から、正規表現にマッチする部分を消し去る
	 * 引数を配列で与えるもの
	 * 
	 * @param content
	 *            対象文字列
	 * @param ignores
	 *            無視したい部分の正規表現
	 * @return 処理結果の文字列
	 * @throws ServiceConfigurationException 
	 */
	public static String executeIgnore(String content,String[] ignores)
	throws ProcessFailedException {

		Pattern pattern;
		Matcher matcher;

		for (int loop=0;loop < ignores.length;loop++) {
			String patternStr = ignores[loop];
			try{
			pattern = Pattern.compile(patternStr);
			matcher = pattern.matcher(content);
			content = matcher.replaceAll("");
			}catch (PatternSyntaxException e){
				throw new ProcessFailedException("Invalid Regex Pattern : "+patternStr);
			}
		}
		return content;
	}


	/***************************************************************************
	 特に用例対訳、対訳辞書で用いるもの
	 **************************************************************************/
	
	/**
	 * HTMLファイルを取得し、 その中から正規表現に従って単語の対訳の組を取り出す。 取り出した組は、既に作成済みの言語列に従い配列に格納される。
	 * そのような配列はArrayList形式で全ての単語について作成される。
	 * 
	 * @param addresses
	 *            対象HTMLのアドレス
	 * @param regexes
	 *            HTML内でマッチする正規表現、表記法は別記
	 * @param ignores
	 *            マッチングの前に、HTMLの中から取り除いておきたい部分の正規表現
	 * @param languages
	 *            与えられたHTML内で扱われている言語
	 * @param texts
	 *            結果の対訳の組を格納するArrayList
	 * @param charSet 
	 * 			　対象アドレスの文字セット
	 * @throws ProcessFailedException
	 *             gather.dat内、記述されたURLの取得に失敗した場合
	 *             gather.dat内、正規表現の記述内の言語コードに問題がある場合
	 * @throws IOException 
	 */
	public static void trimRegexLangPair(ArrayList<String> addresses,
			ArrayList<String> regexes, ArrayList<String> ignores,
			Map<Language, Integer> languages, ArrayList<String[]> texts,
			String charSet)
			throws ProcessFailedException , IOException{
		

		for (int adrloop = 0; adrloop < addresses.size(); adrloop++) {
			String content;
			if(charSet==null)
				content = ContentUtil.getHttp(addresses.get(adrloop));
			else
				content = ContentUtil.getHttp(addresses.get(adrloop),charSet);
			
			// 無視する文字列を消去
			
			trimRegexLangPairCore(content,regexes,ignores,languages,texts);
			
		}
	}

	/**
	 * 与えられた文字列の中から正規表現に従って単語の対訳の組を取り出す。 取り出した組は、既に作成済みの言語列に従い配列に格納される。
	 * そのような配列はArrayList形式で全ての単語について作成される。
	 * @param content 
	 * 			  切り出し対象の文字列
	 * @param regexes
	 *            文字列内でマッチする正規表現
	 * @param ignores
	 *            マッチングの前に、文字列の中から取り除いておきたい部分の正規表現
	 * @param languages
	 *            与えられた文字列内で扱われている言語
	 * @param texts
	 *            結果の対訳の組を格納するArrayList
	 * @throws ProcessFailedException
	 *             gather.dat内、記述されたURLの取得に失敗した場合
	 *             gather.dat内、正規表現の記述内の言語コードに問題がある場合
	 */
	public static void trimRegexLangPairCore(String content,
			ArrayList<String> regexes, ArrayList<String> ignores,
			Map<Language, Integer> languages, ArrayList<String[]> texts)
	throws ProcessFailedException{

		Map<Integer, Integer> langOrd = new HashMap<Integer, Integer>();// 出現順とインデックスを対応

		int tokenOrd;// \:LanguageName:が出現した回数を記憶
		
		content = ContentTracer.executeIgnore(content, ignores);
				
		// contentに順に正規表現を適用するための準備
		for(String regex : regexes) {
							
			// 初期化処理
			tokenOrd = 0;

			// 各言語の単語を、言語に対応させる
			// \:LanguageName:を切り取り、
			// 「言語の登場順」→「textsのインデックス」なるMAPを作る
			// rgx内に、サポート外の言語が入っている場合、
			// ServiceConfigurationExceptionを返す
			langOrd.clear();
			Pattern langToken = Pattern.compile("\\\\:(.*?):");
			Matcher langTokenMatcher = langToken.matcher(regex);
			while (langTokenMatcher.find() == true) {
				
				try {
					tokenOrd++;

					if (languages.containsKey(LanguageParameterValidator
							.getValidLanguage("sourceLang",
									(langTokenMatcher.group(1)))))
						langOrd.put(tokenOrd, languages
								.get(LanguageParameterValidator.getValidLanguage(
										"sourceLang", (langTokenMatcher
												.group(1)))));
					
					else throw new ProcessFailedException(
							"Regex " +regex+" contains unsupported language : "
							+langTokenMatcher.group(1));
					
				} catch (InvalidParameterException e) {
					throw new ProcessFailedException(
							"Bad LanguageName in \\:LanguageName: - "+e.toString());
				}
			}
			regex = regex.replaceAll("\\\\:(.*?):", "");

			// ここから、実際に単語を配列に格納する作業に入る
			// 仮に格納法がCollection<String[]>でなくなった場合、
			// ここからを修正すればよい

			try {
				
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(content);

				while (matcher.find() == true) {
					String[] lngArray = new String[languages.size()];

					// 出現順に対応する言語がlanguagesに含まれていれば追加
					// 含まれていなければ無視
					for (int i = 1; i < langOrd.size() + 1; i++)
						if (langOrd.containsKey(i)) {
							lngArray[langOrd.get(i)] = matcher.group(i);
						}
											
					texts.add(lngArray);

				}
			} catch (PatternSyntaxException e) {
				throw new ProcessFailedException(
						"Invalid Regex Pattern : " + regex);

			}
		}
		
	}
	
	/**
	 * HTMLファイルを取得し、 その中から正規表現に従って単語の対訳の組を取り出す。 取り出した組は、既に作成済みの言語列に従い配列に格納される。
	 * そのような配列はArrayList形式で全ての単語について作成される。
	 * 上の関数の、引数の一部を配列で与えられるようにしたもの
	 * 
	 * @param addresses
	 *            対象HTMLのアドレス
	 * @param regexes
	 *            HTML内でマッチする正規表現、表記法は別記
	 * @param ignores
	 *            マッチングの前に、HTMLの中から取り除いておきたい部分の正規表現
	 * @param languages
	 *            与えられたHTML内で扱われている言語
	 * @param texts
	 *            結果の対訳の組を格納するArrayList
	 * @param charSet 
	 * 			　対象アドレスの文字セット
	 * @throws ProcessFailedException
	 *             gather.dat内、記述されたURLの取得に失敗した場合
	 *             gather.dat内、正規表現の記述内の言語コードに問題がある場合
	 * @throws IOException 
	 */
	public static void trimRegexLangPair(String[] addresses,
			ArrayList<String> regexes, String[] ignores,
			Map<Language, Integer> languages, ArrayList<String[]> texts,
			String charSet)
			throws ProcessFailedException, IOException {
		
		ArrayList<String> alAddress = new ArrayList<String>();
		ArrayList<String> alIgnores = new ArrayList<String>();
		
		for(int i=0;i<addresses.length;i++){
			alAddress.add(addresses[i]);
		}

		for(int i=0;i<ignores.length;i++){
			alIgnores.add(ignores[i]);
		}
		
		trimRegexLangPair(alAddress,regexes,alIgnores,languages,texts,charSet);
		
	}
	

	/***************************************************************************
	 特に形態素解析で用いるもの
	 **************************************************************************/
	
	/**
	 * 与えられた文字列の中から正規表現に従って形態素を切り出す
	 * Trim the morphemes from the content using given regular expressions.
	 * @param content 切り出し対象の文字列 the text that contains original data
	 * @param regexes 文字列内でマッチする正規表現 the regular expressions to match
	 * @param ignores マッチングの前に、文字列の中から取り除いておきたい部分の正規表現
	 * the regular expressions to delete from content before matching
	 * @return 形態素を格納したコレクション A collection that contains morphemes
	 * @throws ProcessFailedException
	 */
	public static Collection<String[]> trimRegexMorphological(String content,
			String[] regexes, String[] ignores)
			throws ProcessFailedException{
		
		content = ContentTracer.executeIgnore(content, ignores);
		
		// 形態素を格納するコレクション
		// 形態素に相当する表現の開始位置が、コレクション内の位置に対応
		TreeMap<Integer,String[]> result = new TreeMap<Integer,String[]>();
		
		//それぞれのregexに対して処理を行う	
		for(int i=0;i<regexes.length;i++){			
			
			//現在のregexから\:...:を消去し、正当な正規表現に
			String regexClear = regexes[i].replaceAll("\\\\:[^:]*:","");
			//word,lemma,partOfSpeechを取得する正規表現
			String regexWord = regexes[i].replaceAll("\\\\:word:","").replaceAll("\\\\:[^:]*:","?:");
			String regexLemma = regexes[i].replaceAll("\\\\:lemma:","").replaceAll("\\\\:[^:]*:","?:");
			String regexPart = regexes[i].replaceAll("\\\\:part:","").replaceAll("\\\\:[^:]*:","?:");
			
			Pattern parentPattern = Pattern.compile(regexClear);
			Matcher parentMatcher = parentPattern.matcher(content);
			
			while (parentMatcher.find() == true) {				
				//マッチした部分の文字列、インデックスを得る
				String aPart = parentMatcher.group();
				Integer idx = parentMatcher.start();

				//wordを格納
				//\\:word:の出現数が１で無かった場合例外
				Pattern pattern = Pattern.compile(regexWord);
				Matcher matcher = pattern.matcher(aPart).reset();
				if(matcher.groupCount()!=1)
					throw new ProcessFailedException(
							regexes[i]+" has more or less than 1 \"Word\" regexes");
				if(!matcher.find())
					throw new ProcessFailedException(
							regexes[i]+" is incorrect");
				String word = matcher.group(1);
				
				//lemmaを格納
				//\\:lemma:の出現数が２以上の場合例外
				//０ならばlemmaには空文字列を代入
				String lemma = "";
				pattern = Pattern.compile(regexLemma);
				matcher = pattern.matcher(aPart).reset();
				if(matcher.groupCount()>1)
					throw new ProcessFailedException(
							regexes[i]+" has more than 1 \"Word\" regexes");
				if(matcher.groupCount()==1){
					if(!matcher.find())
						throw new ProcessFailedException(
							regexes[i]+" is incorrect");
					lemma = matcher.group(1);
				}

				//partを格納
				//\\:part:の出現数が１で無かった場合例外
				pattern = Pattern.compile(regexPart);
				matcher = pattern.matcher(aPart).reset();
				if(matcher.groupCount()!=1)
					throw new ProcessFailedException(
							regexes[i]+" has more or less than 1 \"Word\" regexes");
				if(!matcher.find())
					throw new ProcessFailedException(
							regexes[i]+" is incorrect");
				String part = matcher.group(1);
								
				result.put(idx,new String[]{word,lemma,part});				
			}
			
		}
		return result.values();
	}

}
