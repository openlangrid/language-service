/*
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
package jp.go.nict.langrid.wrapper.cabocha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.dependencyparser.Dependency;
import jp.go.nict.langrid.service_1_2.dependencyparser.typed.DependencyLabel;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;

/**
 * 
 * 
 * @author Masaaki Kamiya
 * @author $Author$
 * @version $Revision$
 */
public class Parser {
	/**
	 * cabochaの実行と解析を行う
	 * @param sentence 解析する原文
	 * @return 解析結果のコレクション
	 * @throws IOException 
	 * @throws ProcessFailedException 実行に失敗した
	 */
	public Collection<Chunk> parse(String sentence, String path, String encoding)
	throws IOException
	{
		List<Chunk> chunks = new ArrayList<Chunk>();
		Process proc = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		String crlf = System.getProperty("line.separator");
		List<String> commands = new ArrayList<String>();
		commands.add(path);
		commands.add("-f1");
		ProcessBuilder pb = new ProcessBuilder(commands);
		proc = pb.start();
		try {
			try {
				bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), encoding));
				bw.write(sentence);
				bw.write(crlf);
			} finally {
				if(bw != null) {
					bw.close();
				}
			}
				
			try {
				br = new BufferedReader(new InputStreamReader(proc.getInputStream(), encoding));
				String line = null;
				List<Morpheme> morphemes = new ArrayList<Morpheme>();
				while((line = br.readLine()) != null) {
					if(line.equals(EOS)) {
						chunks.get(chunks.size() - 1).setMorphemes(morphemes.toArray(new Morpheme[]{}));
						break;
					}
					if(line.startsWith("*")) {
						if(0 < chunks.size()) {							
							chunks.get(chunks.size() - 1).setMorphemes(morphemes.toArray(new Morpheme[]{}));
							morphemes = new ArrayList<Morpheme>();
						}
						
						Chunk c = makeChunk(line);
						chunks.add(c);
					} else {
						Morpheme m = makeMopheme(line);
						morphemes.add(m);
					}
				}
				return chunks;
			} finally {
				if(br != null) {
					br.close();
				}
			}
		} finally {
			proc.destroy();
		}
	}
	
	private Chunk makeChunk(String line) {
		String[] eles = line.split("\\s");
		Chunk c = new Chunk();
		c.setChunkId(eles[1]);
		
		String dep = eles[2];
		String depIndex = dep.substring(0, dep.length() - 1);
		String depKey = dep.substring(dep.length() - 1);
		
		Dependency d = new Dependency();
		d.setHeadChunkId(depIndex);
		d.setLabel(dependencyMap.get(depKey).name());
		
		c.setDependency(d);
		return c;
	}
	
	private Morpheme makeMopheme(String line) {
		String[] set = line.split("\t");
		String[] eles = set[1].split(",");
		
		Morpheme m = new Morpheme();
		m.setWord(set[0]);
		m.setLemma(eles[6]);
		m.setPartOfSpeech(getPos(eles[0], new String[]{eles[1], eles[2], eles[3]}));
		return m;
	}
	
	private String getPos(String pos, String[] classifications) {
		if(pos.equals("名詞")) {
			if(classifications[0].equals("一般")) {
				return "noun.common";
			} else if(classifications[0].equals("固有名詞")) {
				return "noun.proper";
			}
			return "noun.other";
		} else if(pos.equals("動詞")) {
			return "verb";
		} else if(pos.equals("形容詞")) {
			return "adjective";
		} else if(pos.equals("副詞")) {
			return "adverb";
		}
		return "other";
	}
	
	/**
	 * Cabochaの出力の終わりを示すキー
	 */
	private static final String EOS = "EOS";
	
	/**
	 * 係り受け関係
	 */
	private static final HashMap<String, DependencyLabel> dependencyMap =
		new HashMap<String, DependencyLabel>();
	static {
		dependencyMap.put("A", DependencyLabel.APPOSITION);
		dependencyMap.put("D", DependencyLabel.DEPENDENCY);
		dependencyMap.put("P", DependencyLabel.PARALELL);
		dependencyMap.put("O", DependencyLabel.DEPENDENCY);
	}
}
