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
package jp.ac.kyotou.wrapper.igo;

import jp.ac.kyoto_u.i.soc.ai.service.atomic.igo.Igo;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;

import org.junit.Test;


public class IgoTest {
	@Test
	public void test() throws Exception{
		Morpheme[] morphs = new Igo().analyze("ja", "こんにちは世界");
		for(Morpheme m : morphs){
			System.out.println(m);
		}
	}
	
	@Test
	public void test2() throws Exception{
		Morpheme[] morphs = new Igo().analyze("ja", "Igoの形態素解析結果は、基本的にMeCabのそれに合わせるようになっている。");
		for(Morpheme m : morphs){
			System.out.println(m);
		}
	}
	
}
