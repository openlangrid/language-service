/*
 * This is a program to wrap language resources as Web services.
 * 
 * Copyright (C) 2012 Department of Social Informatics, Kyoto University.
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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.yahoophrase;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.keyphraseextract.Keyphrase;
import jp.go.nict.langrid.wrapper.ws_1_2.keyphraseextract.AbstractKeyphraseExtractService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * YahooAPIによるキーフレーズ抽出ラッパー
 * 
 * @author Ryo Morimoto
 */
public class YahooPhrase
extends AbstractKeyphraseExtractService {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(YahooPhrase.class.getName());
	
	/** アプリケーションID */
	private String appId = null;
	
	/** Httpリクエストのタイムアウト時間 */
	private int timeoutMillis = 1000;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** リクエストURL */
	private static final String REQUEST_URL = "http://jlp.yahooapis.jp/KeyphraseService/V1/extract";
	
	/**
	 * コンストラクタ
	 */
	public YahooPhrase() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	/**
	 * YahooAPIによるキーフレーズ抽出を行い、実行結果を整形して返す
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>YahooAPIに解析リクエストを送信する</li>
	 * <li>返却されたレスポンス(XML)を解析してキーフレーズオブジェクトリストを生成する</li>
	 * <li>結果を返却する</li>
	 * </ol>
	 * 
	 * @param language 言語
	 * @param text 原文
	 * @return キーフレーズオブジェクトのコレクション
	 */
	@Override
	protected Collection<Keyphrase> doExtract(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		HttpURLConnection urlConnection = null;
		Collection<Keyphrase> keyphrases = null;
		
		try {
			// 解析リクエストの生成
			String param = "?appId=" + appId + "&sentence=";
			param += URLEncoder.encode(text, CHAR_SET);
			URL url = new URL(REQUEST_URL + param);
			
			urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setReadTimeout(timeoutMillis);
			urlConnection.setRequestMethod("GET");
			urlConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlConnection.addRequestProperty("Accept-Language", "ja,en-us;q=0.7,en;q=0.3");
			urlConnection.addRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.connect();
			
			// HTTPステータスコードが200以外であればエラー
			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("http error. this code is " + urlConnection.getResponseCode());
			}
			
			// レスポンスXML解析用DOMオブジェクトの生成
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(urlConnection.getInputStream());
			
			// レスポンスXMLの解析
			keyphrases = parseResponse(document);
			
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		} catch (SAXException e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		} finally {
			if (urlConnection != null) {
				try {
					urlConnection.disconnect();
				} catch (Exception e) {
					;
				}
			}
		}
		return keyphrases;
	}
	
	/** 取得対象とするタグ名 */
	private static final String TARGET_TAG_NAME = "Result";
	private static final String KEYPHRASE_TAG = "Keyphrase";
	private static final String SCORE_TAG = "Score";
	
	/**
	 * レスポンスのXMLを解析して形態素オブジェクトリストを返す
	 * @param document	レスポンスXML
	 * @return			解析結果の形態素オブジェクトリスト
	 */
	private Collection<Keyphrase> parseResponse(Document document) {
		Collection<Keyphrase> keyphrases = new ArrayList<Keyphrase>();
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName(TARGET_TAG_NAME);
		Node node = null;
		Node child = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			node = nodeList.item(i);
			String keyphrase = null;
			String score = null;
			for (int j=0; j<node.getChildNodes().getLength(); j++) {
				child = node.getChildNodes().item(j);
				if (child.getNodeName().equals(KEYPHRASE_TAG)) {
					keyphrase = child.getTextContent();
				} else if (child.getNodeName().equals(SCORE_TAG)) {
					score = child.getTextContent();
				}
			}
			keyphrases.add(new Keyphrase(keyphrase, Double.parseDouble(score)));
		}
		return keyphrases;
	}
	
	/**
	 * アプリケーションIDをセットする
	 * @param appId アプリケーションID
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	/**
	 * タイムアウト時間をセットする
	 * @param timeoutMillis タイムアウト時間
	 */
	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}
	
}
