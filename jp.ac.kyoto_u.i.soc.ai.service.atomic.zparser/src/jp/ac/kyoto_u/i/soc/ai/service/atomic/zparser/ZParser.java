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

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.zh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.dependencyparser.Dependency;
import jp.go.nict.langrid.service_1_2.dependencyparser.typed.DependencyLabel;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.ws_1_2.dependencyparser.AbstractDependencyParserService;

/**
 * ZParserでの係り受け解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class ZParser extends AbstractDependencyParserService {

	/** Logger */
	private static Logger logger = Logger.getLogger(ZParser.class.getName());
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public ZParser() {
		setSupportedLanguageCollection(Arrays.asList(en, zh));
	}

	/**
	 * Zparserでの係り受け解析の結果を返す
	 * <br />
	 * Zparser出力形式：
	 * <ul>
	 * <li>This	DT	1	SUB</li>
	 * <li>is	VBZ	-1	ROOT</li>
	 * <li>test	NN	1	PRD</li>
	 * <li>sentence.	VBN	2	NMOD</li>
	 * </ul>
	 * 
	 * @param language 言語
	 * @param sentence	原文
	 * @return 係り受け解析結果オブジェクトのリスト
	 */
	@Override
	protected Collection<Chunk> doParseDependency(Language language,
			String sentence) throws InvalidParameterException,
			ProcessFailedException {
		
		Process proc = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		BufferedReader bre = null;
		Collection<Chunk> chunks = new ArrayList<Chunk>();
		
		try {
			
			ProcessBuilder pb = new ProcessBuilder(getArguments(language));
			pb.directory(new File(path));
			proc = pb.start();
			
			bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), CHAR_SET));
			bw.write(sentence);
			bw.flush();
			bw.close();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			String line = null;
			int index = 0;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				String[] parts = line.split(SPLITTER);
				if (parts.length == 4) {
					chunks.add(parseLine(parts, index++));
				}
			}
			bre = new BufferedReader(new InputStreamReader(proc.getErrorStream(), CHAR_SET));
			String tLine;
			String eLine = "";
			while ((tLine = bre.readLine()) != null) {
				eLine += tLine + "\n";

			}
			if( ! eLine.isEmpty()) {
				logger.severe("zparser error: " + eLine);				
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			e.printStackTrace();
			throw new ProcessFailedException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {}
			}
			if (proc != null) {
				proc.destroy();
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		return chunks;
	}
	
	/** 区切り文字 */
	private static final String SPLITTER = "\\t";
	
	/**
	 * 解析結果から係り受け解析結果オブジェクトを生成する
	 * @param parts 区切り文字で分割された解析結果
	 * @param index 単語の連番
	 * @return 係り受け解析結果オブジェクト
	 */
	private Chunk parseLine(String[] parts, int index) {
		Morpheme morpheme = new Morpheme(parts[0],
				parts[0],
				PennTreebank.get(parts[1]).name());
		Dependency dependency = new Dependency(DependencyLabel.DEPENDENCY.name(), parts[2]);
		return new Chunk(Integer.toString(index),
				new Morpheme[]{ morpheme }, dependency);
	}
	
	/**
	 * 言語に応じたコマンドを返す
	 * @param lang 言語
	 * @return 言語に応じた実行コマンド
	 */
	private String[] getArguments(Language lang) {
		String command = null;
		String model = null;
		if (lang.equals(en)) {
			command = path + "/zpar.en";
			model = "models/english";
		} else if (lang.equals(zh)) {
			command = path + "/zpar";
			model = "models/chinese";
		}
		String[] args = {
				command,
				"-od",
				model
		};
		return args;
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
