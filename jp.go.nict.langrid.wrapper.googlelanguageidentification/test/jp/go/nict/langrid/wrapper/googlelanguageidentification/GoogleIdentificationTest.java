package jp.go.nict.langrid.wrapper.googlelanguageidentification;

import java.io.InputStream;

import jp.go.nict.langrid.service_1_2.languageidentification.LanguageAndEncoding;
import jp.go.nict.langrid.wrapper.ws_1_2.languageidentification.AbstractLanguageIdentificationService;
import junit.framework.TestCase;

public class GoogleIdentificationTest extends TestCase {

	public void setUp(){
		try {
			service = new GoogleLanguageIdentification();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test_ja() throws Exception {
		assertEquals("ja", service.identify("言語グリッドとは，インターネット上に分散した言語サービス*1(辞書や対訳集，機械翻訳等)を自由に連携させることで，エンドユーザが目的に合った新たな言語サービスを容易に構築することのできるプラットフォームである．", "UTF-8"));
	}

	public void test_ja_languageAndEncording() throws Exception {
		InputStream is = GoogleIdentificationTest.class.getResourceAsStream("test_ja.txt");
		byte[] bytes = new byte[100];
		is.read(bytes);
		LanguageAndEncoding le = null;
		try {
			le = service.identifyLanguageAndEncoding(bytes);
			assertEquals("ja", le.getLanguage());
			assertEquals("UTF-8", le.getEncoding());
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	public void test_zh() throws Exception {
		assertEquals("zh-CN", service.identify("你好", "UTF-8"));
	}

	public void test_zh_languageAndEncording() throws Exception {
		InputStream is = GoogleIdentificationTest.class.getResourceAsStream("test_zh.txt");
		byte[] bytes = new byte[10];
		is.read(bytes);
		try {
			LanguageAndEncoding le = service.identifyLanguageAndEncoding(bytes);
			assertEquals("zh-CN", le.getLanguage());
			assertEquals("UTF-8", le.getEncoding());
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	public void test_en () throws Exception {
		assertEquals("en", service.identify("Hello.", "UTF-8"));
	}

	public void test_en_languageAndEncording() throws Exception {
		InputStream is = GoogleIdentificationTest.class.getResourceAsStream("test_en.txt");
		byte[] bytes = new byte[10];
		is.read(bytes);
		try {
			LanguageAndEncoding le = service.identifyLanguageAndEncoding(bytes);
			assertEquals("en", le.getLanguage());
			assertEquals("UTF-8", le.getEncoding());
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	public void test_he() throws Exception {
		assertEquals("he", service.identify( "שלום", "UTF-8"));
	}

	AbstractLanguageIdentificationService service;

}
