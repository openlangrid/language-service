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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.ots;

import junit.framework.Assert;

import org.junit.Test;

public class OtsTest {
	
	@Test
	public void testNormal1() throws Exception {
		String sentence = "Came to talk about when I made a module called Geography :: AddressExtract :: Japan give your address to extract from nature the document.\n" +
				"While other people were announced system of artifacts, his was the story of the process of creating a module. Again, it is fully opening, shifting focus.\n" +
				"Upon extraction flow to address is, how did the thin edge of the wedge, what to do with how to implement, such as, or what did you do specifically.\n" +
				"I thought no change 2006 Tokyo YAPC :: Asia and the scale seems appropriate connections as 200 over 100, with the Internet in the opening relay.\n" +
				"I killed time again so that it is off screen is not know I was hurt softening.\n" +
				"Second half is almost cut.\n" +
				"Mr. Dan the Encode.pm Maker, Regexp :: *'ve made Do not try to use their own where you try to sit in a chair over talking about? And suggestions were received. Dan San'no advantage is that they are and there is a meta character.\n" +
				"Presentation materials (DVD) (Director's Cut)\n" +
				"Trac of Geography :: AddressExtract :: Japan\n" +
				"svn is\n" +
				"svn co http://svn.yappo.jp/repos/public/Geography-AddressExtract-Japan/trunk/\n" +
				"Or ideas are welcome patch or because they want to continue to enhance the quality and practical.\n" +
				"I love cats.\n";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", sentence);
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
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test3() throws Exception {
		String text = "\\";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test4() throws Exception {
		String text = "!";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test5() throws Exception {
		String text = "&";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test6() throws Exception {
		String text = "%";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test7() throws Exception {
		String text = "$";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test8() throws Exception {
		String text = "#";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test9() throws Exception {
		String text = "\"";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test10() throws Exception {
		String text = "'";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test11() throws Exception {
		String text = "|";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test12() throws Exception {
		String text = "(";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test13() throws Exception {
		String text = ")";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test14() throws Exception {
		String text = "@";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test15() throws Exception {
		String text = "?";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test16() throws Exception {
		String text = "+";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test17() throws Exception {
		String text = "-";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test18() throws Exception {
		String text = "<";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test19() throws Exception {
		String text = ">";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test20() throws Exception {
		String text = ";";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test21() throws Exception {
		String text = ":";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test23() throws Exception {
		String text = ".";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test24() throws Exception {
		String text = ",";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test25() throws Exception {
		String text = "_";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test26() throws Exception {
		String text = "[";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test27() throws Exception {
		String text = "]";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test28() throws Exception {
		String text = "~";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test29() throws Exception {
		String text = "^";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test30() throws Exception {
		String text = "　";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		// 全角スペースは半角スペースに変換される
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), " ");
	}
	
	@Test
	public void test31() throws Exception {
		String text = "{";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test32() throws Exception {
		String text = "}";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test33() throws Exception {
		String text = "a   a";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}
	
	@Test
	public void test34() throws Exception {
		String text = "///";
		Ots summarizer = new Ots();
		summarizer.setPath("/usr/local/");
		String ret = summarizer.summarize("en", text);
		System.out.println("summarize : " + ret);
		System.out.println("text      : " + text);
		Assert.assertEquals(ret.replace(System.getProperty("line.separator"), ""), text);
	}

}
