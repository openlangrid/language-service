/*
 * $Id: ICTCLAS.java 28009 2010-08-09 10:25:33Z Takao Nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.gensenja;

import jp.go.nict.langrid.service_1_2.keyphraseextract.Keyphrase;
import junit.framework.Assert;

import org.junit.Test;

public class GensenJaTest {
	
	@Test
	public void test1() throws Exception {
		String text = "この情報は重要です。\n" +
				"個人情報を含む機密情報となります。";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase m : ret) {
			System.out.println(m.getPhrase() + " : " + m.getScore());
		}
	}
	
	@Test
	public void test2() throws Exception {
		String text = "*";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test3() throws Exception {
		String text = "\\";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test4() throws Exception {
		String text = "!";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test5() throws Exception {
		String text = "&";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test6() throws Exception {
		String text = "%";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase m : ret) {
			System.out.println(m.getPhrase() + " : " + m.getScore());
			Assert.assertEquals(m.getPhrase(), text);
		}
	}
	
	@Test
	public void test7() throws Exception {
		String text = "$";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase m : ret) {
			System.out.println(m.getPhrase() + " : " + m.getScore());
			Assert.assertEquals(m.getPhrase(), text);
		}
	}
	
	@Test
	public void test8() throws Exception {
		String text = "#";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test9() throws Exception {
		String text = "\"";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test10() throws Exception {
		String text = "'";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test11() throws Exception {
		String text = "|";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test12() throws Exception {
		String text = "(";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test13() throws Exception {
		String text = ")";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test14() throws Exception {
		String text = "@";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test15() throws Exception {
		String text = "?";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test16() throws Exception {
		String text = "+";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test17() throws Exception {
		String text = "-";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test18() throws Exception {
		String text = "<";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test19() throws Exception {
		String text = ">";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test20() throws Exception {
		String text = ";";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test21() throws Exception {
		String text = ":";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test23() throws Exception {
		String text = ".";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test24() throws Exception {
		String text = ",";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test25() throws Exception {
		String text = "_";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test26() throws Exception {
		String text = "[";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test27() throws Exception {
		String text = "]";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test28() throws Exception {
		String text = "~";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test29() throws Exception {
		String text = "^";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test30() throws Exception {
		String text = "　";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		// 全角空白はトリムされる
		Assert.assertEquals(ret.length, 0);
	}
	
	@Test
	public void test31() throws Exception {
		String text = "{";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test32() throws Exception {
		String text = "}";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test33() throws Exception {
		String text = "京都　西陣";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase m : ret) {
			System.out.println(m.getPhrase() + " : " + m.getScore());
		}
	}
	
	@Test
	public void test34() throws Exception {
		String text = "///";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}
	
	@Test
	public void test35() throws Exception {
		String text = "　";
		GensenJa gensen = new GensenJa();
		gensen.setPath("/usr/local/TermExtract-4_08");
		gensen.setMaxSourceLength(3000);
		Keyphrase[] ret = gensen.extract("ja", text);
		for (Keyphrase key : ret) {
			System.out.println(key.getPhrase());
			Assert.assertEquals(key.getPhrase(), text);
		}
	}

}
