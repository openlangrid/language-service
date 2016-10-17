/*
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2010 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.bing;

import static jp.go.nict.langrid.language.IANALanguageTags.zh_Hans;
import static jp.go.nict.langrid.language.IANALanguageTags.zh_Hant;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ar;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.bg;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.cs;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.da;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.el;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.et;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.he;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ht;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.hu;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.id;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.it;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ko;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.lt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.lv;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.nl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.no;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ro;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ru;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sk;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sv;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.th;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.tr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.uk;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.zh;
import static jp.go.nict.langrid.language.LangridLanguageTags.pt_PT;
import static jp.go.nict.langrid.language.LangridLanguageTags.zh_CN;
import static jp.go.nict.langrid.language.LangridLanguageTags.zh_TW;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.ws_1_2.translation.AbstractTranslationService;

/**
 * Microsoft Bing Translation API Wrapper.
 * @author Jun Koyama
 * @author Takao Nakaguchi
 */
public class BingTranslation
extends AbstractTranslationService{
	/**
	 * Constructor.
	 */
	public BingTranslation(){
		setSupportedLanguagePairs(pairs);
	}

	public void setAppId(String appId){
		this.appId = appId;
	}
	
	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	protected String doTranslation(
			Language sourceLang, Language targetLang, String source)
	throws InvalidParameterException, ProcessFailedException 
	{
		String result = invokeTranslation(sourceLang, targetLang, source);
		return result;
	}

	private String invokeTranslation (
			Language sourceLang, Language targetLang, String source) 
		throws InvalidParameterException, ProcessFailedException {
		String sl = getOrDefault(languageCodeConversionMap, sourceLang.getCode(), sourceLang.getCode());
		String tl = getOrDefault(languageCodeConversionMap, targetLang.getCode(), targetLang.getCode());
		HttpURLConnection urlconnection = null;
		BufferedReader br = null;
		try {
			String param = "?appId=" + appId + "&from=" + sl + "&to=" + tl + "&text=";
			param += URLEncoder.encode(source, CHAR_SET);
			URL url = new URL(TRANSLATE_URL + param);
			urlconnection = (HttpURLConnection)url.openConnection();
			urlconnection.setReadTimeout(timeoutMillis);
			urlconnection.setRequestMethod("GET");
			urlconnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlconnection.addRequestProperty("Accept-Language", "ja,en-us;q=0.7,en;q=0.3");
			urlconnection.addRequestProperty("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
			urlconnection.setInstanceFollowRedirects(false);
			urlconnection.connect();
			if (urlconnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException("http error. this code is " + urlconnection.getResponseCode());
			}
			br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), CHAR_SET));
			String line = null;
			StringBuffer ret = new StringBuffer();
			while ((line = br.readLine()) != null) {
				ret.append(line);
			}
			String result = ret.toString();
			result = result.replaceAll(REGEX_XML, "");
			result = reSanitize(result);
			return result.trim();
		} catch (MalformedURLException e) {
			logger.log(
					Level.SEVERE, "failed to execute service.", e
					);
			throw new ProcessFailedException(e);
		} catch (IOException e) {
			logger.log(
					Level.SEVERE, "failed to execute service.", e
					);
			throw new ProcessFailedException(e);
		} catch (Exception e) {
			logger.log(
					Level.SEVERE, "failed to execute service.", e
					);
			throw new ProcessFailedException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					;
				}
			}
			if (urlconnection != null) {
				try {
					urlconnection.disconnect();
				} catch (Exception e) {
				}
			}
		}
	}

	private static <T,U> U getOrDefault(Map<T,U> map, T key, U defaultValue){
		U v = map.get(key);
		if(v != null) return v;
		else return defaultValue;
	}
	/**
	 * It returns resanitize string
	 * @param string message
	 * @return
	 */
	public static String reSanitize(String str) {
		String result = str;
		if (result != null && result.length() > 0) {
			for (int i = 0; i < SANITIZE_STRING.length; i++) {
				result = Pattern.compile(SANITIZE_STRING[i][1]).matcher(result).replaceAll(SANITIZE_STRING[i][0]);
			}
		}
		return result;
	}
	/**
	 * Sanitize string.
	 */
	private static final String SANITIZE_STRING[][] = {
		{ "&",  "&amp;" },
		{ "<",  "&lt;" },
		{ ">",  "&gt;" },
		{ "\"", "&quot;" },
		{ "'",  "&#39;" }
	};

	private String appId = "";
	private int timeoutMillis = 1000;
	private static final String CHAR_SET = "UTF-8";
	private static final String TRANSLATE_URL =
		"http://api.microsofttranslator.com/v2/Http.svc/Translate";
	private static String REGEX_XML = "(<([^ >]+)([^>]*)>)";
	private static Logger logger = Logger.getLogger(BingTranslation.class.getName());
	private static Collection<LanguagePair> pairs = new ArrayList<LanguagePair>();

	static {
		LanguagePairUtil.addBidirectionalRoundrobinformedPairs(
				pairs
				, new Language[]{ar, bg, cs, da, nl, en, et, fi
						, fr, de, el, ht, he, hu, id, it, ja, ko
						, lv, lt, no, pl, pt_PT, ro, ru, sk, sl, es
						, sv, th, tr, uk, vi
						, zh, zh_CN, zh_Hans, zh_TW, zh_Hant}
				);
	}
	private static Map<String, String> languageCodeConversionMap = new HashMap<String, String>();
	static {
		languageCodeConversionMap.put("zh", "zh-CHT");
		languageCodeConversionMap.put("zh-CN", "zh-CHS");
		languageCodeConversionMap.put("zh-TW", "zh-CHT");
		languageCodeConversionMap.put("pt-PT", "pt");
	}
}
