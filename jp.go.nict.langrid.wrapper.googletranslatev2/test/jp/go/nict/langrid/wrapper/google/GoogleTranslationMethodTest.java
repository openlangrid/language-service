package jp.go.nict.langrid.wrapper.google;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;

public class GoogleTranslationMethodTest extends TestCase {

	public void test_ja_en_doTranslate() throws Exception{
		String against =
				"Googleは、\nFCC (米連邦通信委員会) にWhite Spaces Databaseの\n管理者になることを提案した。\n";
		String result = service.doTranslation(
				new Language("ja"),
				new Language("en"),
				against);
		String expect =
				"Google has, FCC (Federal Communications Commission) to be proposed White Spaces Database administrator.";
		assertEquals(expect, result);
	}

	public void test_ja_zh_doTranslate() throws Exception{
		String against =
				"Googleは、\nFCC (米連邦通信委員会) にWhite Spaces Databaseの\n管理者になることを提案した。\n";
		String result = service.doTranslation(
				new Language("ja"),
				new Language("zh"),
				against);
		String expect =
				"谷歌有，FCC（美国联邦通信委员会）将提出的空白数据库管理员。";
		assertEquals(expect, result);
	}

	public void test_ja_en_multistate() throws Exception{
		String[] against = new String[] {
				"Googleは、\nFCC (米連邦通信委員会) にWhite Spaces Databaseの\n管理者になることを提案した。\n",
				"JavaFXには、2010年冬季オリンピックWebサイトなど、注目度の高い早期採用事例も出てきている。"
		};
		String[] result = service.doMultistatementTranslation(
				new Language("ja"),
				new Language("en"),
				against);
		String[] expect = new String[] {
				"Google has, FCC (Federal Communications Commission) to be proposed White Spaces Database administrator.",
				"JavaFX is the Winter Olympics and 2010 Web sites have also come out high-profile cases early adopters."
		};
		int i = 0;
		for (String str : expect) {
			System.out.println(result[i]);
			assertEquals(str, result[i++]);
		}
	}

	public void test_ja_zh_multistate() throws Exception{
		String[] against = new String[] {
				"Googleは、\nFCC (米連邦通信委員会) にWhite Spaces Databaseの\n管理者になることを提案した。\n",
				"JavaFXには、2010年冬季オリンピックWebサイトなど、注目度の高い早期採用事例も出てきている。"
		};
		String[] result = service.doMultistatementTranslation(
				new Language("ja"),
				new Language("zh"),
				against);
		String[] expect = new String[] {
				"谷歌有，FCC（美国联邦通信委员会）将提出的空白数据库管理员。",
				"JavaFX是冬季奥运会和2010年网站也站出来高调案件尝鲜。"
		};
		int i = 0;
		for (String str : expect) {
			System.out.println(result[i]);
			assertEquals(str, result[i++]);
		}
	}

	public void test_ja_en_multistate_oneStatement() throws Exception{
		String[] against = new String[] {
				"Googleは、\nFCC (米連邦通信委員会) にWhite Spaces Databaseの\n管理者になることを提案した。\n"
		};
		String[] result = service.doMultistatementTranslation(
				new Language("ja"),
				new Language("en"),
				against);
		String[] expect = new String[] {
				"Google has, FCC (Federal Communications Commission) to be proposed White Spaces Database administrator."
		};
		int i = 0;
		for (String str : expect) {
			System.out.println(result[i]);
			assertEquals(str, result[i++]);
		}
	}

	static GoogleTranslationV2 service = new GoogleTranslationV2() {
		@Override
		protected String getIDCode(
				ServiceContext serviceContext, String parameterName, String defaultValue)
		{
			return "apikey";
		}
	};
}

