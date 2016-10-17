package jp.go.nict.langrid.wrapper.google;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;
import junit.framework.TestCase;

public class GoogleTranslationExperiment
extends TestCase
{
	public void test() throws Exception{
		URL reqUrl = new URL(url +
				"&key=" + e(key) + "&q=" + e(text) + "&source=" + e("ja") + "&target="  + e("en"));
		System.out.println("url: " + reqUrl);
		System.out.println("url length: " + reqUrl.toString().length());
		String json = StreamUtil.readAsString(reqUrl.openStream(), "UTF-8");
		Map<String, Object> o = JSON.decode(json);
		
		System.out.println(
				"result:"
				+ ((List<Map<String, Object>>)((Map<String, Object>)o.get("data")).get("translations")).get(0).get("translatedText")
				);
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

	private static final String key = "apikey";
	private static final String url =
		"https://www.googleapis.com/language/translate/v2?";
	private static String text =
		"言語グリッド（Language Grid）は，世界の言語資源（辞書，対訳，機械翻訳等）を登録し共有することができるインターネット上の多言語サービス基盤です．"
//		+"これまでの単体の機械翻訳システムと違い，利用者が開発した辞書や対訳を登録して連携させることができるため，利用の現場に応じた，より精度の高い翻訳結果が得られるという特徴を持っています．"
//		+"この多文化共生社会や国際交流を支える多言語サービス基盤ソフトウェアは，独立行政法人情報通信研究機構（NICT）言語グリッドプロジェクト"
		+"この多文化共生社会や国際交流を支える多言語サービス基盤ソフトウェアは，独立行政法人情報通信研究機構（NICT）言語グリッドプロジェクトによって研究開発され，オープンソースとして公開しています．詳しくは次のサイトをご覧ください．"
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
