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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.yahoodepend;

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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.dependencyparser.Dependency;
import jp.go.nict.langrid.service_1_2.dependencyparser.typed.DependencyLabel;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.dependencyparser.AbstractDependencyParserService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Yahoo APIによる日本語係り受け解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class YahooDepend extends AbstractDependencyParserService {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(YahooDepend.class.getName());
	
	/** アプリケーションID */
	private String appId = null;
	
	/** Httpリクエストのタイムアウト時間 */
	private int timeoutMillis = 1000;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** リクエストURL */
	private static final String REQUEST_URL = "http://jlp.yahooapis.jp/DAService/V1/parse";
	
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
	public YahooDepend() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	/**
	 * Yahoo APIでの日本語係り受け解析を実行し、解析結果を返す。
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>YahooAPIに解析リクエストを送信する</li>
	 * <li>返却されたレスポンス(XML)を解析して形態素オブジェクトリストを生成する</li>
	 * <li>結果を返却する</li>
	 * </ol>
	 * 
	 * @param language 言語
	 * @param text 解析する文章
	 */
	@Override
	protected Collection<Chunk> doParseDependency(Language language,
			String text) throws InvalidParameterException,
			ProcessFailedException {
		
		HttpURLConnection urlConnection = null;
		Collection<Chunk> chunks = null;
		
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
			chunks = parseResponse(document);
			
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
		return chunks;
	}
	
	/** 取得対象とするタグ名 */
	private static final String TAG_CHUNK = "Chunk";
	
	/** IDタグ */
	private static final String TAG_ID = "Id";
	
	/** Dependencyタグ */
	private static final String TAG_DEPENDENCY = "Dependency";
	
	/** MorphemeListタグ */
	private static final String TAG_MORPHEME_LIST = "MorphemList";
	
	/**
	 * レスポンスのXMLを解析して係り受け解析結果オブジェクトリストを返す
	 * @param document	レスポンスXML
	 * @return			係り受け解析結果オブジェクトリスト
	 */
	private Collection<Chunk> parseResponse(Document document) {
		Collection<Chunk> chunks = new ArrayList<Chunk>();
		Element root = document.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName(TAG_CHUNK);
		for (int i=0; i<nodeList.getLength(); i++) {
			chunks.add(createChunk(nodeList.item(i)));
		}
		return chunks;
	}
	
	/**
	 * XMLから係り受け解析結果オブジェクトを生成して返す。
	 * @param node 係り受け解析結果のXML
	 * @return 係り受け解析結果オブジェクト
	 */
	private Chunk createChunk(Node node) {
		NodeList nodeList = node.getChildNodes();
		Node child = null;
		String id = null;
		Dependency dependency = null;
		Morpheme[] morphs = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			child = nodeList.item(i);
			if (child.getNodeName().equals(TAG_ID)) {
				id = child.getTextContent();
			}
			if (child.getNodeName().equals(TAG_DEPENDENCY)) {
				dependency = new Dependency(DependencyLabel.DEPENDENCY.name(),
						child.getTextContent());
			}
			if (child.getNodeName().equals(TAG_MORPHEME_LIST)) {
				morphs = createMorphemes(child.getChildNodes());
			}
		}
		return new Chunk(id, morphs, dependency);
	}
	
	/** Surfaceタグ */
	private static final String TAG_SURFACE = "Surface";
	
	/** Baseformタグ */
	private static final String TAG_BASE_FORM = "Baseform";
	
	/** POSタグ */
	private static final String TAG_POS = "POS";
	
	/**
	 * XMLから形態素オブジェクトの配列を生成して返す。
	 * @param nodeList XMLで記述された形態素情報
	 * @return 形態素オブジェクトの配列
	 */
	private Morpheme[] createMorphemes(NodeList nodeList) {
		List<Morpheme> morphList = new ArrayList<Morpheme>();
		Node child = null;
		Node node = null;
		for (int i=0; i<nodeList.getLength(); i++) {
			String surface = null;
			String baseform = null;
			String partOfSpeech = null;
			String pos = "unkown";
			child = nodeList.item(i);
			if (child.getNodeName().equals("#text")) continue;
			for (int j=0; j<child.getChildNodes().getLength(); j++) {
				node = child.getChildNodes().item(j);
				if (node.getNodeName().equals(TAG_SURFACE)) {
					surface = node.getTextContent();
				}
				if (node.getNodeName().equals(TAG_BASE_FORM)) {
					baseform = node.getTextContent();
				}
				if (node.getNodeName().equals(TAG_POS)) {
					partOfSpeech = node.getTextContent();
				}
			}
			if (partsOfSpeechMap.containsKey(partOfSpeech)) {
				pos = partsOfSpeechMap.get(partOfSpeech).name();
			}
			morphList.add(new Morpheme(surface, baseform, pos));
		}
		return morphList.toArray(new Morpheme[] {});
	}
	
	/**
	 * アプリケーションIDをセットする
	 * @param appId アプリケーションID
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

}
