/**
 * $Id: TreeTaggerTest.java 26911 2009-01-30 07:27:34Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.treetagger;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.TestCase;

public class TreeTaggerTest extends TestCase {

	protected void setUp() throws Exception {
		System.setProperty("en-engine", "server:50001");
		System.setProperty("de-engine", "server:50002");
		System.setProperty("fr-engine", "command");
		System.setProperty("treeTaggerHome", "/usr/local/treetagger");
		service = new TreeTagger();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_en() throws Exception{
		for(Morpheme m : service.analyze("en", "hello world")){
			System.out.println(m);
		}
	}

	public void test_de() throws Exception{
		for(Morpheme m : service.analyze("de", "Guten tag")){
			System.out.println(m);
		}
	}

	public void test_fr() throws Exception{
		for(Morpheme m : service.analyze("fr", "Juteim")){
			System.out.println(m);
		}
	}

	public void test_it() throws Exception{
		for(Morpheme m : service.analyze("it", "hello")){
			System.out.println(m);
		}
	}

	private TreeTagger service;
}
