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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.japanesesummarizer;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.ws_1_2.textsummarize.AbstractTextSummarizeService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Linguaでのテキスト要約ラッパー
 * 
 * @author Ryo Morimoto
 */
public class JapaneseSummarizer extends AbstractTextSummarizeService {
	
	/** Logger */
	private static Log logger = LogFactory.getLog(JapaneseSummarizer.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/**
	 * コンストラクタ
	 */
	public JapaneseSummarizer() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	/**
	 * Linguaでのテキスト要約を実行し、結果を返却する
	 * @param language 言語
	 * @param text 原文
	 * @return 要約されたテキスト
	 */
	@Override
	protected String doSummarize(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		BufferedWriter bw = null;
		BufferedReader br = null;
		StringBuffer sb = null;
		String crlf = System.getProperty("line.separator");
		
		try {
			
			String[] args = {
				"perl",
				"/usr/local/Lingua-JA-Summarize-Extract-0.02/bin/summarize.pl"
			};
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			
			bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream(), CHAR_SET));
			bw.write(text);
			bw.flush();
			bw.close();
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			String line = null;
			sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line + crlf);
			}
			
		} catch (Exception e) {
			logger.error("japanesesummarizer error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
		
		return sb.toString();
	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
