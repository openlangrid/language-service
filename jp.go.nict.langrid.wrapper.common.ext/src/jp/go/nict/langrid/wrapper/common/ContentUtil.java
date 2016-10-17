/*
 * $Id: ContentUtil.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

/**
 * 各種コンテンツを文字列として取得、作成などするための関数群。
 * 現在対応しているのは以下の操作
 * 取得
 * ・POSTによるcgi ・GETによるcgi ・HTTPで提供されている文書 ・ローカルの文書
 * 作成
 * ・ローカルの文書
 * 複製
 * ・ローカルの文書
 * 置換
 * ・ローカルの文書
 * 
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class ContentUtil {

	/**
	 * 対象URLのcgiを、指定する引数を与えて呼び出す
	 * Collection形式でパラメータを指定する
	 * @param url 対象URL
	 * @param method 呼び出し方法、POST or GET
	 * @param data パラメータ。LangPair<String1,String2>が、
	 * 	cgiには　"String1=String2"　として与えられる。
	 * @param charSet cgiの返す結果の文字セット
	 * @return cgiの返す結果
	 * @throws ProcessFailedException
	 */
	@SuppressWarnings("unchecked")
	public static String getCGI(String url, String method,
			Collection<Pair<String, String>> data, String charSet)
			throws ProcessFailedException {
		String dataStr = "";// cgiに引数として与える文字列

		// dataStrにcgiへの引数を格納
		Iterator itr = data.iterator();
		while (itr.hasNext()) {
			Pair<String, String> pair = (Pair<String, String>) itr.next();
			dataStr = dataStr.concat(pair.getFirst() + "=" + pair.getSecond()
					+ "&");
		}
		// 末尾の&を削除
		dataStr = dataStr.substring(0, dataStr.length() - 1);
		
		return getCGI(url,method,dataStr,charSet);
	}
		
	/**
	 * 対象URLのcgiを、指定する引数を与えて呼び出す
	 * String形式でパラメータを指定する
	 * @param url 対象URL
	 * @param method 呼び出し方法、POST or GET
	 * @param dataStr パラメータ
	 * @param charSet cgiの返す結果の文字セット
	 * @return cgiの返す結果
	 * @throws ProcessFailedException
	 */
	public static String getCGI(String url,String method,String dataStr,String charSet	) 
	throws ProcessFailedException{
		
		// メソッドがGETだった場合の処理
		if (method.matches("[Gg][Ee][Tt]")) {
			return ContentUtil.getHttp(url+"?"+dataStr , charSet);
		}

		// メソッドがPOSTだった場合の処理
		// 岩部さんのMEDOのコードを転用
		else if (method.matches("[Pp][Oo][Ss][Tt]")) {
			try{
			// バッファ用文字列
			String buff = "";

			// URLクラスのインスタンスを生成
			URL helloURL = new URL(url);
			// 接続します
			URLConnection con = helloURL.openConnection();

			// 出力を行うように設定します
			con.setDoOutput(true);
			con.setUseCaches(false);
			// 出力ストリームを取得
			PrintWriter out = new PrintWriter(con.getOutputStream());
			out.print(dataStr);
			out.close();
			// 入力ストリームを取得
			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream(), charSet));

			// 一行ずつ読み込みます
			String line;
			while ((line = in.readLine()) != null) {
				// バッファに格納
				buff = buff.concat(line + "\n");
			}
			// 入力ストリームを閉じます
			in.close();

			return buff;
			}catch (IOException e){
				throw new ProcessFailedException(e.toString());
			}
		} else {
			throw new ProcessFailedException(
					"method must be GET or POST");
		}

	}

	/**
	 * 対象URLのhtmlファイルを文字列として返す
	 * 
	 * @param targetURL
	 *            読み取り対象のURL
	 * @return 結果文字列
	 * @throws IOException
	 *             URLの書式が無効だった場合 内容の文字列化に失敗した場合
	 */
	public static String getHttp(String targetURL)
			throws IOException {
			URL url = new URL(targetURL);
			Object c = url.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					(InputStream) c));
			
			return getString(reader);
		
	}

	/**
	 * オーバーライド 引数として、対象URLの文書のキャラセットを持つ
	 * 
	 * @param targetURL
	 *            読み取り対象のURL
	 * @param charSet 対象URLの文字セット
	 * @return 結果文字列
	 * @throws ProcessFailedException
	 *             URLの書式が無効だった場合 内容の文字列化に失敗した場合
	 */
	public static String getHttp(String targetURL, String charSet)
			throws ProcessFailedException {

		try {
			URL url = new URL(targetURL);
			Object c = url.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					(InputStream) c, "iso-8859-1"));
			
			String rtn = new String(getString(reader).getBytes("iso-8859-1"),charSet);
								    
			return rtn;
			
		} catch (IOException e) {
			throw new ProcessFailedException(e.toString());
		}
	}

	/**
	 * 対象ファイル(UTF-8フォーマット)の内容を文字列として返す
	 * 
	 * @param filename
	 *            読み取り対象のファイルネーム
	 * @return 結果文字列
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String getLocal(String filename) throws IOException,
			FileNotFoundException {

		InputStream is = new FileInputStream(filename);
		BufferedReader reader = new BufferedReader(StreamUtil
				.createUTF8Reader(is));
		
		return getString(reader);
	}

	/**
	 * 対象ファイル(UTF-8フォーマット)の内容を文字列として返す
	 * 引数がストリーム
	 * @param is 読み取り対象のストリーム
	 * @return 結果文字列
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String getLocal(InputStream is) throws IOException,
			FileNotFoundException {

		BufferedReader reader = new BufferedReader(StreamUtil
				.createUTF8Reader(is));
		
		return getString(reader);
	}
	
	/**
	 * getCGI,getHTML,getTXT内で使用。 リーダから読み取った内容を全て一つの文字列として返す。
	 * 
	 * @param r 読み取り元に対応するリーダ
	 * @return 結果文字列
	 * @throws IOException
	 */
	public static String getString(BufferedReader r) throws IOException {
		
		StringBuffer content = new StringBuffer();
		String line;
		while ((line = r.readLine()) != null) {
			content.append(line + "\n");
		}
		r.close();

		return content.toString();

	}
	
	/**
	 * 文字列から、ローカルに新たなファイルを作成する。
	 * 既にファイルが存在する場合、上書き。
	 * @param filename 作成するファイル名
	 * @param content 作成するファイルの元となる文字列
	 * @throws IOException
	 */
	public static void setLocal(String filename, String content)
	throws IOException{
		
		PrintWriter toFile = 
			new PrintWriter(new OutputStreamWriter( new FileOutputStream(filename,false), "UTF8" ));
		toFile.print(content);
		toFile.flush();
		toFile.close();
	}
	
	/**
	 * 文字列から、ローカルに新たなファイルを作成する。
	 * 既にファイルが存在する場合、上書き。
	 * @param filename 作成するファイル名
	 * @param content 作成するファイルの元となる文字列
	 * @throws IOException
	 */
	public static void setLocal(String filename, String content,String charSet)
	throws IOException{
		
		PrintWriter toFile = 
			new PrintWriter(new OutputStreamWriter( new FileOutputStream(filename,false), charSet ));
		toFile.print(content);
		toFile.flush();
		toFile.close();
	}
		
	/**
	 * ローカルに存在するファイルを別名でコピーする。
	 * @param source コピー元のファイル名
	 * @param dest コピー先のファイル名(存在する場合上書き)
	 * @return コピーに成功した場合true
	 */
	public static boolean copy(String source, String dest){
		
		try {
			ContentUtil.setLocal(dest,ContentUtil.getLocal(source));

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * ローカルに存在するファイル内で、指定の文字列を置換する
	 * @param source 置換するファイル
	 * @param regex 置換元の文字列を表す正規表現
	 * @param replacement 置換文字列
	 * @throws IOException
	 */
	public static void replaceFileStr(String source, String regex, String replacement) 
	throws IOException{
		String buff = ContentUtil.getLocal(source);
		
		buff.replaceAll(regex,replacement);
		
		ContentUtil.setLocal(source,buff);
	}

}
