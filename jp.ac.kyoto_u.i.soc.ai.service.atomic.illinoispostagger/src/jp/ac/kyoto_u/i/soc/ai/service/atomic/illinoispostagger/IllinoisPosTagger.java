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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.illinoispostagger;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

/**
 * Illinois Pos Taggerでの形態素解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class IllinoisPosTagger extends AbstractMorphologicalAnalysisService {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(IllinoisPosTagger.class.getName());
	
	/** 作業ディレクトリのパス */
	private String path = null;
	
	/** 中間ファイル */
	private static final String TMP_FILE = "/var/tmp/ipost_input.txt";
	
	/**
	 * コンストラクタ
	 */
	public IllinoisPosTagger() {
		setSupportedLanguageCollection(Arrays.asList(en));
	}

	/**
	 * IllinoisPosTaggerでの解析結果から形態素オブジェクトリストを生成して返す。
	 * @param language 言語
	 * @param text 解析する文章
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		FileWriter fw = null;
		BufferedReader br = null;
		File tmp = null;
		
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		
		try {
			tmp = new File(TMP_FILE);
			fw = new FileWriter(tmp);
			fw.write(text);
			fw.flush();
			fw.close();
			
			String[] args = {
				"java",
				"-cp",
				"LBJPOS.jar:LBJ2Library.jar",
				"edu.illinois.cs.cogcomp.lbj.pos.POSTagPlain",
				TMP_FILE
			};
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				morphemes.addAll(parseLine(line));
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
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {}
			}
			if (proc != null) {
				proc.destroy();
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				proc = null;
			}
			if (tmp != null && !tmp.delete()) {
				tmp.deleteOnExit();
			}
		}
		return morphemes;
	}
	
	/**
	 * 解析結果から形態素オブジェクトリストを生成して返す。
	 * <br />
	 * 出力形式：
	 * <br />
	 * (DT This) (VBZ is) (NN test) (NNP Text) (. .)
	 * <br /><br />
	 * (xx)を抽出後、"(" と ")" を削除し、半角スペースで区切る
	 * @param line 解析結果出力
	 * @return 形態素オブジェクトリスト
	 */
	private List<Morpheme> parseLine(String line) {
		List<Morpheme> morphs = new ArrayList<Morpheme>();
		String[] words = line.split(" ");
		String word = null;
		for (int i=0; i<words.length; i=i+2) {
			word = words[i+1].substring(0, words[i+1].length() - 1);
			morphs.add(new Morpheme(
					word,
					word,
					PennTreebank.get(words[i].replaceFirst("\\(", ""))
					));
		}
		return morphs;
	}
	
	/**
	 * 作業ディレクトリのパスをセットする
	 * @param path 作業ディレクトリのパス
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
