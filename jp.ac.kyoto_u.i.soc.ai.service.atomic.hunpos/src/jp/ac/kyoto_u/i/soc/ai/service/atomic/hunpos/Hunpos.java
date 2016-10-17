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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.hunpos;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.hu;

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
import java.util.StringTokenizer;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Hunposでの形態素解析ラッパークラス
 * 
 * @author Ryo Morimoto
 */
public class Hunpos
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(Hunpos.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public Hunpos() {
		setSupportedLanguageCollection(Arrays.asList(en, hu));
	}

	/**
	 * Hunposでの形態素解析を実行し、結果を返却する
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>"'", "."を" ' ", " . "に変換する</li>
	 * <li>文章を文節単位で分割する</li>
	 * <li>指定された言語からモデルファイルを特定し、Hunposでの解析を実行する</li>
	 * </ol>
	 * @param language 言語
	 * @param text 解析を行う文章
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		Process proc = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try {
			
			String[] args = {
				"/usr/local/hunpos-1.0-linux/hunpos-tag",
				getOption(language)
			};
			String crlf = System.getProperty("line.separator");
			
			text = text.replace("'", " ' ").replace(".", " . ");
			
			// 単語単位での解析となるため、文節ごとに分割
			StringTokenizer tokens = new StringTokenizer(text);
			List<String> wordList = new ArrayList<String>();
			String token = null;
			while (tokens.hasMoreElements()) {
				token = tokens.nextToken();
				wordList.add(token);
			}
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			
			// 出力ストリームを取得する
			bw = new BufferedWriter(new OutputStreamWriter(
					proc.getOutputStream(), CHAR_SET));
			for (String word : wordList) {
				bw.write(word);
				bw.write(crlf);
				bw.flush();
			}
			bw.close();
			
			// 入力ストリームを取得する
			br = new BufferedReader(new InputStreamReader(
					proc.getInputStream(), CHAR_SET));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				if (!line.equals("")) {
					morphemes.add(parseMorpheme(line));
				}
			}
		} catch (Exception e) {
			_log.error("hunpos error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("hunpos error : " + e.getLocalizedMessage());
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
	
	/** 単語INDEX */
	private static final int INDEX_WORD = 0;
	
	/** 品詞INDEX */
	private static final int INDEX_PART_OF_SPEECH = 1;
	
	/** 形態素情報の区切り文字 */
	private static final String HUNPOS_SEPARATOR = "\t";
	
	/**
	 * 形態素を解析し形態素オブジェクトを生成して返す。
	 * <br />
	 * have	NOUN<CAS<SBL>>	
	 * <br />
	 * @param str	形態素
	 * @return		解析結果の形態素オブジェクト
	 */
	private static Morpheme parseMorpheme(String str) {
		String[] list = str.split(HUNPOS_SEPARATOR);
		String word = list[INDEX_WORD];
		return new Morpheme(word, word,  PennTreebank.get(list[INDEX_PART_OF_SPEECH]));
	}
	
	/**
	 * 言語ごとにコマンドオプションを取得する
	 * @param lang 言語
	 * @return 言語がhuであればハンガリー語モデルファイル名、それ以外は英語モデルファイル名
	 */
	private static String getOption(Language lang) {
		if (lang.equals(hu)) {
			return "hu_szeged_kr.model";
		}
		return "en_wsj.model";
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
