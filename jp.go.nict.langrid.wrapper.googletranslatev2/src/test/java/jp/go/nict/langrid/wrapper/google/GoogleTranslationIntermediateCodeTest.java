/*
 * $Id: GoogleTranslationIntermediateCodeTest.java 28453 2011-05-24 08:51:11Z Takao Nakaguchi $
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

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguagePairException;
import jp.go.nict.langrid.service_1_2.translation.TranslationService;
import junit.framework.TestCase;

/**
 * Test class for GoogleTranslation.
 * @author Takao Nakaguchi
 * @author Masaaki Kamiya
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 28453 $
 */
public class GoogleTranslationIntermediateCodeTest
extends TestCase
{
	public void test_en_ja() throws Exception{
		assertEquals(
				"こんにちは"
				, service.translate("en", "ja", "*%$. xxxkljewrkjxxx is rule. *%$. ")
				);
	}

	public void test_en_ja2() throws Exception{
		assertEquals(
				"こんにちは"
				, service.translate("en", "ja", "xxxlpmhmomfmfigihigkdmhmpmchnlxxx")
				);
	}

	public void test_en_ja_twice() throws Exception{
		test_en_ja();
		test_en_ja();
	}

	public void test_en_ar() throws Exception{
		assertEquals(
				"مرحبا"
				, service.translate("en", "ar", "hello")
				);
	}

	public void test_ja_en() throws Exception{
		assertEquals(
				"Hi"
				, service.translate("ja", "en", "こんにちは")
				);
	}

	public void test_ja_tl() throws Exception{
		assertEquals(
				"Hi"
				, service.translate("ja", "tl", "こんにちは")
				);
	}

	public void test_ja_th() throws Exception{
		assertEquals(
				"สวัสดี"
				, service.translate("ja", "th", "こんにちは")
				);
	}

	public void test_ja_tg() throws Exception{
		try{
			service.translate("ja", "tg", "こんにちは");
			fail();
		} catch(UnsupportedLanguagePairException e){
		}
	}

	public void test_ja_tg_twice() throws Exception{
		test_ja_tg();
		test_ja_tg();
	}

	public void test_ja_zhCN() throws Exception{
		assertEquals(
				"你好"
				, service.translate("ja", "zh-CN", "こんにちは")
				);
	}

	public void test_specialChars() throws Exception{
		assertEquals(
				"<>(){}[]!@#$%^&*?/-_;:'\",. \\ | = +"
				, service.translate("en", "fr", "<>(){}[]!@#$%^&*?/-_;:'\",.\\ | =+")
		);
	}

	public void test_internalcode() throws Exception{
		assertEquals(
				"Today xxxjgdhjgfjjgbijgaiixxx the \"Create animated,\" I had a."
				, service.translate("ja", "en", "今日のxxxjgdhjgfjjgbijgaiixxxで「アニメを作ろう」をやりました。")
				);
	}

	public void test_supportedLanguageCache() throws Exception{
		final AtomicInteger count = new AtomicInteger(0);
		GoogleTranslationV2 trans = new GoogleTranslationV2(){
			@Override
			protected String doTranslation(Language sourceLang,
					Language targetLang, String source)
					throws InvalidParameterException, ProcessFailedException {
				count.incrementAndGet();
				return super.doTranslation(sourceLang, targetLang, source);
			}
			@Override
			protected String getIDCode(
					ServiceContext serviceContext, String parameterName, String defaultValue)
			{
				return "apikey";
			}
		};
		try{
			trans.translate("en", "tg", "hello");
			fail();
		} catch(UnsupportedLanguagePairException e){
		}
		try{
			trans.translate("en", "tg", "hello");
			fail();
		} catch(UnsupportedLanguagePairException e){
		}
		assertEquals(1, count.get());
	}

	public void test_variousPairs() throws Exception{
		// Test forward and backward translation in each pair
		int i = 1;
		long firstest = Long.MAX_VALUE;
		long latest = Long.MIN_VALUE;
		for(Pair<Language, String> ss : sourceLangAndSources){
			Language sl = ss.getFirst();
			String source = ss.getSecond();
			for(Language tl : supportedLangs){
				if(sl.equals(tl)) continue;
				System.out.println(i++ + ". " + sl + " -> " + tl + " -> " + sl);
				System.out.println("source: " + source + "(" + source.length() + "chars)");
				System.out.print("translate: ");
				long s = System.currentTimeMillis();
				String result = doTranslate(service, source, sl, tl);
				long d = System.currentTimeMillis() - s;
				assertNotNull(result);
				System.out.println("- " + result + " " + d + "msec.");
				firstest = Math.min(firstest, d);
				latest = Math.max(latest, d);

				System.out.print("back translate: ");
				s = System.currentTimeMillis();
				result = doTranslate(service, result, tl, sl);
				d = System.currentTimeMillis() - s;
				assertNotNull(result);
				System.out.println("- " + result + " " + d + "msec.");
				firstest = Math.min(firstest, d);
				latest = Math.max(latest, d);

				System.out.println();
				Thread.sleep(100);
			}
			i = 1;
		}
		System.out.println("done.  firstest: " + firstest + "msec."
				+ "  latest: " + latest + "msec.");
	}

	public void test_longMultiByteText() throws Exception{
/*
		try{
			doTranslate(service, longText + "究", "ja", "en");
			fail();
		}catch(Exception e){
			System.out.println("Invalid multi byte text size is " + (longText + "究").length());
		}
*/
		System.out.println(doTranslate(service, longText, ja, en));
		System.out.println("Valid multi byte text size is " + longText.length());
	}

	public void test_ja_en_multiline() throws Exception{
		System.out.println(service.translate("ja", "en"
				, "Googleは、\nFCC (米連邦通信委員会) にWhite Spaces Databaseの\n管理者になることを提案した。\n" +
						"JavaFXには、2010年冬季オリンピックWebサイトなど、注目度の高い早期採用事例も出てきている。"));
	}

	private String doTranslate(
			TranslationService service, String source
			, Language sourceLang, Language targetLang)
	throws Exception{
		return service.translate(
				sourceLang.getCode(), targetLang.getCode(), source);
	}

	private static GoogleTranslationV2 service = new GoogleTranslationV2() {
		@Override
		protected String getIDCode(
				ServiceContext serviceContext, String parameterName, String defaultValue)
		{
			return "apikey";
		}
	};

	private String longText =
		"言語グリッドは、世界の言語資源（辞書、対訳、機械翻訳等）を登録し共有することができるインターネット上の多言語サービス基盤です。"+
		"これまでの単体の機械翻訳システムと違い、利用者が開発した辞書や対訳を登録して連携させることができるため，利用の現場に応じた、より精度の高い翻訳結果が得られるという特徴を持っています。"
		+ "この多文化共生社会や国際交流を支える多言語サービス基盤ソフトウェアは、独立行政法人情報通信研究機構言語グリッドプロジェクト"
		+ "によって研"
		+ "究"
		+ "開発され、オープンソースとして公開しています。詳しくは次のサイトをご覧ください。"
		;
	private static List<Pair<Language, String>> sourceLangAndSources = new ArrayList<Pair<Language, String>>();
	private static List<Language> supportedLangs = new ArrayList<Language>();
	static{
		sourceLangAndSources.add(Pair.create(
				ja, "言語グリッドはインターネット上の多言語サービス基盤です。"
				));
		sourceLangAndSources.add(Pair.create(
				en, "The Language Grid is a multilingual service foundation on the Internet."));
		sourceLangAndSources.add(Pair.create(
				fr, "Une grille de la langue est une base du service multilingue dans l'Internet."));

		InputStream is = GoogleTranslationIntermediateCodeTest.class.getResourceAsStream("supportedLanguages.txt");
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String line = null;
//			int i = 1;
			while((line = r.readLine()) != null){
//				System.out.println(i++ + ": " + line);
				supportedLangs.add(Language.parse(line.split("\\*")[0].trim()));
			}
		} catch(IOException e){
			throw new RuntimeException(e);
		} catch (InvalidLanguageTagException e) {
			throw new RuntimeException(e);
		} finally{
			try{
				is.close();
			} catch(IOException e){
				throw new RuntimeException(e);
			}
		}
	}
}
