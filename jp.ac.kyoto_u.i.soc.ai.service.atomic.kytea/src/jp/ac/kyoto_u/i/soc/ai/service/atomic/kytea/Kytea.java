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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.kytea;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

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
 * Kyteaでの形態素解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class Kytea
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(Kytea.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public Kytea() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	/**
	 * Kyteaコマンドを実行して文章を解析し、解析結果を返す。
	 * 
	 * <br />
	 * Kytea出力形式：<br />
	 * Kytea/名詞/UNK を/助詞/を インストール/名詞/いんすとーる する/動詞/する
	 * 
	 * @param language 言語
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
		
		try {
			String[] args = {
				"/usr/local/bin/kytea"	
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
			bw.flush();
			bw.close();
			
			// 入力ストリームを取得する
			br = new BufferedReader(new InputStreamReader(
					proc.getInputStream(), CHAR_SET));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				String[] words = line.split(SPLITTER);
				for (String str : words) {
					if (str.indexOf("/") != -1) {
						if (str.equals("/補助記号/UNK")) str = " " + str;
						morphemes.add(parseMorpheme(str));
					}
				}
			}
		} catch (Exception e) {
			_log.error("kytea error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("kytea error : " + e.getLocalizedMessage());
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
	
	/** Splitter */
	private static final String SPLITTER = "\\s";
	
	/** 品詞Map */
	private static final HashMap<String, PartOfSpeech> partsOfSpeechMap = new HashMap<String, PartOfSpeech>();
	static {
		partsOfSpeechMap.put("名詞", PartOfSpeech.noun);
		partsOfSpeechMap.put("代名詞", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("動詞", PartOfSpeech.verb);
		partsOfSpeechMap.put("形容詞", PartOfSpeech.adjective);
		partsOfSpeechMap.put("副詞", PartOfSpeech.adverb);
		partsOfSpeechMap.put("接頭詞", PartOfSpeech.other);
		partsOfSpeechMap.put("連体詞", PartOfSpeech.other);
		partsOfSpeechMap.put("接続詞", PartOfSpeech.other);
		partsOfSpeechMap.put("助詞", PartOfSpeech.other);
		partsOfSpeechMap.put("助動詞", PartOfSpeech.other);
		partsOfSpeechMap.put("感動詞", PartOfSpeech.other);
		partsOfSpeechMap.put("語尾", PartOfSpeech.other);
		partsOfSpeechMap.put("補助記号", PartOfSpeech.other);
		partsOfSpeechMap.put("接尾詞", PartOfSpeech.other);
	}
	
	/** 単語INDEX */
	private static final int INDEX_WORD = 0;
	
	/** 品詞INDEX */
	private static final int INDEX_PART_OF_SPEECH = 1;
	
	/** 原型INDEX */
	private static final int INDEX_PROTOTYPE = 2;
	
	/** 原型が不明の場合の戻り値 */
	private static final String UNKNOWN = "UNK";
	
	/** 形態素情報の区切り文字 */
	private static final String KYTEA_SEPARATOR = "/";
	
	/**
	 * 形態素を解析し形態素オブジェクトを生成して返す。
	 * <br />
	 * 星/名詞/ほし
	 * <ul>
	 * 	<li>単語：星</li>
	 * 	<li>品詞：名詞</li>
	 * 	<li>原形：ほし</li>
	 * </ul>
	 * ※原形がUNKの場合は単語を原形とする
	 * <br />
	 * @param str	形態素
	 * @return		解析結果の形態素オブジェクト
	 */
	private static Morpheme parseMorpheme(String str) {
		String[] list = str.split(KYTEA_SEPARATOR);
		if (list.length >= 4) {
			return new Morpheme("/", "/", partsOfSpeechMap.get("補助記号"));
		}
		String word = list[INDEX_WORD];
		PartOfSpeech pos = PartOfSpeech.unknown;
		if (partsOfSpeechMap.containsKey(list[INDEX_PART_OF_SPEECH])) {
			pos = partsOfSpeechMap.get(list[INDEX_PART_OF_SPEECH]);
		}
		String protoType = list[INDEX_PROTOTYPE];
		// 原形がUNKの場合は単語を原形とする
		if (protoType.equals(UNKNOWN)) {
			protoType = word;
		}
		return new Morpheme(word, protoType, pos);
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
