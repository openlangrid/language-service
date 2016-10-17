/*
 * $Id: WebappTranslationService.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.ws_1_2.translation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.translation.Webapp_Action;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.translation.Webapp_Content;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.translation.Webapp_Resource;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.translation.Webapp_ServiceDescription;

/**
 * 翻訳サービスをラッピングする際に使用するライブラリ
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class WebappTranslationService {

	/**
	 * @param sLang 翻訳元言語のLanguageクラス
	 * @param tLang 翻訳後言語のLanguageクラス
	 * @param source 翻訳したい文字列
	 * @param sd iniファイルを読み込んで生成したServiceDescriptionクラス
	 * @return 翻訳後の文字列
	 * @throws ProcessFailedException
	 */
	public static String doTranslationImpl(Language sLang, Language tLang,
			String source, Webapp_ServiceDescription sd)
	throws ProcessFailedException
	{
		String url = "";
		String regex = "";
		String sCharset = "";
		String tCharset = "";
		String query = "";
		String result = "";
		String method = "";
		LanguagePair langPair = new LanguagePair(sLang,tLang);

		Webapp_Resource pair = sd.getResources(langPair);
		Webapp_Action action = pair.getAction();
		Webapp_Content content = pair.getContent();
		url = action.getUrl();
		method = action.getMethod().toUpperCase();
		
		regex = content.getScraper().getRegexes()[0];

		sCharset = content.getSourceCharSet();
		tCharset = content.getTargetCharSet();

		//ArrayList<String> stringOfQuery = pair.getVariables();
		try {

			query = makeQuery(sLang.getCode(), tLang.getCode(), source, action.getQuery(), sd.getLangMap(), sCharset);

			if (method.equals("GET")) {
				result = getResultByGet(url, query, tCharset, regex);
			} else if (method.equals("POST")) {
				result = getResultByPost(url, query, tCharset, regex);
			}

		} catch (ProcessFailedException e) {
			
			throw e;
			
		} catch (Exception e) {
			//e.printStackTrace();
			throw new ProcessFailedException("missed");
		}

		return result;
	}

	/**
	 * クエリを生成するメソッド
	 * @param sLang 翻訳元言語
	 * @param tLang 翻訳後言語
	 * @param source 翻訳を行う文字列
	 * @param modelOfQuery 生成するクエリのモデル
	 * @param stringOfQuery クエリの中に埋め込む文字列のArrayList
	 * @param charset 翻訳元の文字コード
	 * @return 生成されたクエリ
	 * @throws ProcessFailedException
	 */
	public static String makeQuery(String sLang, String tLang, String source, String modelOfQuery,
			HashMap<String,String> stringOfQuery ,String charset)
	throws ProcessFailedException {

		String query = modelOfQuery;

		try {

			query = query.replaceAll("\\$\\{sourceLang\\}", stringOfQuery.get(sLang));
			query = query.replaceAll("\\$\\{targetLang\\}", stringOfQuery.get(tLang));
			
			query = query.replaceAll("\\$\\{StoT\\}", stringOfQuery.get(sLang+tLang));
			
			query = query.replaceAll("\\$\\{source\\}", URLEncoder.encode(source,charset));
		} catch (Exception e) {
			throw new ProcessFailedException("Error in making Query");
		}
		
		return query;
	}
	
	/**
	 * GETメソッドで結果を得るメソッド
	 * @param action GETを行う際のURL
	 * @param query GETを行う際にURLにつなげるクエリ
	 * @param charset GETメソッドで帰ってきた情報の文字コード
	 * @param regex GETメソッドで帰ってきた情報から翻訳結果を切り出すための文字コード
	 * @return 翻訳結果
	 * @throws ProcessFailedException
	 */
	public static String getResultByGet(String action, String query,
			String charset, String regex) throws ProcessFailedException {

		String result = "";
		try {
			// URLクラスのインスタンスを生成
			URL reqUrl = new URL(action+query);

			// 接続し結果を読み込む
			HttpURLConnection connection = (HttpURLConnection) reqUrl
					.openConnection();
			InputStreamReader http = new InputStreamReader(connection
					.getInputStream(), "iso-8859-1");
			BufferedReader in = new BufferedReader(http);

			String line;
			
			String content = new String(in.readLine().getBytes("iso-8859-1"),charset);
			
			while ((line = in.readLine()) != null) {
				String line3 = new String(line.getBytes("iso-8859-1"), charset);
				content += line3 + "\n";
			}


			Pattern ptn = Pattern.compile(regex, Pattern.DOTALL);
			Matcher mch = ptn.matcher(content);
			mch.find();
			result = mch.group(1);
			result = convertSpecialChar(result);

			// 入力ストリームを閉じます
			in.close();
		} catch (ProcessFailedException e) {

			throw e;
			
		} catch (Exception e) {

			throw new ProcessFailedException("Error in getting result");
		
		}

		return result;
	}

	/**
	 * POSTメソッドで結果を得るメソッド
	 * @param action POSTを行う際のURL
	 * @param query POSTを行う際のクエリ
	 * @param charset POSTメソッドで帰ってきた情報の文字コード
	 * @param regex POSTメソッドで帰ってきた情報から翻訳結果を切り出すための文字コード
	 * @return 翻訳結果
	 * @throws ProcessFailedException
	 */
	public static String getResultByPost(String action, String query,
			String charset, String regex) throws ProcessFailedException {

		String result = "";
		
		try {
			// URLクラスのインスタンスを生成
			URL helloURL = new URL(action);
			// 接続します
			URLConnection con = helloURL.openConnection();
			// 出力を行うように設定します
			con.setDoOutput(true);
			// 出力ストリームを取得
			PrintWriter out = new PrintWriter(con.getOutputStream());

			out.print(query);
			out.close();

			// 入力ストリームを取得
			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream(), "iso-8859-1"));
			// 一行ずつ読み込みます
			String line;
			String content = new String(in.readLine().getBytes("iso-8859-1"),charset);

			while ((line = in.readLine()) != null) {
				String line3 = new String(line.getBytes("iso-8859-1"), charset);
				content += line3 + "\n";
			}

			Pattern ptn = Pattern.compile(regex);
			//System.out.println(content);
			Matcher mch = ptn.matcher(content);
			mch.find();
			result = mch.group(1);

			result = convertSpecialChar(result);

			// 入力ストリームを閉じます
			in.close();
		} catch (ProcessFailedException e) {
			throw e;
		} catch (Exception e) {
			throw new ProcessFailedException("Error in getting result");
		}

		return result;
	}

	/**
	 * HTMLの特殊文字を変換するメソッド
	 * @param src 変換したい文字列
	 * @return 変換後の文字列
	 * @throws ProcessFailedException
	 */
	public static String convertSpecialChar(String src)
	throws ProcessFailedException {
		String result = "";
		// String f_result = "";

		try {
			result = src;
			Pattern ptn = Pattern.compile("&#([0-9]+);");
			Matcher mch = ptn.matcher(result);
			while (mch.find()) {
				result = result.replaceAll("&#" + mch.group(1) + ";", String
						.valueOf((char) Integer.parseInt(mch.group(1))));
			}
		} catch (Exception e) {

		throw new ProcessFailedException("error in convert Special Char");
		
		}

		return result;

	}
}
