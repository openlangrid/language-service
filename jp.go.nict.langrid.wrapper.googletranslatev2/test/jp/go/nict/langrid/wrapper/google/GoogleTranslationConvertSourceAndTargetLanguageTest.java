package jp.go.nict.langrid.wrapper.google;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import junit.framework.TestCase;

public class GoogleTranslationConvertSourceAndTargetLanguageTest extends TestCase {

	public void setUp() throws Exception {
		service = new GoogleTranslationV2() {
			@Override
			protected String getIDCode(
					ServiceContext serviceContext, String parameterName, String defaultValue)
			{
				return "apikey";
			}
		};
	}

	public void test_he_en() throws Exception {
		assertEquals("Hello", service.translate("he", "en", "שלום"));
	}

	public void test_en_he() throws Exception {
		assertEquals("שלום", service.translate("en", "he", "Hello"));
	}

	public void test_iw_en() throws Exception {
		assertEquals("Hello", service.translate("iw", "en", "שלום"));
	}

	public void test_en_iw() throws Exception {
		assertEquals("שלום", service.translate("en", "iw", "Hello"));
	}

	public void test_pt_en() throws Exception {
		assertEquals("Hello", service.translate("pt", "en", "Olá"));
	}

	public void test_en_pt() throws Exception {
		assertEquals("Olá", service.translate("en", "pt", "Hello"));
	}

	public void test_ptPT_en() throws Exception {
		assertEquals("Hello", service.translate("pt-PT", "en", "Olá"));
	}

	public void test_en_ptPT() throws Exception {
		assertEquals("Olá", service.translate("en", "pt-PT", "Hello"));
	}

	private GoogleTranslationV2 service;
	private String source;
}
