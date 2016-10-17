/*
 * This is a program to wrap language resources as Web services.
 * 
 * Copyright (C) 2012 Department of Social Informatics, Kyoto University.
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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.japanesesummarizer;

import junit.framework.Assert;

import org.junit.Test;

public class JapaneseSummarizerTest {
	
	@Test
	public void testNormal1() throws Exception {
		String sentence = "Shibuya.pm Tech Talks #7で話して来た\n" +
				"Shibuya.pm Tech Talks #7でお話しして来ました。\n" +
				"皆様おつかれさまです。\n" +
				"自分は自然文書から住所抽出してくれるGeography::AddressExtract::Japanというモジュールを作った時の話をして来ました。\n" +
				"他の方々は成果物の発表系だったのに対し、自分はモジュール作成の過程の話をしました。ここでもピントずらすの全開です。\n" +
				"住所抽出をするにあたり、とっかかりをどうしたか、実装方法をどうするか、具体的になにしたか、といった流れです。\n" +
				"開場で100名オーバ、インターネット中継で200接続程合ったらしくYAPC::Asia 2006 Tokyoと規模変わらんと思った。\n" +
				"なんかしらんけど画面が切れてるのを直してるので時間を潰したのが痛かった。\n" +
				"後半がほぼカットです。\n" +
				"話し終わって椅子に座ろうとした所で Dan the Encode.pm Maker さんから、自分の作ったRegexp::*つかってみなよ？と提案をうけました。Danさんのはメタ文字があると有利だという事です。\n" +
				"発表資料(ディレクターズカット版)\n" +
				"Geography::AddressExtract::Japanのtrac\n" +
				"svnは\n" +
				"svn co http://svn.yappo.jp/repos/public/Geography-AddressExtract-Japan/trunk/\n" +
				"クオリティを高めて実用的なのにしていきたいと思っているのでアイディアとかパッチとか大歓迎です。\n" +
				"ねこ大好き。\n";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", sentence);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + sentence);
		String[] lines = ret.split(System.getProperty("line.separator"));
		for (String line : lines) {
			Assert.assertNotSame(sentence.indexOf(line), -1);
		}
	}
	
	@Test
	public void test2() throws Exception {
		String text = "*";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test3() throws Exception {
		String text = "\\";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test4() throws Exception {
		String text = "!";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test5() throws Exception {
		String text = "&";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test6() throws Exception {
		String text = "%";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test7() throws Exception {
		String text = "$";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test8() throws Exception {
		String text = "#";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test9() throws Exception {
		String text = "\"";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test10() throws Exception {
		String text = "'";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test11() throws Exception {
		String text = "|";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test12() throws Exception {
		String text = "(";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test13() throws Exception {
		String text = ")";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test14() throws Exception {
		String text = "@";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test15() throws Exception {
		String text = "?";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test16() throws Exception {
		String text = "+";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test17() throws Exception {
		String text = "-";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test18() throws Exception {
		String text = "<";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test19() throws Exception {
		String text = ">";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test20() throws Exception {
		String text = ";";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test21() throws Exception {
		String text = ":";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test23() throws Exception {
		String text = ".";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test24() throws Exception {
		String text = ",";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test25() throws Exception {
		String text = "_";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test26() throws Exception {
		String text = "[";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test27() throws Exception {
		String text = "]";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test28() throws Exception {
		String text = "~";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test29() throws Exception {
		String text = "^";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test30() throws Exception {
		String text = "　";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		// 全角スペースは半角スペースに変換される
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), " ");
	}
	
	@Test
	public void test31() throws Exception {
		String text = "{";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test32() throws Exception {
		String text = "}";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test33() throws Exception {
		String text = "a   a";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		// 連続したスペースは単一の半角スペースに変換される
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), "a a");
	}
	
	@Test
	public void test34() throws Exception {
		String text = "///";
		JapaneseSummarizer summarizer = new JapaneseSummarizer();
		summarizer.setPath("/usr/local/Lingua-JA-Summarize-Extract-0.02/");
		String ret = summarizer.summarize("ja", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
}
