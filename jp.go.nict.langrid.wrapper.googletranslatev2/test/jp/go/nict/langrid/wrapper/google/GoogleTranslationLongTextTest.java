package jp.go.nict.langrid.wrapper.google;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.service_1_2.translation.TranslationService;
import junit.framework.TestCase;

public class GoogleTranslationLongTextTest extends TestCase {
	protected void setUp() throws Exception{
		System.setProperty("langrid.maxSourceLength", "5000");
		System.setProperty("langrid.translation.sentenceDivision", "NONE");
		service = new GoogleTranslationV2() {
			@Override
			protected String getIDCode(
					ServiceContext serviceContext, String parameterName, String defaultValue)
			{
				return "apikey";
			}
		};
	}

	public void test_ja_en() throws Exception {
		String sourceJa = StreamUtil.readAsString(
				getClass().getResourceAsStream("longtext.txt")
				, "UTF-8"
				);
		String r = service.translate("ja", "en", sourceJa);
		String[] sources = sourceJa.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)");
		String[] results = r.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)");
		System.out.println("source(" + sources.length + ") : result(" + results.length + ")");
		int n = Math.min(sources.length, results.length);
		for(int i = 0; i < n; i++){
			System.out.println((i + 1) + ": [\"" + sources[i] + "\":\"" + results[i] + "\"]");
		}
	}

	public void test_ja_en_intermediate() throws Exception {
		String sourceJa = StreamUtil.readAsString(
				getClass().getResourceAsStream("longtext_ja_intermediate.txt")
				, "UTF-8"
				);
		String r = service.translate("ja", "en", sourceJa);
		String[] sources = sourceJa.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)([\\.。.] )?");
		String[] results = r.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)([\\.。.] )?");
		System.out.println("source(" + sources.length + ") : result(" + results.length + ")");
		int n = Math.min(sources.length, results.length);
		for(int i = 0; i < n; i++){
			System.out.println((i + 1) + ": [\"" + sources[i] + "\":\"" + results[i] + "\"]");
		}
	}

	public void test_en_ja() throws Exception {
		String sourceEn = StreamUtil.readAsString(
				getClass().getResourceAsStream("longtext_en.txt")
				, "UTF-8"
				);
		String r = service.translate("en", "ja", sourceEn);
		String[] sources = sourceEn.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)([\\.。.] )?");
		String[] results = r.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)([\\.。.] )?");
		System.out.println("source(" + sources.length + ") : result(" + results.length + ")");
		int n = Math.min(sources.length, results.length);
		for(int i = 0; i < n; i++){
			System.out.println((i + 1) + ": [\"" + sources[i] + "\":\"" + results[i] + "\"]");
		}
	}

	public void test_en_ja_withic() throws Exception {
		String sourceEn = StreamUtil.readAsString(
				getClass().getResourceAsStream("longtext_en_withInternalCode.txt")
				, "UTF-8"
				);
		String r = service.translate("en", "ja", sourceEn);
		String[] sources = sourceEn.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)([\\.。.] )?");
		String[] results = r.split("(\\*\\$\\%\\*|\\*\\%\\$\\*)([\\.。.] )?");
		System.out.println("source(" + sources.length + ") : result(" + results.length + ")");
		int n = Math.min(sources.length, results.length);
		for(int i = 0; i < n; i++){
			System.out.println((i + 1) + ": [\"" + sources[i] + "\":\"" + results[i] + "\"]");
		}
	}

	private TranslationService service;

	private String sourceJa;
}
