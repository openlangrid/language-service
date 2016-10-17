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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.ots;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.ws_1_2.textsummarize.AbstractTextSummarizeService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Open Text Summarizerのラッパー
 * 
 * @author Ryo Morimoto
 */
public class Ots extends AbstractTextSummarizeService {
	
	/** Logger */
	private static Log logger = LogFactory.getLog(Ots.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** 中間ファイル */
	private static final String TMP_FILE = "/var/tmp/ots.txt";
	
	/**
	 * コンストラクタ
	 */
	public Ots() {
		setSupportedLanguageCollection(Arrays.asList(en));
	}

	/**
	 * OpenTextSummarizerによるテキスト要約を行い、結果を返却する
	 * @param language 言語
	 * @param text	原文
	 * @return 要約されたテキスト
	 */
	@Override
	protected String doSummarize(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		File tmp = null;
		FileWriter fw = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String crlf = System.getProperty("line.separator");
		
		try {
			
			tmp = new File(TMP_FILE);
			fw = new FileWriter(tmp);
			fw.write(text);
			fw.write(crlf);
			fw.flush();
			fw.close();
			
			String[] args = {
				"ots",
				TMP_FILE
			};
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				sb.append(line);
				sb.append(crlf);
			}
			
		} catch (Exception e) {
			logger.error("ots error. " + e.getLocalizedMessage());
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
		
		return sb.toString();
	}
	
	/**
	 * 作業ディレクトリのパスをセットする
	 * @param path 作業ディレクトリのパス
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
