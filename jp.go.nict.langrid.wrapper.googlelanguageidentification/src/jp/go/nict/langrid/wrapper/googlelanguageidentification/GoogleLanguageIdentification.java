package jp.go.nict.langrid.wrapper.googlelanguageidentification;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.af;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ar;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.be;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.bg;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ca;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.cs;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.cy;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.da;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.el;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.et;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fa;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ga;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.gl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.he;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.hi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.hr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ht;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.hu;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.id;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.is;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.it;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.lt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.lv;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.mk;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ms;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.mt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.nl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.no;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ro;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ru;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sk;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sq;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sv;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sw;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.th;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.tl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.tr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.uk;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.yi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.zh;
import static jp.go.nict.langrid.language.LangridLanguageTags.zh_CN;
import static jp.go.nict.langrid.language.LangridLanguageTags.zh_TW;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.net.URLUtil;
import jp.go.nict.langrid.commons.nio.charset.CharsetUtil;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.languageidentification.LanguageAndEncoding;
import jp.go.nict.langrid.wrapper.ws_1_2.languageidentification.AbstractLanguageIdentificationService;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.methods.PostMethod;

public class GoogleLanguageIdentification
extends AbstractLanguageIdentificationService{

	public GoogleLanguageIdentification() {
		setSupportedLanguageCollection(Arrays.asList(af, sq, ar, be, bg, ca, zh, zh_CN, zh_TW, hr, cs, da, nl, en, et, tl, fi, fr, gl, de, el, ht, he, hi, hu, is, id, ga, it, ja, lv, lt, mk, ms, mt, no, fa, pl, pt, ro, ru, sr, sk, sl, es, sw, sv, th, tr, uk, vi, cy, yi));
		setSupportedEncodings(new String[]{"UTF-8"});
		defaultApiKey = getInitParameterString("apikey", "");
		referer = getInitParameterString("referer", "http://langrid.nict.go.jp/");
	}

	public void setDefaultApiKey(String defaultApiKey) {
		this.defaultApiKey = defaultApiKey;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	protected String doIdentify(String text, String originalEncoding)
			throws InvalidParameterException, ProcessFailedException {
		try {
			if (! Charset.isSupported(originalEncoding)) {
				throw new InvalidParameterException("charset", "unsuppported" + originalEncoding);
			}
			String lang = detectLanguage(text);
			lang = convertTo(lang);
			lang = new Language(lang).toString();
			return lang;
		} catch (InvalidLanguageTagException e) {
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected LanguageAndEncoding doIdentifyLanguageAndEncoding(byte[] arg0)
			throws InvalidParameterException, ProcessFailedException {
		throw new UnsupportedOperationException("unsupport");
//		String source = null;
//		source = new String(arg0, "");
//		String result = detectLanguage(source);
//		LanguageAndEncoding le = new LanguageAndEncoding(result, "UTF-8");
//		return le;
	}

	private String detectLanguage(String source) throws ProcessFailedException {
		HttpsURLConnection con = null;

		PostMethod m = setupPost(new String(source), getApiKey());
		InputStream is = null ;
		String json = null;
		try {
			con = setupConnection();
			m.getRequestEntity().writeRequest(con.getOutputStream());
			if (isRequestSuccess(con)) {
				is = con.getInputStream();
				json = StreamUtil.readAsString(is, CharsetUtil.newUTF8Decoder());
				return decodeJsonForSuccess(json);
			} else {
				is = con.getErrorStream();
				json = StreamUtil.readAsString(is, CharsetUtil.newUTF8Decoder());
				return decodeJsonForError(json);
			}
		} catch (JSONException je) {
			String message = "failed to read translated text: " + json;
			logger.log(Level.WARNING, message, je);
			throw new ProcessFailedException(message);
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(
					Level.SEVERE, "failed to execute service.", e
					);
			throw new ProcessFailedException(e);
		}
	}

	private String decodeJsonForSuccess(String json) throws JSONException {
		JSONObject root = JSONObject.fromObject(json);
		String sourceLang = root
								.getJSONObject("data")
								.getJSONArray("translations")
								.getJSONObject(0)
								.getString("detectedSourceLanguage");
		return sourceLang;
	}

	private String decodeJsonForError(String json) throws JSONException, ProcessFailedException {
		JSONObject root = JSONObject.fromObject(json);
		JSONObject error = root
						.getJSONObject("error");
		String code = error.getString("code");
		String message = error.getString("message");
		throw new ProcessFailedException(code + ": " + message);
	}

	private HttpsURLConnection setupConnection() throws MalformedURLException, IOException {
		HttpsURLConnection con = (HttpsURLConnection) new URL(TRANSLATE_URL).openConnection();
		con.setDoOutput(true);
		con.addRequestProperty("X-HTTP-Method-Override", "GET");
		con.setReadTimeout(timeoutMillis);
		if(referer != null){
			con.addRequestProperty("Referer", referer);
		}
		return con;
	}

	private PostMethod setupPost(String source, String apiKey) {
		PostMethod m = new PostMethod();
		m.addParameter("key", apiKey);
		m.addParameter("target", dummyTarget);
		m.addParameter("q", source);
		m.setRequestHeader(
				"Content-type"
				, "application/x-www-form-urlencoded; charset=UTF-8"
				);
		return m;
	}

	private String convertTo(String lang) {
		String to = languageCodeConversionMap.get(lang);
		if (to != null) {
			return to;
		} else {
			return lang;
		}
	}

	private String getApiKey(){
		String apiKey = URLUtil.getQueryParameters(getServiceContext().getRequestUrl())
				.get("apikey");
		if (apiKey == null) {
			apiKey = defaultApiKey;
		}
		return apiKey;
	}

	private boolean isRequestSuccess(HttpsURLConnection con) throws IOException {
		return errorCode.indexOf(con.getResponseCode()) < 0;
	}

	private String defaultApiKey = "";
	private String referer;
	private int timeoutMillis = 10000;
	private static final String TRANSLATE_URL = "https://www.googleapis.com/language/translate/v2?";
	private static final String dummyTarget = "en";
	private static final List<Integer> errorCode = Arrays.asList(
			HttpsURLConnection.HTTP_BAD_REQUEST, HttpsURLConnection.HTTP_FORBIDDEN);
	private static Logger logger = Logger.getLogger(GoogleLanguageIdentification.class.getName());
	private static Map<String, String> languageCodeConversionMap = new HashMap<String, String>();
	static {
		languageCodeConversionMap.put("iw", "he");
		languageCodeConversionMap.put("pt", "pt-PT");
	}
}
