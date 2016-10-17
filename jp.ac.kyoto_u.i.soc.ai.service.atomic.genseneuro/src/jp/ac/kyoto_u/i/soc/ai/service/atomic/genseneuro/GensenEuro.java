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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.genseneuro;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fi;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.it;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.sv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.keyphraseextract.Keyphrase;
import jp.go.nict.langrid.wrapper.ws_1_2.keyphraseextract.AbstractKeyphraseExtractService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 言選Webによる日本語、中国語以外のキーフレーズ抽出ラッパー
 * 
 * @author Ryo Morimoto
 */
public class GensenEuro extends AbstractKeyphraseExtractService {
	
	/** Logger */
	private static Log logger = LogFactory.getLog(GensenEuro.class);
	
	/** Brill's Tagger作業ディレクトリ */
	private String btPath = null;
	
	/** extractスクリプト作業ディレクトリ */
	private String extractPath = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** Brill's Tagger用インプットファイル */
	private static final String BT_INPUT_FILE = "/var/tmp/BT_input.txt";
	
	/** 中間ファイル */
	private static final String TMP_FILE = "/var/tmp/BT_out.txt";
	
	/**
	 * コンストラクタ
	 */
	public GensenEuro() {
		// 英、仏、独、伊、西、芬、瑞典
		setSupportedLanguageCollection(Arrays.asList(en, fr, de, it, es, fi, sv));
	}

	/**
	 * 言選Webによる日本語,中国語以外の対応言語のキーフレーズ抽出結果を返す。
	 * Brill's Taggerでの解析結果からキーフレーズを抽出する。
	 * <br />
	 * 出力形式：
	 * <br />
	 * 太郎                                       2
	 * <br /><br />
	 * @param language 言語 
	 * @param text キーフレーズ抽出元の文章
	 */
	@Override
	protected Collection<Keyphrase> doExtract(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		FileWriter fw = null;
		BufferedReader br = null;
		List<Keyphrase> keyphrases = new ArrayList<Keyphrase>();
		File input = null;
		File output = null;
		
		try {
			
			input = new File(BT_INPUT_FILE);
			fw = new FileWriter(input);
			fw.write(text);
			fw.flush();
			fw.close();
			
			String[] args1 = {
					"/usr/local/RULE_BASED_TAGGER/tagger",
					"LEXICON",
					BT_INPUT_FILE,
					"BIGRAMS",
					"LEXICALRULEFILE",
					"CONTEXTUALRULEFILE"
			};
			
			ProcessBuilder pb = new ProcessBuilder(args1);
			pb.directory(new File(btPath));
			proc = pb.start();
			proc.waitFor();
			
			output = new File(TMP_FILE);
			fw = new FileWriter(output);
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				fw.write(line);
			}
			br.close();
			fw.close();
			proc.destroy();
			
			String[] args2 = {
				"perl",
				"/usr/local/TermExtract-4_08/ex_BT.pl"
			};
			
			pb = new ProcessBuilder(args2);
			pb.directory(new File(extractPath));
			proc = pb.start();
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				keyphrases.add(parseLine(line));
			}
			proc.destroy();
			
		} catch (Exception e) {
			logger.error("genseneuro error. " + e.getLocalizedMessage());
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
			if (input != null && !input.delete()) {
				input.deleteOnExit();
			}
			if (output != null && !output.delete()) {
				output.deleteOnExit();
			}
		}
		return keyphrases;
	}
	
	/**
	 * 解析結果からキーフレーズを抽出する
	 * @param line 解析結果
	 * @return キーフレーズオブジェクト
	 */
	private Keyphrase parseLine(String line) {
		line = line.replaceAll("\\s{2,}", " ");
		String[] parts = line.split(" ");
		String keyphrase = "";
		for (int i=0; i<parts.length - 1; i++) {
			keyphrase += " " + parts[i];
		}
		return new Keyphrase(keyphrase.replaceFirst("\\s", ""), Double.parseDouble(parts[parts.length - 1]));
	}
	
	/**
	 * Brill's Taggerの作業ディレクトリのパスをセットする
	 * @param btPath Brill's Taggerの作業ディレクトリのパス
	 */
	public void setBtPath(String btPath) {
		this.btPath = btPath;
	}
	
	/**
	 * extractスクリプトの作業ディレクトリのパスをセットする
	 * @param extractPath extractスクリプトの作業ディレクトリのパス
	 */
	public void setExtractPath(String extractPath) {
		this.extractPath = extractPath;
	}

}
