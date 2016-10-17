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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.mbt;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.nl;

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
 * MBTaggerでの形態素解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class MBTagger
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(MBTagger.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public MBTagger() {
		setSupportedLanguageCollection(Arrays.asList(nl));
	}

	/**
	 * MBTコマンドを実行して文章を解析し、解析結果を返す。
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>"'", "."を" ' ", " . "に変換する</li>
	 * <li>mbtでの解析を実行する</li>
	 * </ol>
	 * 
	 * ※入力の最後を示すキーワードとして<utt>を入力する
	 * 
	 * @param language	言語
	 * @param text	原文
	 * @return			解析結果の形態素オブジェクトのコレクション
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		Process proc = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		text = text.replace(".", " .").replace("'", " ' ");
		
		try {
			
			String[] args = {
					"mbt",
					"-s",
					"/usr/local/mbt/eindh.data.settings"
			};
			String crlf = System.getProperty("line.separator");
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			
			// 出力ストリームを取得する
			bw = new BufferedWriter(new OutputStreamWriter(
					proc.getOutputStream(), CHAR_SET));
			bw.write(text);
			bw.write(crlf);
			bw.write(EOL);
			bw.write(crlf);
			bw.flush();
			bw.close();
			
			// 入力ストリームを取得する
			br = new BufferedReader(new InputStreamReader(
					proc.getInputStream(), CHAR_SET));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				String[] words = line.split(SPACE);
				for (String str : words) {
					if (str.indexOf("/") != -1) {
						morphemes.add(parseMorpheme(str));
					}
				}
			}
		} catch (Exception e) {
			_log.error("mbt error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("mbt error : " + e.getLocalizedMessage());
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
	
	/** SPACE */
	private static final String SPACE = "\\s";
	
	/** End Of Stream */
	private static final String EOL = "<utt>";
	
	/** 品詞Map */
	private static final HashMap<String, PartOfSpeech> partsOfSpeechMap = new HashMap<String, PartOfSpeech>();
	static {
		partsOfSpeechMap.put("Adj", PartOfSpeech.adjective);
		partsOfSpeechMap.put("Adv", PartOfSpeech.adverb);
		partsOfSpeechMap.put("Art", PartOfSpeech.other);
		partsOfSpeechMap.put("Conj", PartOfSpeech.other);
		partsOfSpeechMap.put("Int", PartOfSpeech.other);
		partsOfSpeechMap.put("Misc", PartOfSpeech.other);
		partsOfSpeechMap.put("N", PartOfSpeech.noun);
		partsOfSpeechMap.put("Num", PartOfSpeech.other);
		partsOfSpeechMap.put("Prep", PartOfSpeech.other);
		partsOfSpeechMap.put("Pron", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("Punc", PartOfSpeech.other);
		partsOfSpeechMap.put("V", PartOfSpeech.verb);
		// "//" で区切られている場合は UNKOWN + 予測となる
	}
	
	/** 単語INDEX */
	private static final int INDEX_WORD = 0;
	
	/** 品詞INDEX */
	private static final int INDEX_PART_OF_SPEECH = 1;
	
	/** 形態素情報の区切り文字 */
	private static final String MBT_SEPARATOR = "/";
	
	/**
	 * 形態素を解析し形態素オブジェクトを生成して返す。
	 * <br />
	 * Dit/Pron is-test//N zin/N ./Punc <utt>
	 * <br />
	 * @param str	形態素
	 * @return		解析結果の形態素オブジェクト
	 */
	private static Morpheme parseMorpheme(String str) {
		if (str.indexOf("//") != -1) {
			String word = str.substring(0, str.lastIndexOf("//"));
			return new Morpheme(word, word, partsOfSpeechMap.get("Punc"));
		}
		String[] list = str.replace("//", "/").split(MBT_SEPARATOR);
		String word = list[INDEX_WORD];
		PartOfSpeech pos = PartOfSpeech.unknown;
		if (partsOfSpeechMap.containsKey(list[INDEX_PART_OF_SPEECH])) {
			pos = partsOfSpeechMap.get(list[INDEX_PART_OF_SPEECH]);
		}
		return new Morpheme(word, word, pos);
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
