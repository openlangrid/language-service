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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.stanfordner;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.go.nict.langrid.commons.io.FileUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.namedentitytagging.NamedEntity;
import jp.go.nict.langrid.service_1_2.namedentitytagging.typed.Tag;
import jp.go.nict.langrid.wrapper.ws_1_2.namedentitytagging.AbstractNamedEntityTaggingService;

/**
 * StanfordNerのラッパークラス
 * 
 * @author Ryo Morimoto
 */
public class StanfordNer extends AbstractNamedEntityTaggingService {
	
	/** Logger  */
	private static Log _log = LogFactory.getLog(StanfordNer.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 変換リスト */
	private List<String> convertList = null;
	
	/**
	 * コンストラクタ
	 */
	public StanfordNer() {
		setSupportedLanguageCollection(Arrays.asList(en));
		convertList = new ArrayList<String>();
	}
	
	/**
	 * パスをセットする
	 * @param path	ファイルパス
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	/**
	 * StanfordNerを実行して固有表現を抽出する。
	 * 
	 * <br />
	 * 出力形式：<br />
	 * The/O fate/O Lehman/ORGANIZATION Brothers/ORGANIZATION ,/O
	 * 
	 * @param language	言語
	 * @param text 原文
	 * @return			抽出した固有表現オブジェクトのコレクション
	 * @throws ProcessFailedException
	 */
	@Override
	protected Collection<NamedEntity> doTag(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		BufferedReader br = null;
		FileWriter fw = null;
		List<NamedEntity> resultList = new ArrayList<NamedEntity>();
		
		// (), [], <> が 同じ文字列に変換されてしまうため、一時的にリストに保持する
		text = text.replaceAll("<", "< ").replaceAll(">", " >");
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
		
		File tmp = null;
		try{
			tmp = FileUtil.createUniqueFile(getWorkPathFile(), "temp");
			fw = new FileWriter(tmp);
			fw.write(text);
			fw.flush();
			fw.close();

			String[] args = {
					"java",
					"-mx600m",
					"-cp",
					"stanford-ner.jar",
					"edu.stanford.nlp.ie.crf.CRFClassifier",
					"-loadClassifier",
					"classifiers/all.3class.distsim.crf.ser.gz",
					"-textFile", tmp.getAbsolutePath()
			};
		
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(SPACE);
				for (String word : words) {
//					System.out.println(word);
					resultList.add(extract(word));
				}
			}
			return resultList;
			
		} catch (Exception e) {
			_log.error("stanford-ner error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("stanford-ner error : " + e.getLocalizedMessage());
		} finally {
			if (tmp != null && !tmp.delete()) {
				tmp.deleteOnExit();
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
	
	/** 空白 */
	private static final String SPACE = "\\s";
	
	/** 区切り文字 */
	private static final String SPLITTER = "/";
	
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
			tag = tagMap.get(parts[1]);
		}
		return new NamedEntity(entity, tag.name());
	}
	
	private synchronized File getWorkPathFile()
	throws IOException{
		if(workPathFile == null){
			if(workPath != null){
				workPathFile = new File(workPath);
			} else{
				workPathFile = getWorkDirectory();
			}
			if(!workPathFile.exists()){
				if(!workPathFile.mkdirs()) throw new IOException("failed to create path: " + workPathFile);
			}
		}
		return workPathFile;
	}

	private String workPath = "/tmp";
	private File workPathFile;
}
