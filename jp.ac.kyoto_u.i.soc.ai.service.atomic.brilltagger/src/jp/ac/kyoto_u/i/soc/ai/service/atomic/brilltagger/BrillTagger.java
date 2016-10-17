/*
 * $Id: ICTCLAS.java 28009 2010-08-09 10:25:33Z Takao Nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.brilltagger;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

/**
 * Brill's Taggerでの形態素解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class BrillTagger
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Logger _log = Logger.getLogger(BrillTagger.class.getName());
	
	/** 作業ディレクトリ */
	private String path = "/usr/local/RULE_BASED_TAGGER/Bin_and_Data";
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** 一時ファイル */
	private static final String TMP_FILE = "/var/tmp/brill_input.txt";
	
	/**
	 * コンストラクタ
	 */
	public BrillTagger() {
		setSupportedLanguageCollection(Arrays.asList(en));
	}

	/**
	 * Brill's Taggerでの解析結果リストを返す
	 * @param language 言語
	 * @param text 解析する文章
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>"'", "."を" ' ", " . "に変換する</li>
	 * <li>文章を文節単位で改行する</li>
	 * <li>改行した文章を一時ファイルに出力する</li>
	 * <li>Brill's Taggerでの解析を実行する</li>
	 * <li>実行結果をMorphemeリストに変換する</li>
	 * <li>一時ファイルを削除する</li>
	 * </ol>
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		Process proc = null;
		BufferedReader br = null;
		BufferedReader bre = null;
		FileWriter fw = null;
		File tmp = null;
		
		try {
			
			String[] args = {
				path + "/tagger",
				"LEXICON",
				TMP_FILE,
				"BIGRAMS",
				"LEXICALRULEFILE",
				"CONTEXTUALRULEFILE"
			};
			String crlf = System.getProperty("line.separator");
			
			text = text.replace("'", " ' ").replace(".", " . ");
			
			tmp = new File(TMP_FILE);
			fw = new FileWriter(tmp);
			fw.write(text);
			fw.write(crlf);
			fw.flush();
			fw.close();
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			
			proc = pb.start();
			
			// 入力ストリームを取得する
			br = new BufferedReader(new InputStreamReader(
					proc.getInputStream(), CHAR_SET));
			bre = new BufferedReader(new InputStreamReader(proc.getErrorStream(), CHAR_SET));
			
			String line = null;
			while ((line = br.readLine()) != null && ! line.isEmpty()) {
//				System.out.println(line);
				String[] words = line.split(SPACE);
				for (String str : words) {
					morphemes.add(parseMorpheme(str));
				}
			}
			String tLine;
			String eLine = "";
			while ((tLine = bre.readLine()) != null) {
				eLine += tLine + "\n";

			}
			if( ! eLine.isEmpty()) {
				_log.severe("brilltagger error: " + eLine);				
			}
		} catch (Exception e) {

			_log.severe("brilltagger error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("brilltagger error : " + e.getLocalizedMessage());
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
	
	/** SPACE */
	private static final String SPACE = "\\s";
	
	/** 単語INDEX */
	private static final int INDEX_WORD = 0;
	
	/** 品詞INDEX */
	private static final int INDEX_PART_OF_SPEECH = 1;
	
	/** 形態素情報の区切り文字 */
	private static final String SEPARATOR = "/";
	
	/**
	 * 形態素を解析し形態素オブジェクトを生成して返す。
	 * <br />
	 * This/DT
	 * <ul>
	 * 	<li>単語：This</li>
	 * 	<li>品詞：DT</li>
	 * 	<li>原形：This</li>
	 * </ul>
	 * <br />
	 * @param str	形態素
	 * @return		解析結果の形態素オブジェクト
	 */
	private static Morpheme parseMorpheme(String str) {
		System.out.println(str);
		String[] list = str.split(SEPARATOR);
		if (list.length == 0) {
			return new Morpheme(str, str, PartOfSpeech.unknown);
		}
		String word = list[INDEX_WORD];
		return new Morpheme(word, word, PennTreebank.get(list[INDEX_PART_OF_SPEECH]));
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
