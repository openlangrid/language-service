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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.stanfordparser;

import static org.junit.Assert.assertEquals;

import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;

import org.junit.Test;

public class StanfordParserTest {
	@Test
	public void test() throws Exception{
		StanfordParser p = new StanfordParser();
		p.setPath("C:\\resources\\stanford-parser-2012-01-06");
		Chunk[] chunks = p.parseDependency("en", "The quick brown fox jumped over the lazy/dog.");
//*
		assertEquals(8, chunks.length);
		assertEquals("0", chunks[0].getChunkId());
		assertEquals("3", chunks[0].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[0].getDependency().getLabel());
		assertEquals(1, chunks[0].getMorphemes().length);
		assertEquals("The", chunks[0].getMorphemes()[0].getWord());
		assertEquals("The", chunks[0].getMorphemes()[0].getLemma());
		assertEquals("unknown", chunks[0].getMorphemes()[0].getPartOfSpeech());
		assertEquals(8, chunks.length);
		assertEquals("0", chunks[0].getChunkId());
		assertEquals("3", chunks[0].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[0].getDependency().getLabel());
		assertEquals(1, chunks[0].getMorphemes().length);
		assertEquals("The", chunks[0].getMorphemes()[0].getWord());
		assertEquals("The", chunks[0].getMorphemes()[0].getLemma());
		assertEquals("unknown", chunks[0].getMorphemes()[0].getPartOfSpeech());
		assertEquals("1", chunks[1].getChunkId());
		assertEquals("3", chunks[1].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[1].getDependency().getLabel());
		assertEquals(1, chunks[1].getMorphemes().length);
		assertEquals("quick", chunks[1].getMorphemes()[0].getWord());
		assertEquals("quick", chunks[1].getMorphemes()[0].getLemma());
		assertEquals("adjective", chunks[1].getMorphemes()[0].getPartOfSpeech());
		assertEquals("2", chunks[2].getChunkId());
		assertEquals("3", chunks[2].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[2].getDependency().getLabel());
		assertEquals(1, chunks[2].getMorphemes().length);
		assertEquals("brown", chunks[2].getMorphemes()[0].getWord());
		assertEquals("brown", chunks[2].getMorphemes()[0].getLemma());
		assertEquals("adjective", chunks[2].getMorphemes()[0].getPartOfSpeech());
		assertEquals("3", chunks[3].getChunkId());
		assertEquals("4", chunks[3].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[3].getDependency().getLabel());
		assertEquals(1, chunks[3].getMorphemes().length);
		assertEquals("fox", chunks[3].getMorphemes()[0].getWord());
		assertEquals("fox", chunks[3].getMorphemes()[0].getLemma());
		assertEquals("noun.common", chunks[3].getMorphemes()[0].getPartOfSpeech());
		assertEquals("4", chunks[4].getChunkId());
		assertEquals("-1", chunks[4].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[4].getDependency().getLabel());
		assertEquals(1, chunks[4].getMorphemes().length);
		assertEquals("jumped", chunks[4].getMorphemes()[0].getWord());
		assertEquals("jumped", chunks[4].getMorphemes()[0].getLemma());
		assertEquals("verb", chunks[4].getMorphemes()[0].getPartOfSpeech());
		assertEquals("5", chunks[5].getChunkId());
		assertEquals("4", chunks[5].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[5].getDependency().getLabel());
		assertEquals(1, chunks[5].getMorphemes().length);
		assertEquals("over", chunks[5].getMorphemes()[0].getWord());
		assertEquals("over", chunks[5].getMorphemes()[0].getLemma());
		assertEquals("unknown", chunks[5].getMorphemes()[0].getPartOfSpeech());
		assertEquals("6", chunks[6].getChunkId());
		assertEquals("7", chunks[6].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[6].getDependency().getLabel());
		assertEquals(1, chunks[6].getMorphemes().length);
		assertEquals("the", chunks[6].getMorphemes()[0].getWord());
		assertEquals("the", chunks[6].getMorphemes()[0].getLemma());
		assertEquals("unknown", chunks[6].getMorphemes()[0].getPartOfSpeech());
		assertEquals("7", chunks[7].getChunkId());
		assertEquals("5", chunks[7].getDependency().getHeadChunkId());
		assertEquals("DEPENDENCY", chunks[7].getDependency().getLabel());
		assertEquals(1, chunks[7].getMorphemes().length);
		assertEquals("lazy/dog", chunks[7].getMorphemes()[0].getWord());
		assertEquals("lazy/dog", chunks[7].getMorphemes()[0].getLemma());
		assertEquals("noun.common", chunks[7].getMorphemes()[0].getPartOfSpeech());
/*/
		// generate tests
		System.out.println(String.format(
				"\t\tassertEquals(%d, chunks.length);"
				, chunks.length));
		for(int i = 0; i < chunks.length; i++){
			Chunk c = chunks[i];
			System.out.println(String.format(
					"\t\tassertEquals(\"%d\", chunks[%1$d].getChunkId());"
					, i));
			System.out.println(String.format(
					"\t\tassertEquals(\"%s\", chunks[%d].getDependency().getHeadChunkId());"
					, chunks[i].getDependency().getHeadChunkId()
					, i));
			System.out.println(String.format(
					"\t\tassertEquals(\"%s\", chunks[%d].getDependency().getLabel());"
					, chunks[i].getDependency().getLabel()
					, i));
			System.out.println(String.format(
					"\t\tassertEquals(%d, chunks[%d].getMorphemes().length);"
					, chunks[i].getMorphemes().length
					, i));
			System.out.println(String.format(
					"\t\tassertEquals(\"%s\", chunks[%d].getMorphemes()[0].getWord());"
					, chunks[i].getMorphemes()[0].getWord()
					, i));
			System.out.println(String.format(
					"\t\tassertEquals(\"%s\", chunks[%d].getMorphemes()[0].getLemma());"
					, chunks[i].getMorphemes()[0].getLemma()
					, i));
			System.out.println(String.format(
					"\t\tassertEquals(\"%s\", chunks[%d].getMorphemes()[0].getPartOfSpeech());"
					, chunks[i].getMorphemes()[0].getPartOfSpeech()
					, i));
		}
//*/
	}
}
