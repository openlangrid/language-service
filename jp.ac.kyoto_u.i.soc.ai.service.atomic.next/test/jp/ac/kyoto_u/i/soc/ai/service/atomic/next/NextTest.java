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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.next;

import jp.go.nict.langrid.service_1_2.namedentitytagging.NamedEntity;
import junit.framework.Assert;

import org.junit.Test;

public class NextTest {
	
	@Test
	public void testNormal1() throws Exception {
		String sentence = "太郎は日本生まれです。日銀で5年働いています。500円貯金が趣味で40％ほどたまっています。";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] NamedEntitys = next.tag("ja", sentence);
		for (NamedEntity NamedEntity : NamedEntitys) {
			System.out.println(NamedEntity);
		}
	}
	
	@Test
	public void test2() throws Exception {
		String text = "*";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test3() throws Exception {
		String text = "\\";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test4() throws Exception {
		String text = "!";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test5() throws Exception {
		String text = "&";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test6() throws Exception {
		String text = "%";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test7() throws Exception {
		String text = "$";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test8() throws Exception {
		String text = "#";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test9() throws Exception {
		String text = "\"";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test10() throws Exception {
		String text = "'";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test11() throws Exception {
		String text = "|";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test12() throws Exception {
		String text = "(";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test13() throws Exception {
		String text = ")";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test14() throws Exception {
		String text = "@";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test15() throws Exception {
		String text = "?";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test16() throws Exception {
		String text = "+";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test17() throws Exception {
		String text = "-";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test18() throws Exception {
		String text = "<";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test19() throws Exception {
		String text = ">";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test20() throws Exception {
		String text = ";";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test21() throws Exception {
		String text = ":";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test23() throws Exception {
		String text = ".";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test24() throws Exception {
		String text = ",";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test25() throws Exception {
		String text = "_";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test26() throws Exception {
		String text = "[";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test27() throws Exception {
		String text = "]";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test28() throws Exception {
		String text = "~";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test29() throws Exception {
		String text = "^";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test30() throws Exception {
		String text = "　";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test31() throws Exception {
		String text = "{";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test32() throws Exception {
		String text = "}";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), text);
		}
	}
	
	@Test
	public void test33() throws Exception {
		String text = "a   a";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
		}
	}
	
	@Test
	public void test34() throws Exception {
		String text = "///";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), "/");
		}
	}
	
	@Test
	public void test35() throws Exception {
		String text = "(Henry)";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		String[] check = { "(", "Henry", ")" };
		int index = 0;
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), check[index++]);
		}
	}
	
	@Test
	public void test36() throws Exception {
		String text = "[Henry]";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		String[] check = { "[", "Henry", "]" };
		int index = 0;
		for (NamedEntity m : ret) {
			System.out.println(m);
			Assert.assertEquals(m.getEntity(), check[index++]);
		}
	}
	
	@Test
	public void test37() throws Exception {
		String text = "<Henry>";
		Next next = new Next();
		next.setPath("/usr/local/NExT");
		NamedEntity[] ret = next.tag("ja", text);
		for (NamedEntity m : ret) {
			System.out.println(m);
		}
	}

}
