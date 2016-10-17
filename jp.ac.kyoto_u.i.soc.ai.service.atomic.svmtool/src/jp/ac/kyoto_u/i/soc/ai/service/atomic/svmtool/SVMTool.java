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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.svmtool;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SVMToolでの形態素解析ラッパークラス
 * 
 * @author Ryo Morimoto
 */
public class SVMTool
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(SVMTool.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public SVMTool() {
		setSupportedLanguageCollection(Arrays.asList(en, es));
	}

	/**
	 * SVMToolコマンドを実行して文章を解析し、解析結果を返す。
	 * 
	 * <br />
	 * SVMTool出力形式：<br />
	 * I PRP	<br />
	 * have VBP	<br />
	 * lived VBN	<br />
	 * 
	 * @param language	言語
	 * @param text		原文
	 * @return			解析結果の形態素オブジェクトのコレクション
	 * @throws ProcessFailedException
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		Process proc = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try {
			
			String crlf = System.getProperty("line.separator");

			List<String> wordList = new ArrayList<String>();
			StringTokenizer tokens = new StringTokenizer(text);
			String token = null;
			while (tokens.hasMoreElements()) {
				token = tokens.nextToken();
				wordList.add(token);
			}
			
			String[] args = {
				"/usr/local/SVMTool-1.3.1/bin/SVMTagger",
				getOption(language)
			};
			
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
					morphemes.add(parseMorpheme(line, language));
				}
			}
		} catch (Exception e) {
			_log.error("svmtool error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("svmtool error : " + e.getLocalizedMessage());
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
	
	/** 品詞Map */
	private static final HashMap<String, PartOfSpeech> partsOfSpeechMap = new HashMap<String, PartOfSpeech>();
	static {
		partsOfSpeechMap.put("#", PartOfSpeech.other);
		partsOfSpeechMap.put("$", PartOfSpeech.other);
		partsOfSpeechMap.put("’’", PartOfSpeech.other);
		partsOfSpeechMap.put("(", PartOfSpeech.other);
		partsOfSpeechMap.put(")", PartOfSpeech.other);
		partsOfSpeechMap.put(",", PartOfSpeech.other);
		partsOfSpeechMap.put(".", PartOfSpeech.other);
		partsOfSpeechMap.put(":", PartOfSpeech.other);
		partsOfSpeechMap.put("CC", PartOfSpeech.other);
		partsOfSpeechMap.put("CD", PartOfSpeech.other);
		partsOfSpeechMap.put("DT", PartOfSpeech.other);
		partsOfSpeechMap.put("EX", PartOfSpeech.other);
		partsOfSpeechMap.put("FW", PartOfSpeech.other);
		partsOfSpeechMap.put("IN", PartOfSpeech.other);
		partsOfSpeechMap.put("JJ", PartOfSpeech.adjective);
		partsOfSpeechMap.put("JJR", PartOfSpeech.adjective);
		partsOfSpeechMap.put("JJS", PartOfSpeech.adjective);
		partsOfSpeechMap.put("LS", PartOfSpeech.other);
		partsOfSpeechMap.put("MD", PartOfSpeech.other);
		partsOfSpeechMap.put("NN", PartOfSpeech.noun_common);
		partsOfSpeechMap.put("NNP", PartOfSpeech.noun_common);
		partsOfSpeechMap.put("NNPS", PartOfSpeech.noun_proper);
		partsOfSpeechMap.put("NNS", PartOfSpeech.noun_proper);
		partsOfSpeechMap.put("PDT", PartOfSpeech.other);
		partsOfSpeechMap.put("POS", PartOfSpeech.other);
		partsOfSpeechMap.put("PRP", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("PRP$", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("RB", PartOfSpeech.adverb);
		partsOfSpeechMap.put("RBR", PartOfSpeech.adverb);
		partsOfSpeechMap.put("RBS", PartOfSpeech.adverb);
		partsOfSpeechMap.put("RP", PartOfSpeech.other);
		partsOfSpeechMap.put("SYM", PartOfSpeech.other);
		partsOfSpeechMap.put("TO", PartOfSpeech.other);
		partsOfSpeechMap.put("UH", PartOfSpeech.other);
		partsOfSpeechMap.put("VB", PartOfSpeech.verb);
		partsOfSpeechMap.put("VBD", PartOfSpeech.verb);
		partsOfSpeechMap.put("VBG", PartOfSpeech.verb);
		partsOfSpeechMap.put("VBN", PartOfSpeech.verb);
		partsOfSpeechMap.put("VBP", PartOfSpeech.verb);
		partsOfSpeechMap.put("VBZ", PartOfSpeech.verb);
		partsOfSpeechMap.put("WDT", PartOfSpeech.other);
		partsOfSpeechMap.put("WP", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("WP$", PartOfSpeech.noun_other);
		partsOfSpeechMap.put("WRB", PartOfSpeech.other);
		partsOfSpeechMap.put("‘‘", PartOfSpeech.other);
	}
	// TODO スペイン語用のマップが必要
	
	/** 単語INDEX */
	private static final int INDEX_WORD = 0;
	
	/** 品詞INDEX */
	private static final int INDEX_PART_OF_SPEECH = 1;
	
	/** 形態素情報の区切り文字 */
	private static final String SVM_SEPARATOR = "\\s";
	
	/**
	 * 形態素を解析し形態素オブジェクトを生成して返す。
	 * <br />
	 * This DT <br />
	 * is VBZ <br />
	 * test NN <br />
	 * <br />
	 * @param str	形態素
	 * @param language 言語
	 * @return		解析結果の形態素オブジェクト
	 */
	private static Morpheme parseMorpheme(String str, Language language) {
		String[] list = str.split(SVM_SEPARATOR);
		String word = list[INDEX_WORD];
		PartOfSpeech partOfSpeech = PartOfSpeech.unknown;
		if (partsOfSpeechMap.containsKey(list[INDEX_PART_OF_SPEECH])) {
			if (language.equals(en)) {
				partOfSpeech = partsOfSpeechMap.get(list[INDEX_PART_OF_SPEECH]);
			} else {
				// 3LB corpus?のタグリストが必要
			}
		}
		return new Morpheme(word, word,  partOfSpeech);
	}
	
	/**
	 * 実行コマンドを取得する
	 * @param lang 言語
	 * @return 言語に応じたオプション
	 */
	private static String getOption(Language lang) {
		if (lang.equals(en)) {
			return "eng/WSJTP";
		}
		return "spa/3LB.SPA";
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
