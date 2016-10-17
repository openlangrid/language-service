/*
 * $Id: GoogleTranslationV2.java 28684 2011-11-22 05:19:49Z Takao Nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2009 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.google;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.net.URLUtil;
import jp.go.nict.langrid.commons.nio.charset.CharsetUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSONException;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguagePairNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguagePairException;
import jp.go.nict.langrid.service_1_2.util.validator.LanguagePairValidator;
import jp.go.nict.langrid.wrapper.ws_1_2.translation.AbstractTranslationService;

import org.apache.commons.lang.StringEscapeUtils;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
/**
 * Google AJAX Translation API Wrapper.
 * @author Takao Nakaguchi
 * @author Masaaki Kamiya
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 28684 $
 */
public class GoogleTranslationV2
extends AbstractTranslationService{
	/**
	 * Constructor.
	 */
	public GoogleTranslationV2(){
		timeoutMillis = getInitParameterInt(
				"googleTranslation.timeoutMillis", 10000
				);
		maxQueryCount = getInitParameterInt(
				"googleTranslation.maxQueryCount", 128
				);
		maxTotalQueryLength = getInitParameterInt(
				"googleTranslation.maxTotalQueryLength", 5120
				);
		defaultApiKey = getInitParameterString(defaultGetString, "");
	}

	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	public void setMaxQueryCount(int maxQueryCount) {
		this.maxQueryCount = maxQueryCount;
	}
	
	public void setMaxTotalQueryLength(int maxTotalQueryLength) {
		this.maxTotalQueryLength = maxTotalQueryLength;
	}

	public void setDefaultApiKey(String defaultApiKey) {
		this.defaultApiKey = defaultApiKey;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected LanguagePair validateLanguagePair(
			LanguagePairValidator languagePair)
	throws InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			UnsupportedLanguagePairException
	{
		LanguagePair pair = languagePair.notNull().trim().notEmpty()
			.getLanguagePair();
		String key = pair.toCodeString(":");
		try {
			boolean supported = (Boolean)langPairCache.getFromCache(
					key, 60 * 60 * 24);
			if(supported){
				return pair;
			} else{
				languagePair.getUniqueLanguagePair(Collections.EMPTY_LIST);
			}
		} catch (NeedsRefreshException e) {
			languagePairValidator.set(languagePair);
			langPairCache.cancelUpdate(key);
		}
		return pair;
	}

	@Override
	public String translate(String sourceLang, String targetLang, String source)
		throws AccessLimitExceededException, InvalidParameterException
		, LanguagePairNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException, UnsupportedLanguagePairException
	{
		sourceLang = getOrDefault(languageCodeConversionMap, sourceLang, sourceLang);
		targetLang = getOrDefault(languageCodeConversionMap, targetLang, targetLang);

		return super.translate(sourceLang, targetLang, source);
	}

	@Override
	protected String doTranslation(
			Language sourceLang, Language targetLang, String source)
	throws InvalidParameterException, ProcessFailedException{
		source = convertCodes(source);
		String result = invokeTranslation(sourceLang, targetLang, source);
		return restoreCodes(result);
	}

	@Override
	protected String[] doMultistatementTranslation(
			Language sourceLang, Language targetLang, String[] sources)
	throws InvalidParameterException, ProcessFailedException
	{
		for(int i=0; i < sources.length; i++) {
			sources[i] = convertCodes(sources[i]);
		}

		ArrayList<String[]> separatedSources =  separateSourcesConsideredBatchAPILimitation(sources);
		ArrayList <String> res = new ArrayList<String>();
		for (String[] srcs : separatedSources) {
			if(srcs.length == 1) {
				res.add(invokeTranslation(sourceLang, targetLang, srcs[0]));
			} else {
				res.addAll(invokeBatchTranslation(sourceLang, targetLang, srcs));
			}
		}

		String[] results = res.toArray(new String[]{});
		for(int i=0; i < results.length; i++) {
			results[i] = restoreCodes(results[i]);
		}
		return results;
	}

	static String convertCodes(String src){
		return src.replaceAll("xxx(\\w+)xxx", "XXX$1XXX");
	}

	static String restoreCodes(String src){
		Pattern p = Pattern.compile("(X\\u200b*){3}([\\w\\u200b]+)(\\u200b*X){3}");
		Matcher m = p.matcher(src);
		StringBuffer b = new StringBuffer();
		while(m.find()){
			m.appendReplacement(b, "xxx" + m.group(2).replaceAll("\u200b", "") + "xxx");
		}
		m.appendTail(b);
		return b.toString();
	}

	@SuppressWarnings("unchecked")
	private String invokeTranslation(
			Language sourceLang, Language targetLang, String source)
	throws InvalidParameterException, ProcessFailedException
	{
		InputStream is = null;
		try{
			String apiKey = getIDCode(getServiceContext(), "apikey", defaultApiKey);
			HttpsURLConnection con = (HttpsURLConnection) new URL(TRANSLATE_URL + "key=" + apiKey).openConnection();
			con.setDoOutput(true);
			con.addRequestProperty("X-HTTP-Method-Override", "GET");
			con.setReadTimeout(timeoutMillis);
			writeParameters(con, apiKey, sourceLang, targetLang, source);

			if (con.getResponseCode() != HttpsURLConnection.HTTP_BAD_REQUEST &&
					con.getResponseCode() != HttpsURLConnection.HTTP_FORBIDDEN) {
				is = con.getInputStream();
			} else {
				is = con.getErrorStream();
			}
			String json = StreamUtil.readAsString(
					is, CharsetUtil.newUTF8Decoder()
					);
			try{
				Map<String, Object> root = JSON.decode(json);
				Map<String, Object> responseData = (Map<String, Object>)root.get("data");
				String key = sourceLang.getCode() + ":" + targetLang.getCode();
				if(con.getResponseCode() == 200){
					langPairCache.putInCache(key, true);
					List<String> results = new ArrayList<String>();
					List<Map<String, Object>> list = (List<Map<String, Object>>)responseData.get("translations");
					for (Map<String, Object> obj : list) {
						results.add((String)obj.get("translatedText"));
					}
					String result = results.get(0);
					return StringEscapeUtils.unescapeHtml(result.replaceAll("&#39;", "'"));
				} else {
					String details = (String)(((Map<String, Object>)root.get("error")).get("message"));
					if (details.indexOf("Bad language pair") != -1) {
						langPairCache.putInCache(key, false);
						languagePairValidator.get().getUniqueLanguagePair(
								Collections.EMPTY_LIST)
								;
					}
					throw new ProcessFailedException(
							((Map<String, Object>)root.get("error")).get("code")
							+ ": " + details);
				}
			} catch(JSONException e){
				String message = "failed to read translated text: " + json;
				logger.log(Level.WARNING, message, e);
				throw new ProcessFailedException(message);
			}
		} catch(IOException e){
			logger.log(
					Level.SEVERE, "failed to execute service.", e
					);
			throw new ProcessFailedException(e);
		} finally{
			if(is != null){
				try{
					is.close();
				} catch(IOException e){
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> invokeBatchTranslation(
			Language sourceLang, Language targetLang, String[] sources)
	throws InvalidParameterException, ProcessFailedException
	{
		InputStream is = null;
		try{
			String apiKey = getIDCode(getServiceContext(), "key", defaultApiKey);
			HttpsURLConnection con = (HttpsURLConnection) new URL(TRANSLATE_URL).openConnection();
			con.setDoOutput(true);
			con.addRequestProperty("X-HTTP-Method-Override", "GET");
			con.setReadTimeout(timeoutMillis);
			writeParameters(con, apiKey, sourceLang, targetLang, sources);

			if (con.getResponseCode() != HttpsURLConnection.HTTP_BAD_REQUEST &&
					con.getResponseCode() != HttpsURLConnection.HTTP_FORBIDDEN) {
				is = con.getInputStream();
			} else {
				is = con.getErrorStream();
			}
			String json = StreamUtil.readAsString(
					is, CharsetUtil.newUTF8Decoder()
					);
			try{
				Map<String, Object> root = JSON.decode(json);
				List<Map<String, Object>> array = (List<Map<String, Object>>)
					(((Map<String, Object>)root.get("data")).get("translations"));
				int status = con.getResponseCode();
				String key = sourceLang.getCode() + ":" + targetLang.getCode();

				if (status == 200) {
					langPairCache.putInCache(key, true);
					ArrayList<String> resultArray = new ArrayList<String>();
					for (Map<String, Object> obj : array) {
						String result = (String)obj.get("translatedText");
						resultArray.add(StringEscapeUtils.unescapeHtml(result.replaceAll("&#39;", "'")));
					}
					return resultArray;
				} else{
					String details = (String)(((Map<String, Object>)root.get("error")).get("message"));
					if (details.indexOf("Bad language pair") != -1) {
						langPairCache.putInCache(key, false);
						languagePairValidator.get().getUniqueLanguagePair(
								Collections.EMPTY_LIST)
								;
					}
					throw new ProcessFailedException(
							(String)(((Map<String, Object>)root.get("error")).get("code"))
							+ ": " + details);
				}
			} catch(JSONException e){
				String message = "failed to read translated text: " + json;
				logger.log(Level.WARNING, message, e);
				throw new ProcessFailedException(message);
			} catch(NullPointerException e){
				String message = "failed to read translated text: " + json;
				logger.log(Level.WARNING, message, e);
				throw new ProcessFailedException(message);
			}
		} catch(IOException e){
			logger.log(
					Level.SEVERE, "failed to execute service.", e
					);
			throw new ProcessFailedException(e);
		} finally{
			if(is != null){
				try{
					is.close();
				} catch(IOException e){
				}
			}
		}
	}

	private ArrayList<String[]> separateSourcesConsideredBatchAPILimitation(String[] sources) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		ArrayList<String> sourceArray = new ArrayList<String>(Arrays.asList(sources));

		int totalLength = 0;
		int index = 0;
		int count = 0;
		for (String source: sourceArray) {
			totalLength += source.length();
			if(totalLength >= maxTotalQueryLength || (count - index) >= maxQueryCount) {
				result.add(sourceArray.subList(index, count).toArray(new String[]{}));
				index = count;
				totalLength = source.length();
			}
			count++;
		}

		if (index < sourceArray.size()) {
			result.add(sourceArray.subList(index, sourceArray.size()).toArray(new String[]{}));
		}

		return result;
	}

	private static <T,U> U getOrDefault(Map<T,U> map, T key, U defaultValue){
		U v = map.get(key);
		if(v != null) return v;
		else return defaultValue;
	}

	protected String getIDCode(
			ServiceContext serviceContext, String parameterName, String defaultValue)
	throws ProcessFailedException
	{
		String getParam = URLUtil.getQueryParameters(
				serviceContext.getRequestUrl()
				).get(parameterName);
		if (getParam == null) {
			getParam = defaultValue;
		}
		return getParam;
	}

	static void writeParameters(
			URLConnection c, String apiKey, Language sourceLang, Language targetLang, String... sources)
	throws IOException{
		StringBuilder b = new StringBuilder();
		b.append("key=").append(apiKey)
			.append("&source=").append(sourceLang.getCode())
			.append("&target=").append(targetLang.getCode());
		for(String s : sources){
			b.append("&q=").append(URLEncoder.encode(s, "UTF-8"));
		}
		c.addRequestProperty(
				"Content-Type"
				, "application/x-www-form-urlencoded; charset=UTF-8"
				);
		c.getOutputStream().write(b.toString().getBytes("UTF-8"));
	}

	private String defaultApiKey = "";
	private static String defaultGetString = "apikey";
	private int timeoutMillis;
	private int maxQueryCount;
	private int maxTotalQueryLength;
	private static final String TRANSLATE_URL =
		"https://www.googleapis.com/language/translate/v2?";
	private static Cache langPairCache = new Cache(
			true, false, false, true, null, 10000);
	private static Map<String, String> languageCodeConversionMap = new HashMap<String, String>();
	private static ThreadLocal<LanguagePairValidator> languagePairValidator
			= new ThreadLocal<LanguagePairValidator>();
	private static Logger logger = Logger.getLogger(GoogleTranslationV2.class.getName());

	static {
		languageCodeConversionMap.put("iw", "he");
		languageCodeConversionMap.put("pt", "pt-PT");
	}
}
