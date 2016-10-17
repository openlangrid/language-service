package jp.go.nict.langrid.wrapper.googlelanguageidentification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.methods.PostMethod;

public class GoogleIdentifyExperiment
extends TestCase
{
	public void test() throws Exception{
		URL reqUrl = new URL(url +
				"&key=" + e(key) + "&q=" + e(text) + "&source=" + e("ja") + "&target="  + e("en"));
		System.out.println("url: " + reqUrl);
		System.out.println("url length: " + reqUrl.toString().length());
		String json = StreamUtil.readAsString(reqUrl.openStream(), "UTF-8");
		JSONObject o = JSONObject.fromObject(json);
		System.out.println(
				"result:"
				+ ((o.getJSONObject("data")).getJSONArray("translations")).getJSONObject(0).get("translatedText")
				);
	}

	public void testByCommonsHttp_POST() throws Exception{
		HttpsURLConnection c = (HttpsURLConnection)new URL(url).openConnection();
		try{
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.addRequestProperty("X-HTTP-Method-Override", "GET");

			PostMethod m = new PostMethod();
			m.addParameter("key", key);
			m.addParameter("q", text);
			m.addParameter("source", "ja");
			m.addParameter("target", "en");
			m.setRequestHeader(
					"Content-type"
					, "application/x-www-form-urlencoded;"
					);
			m.getRequestEntity().writeRequest(c.getOutputStream());
			System.out.println(StreamUtil.readAsString(
					c.getInputStream(), "ISO-8859-1"
					));
			System.out.println("Content-Length: "
					+ c.getRequestProperty("Content-Length")
					);
		} finally {
			c.disconnect();
		}
	}

	public void test_isTranslatable() throws Exception{
		HttpsURLConnection c = (HttpsURLConnection)new URL(url).openConnection();
		try{
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.addRequestProperty("X-HTTP-Method-Override", "GET");

			PostMethod m = new PostMethod();
			m.addParameter("key", key);
			m.addParameter("q", text);
			m.addParameter("source", "ja");
			m.addParameter("target", "tg");
			m.setRequestHeader(
					"Content-type"
					, "application/x-www-form-urlencoded;"
					);
			m.getRequestEntity().writeRequest(c.getOutputStream());
			System.out.println(StreamUtil.readAsString(
					c.getInputStream(), "ISO-8859-1"
					));
			System.out.println("Content-Length: "
					+ c.getRequestProperty("Content-Length")
					);
		} catch (IOException e) {
			e.printStackTrace();
			if ((((HttpURLConnection)c).getResponseCode()) != HttpsURLConnection.HTTP_BAD_REQUEST) {
				throw e;
			}
		} finally{
			c.disconnect();
		}
	}

	public void test_ja_zh() throws Exception{
		HttpsURLConnection c = (HttpsURLConnection)new URL(url).openConnection();
		try{
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.addRequestProperty("X-HTTP-Method-Override", "GET");

			PostMethod m = new PostMethod();
			m.addParameter("q", text);
			m.addParameter("key", key);
			m.addParameter("source", "ja");
			m.addParameter("target", "zh");
			m.setRequestHeader(
					"Content-type"
					, "application/x-www-form-urlencoded; charset=UTF-8"
					);
			m.getRequestEntity().writeRequest(c.getOutputStream());
			System.out.println(StreamUtil.readAsString(
					c.getInputStream(), "UTF8"
					));
			System.out.println("Content-Length: "
					+ c.getRequestProperty("Content-Length")
					);
		} finally{
			c.disconnect();
		}
	}

	public void testByCommonsHttp_POST2() throws Exception{
		HttpsURLConnection c = (HttpsURLConnection)new URL(url).openConnection();
		try{
			c.setDoOutput(true);
			c.setRequestMethod("POST");
			c.addRequestProperty("X-HTTP-Method-Override", "GET");

			String text = StreamUtil.readAsString(
					getClass().getResourceAsStream("test_ja.txt"), "UTF-8"
					);
			PostMethod m = new PostMethod();
			m.addParameter("q", text);
			m.addParameter("key", key);
			m.addParameter("source", "ja");
			m.addParameter("target", "en");
			m.setRequestHeader(
					"Content-type"
					, "application/x-www-form-urlencoded; charset=UTF-8"
					);
			m.getRequestEntity().writeRequest(c.getOutputStream());


			InputStream is;
			if (c.getResponseCode() != HttpsURLConnection.HTTP_BAD_REQUEST &&
					c.getResponseCode() != HttpsURLConnection.HTTP_FORBIDDEN) {
				is = c.getInputStream();
			} else {
				is = c.getErrorStream();
			}

			System.out.println(StreamUtil.readAsString(
					is, "ISO-8859-1"
					));
			System.out.println("textlen: " + text.length());
			System.out.println("Content-Length: "
					+ c.getRequestProperty("Content-Length")
					);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally{
			c.disconnect();
		}
	}

	public void test_match() throws Exception{
		Pattern p = Pattern.compile(" +'([A-Z]+)' : '([a-zA-Z-]+)',");
		List<Pair<String, String>> langs = new ArrayList<Pair<String,String>>();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream("languagesEnum.txt")
				));
		String line = null;
		while((line = r.readLine()) != null){
			Matcher m = p.matcher(line);
			if(m.matches() && m.groupCount() == 2){
				langs.add(Pair.create(m.group(1), m.group(2)));
			}
		}

		for(Pair<String,String> l : langs){
			Language lang = null;
			try{
				lang = Language.parse(l.getSecond());
			} catch(InvalidLanguageTagException e){
				e.printStackTrace();
			}
			String lText = l.getFirst();
			String rText = (lang != null ? lang.getLocalizedName(Locale.ENGLISH) : "");
			if(lText.equalsIgnoreCase(rText)) continue;
			System.out.println(l.getSecond() + "(" + lText
					+ "): " + lang + "(" + rText + ")");
		}
	}

	private static String e(String s) throws Exception{
		return URLEncoder.encode(s, "UTF-8");
	}

	public static String getKey() {
		return key;
	}

	private static String key = "apikey";
	private static final String url =
		"https://www.googleapis.com/language/translate/v2?";
	private static String text =
		"言語グリッド（Language Grid）は，世界の言語資源（辞書，対訳，機械翻訳等）を登録し共有することができるインターネット上の多言語サービス基盤です．"
