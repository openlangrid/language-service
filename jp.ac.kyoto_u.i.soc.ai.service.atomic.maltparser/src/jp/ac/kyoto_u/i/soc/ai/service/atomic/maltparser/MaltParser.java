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

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.da;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sw;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.dependencyparser.Dependency;
import jp.go.nict.langrid.service_1_2.dependencyparser.MorphemesDependencyParserService;
import jp.go.nict.langrid.service_1_2.dependencyparser.typed.DependencyLabel;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.common.process.ProcessFacade;
import jp.go.nict.langrid.wrapper.ws_1_2.dependencyparser.AbstractMorphemesDependencyParserService;

public class MaltParser
extends AbstractMorphemesDependencyParserService
implements MorphemesDependencyParserService{
	public MaltParser(){
		setSupportedLanguageCollection(Arrays.asList(da, de, en, fr, pt, sw));
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	protected Collection<Chunk> doParseDependency(
			Language language, Morpheme[] morphs)
	throws InvalidParameterException, ProcessFailedException {
		List<Chunk> ret = new ArrayList<Chunk>();
		ProcessFacade f = new ProcessFacade(
				new File(path)
				, "java", "-Xmx1024m", "-jar", "malt.jar"
				, "-c", langToConf.get(language)
				, "-m", "parse"
				);
		try{
			try{
				f.start();

				OutputStream out = f.getOutputStream();
				PrintWriter w = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
				int c = 1;
				for(Morpheme m : morphs){
					String s = String.format("%d\t%s\t_\t%s\t%3$s"
							, c++, m.getWord()
							, PennTreebank.lgToPt(PartOfSpeech.valueOfExpression(m.getPartOfSpeech()), m.getWord())
							);
					w.println(s);
//					System.out.println(s);
				}
				w.println();
				w.flush();
				w.close();
				
				String[] results = StreamUtil.readAsString(
						f.getInputStream(), "UTF-8").split("\\n");
				int i = 0;
				for(String r : results){
					String[] elements = r.split("\\t");
					ret.add(new Chunk(
							Integer.toString(i + 1)
							, new Morpheme[]{morphs[i]}
							, new Dependency(
									DependencyLabel.DEPENDENCY.name()
									, elements[6])));
					i++;
//					System.out.println(r);
				}
				String err = StreamUtil.readAsString(f.getErrorStream(), "UTF-8");
				if(ret.size() == 0 && err.length() != 0){
					String[] errs = err.split("\\n");
					throw new ProcessFailedException(errs[errs.length - 1]);
				}
			} finally{
				f.close();
			}
			return ret;
		} catch(InterruptedException e){
			throw new ProcessFailedException(e);
		} catch(IOException e){
			throw new ProcessFailedException(e);
		}
	}

	private String path = "/usr/local/malt";
	private static Map<Language, String> langToConf = new HashMap<Language, String>();
	static{
		langToConf.put(da, "danish_ddt");
		langToConf.put(de, "dutch_alpino");
		langToConf.put(en, "engmalt.linear");
		langToConf.put(fr, "fremalt.mco");
		langToConf.put(pt, "portuguese_bosque");
		langToConf.put(sw, "swedish_talbanken05");
	}
}
