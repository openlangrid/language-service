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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.gensenja;

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
import java.util.List;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.keyphraseextract.Keyphrase;
import jp.go.nict.langrid.wrapper.ws_1_2.keyphraseextract.AbstractKeyphraseExtractService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 言選Webによる日本語キーフレーズ抽出ラッパー
 * 
 * @author Ryo Morimoto
 */
public class GensenJa extends AbstractKeyphraseExtractService {
	
	/** Logger */
	private static Log logger = LogFactory.getLog(GensenJa.class);
	
	/** 作業ディレクトリのパス */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** 中間ファイル */
	private static final String TMP_FILE = "/var/tmp/mecab_out.txt";
	
	/**
	 * コンストラクタ
	 */
	public GensenJa() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}
 
	/**
	 * 言選Webによる日本語キーフレーズ抽出結果を返す。
	 * Mecabでの日本語の解析結果からキーフレーズを抽出する。
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
		BufferedWriter bw = null;
		BufferedReader br = null;
		List<Keyphrase> keyphrases = new ArrayList<Keyphrase>();
		
		try {
			String[] args1 = {
				"mecab",
				"-o",
				TMP_FILE
			};
			ProcessBuilder pb = new ProcessBuilder(args1);
			pb.directory(new File(path));
			proc = pb.start();
			
			bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), CHAR_SET));
			bw.write(text);
			bw.flush();
			bw.close();
			proc.waitFor();
			proc.destroy();
			
			String[] args2 = {
				"perl",
				"/usr/local/TermExtract-4_08/ex_mecab.pl"	
			};
			
			pb = new ProcessBuilder(args2);
			pb.directory(new File(path));
			proc = pb.start();
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				keyphrases.add(parseLine(line));
			}
			
		} catch (Exception e) {
			logger.error("gensenja error. " + e.getLocalizedMessage());
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
			File file = new File(TMP_FILE);
			if (!file.delete()) {
				file.deleteOnExit();
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
		return new Keyphrase(parts[0], Double.parseDouble(parts[1]));
	}
	
	/**
	 * 作業ディレクトリのパスをセットする
	 * @param path 作業ディレクトリのパス
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
}
