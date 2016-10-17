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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.next;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
 * Nextでの固有表現抽出ラッパー
 * 
 * @author Ryo Morimoto
 */
public class Next extends AbstractNamedEntityTaggingService {
	
	/** Logger  */
	private static Log _log = LogFactory.getLog(Next.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "EUC-JP";
	
	/** 茶筅用一時ファイル */
	private static final String CHASEN_INPUT = "/var/tmp/chasen_input.txt";
	
	/** NExT用一時ファイル */
	private static final String NEXT_INPUT = "/var/tmp/next_input.txt";
	
	/** タグMap */
	private static final Map<String, String> tagMap = new HashMap<String, String>();
	static {
		tagMap.put("NUM", Tag.QUANTITY.name());
		tagMap.put("DATE", Tag.DATETIME.name());
		tagMap.put("TIME", Tag.DATETIME.name());
		tagMap.put("MONEY", Tag.CURRENCY.name());
		tagMap.put("PERCENT", Tag.RATIO.name());
		tagMap.put("PERSON", Tag.PERSON.name());
		tagMap.put("LOCATION", Tag.LOCATION.name());
		tagMap.put("ORGANIZATION", Tag.ORGANIZATION.name());
	}
	
	/**
	 * コンストラクタ
	 */
	public Next() {
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	/**
	 * NExTでの固有表現抽出を行い、結果を返却する
	 * <br />
	 * 処理内容：
	 * <ol>
	 * <li>受け取った原文をファイル出力</li>
	 * <li>出力したファイルに茶筅を実行</li>
	 * <li>茶筅の実行結果をファイル出力</li>
	 * <li>茶筅の実行結果にNExTのパールスクリプトを実行</li>
	 * <li>結果を整形して返却</li>
	 * </ol>
	 * @param language 言語
	 * @param text 原文
	 * @return 固有表現オブジェクトのコレクション
	 */
	@Override
	protected Collection<NamedEntity> doTag(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		Process proc = null;
		OutputStreamWriter osw = null;
		File chasenInput = null;
		File nextInput = null;
		BufferedReader br = null;
		Collection<NamedEntity> entities = null;
		
		String crlf = System.getProperty("line.separator");
		
		try {
			chasenInput = new File(CHASEN_INPUT);
			osw = new OutputStreamWriter(new FileOutputStream(chasenInput), CHAR_SET);
			osw.write(text);
			osw.write(crlf);
			osw.flush();
			osw.close();
			
			String[] args1 = {
				"chasen",
				"-i",
				"e",
				"-c",
				CHASEN_INPUT
			};
			
			// 茶筅の実行
			ProcessBuilder pb = new ProcessBuilder(args1);
			pb.directory(new File(path));
			proc = pb.start();
			proc.waitFor();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			nextInput = new File(NEXT_INPUT);
			osw = new OutputStreamWriter(new FileOutputStream(nextInput), CHAR_SET);
			String line = null;
			while ((line = br.readLine()) != null) {
				osw.write(line);
				osw.write(crlf);
			}
			osw.flush();
			osw.close();
			br.close();
			proc.destroy();
			
			String[] args2 = {
					"/usr/local/NExT/ne.pl",
					"-ci",
					NEXT_INPUT,
					"e"
			};
			
			// perlの実行
			pb = new ProcessBuilder(args2);
			pb.directory(new File(path));
			proc = pb.start();
			
			br = new BufferedReader(new InputStreamReader(proc.getInputStream(), CHAR_SET));
			line = null;
			while ((line = br.readLine()) != null) {
//				System.out.println(line);
				entities = parseLine(line);
			}
			
		} catch (Exception e) {
			_log.error("next error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("next error : " + e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
			if (osw != null) {
				try {
					osw.close();
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
			if (chasenInput != null && !chasenInput.delete()) {
				chasenInput.deleteOnExit();
			}
			if (nextInput != null && !nextInput.delete()) {
				nextInput.deleteOnExit();
			}
		}
		return entities;
	}
	
	/**
	 * 解析結果から固有表現オブジェクトリストを生成する
	 * @param line 解析結果
	 * @return 固有表現オブジェクトリスト
	 */
	private List<NamedEntity> parseLine(String line) {
		Pattern pattern = Pattern.compile("<[A-Z]*?>[^<]*<\\/[A-Z]*?>");
		Matcher matcher = pattern.matcher(line);
		List<NamedEntity> entities = new ArrayList<NamedEntity>();
		String value = null;
		String tagName = Tag.MISC.name();
		String tag = null;
		while (matcher.find()) {
			value = matcher.group().replaceAll("<[\\/A-Z]*?>", "");
			tag = getTagName(matcher.group());
			if (tagMap.containsKey(tag)) tagName = tagMap.get(tag);
			entities.add(new NamedEntity(value, tagName));
		}
		return entities;
	}
	
	/**
	 * タグの名称を抽出する
	 * @param str タグ
	 * @return タグの名称
	 */
	private String getTagName(String str) {
		return str.substring(1, str.indexOf(">"));
	}
	
	/**
	 * 作業ディレクトリのパスをセットする
	 * @param path 作業ディレクトリのパス
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
