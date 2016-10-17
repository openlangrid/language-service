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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.hunpos;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.Assert;

import org.junit.Test;

public class HunposTest {
	
	@Test
	public void testEnglish1() throws Exception {
		String sentence = "This is test sentence.";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] morphemes = hunpos.analyze("en", sentence);
		for (Morpheme morpheme : morphemes) {
			System.out.println(morpheme);
		}
	}
	
	@Test
	public void testEnglish2() throws Exception {
		String sentence = "This is test sentence. It is rainy day.";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] morphemes = hunpos.analyze("en", sentence);
		for (Morpheme morpheme : morphemes) {
			System.out.println(morpheme);
		}
	}
	
	@Test
	public void testEnglish3() throws Exception {
		String sentence = "I have lived in U.S.A when I was so young.";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] morphemes = hunpos.analyze("en", sentence);
		for (Morpheme morpheme : morphemes) {
			System.out.println(morpheme);
		}
	}
	
	@Test
	public void test2() throws Exception {
		String text = "*";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test3() throws Exception {
		String text = "\\";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test4() throws Exception {
		String text = "!";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test5() throws Exception {
		String text = "&";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test6() throws Exception {
		String text = "%";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test7() throws Exception {
		String text = "$";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test8() throws Exception {
		String text = "#";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test9() throws Exception {
		String text = "\"";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test10() throws Exception {
		String text = "'";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test11() throws Exception {
		String text = "|";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test12() throws Exception {
		String text = "(";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test13() throws Exception {
		String text = ")";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test14() throws Exception {
		String text = "@";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test15() throws Exception {
		String text = "?";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test16() throws Exception {
		String text = "+";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test17() throws Exception {
		String text = "-";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test18() throws Exception {
		String text = "<";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test19() throws Exception {
		String text = ">";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test20() throws Exception {
		String text = ";";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test21() throws Exception {
		String text = ":";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test23() throws Exception {
		String text = ".";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test24() throws Exception {
		String text = ",";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test25() throws Exception {
		String text = "_";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test26() throws Exception {
		String text = "[";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test27() throws Exception {
		String text = "]";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test28() throws Exception {
		String text = "~";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test29() throws Exception {
		String text = "^";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test30() throws Exception {
		String text = "　";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test31() throws Exception {
		String text = "{";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test32() throws Exception {
		String text = "}";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getWord(), text);
		}
	}
	
	@Test
	public void test33() throws Exception {
		String text = "a   a";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
		}
	}
	
	@Test
	public void test34() throws Exception {
		String text = "///";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] ret = hunpos.analyze("en", text);
		for (Morpheme m : ret) {
			System.out.println(m);
		}
	}
	
	@Test
	public void testHungarian() throws Exception {
		String sentence = "Éltem U.S.A koromban olyan fiatal.";
		Hunpos hunpos = new Hunpos();
		hunpos.setPath("/usr/local/hunpos-1.0-linux");
		Morpheme[] morphemes = hunpos.analyze("hu", sentence);
		for (Morpheme morpheme : morphemes) {
			System.out.println(morpheme);
		}
	}

}
