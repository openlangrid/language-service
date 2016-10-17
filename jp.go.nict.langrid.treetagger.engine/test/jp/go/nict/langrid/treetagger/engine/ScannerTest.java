/*
 * $Id: ScannerTest.java 26403 2008-08-28 08:12:22Z nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.treetagger.engine;

import java.util.Scanner;
import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * 
 * @author $Author: nakaguchi $
 * @version $Revision: 26403 $
 */
public class ScannerTest extends TestCase {
	
	/**
	 * 
	 * @throws Exception
	 */
	public void test1() throws Exception{
		System.out.println("-- test1 --");
		Scanner s = new Scanner(text);
		s.useDelimiter(" |(,)|(\\.)");
		while(s.hasNext()){
			System.out.println(s.next());
			System.out.println(s.match().group());
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void test2() throws Exception{
		System.out.println("-- test2 --");
		for(String w : text.split(" ")){
			System.out.println(w);
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void test3() throws Exception{
		System.out.println("-- test3 --");
		StringTokenizer t = new StringTokenizer(text, " ,.", true);
		while(t.hasMoreTokens()){
			String w = t.nextToken().trim();
			if(w.length() > 0) System.out.println(w);
		}
	}
	
	private final String text = "Hello, I have a pen.";
}
