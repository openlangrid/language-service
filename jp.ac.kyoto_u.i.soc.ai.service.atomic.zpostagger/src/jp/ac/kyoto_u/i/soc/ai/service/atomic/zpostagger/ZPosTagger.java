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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.zpostagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.zh;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

/**
 * ZParserによる形態素解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class ZPosTagger extends AbstractMorphologicalAnalysisService {
	
	/** Logger */
	private static Logger logger = Logger.getLogger(ZPosTagger.class.getName());
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public ZPosTagger() {
		setSupportedLanguageCollection(Arrays.asList(en, zh));
	}

	/**
	 * Zparコマンドを実行して文章を解析し、解析結果を返す。
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>zparでの解析を実行する</li>
	 * </ol>
	 * 
	 * @param language	言語
	 * @param text	原文
	 * @return			解析結果の形態素オブジェクトのコレクション
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		
		try {
			
			ProcessBuilder pb = new ProcessBuilder(getArguments(language));
			pb.directory(new File(path));
			proc = pb.start();
			
			bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), CHAR_SET));
			bw.write(text);
			bw.flush();
			bw.close();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			String line = null;
			int index = 0;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				if (index > 1 && line.indexOf(getSeparator(language)) >= 0) {
					morphemes.addAll(parseLine(line, language));
				}
				index++;
			}
			proc.waitFor();
			
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
				proc = null;
			}
		}
		return morphemes;
	}
	
	/** 形態素の区切り */
	private static final String WORD_SEPARATOR = " ";
	
	/** 単語と品詞の区切り:英語 */
	private static final String ENGLISH_SEPARATOR = "/";
	
	/** 単語と品詞の区切り:中国語 */
	private static final String CHINESE_SEPARATOR = "_";
	
	/**
	 * 解析結果から形態素オブジェクトリストを生成して返す
	 * 
	 * @param line 解析結果
	 * @return 形態素オブジェクトリスト
	 */
	private List<Morpheme> parseLine(String line, Language lang) {
//		System.out.println("parseList : " + line);
		List<Morpheme> morphs = new ArrayList<Morpheme>();
		String[] words = line.split(WORD_SEPARATOR);
		for (String word : words) {
			String[] parts = word.split(getSeparator(lang));
			// 言語が英語で解析する単語が "/" の場合
			if (lang.equals(en) && parts.length > 2) {
				morphs.add(new Morpheme(
							word.substring(0, word.lastIndexOf("/")),
							word.substring(0, word.lastIndexOf("/")),
							PennTreebank.get("LS")
						));
				continue;
			// 言語が中国語で解析する単語が "_" の場合
			} else if (lang.equals(zh) && parts.length > 2) {
				morphs.add(new Morpheme(
						word.substring(0, word.lastIndexOf("_")),
						word.substring(0, word.lastIndexOf("_")),
						PennTreebank.get(word.substring(word.lastIndexOf("_") + 1))));
				continue;
			}
			morphs.add(new Morpheme(parts[0],
					parts[0],
					PennTreebank.get(parts[1])));
		}
		return morphs;
	}
	
	/**
	 * 実行コマンドを返す
	 * @param lang 言語
	 * @return 言語に応じたコマンド
	 */
	private String[] getArguments(Language lang) {
		String command = null;
		String option = "-ot";
		String model = null;
//		System.out.println(lang.getCode());
//		System.out.println(en.getCode());
		if (lang.equals(en)) {
			command = "/usr/local/zpar/dist/zpar.en";
			model = "models/english";
		} else if (lang.equals(zh)) {
			command = "/usr/local/zpar/dist/zpar";
			model = "models/chinese";
		}
		String[] args = {
			command,
			option,
			model
		};
		return args;
	}
	
	/**
	 * 言語に応じたセパレータを返す
	 * @param language 言語
	 * @return 単語を区切るセパレータ文字
	 */
	private String getSeparator(Language language) {
		if (language.equals(en)) {
			return ENGLISH_SEPARATOR;
		}
		return CHINESE_SEPARATOR;
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
