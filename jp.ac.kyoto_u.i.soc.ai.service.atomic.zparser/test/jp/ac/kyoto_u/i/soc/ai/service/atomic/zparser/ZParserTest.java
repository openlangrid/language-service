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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.zparser;

import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.Assert;

import org.junit.Test;

public class ZParserTest {
	
	@Test
	public void test1() throws Exception {
		String text = "This is test sentence.";
		ZParser parser = new ZParser();
		parser.setPath("/usr/local/zpar");
		Chunk[] ret = parser.parseDependency("en", text);
		Assert.assertEquals(ret.length, 3);
		for (Chunk m : ret) {
			System.out.println(m.getChunkId());
			System.out.println(m.getDependency().getHeadChunkId());
			System.out.println(m.getDependency().getLabel());
			for (Morpheme mor : m.getMorphemes()) {
				System.out.println(mor);
			}
		}
	}

	@Test
	public void testZH() throws Exception {
		String text = "千里之行始于足下。";
		ZParser parser = new ZParser();
		parser.setPath("/usr/local/zpar");
		Chunk[] ret = parser.parseDependency("zh", text);
//		Assert.assertEquals(ret.length, 3);
		for (Chunk m : ret) {
			System.out.println(m.getChunkId());
			System.out.println(m.getDependency().getHeadChunkId());
			System.out.println(m.getDependency().getLabel());
			for (Morpheme mor : m.getMorphemes()) {
				System.out.println(mor);
			}
		}
	}
}
