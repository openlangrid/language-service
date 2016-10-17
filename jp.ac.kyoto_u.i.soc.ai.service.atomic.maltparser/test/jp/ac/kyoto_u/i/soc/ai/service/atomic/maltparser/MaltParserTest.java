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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.maltparser;

import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;

import org.junit.Test;

public class MaltParserTest {
	@Test
	public void test() throws Exception{
		MaltParser p = new MaltParser();
		Chunk[] ret = p.parseDependency("en", new Morpheme[]{
				new Morpheme("Mr.", "Mr.", "noun.proper"),
				new Morpheme("Vinken", "Vinken", "noun.proper"),
				new Morpheme("is", "is", "verb"),
				new Morpheme("chairman", "chairman", "noun.common"),
				new Morpheme("of", "of", "other"),
				new Morpheme("Elsevier", "Elsevier", "noun.proper"),
				new Morpheme("N.", "N.", "noun.proper"),
				new Morpheme("V.", "V.", "noun.proper"),
				new Morpheme(",", ",", "other"),
				new Morpheme("the", "the", "other"),
				new Morpheme("Dutch", "Dutch", "adjective"),
				new Morpheme("publishing", "publishing", "noun.common"),
				new Morpheme("group", "group", "noun.common"),
				new Morpheme(".", ".", "other"),
		});
		for(Chunk c : ret){
			System.out.println(c);
		}
	}
}
