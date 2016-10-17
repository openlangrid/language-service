package jp.go.nict.langrid.wrapper.googlelanguageidentification;

import java.util.Arrays;
import java.util.List;

import jp.go.nict.langrid.wrapper.googlelanguageidentification.GoogleLanguageIdentification;
import jp.go.nict.langrid.wrapper.ws_1_2.languageidentification.AbstractLanguageIdentificationService;
import junit.framework.TestCase;

public class SupportedLanguagesAndEncodingsTest extends TestCase {

	public void setUp(){
		try {
			service = new GoogleLanguageIdentification();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void test_getSupportedLanguages() throws Exception {
		List<String> list = Arrays.asList(service.getSupportedLanguages());
		for (String str : list) {
			System.out.println(str);
		}
		assertFalse(list.size() <= 0);
	}

	public void test_getSupportedEncodeings() throws Exception {
		List<String> list = Arrays.asList(service.getSupportedEncodings());
		for (String str : list) {
			System.out.println(str);
		}
		assertFalse(list.size() <= 0);
	}

	AbstractLanguageIdentificationService service;
}
