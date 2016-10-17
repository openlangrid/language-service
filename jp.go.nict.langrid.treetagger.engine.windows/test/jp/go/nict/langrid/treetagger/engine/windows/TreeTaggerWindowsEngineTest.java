/*
 * $Id: TreeTaggerWindowsEngineTest.java 26971 2009-02-17 10:45:27Z Takao Nakaguchi $
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
package jp.go.nict.langrid.treetagger.engine.windows;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;

import java.io.File;

import jp.go.nict.langrid.treetagger.engine.Tag;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;
import junit.framework.TestCase;

/**
 * 
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26971 $
 */
public class TreeTaggerWindowsEngineTest extends TestCase {

	protected void setUp() throws Exception {
		treeTagger = new TreeTaggerWindowsEngine(
				new File("work"), en);
	}

	protected void tearDown() throws Exception {
	}

	public void testTag00() throws Exception
	{
		Tag[] result = treeTagger.tag("0"); 
		assertEquals("00", result[0].word);
		assertEquals("@card@", result[0].lemma);
	}

	public void testTag_atcardat() throws Exception
	{
		Tag[] result = treeTagger.tag("@card@"); 
		assertEquals("00", result[0].word);
		assertEquals("@card@", result[0].lemma);
	}

	public void testTag00_french() throws Exception
	{
		treeTagger = new TreeTaggerWindowsEngine(
				new File("work"), fr);
		Tag[] result = treeTagger.tag("00"); 
		assertEquals("00", result[0].word);
		assertEquals("@card@", result[0].lemma);
	}

	public void testTag00_de() throws Exception
	{
		treeTagger = new TreeTaggerWindowsEngine(
				new File("work"), de);
		Tag[] result = treeTagger.tag("0"); 
		assertEquals("00", result[0].word);
		assertEquals("@card@", result[0].lemma);
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「文字列」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testTag_Normal1() throws Exception
	{
		Tag[] result = treeTagger.tag("I have a pen."); 
		assertEquals("I", result[0].word);
		assertEquals("have", result[1].word);
		assertEquals("a", result[2].word);
		assertEquals("pen", result[3].word);
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「原型」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testTag_Normal2() throws Exception{
		Tag[] result = treeTagger.tag("I have a pen."); 
		assertEquals("I", result[0].lemma);
		assertEquals("have", result[1].lemma);
		assertEquals("a", result[2].lemma);
		assertEquals("pen", result[3].lemma);
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testTag_Normal3() throws Exception{
		Tag[] result = treeTagger.tag("I have a pen."); 
		assertEquals("PP", result[0].tag);
		assertEquals("VHP", result[1].tag);
		assertEquals("DT", result[2].tag);
		assertEquals("NN", result[3].tag);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testTag_Normal4() throws Exception{
		TreeTaggerEngine deTagger = new TreeTaggerWindowsEngine(
				new File("work"), de);
		assertEquals("NE", deTagger.tag("Michael")[0].tag);
		TreeTaggerEngine esTagger = new TreeTaggerWindowsEngine(
				new File("work"), es);
		assertEquals("PREP", esTagger.tag("Michael")[0].tag);
	}

	private TreeTaggerEngine treeTagger;
}
