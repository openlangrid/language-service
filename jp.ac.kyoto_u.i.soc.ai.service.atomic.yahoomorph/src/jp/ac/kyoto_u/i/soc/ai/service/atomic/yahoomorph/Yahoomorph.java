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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.yahoomorph;

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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Yahoo日本語形態素解析用ラッパークラス
 * 
 * @author Ryo Morimoto
 */
public class Yahoomorph
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(Yahoomorph.class.getName());
	
	/** アプリケーションID */
	private String appId = null;
	
	/** Httpリクエストのタイムアウト時間 */
	private int timeoutMillis = 1000;
	
	/** 返却されるXMLのタイプ */
	private static final String RESULT_TYPE = "ma";
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** リクエストURL */
	private static final String REQUEST_URL = "http://jlp.yahooapis.jp/MAService/V1/parse";
	
	/** 品詞Map */
	private static final HashMap<String, PartOfSpeech> partsOfSpeechMap = new HashMap<String, PartOfSpeech>();
	static {
		partsOfSpeechMap.put("名詞", PartOfSpeech.noun);
		partsOfSpeechMap.put("動詞", PartOfSpeech.verb);
		partsOfSpeechMap.put("形容詞", PartOfSpeech.adjective);
		partsOfSpeechMap.put("副詞", PartOfSpeech.adverb);
		partsOfSpeechMap.put("助詞", PartOfSpeech.other);
		partsOfSpeechMap.put("助動詞", PartOfSpeech.other);
		partsOfSpeechMap.put("接続詞", PartOfSpeech.other);
		partsOfSpeechMap.put("感動詞", PartOfSpeech.other);
		partsOfSpeechMap.put("連体詞", PartOfSpeech.other);
		partsOfSpeechMap.put("接頭辞", PartOfSpeech.other);
		partsOfSpeechMap.put("接尾辞", PartOfSpeech.other);
		partsOfSpeechMap.put("形容動詞", PartOfSpeech.other);
		partsOfSpeechMap.put("特殊", PartOfSpeech.other);
	}
	
	/**
	 * コンストラクタ
	 */
	public Yahoomorph() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	/**
	 * YahooAPIによる形態素解析を行い、実行結果を整形して返す
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>YahooAPIに解析リクエストを送信する</li>
	 * <li>返却されたレスポンス(XML)を解析して形態素オブジェクトリストを生成する</li>
	 * <li>結果を返却する</li>
	 * </ol>
	 * 
	 * @param language 言語
	 * @param text 原文
	 * @return 形態素オブジェクトのコレクション
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		HttpURLConnection urlConnection = null;
		Collection<Morpheme> morphemes = null;
		
		try {
			// 解析リクエストの生成
			String param = "?appId=" + appId + "&results=" + RESULT_TYPE + "&sentence=";
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
			morphemes = parseResponse(document);
			
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
		return morphemes;
	}
	
	/** 取得対象とするタグ名 */
	private static final String TARGET_TAG_NAME = "word";
	
	/**
	 * レスポンスのXMLを解析して形態素オブジェクトリストを返す
	 * @param document	レスポンスXML
	 * @return			解析結果の形態素オブジェクトリスト
	 */
	private Collection<Morpheme> parseResponse(Document document) {
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName(TARGET_TAG_NAME);
		for (int i=0; i<nodeList.getLength(); i++) {
			morphemes.add(createMorpheme(nodeList.item(i)));
		}
		return morphemes;
	}
	
	/**
	 * 取得対象となるXMLノードから形態素オブジェクトを生成する
	 * @param node	XMLノード
	 * @return		形態素オブジェクト
	 */
	private Morpheme createMorpheme(Node node) {
		Node child = node.getFirstChild();
		String word = child.getTextContent();
		String partOfSpeech = child.getNextSibling().getNextSibling().getTextContent();
		String protoType = child.getNextSibling().getTextContent();
		PartOfSpeech pos = PartOfSpeech.unknown;
		if (partsOfSpeechMap.containsKey(partOfSpeech)) {
			pos = partsOfSpeechMap.get(partOfSpeech);
		}
		return new Morpheme(word, protoType, pos);
	}
	
	/**
	 * アプリケーションIDをセットする
	 * @param appId	 アプリケーションID
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	/**
	 * タイムアウト
	 * @param timeoutMillis
	 */
	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

}