//		+"これまでの単体の機械翻訳システムと違い，利用者が開発した辞書や対訳を登録して連携させることができるため，利用の現場に応じた，より精度の高い翻訳結果が得られるという特徴を持っています．"
//		+"この多文化共生社会や国際交流を支える多言語サービス基盤ソフトウェアは，独立行政法人情報通信研究機構（NICT）言語グリッドプロジェクト"
//		+"この多文化共生社会や国際交流を支える多言語サービス基盤ソフトウェアは，独立行政法人情報通信研究機構（NICT）言語グリッドプロジェクトによって研究開発され，オープンソースとして公開しています．詳しくは次のサイトをご覧ください．"
		;

/*
原文:
言語グリッド（Language Grid）は，世界の言語資源（辞書，対訳，機械翻訳等）を登録し共有することができるインターネット上の多言語サービス基盤です．これまでの単体の機械翻訳システムと違い，利用者が開発した辞書や対訳を登録して連携させることができるため，利用の現場に応じた，より精度の高い翻訳結果が得られるという特徴を持っています．

Google翻訳API(2.625sec, 2回目0.281):
Grid Language (Language Grid) is the world&#39;s language resources (dictionaries, bilingual, and machine translation) is based on the multilingual Internet services to registered shares. Unlike machine translation system to the standalone, you can register to be linked to the development of bilingual dictionaries and the user, according to the site of use, with features that give a more accurate translation results and.
リクエスト全体: 2227bytes
クエリの翻訳文部分: 1979bytes


JServer(ホスティングサイト上のものを利用, 1.359sec):
A linguistic grid (Language Grid) registers the linguistic wealth of the world (dictionary, interlinear translation and machine translation, etc.), and is a multi-lingual service foundation on the internet which can be shared. I have the feature from which a more highly precise translation result according to the site of use is obtained because it's different from a machine translation system of the former element, and it's possible to register the dictionary and the interlinear translation the user has developed and make them cooperate.

 */
}
