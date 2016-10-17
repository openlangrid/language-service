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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.germanner;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.namedentitytagging.NamedEntity;
import jp.go.nict.langrid.service_1_2.namedentitytagging.typed.Tag;
import jp.go.nict.langrid.wrapper.ws_1_2.namedentitytagging.AbstractNamedEntityTaggingService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GermanNerのラッパークラス
 * 
 * @author Ryo Morimoto
 */
public class GermanNer extends AbstractNamedEntityTaggingService {
	
	/** Logger  */
	private static Log _log = LogFactory.getLog(GermanNer.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 変換リスト */
	private List<String> convertList = null;
	
	/**
	 * コンストラクタ
	 */
	public GermanNer() {
		setSupportedLanguageCollection(Arrays.asList(de));
		convertList = new ArrayList<String>();
	}
	
	/**
	 * パスをセットする
	 * @param path	ファイルパス
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * 文字列から固有表現を抽出する
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>stanford-nerでの単語分割を実行し、結果を一時ファイルに整形しながら出力する</li>
	 * <li>stanford-nerでの解析を実行し、結果を一時ファイルに出力する</li>
	 * <li>一時ファイルを削除する</li>
	 * </ol>
	 */
	@Override
	protected Collection<NamedEntity> doTag(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		BufferedReader br = null;
		File tmp = null;
		File tok = null;
		FileWriter fw = null;
		List<NamedEntity> resultList = new ArrayList<NamedEntity>();
		String crlf = System.getProperty("line.separator");
		
		// (), [], <> が 同じ文字列に変換されてしまうため、一時的にリストに保持する
		for (String key : convertTargetList) {
			Pattern pattern = null;
			if (!key.equals("<") || !key.equals(">")) {
				pattern = Pattern.compile("\\" + key);
			} else {
				pattern = Pattern.compile(key);
			}
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				convertList.add(key);
			}
		}
		
		try {
			tmp = new File(INPUT_FILE);
			fw = new FileWriter(tmp);
			fw.write(text);
			fw.flush();
			fw.close();
			
			String[] args1 = {
				"java", "-cp", "stanford-ner.jar",
				"edu.stanford.nlp.process.PTBTokenizer",
				INPUT_FILE
			};
			ProcessBuilder pb = new ProcessBuilder(args1);
			pb.directory(new File(path));
			proc = pb.start();
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			tok = new File(TOK_FILE);
			fw = new FileWriter(tok);
			String line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				fw.write(line + " O O O O\n");
			}
			fw.flush();
			fw.close();
			br.close();
			proc.destroy();
			
			String[] args2 = {
					"java", "-mx600m",
					"-cp", "stanford-ner.jar",
					"edu.stanford.nlp.ie.crf.CRFClassifier",
					"-loadClassifier", "classifiers/all.3class.distsim.crf.ser.gz",
					"-testFile", TOK_FILE
			};
			
			pb = new ProcessBuilder(args2);
			pb.directory(new File(path));
			proc = pb.start();
			
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				String[] words = line.split(crlf);
				for (String word : words) {
					if (word != null && !word.equals("")) {
						resultList.add(extract(word));
					}
				}
			}
			return resultList;
			
		} catch (Exception e) {
			_log.error("tfsss error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("tfsss error : " + e.getLocalizedMessage());
		} finally {
			if (tmp != null && !tmp.delete()) {
				tmp.deleteOnExit();
			}
			if (tok != null && !tok.delete()) {
				tok.deleteOnExit();
			}
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
		}
	}
	
	/** タグMap */
	private static final Map<String, Tag> tagMap = new HashMap<String, Tag>();
	static {
		tagMap.put("PERSON", Tag.PERSON);
		tagMap.put("ORGANIZATION", Tag.ORGANIZATION);
		tagMap.put("LOCATION", Tag.LOCATION);
		tagMap.put("O", Tag.MISC);
	}
	
	/** 中間ファイル */
	private static final String INPUT_FILE = "/var/tmp/germanner_input.txt";
	
	/** トークンファイル */
	private static final String TOK_FILE = "/var/tmp/germanner.tok";
	
	/** 区切り文字 */
	private static final String SPLITTER = "\t";
	
	/** 変換Map */
	private static final List<String> convertTargetList = new ArrayList<String>();
	static {
		convertTargetList.add("(");
		convertTargetList.add(")");
		convertTargetList.add("<");
		convertTargetList.add(">");
		convertTargetList.add("[");
		convertTargetList.add("]");
	}
	
	/**
	 * 解析結果を固有表現オブジェクトに変換して返す
	 * @param word	解析結果
	 * @return		固有表現オブジェクト
	 */
	private NamedEntity extract(String word) {
		String[] parts = word.split(SPLITTER);
		String entity = parts[0];
		// (, [, < は -LRB-に、), [, > は -RRB-に変換されるため、元に戻す
		if (entity.equals("-LRB-") || entity.equals("-RRB-")) {
			entity = convertList.get(0);
			convertList.remove(0);
			return new NamedEntity(entity, Tag.MISC.name());
		} else if (entity.equals("\\*")) {
			// * は \* に変換されるため、元に戻す
			entity = "*";
		} else if (entity.equals("''")) {
			// " は '' に変換されるため、元に戻す
			entity = "\"";
		} else if (entity.equals("\\/")) {
			// / は \/ に変換されるため、元に戻す
			entity = "/";
		}
		// {} は -LCB-, -RCB- に変換されるため、元に戻す
		if (entity.equals("-LCB-")) entity = "{";
		if (entity.equals("-RCB-")) entity = "}";
		Tag tag = Tag.NONE;
		if (tagMap.containsKey(parts[1])) {
			tag = tagMap.get(parts[2]);
		}
		return new NamedEntity(entity, tag.name());
	}

}
