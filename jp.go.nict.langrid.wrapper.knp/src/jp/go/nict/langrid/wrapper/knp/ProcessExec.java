/*
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.knp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.dependencyparser.Dependency;
import jp.go.nict.langrid.service_1_2.dependencyparser.typed.DependencyLabel;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.wrapper.common.process.ProcessFacade;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessExec {
	/**
	 * TAB or SPACE
	 */
	private static final String TAB_SPACE = "\\s";
	/**
	 * Logger
	 */
	private static Log _log = LogFactory.getLog(ProcessExec.class);
	/**
	 * 係り受けラベル
	 */
	private static final String PATTERN_DEPENDENCY = "(^-?[0-9]+)([A-Z]+)(.*)$";
	/**
	 * 正規表現パターン
	 */
	private static final Pattern pattern = Pattern.compile(PATTERN_DEPENDENCY);
	/**
	 * ウィンドウズかどうか
	 */
	private static final boolean windows = System.getProperty("os.name").indexOf("Windows") != -1;
	/**
	 * 実行コマンド
	 */
	private static final String KEY_COMMAND = (windows ? "win.knp.command" : "linux.knp.command");
	/**
	 * 実行コマンドオプション
	 */
	private static final String KEY_OPTION = (windows ? "win.knp.option" : "linux.knp.option");
	/**
	 * 作業ディレクトリ
	 */
	private static final String KEY_DIRECTORY = (windows ? "win.knp.directory" : "linux.knp.directory");
	/**
	 * 文字コード
	 */
	private static final String KEY_ENCODE = (windows ? "win.knp.encode" : "linux.knp.encode");
	/**
	 * End Of Stream
	 */
	private static final String EOS = "EOS";
	/**
	 * 品詞マッピング
	 */
	private static final HashMap<String, String> partsOfSpeechMap = new HashMap<String, String>();
	
	static {
		partsOfSpeechMap.put("特殊", "other");
		partsOfSpeechMap.put("特殊-句点", "other");
		partsOfSpeechMap.put("特殊-読点", "other");
		partsOfSpeechMap.put("特殊-括弧始", "other");
		partsOfSpeechMap.put("特殊-括弧終", "other");
		partsOfSpeechMap.put("特殊-記号", "other");
		partsOfSpeechMap.put("特殊-空白", "other");
		partsOfSpeechMap.put("動詞", "verb");
		partsOfSpeechMap.put("形容詞", "adjective");
		partsOfSpeechMap.put("判定詞", "other");
		partsOfSpeechMap.put("助動詞", "other");
		partsOfSpeechMap.put("名詞", "noun.other");
		partsOfSpeechMap.put("名詞-普通名詞", "noun.other");
		partsOfSpeechMap.put("名詞-サ変名詞", "noun.other");
		partsOfSpeechMap.put("名詞-固有名詞", "noun.other");
		partsOfSpeechMap.put("名詞-地名", "noun.other");
		partsOfSpeechMap.put("名詞-人名", "noun.other");
		partsOfSpeechMap.put("名詞-組織名", "noun.other");
		partsOfSpeechMap.put("名詞-数詞", "noun.other");
		partsOfSpeechMap.put("名詞-形式名詞", "noun.other");
		partsOfSpeechMap.put("名詞-副詞的名詞", "noun.other");
		partsOfSpeechMap.put("名詞-時相名詞", "noun.other");
		partsOfSpeechMap.put("指示詞", "other");
		partsOfSpeechMap.put("指示詞-名詞形態指示詞", "other");
		partsOfSpeechMap.put("指示詞-連体詞形態指示詞", "other");
		partsOfSpeechMap.put("指示詞-副詞形態指示詞", "other");
		partsOfSpeechMap.put("副詞", "adverb");
		partsOfSpeechMap.put("副詞-様態副詞", "adverb");
		partsOfSpeechMap.put("副詞-程度副詞", "adverb");
		partsOfSpeechMap.put("副詞-量副詞", "adverb");
		partsOfSpeechMap.put("副詞-頻度副詞", "adverb");
		partsOfSpeechMap.put("副詞-時制相副詞", "adverb");
		partsOfSpeechMap.put("副詞-陳述副詞", "adverb");
		partsOfSpeechMap.put("副詞-評価副詞", "adverb");
		partsOfSpeechMap.put("副詞-発言副詞", "adverb");
		partsOfSpeechMap.put("助詞-格助詞", "other");
		partsOfSpeechMap.put("助詞-副助詞", "other");
		partsOfSpeechMap.put("助詞-接続助詞", "other");
		partsOfSpeechMap.put("助詞-終助詞", "other");
		partsOfSpeechMap.put("接続詞", "other");
		partsOfSpeechMap.put("連体詞", "other");
		partsOfSpeechMap.put("感動詞", "other");
		partsOfSpeechMap.put("接頭辞", "other");
		partsOfSpeechMap.put("接頭辞-名詞接頭辞", "other");
		partsOfSpeechMap.put("接頭辞-動詞接頭辞", "other");
		partsOfSpeechMap.put("接頭辞-イ形容詞接頭辞", "other");
		partsOfSpeechMap.put("接頭辞-ナ形容詞接頭辞", "other");
		partsOfSpeechMap.put("接尾辞-名詞性述語接尾辞", "other");
		partsOfSpeechMap.put("接尾辞-名詞性名詞接尾辞", "other");
		partsOfSpeechMap.put("接尾辞-名詞性名詞助数辞", "other");
		partsOfSpeechMap.put("接尾辞-名詞性特殊接尾辞", "other");
		partsOfSpeechMap.put("接尾辞-形容詞性述語接尾辞", "other");
		partsOfSpeechMap.put("接尾辞-形容詞性名詞接尾辞", "other");
		partsOfSpeechMap.put("接尾辞-動詞性接尾辞", "other");
		partsOfSpeechMap.put("未定義語-その他", "unknown");
		partsOfSpeechMap.put("未定義語-カタカナ", "unknown");
		partsOfSpeechMap.put("未定義語-アルファベット", "unknown");
	}

	/**
	 * Dependencyマッピング
	 */
	private static final HashMap<String, DependencyLabel> dependencyMap = new HashMap<String, DependencyLabel>();
	
	static {
		dependencyMap.put("A", DependencyLabel.APPOSITION);
		dependencyMap.put("D", DependencyLabel.DEPENDENCY);
		dependencyMap.put("P", DependencyLabel.PARALELL);
		dependencyMap.put("O", DependencyLabel.DEPENDENCY);
	}
	/**
	 * knpの実行
	 * @param sentence 係り受け解析文
	 * @return
	 * @throws ProcessFailedException
	 */
	public synchronized static Collection<Chunk> doService(
			String sentence, Properties props
			, int timeoutMillis
			)
	throws ProcessFailedException {
		Collection<Chunk> chunks = new ArrayList<Chunk>();
		ProcessFacade f = new ProcessFacade();
		try {
			String crlf = System.getProperty("line.separator");
			List<String> commands = new ArrayList<String>();
			commands.add(props.getProperty(KEY_COMMAND));
			if (windows) {
				commands.add("/c");
			} else {
				commands.add("-c");
			}
			commands.add(props.getProperty(KEY_OPTION));
			f.builder().command(commands);
			f.builder().directory(new File(props.getProperty(KEY_DIRECTORY)));
			f.start();
			/**
			 * 出力ストリームを取得
			 */
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					f.getOutputStream(), props.getProperty(KEY_ENCODE)));
			bw.write(sentence);
			bw.write(crlf);
			bw.flush();

			/**
			 * 入力ストリームを取得
			 */
			BufferedReader br = new BufferedReader(new InputStreamReader(
					f.getInputStream(), props.getProperty(KEY_ENCODE)));
			String result = null;
			String line = null;
			ArrayList<String> morphemes = new ArrayList<String>();
			int chunkId = 0; // 文節ID
			while((line = br.readLine()) != null) {
				if (line.equals(EOS)) {
					chunks.add(parseLine(sentence, chunkId++, result, morphemes));
					break;
				}
				if (line.indexOf("*") == 0) {
					if (result != null) {
						chunks.add(parseLine(sentence, chunkId++, result, morphemes));
					}
					result = line;
					morphemes.clear();
					continue;
				}
				morphemes.add(line);
			}
		} catch(Exception e){
			_log.error("knp error. " + e.getLocalizedMessage(), e);
			throw new ProcessFailedException(e);
		} finally {
			try{
				f.close(timeoutMillis);
			} catch(InterruptedException e){
				throw new ProcessFailedException(e);
			} catch(IOException e){
				throw new ProcessFailedException(e);
			} catch(TimeoutException e){
				_log.warn("KNP timeout(5000) exceeded. sentence: " + sentence);
				throw new ProcessFailedException(e);
			}
		}
		return chunks;
	}
	/**
	 * 文字列インデックス
	 */
	private static final int INDEX_WORD = 0;
	/**
	 * 原型インデックス
	 */
	private static final int INDEX_LEMMA = 2;
	/**
	 * 係り受けラベルインデックス
	 */
	private static final int INDEX_DEPENDENCY = 1;
	/**
	 * 品詞インデックス大項目
	 */
	private static final int INDEX_PARTOFSPEECH_1 = 3;
	/**
	 * 品詞インデックス小項目
	 */
	private static final int INDEX_PARTOFSPEECH_2 = 5;
	/**
	 * * 3D <文頭><時間><強時間><外の関係><ハ><助詞><体言><係:未格><提題><区切:3-5><RID:1252><格要素><連用要素>
	 *今日 きょう 今日 名詞 6 時相名詞 10 * 0 * 0 "代表表記:今日" <代表表記:今日><品曖><ALT-今日-こんにち-今日-6-10-0-0-"代表表記:今日"><品曖-時相名詞><品曖-その他><文頭><漢字><かな漢字><名詞相当語><自立><タグ単位始><文節始>
	 *は は は 助詞 9 副助詞 2 * 0 * 0 NIL <かな漢字><ひらがな><付属>
	 * @param sentence 入力文字列
	 * @param result 結果行
	 * @param list 形態素行
	 * @return Chunk
	 */
	private static Chunk parseLine(String sentence, int chunkId, String result, ArrayList<String> list) {
		Chunk chunk = new Chunk();
		String[] results = result.split(TAB_SPACE);
		ArrayList<Morpheme> morphemesList = new ArrayList<Morpheme>();
		
		for (String word : list) {
			String[] w = word.split(TAB_SPACE);
			String str = null;
			if(w.length < 6) continue;
			if (w[INDEX_PARTOFSPEECH_2].equals("*")) {
				str = partsOfSpeechMap.get(w[INDEX_PARTOFSPEECH_1]);
			} else {
				str = partsOfSpeechMap.get((w[INDEX_PARTOFSPEECH_1] + "-" + w[INDEX_PARTOFSPEECH_2]));
			}
			if (str == null) {
				str = "unknown";
			}
			Morpheme morpheme = new Morpheme(w[INDEX_WORD], w[INDEX_LEMMA], str);
			morphemesList.add(morpheme);
		}
		Morpheme[] morphemes = new Morpheme[morphemesList.size()];
		morphemesList.toArray(morphemes);
		chunk.setMorphemes(morphemes);
		String dependency = results[INDEX_DEPENDENCY];
		Matcher m = pattern.matcher(dependency);
		m.matches();
		String id = m.group(1);
		String label = m.group(2);
		label = (dependencyMap.get(label) == null) ? "" : dependencyMap.get(label).toString();
		chunk.setDependency(new Dependency(label, id));
		chunk.setChunkId(String.valueOf(chunkId));
		return chunk;
	}
	
	public static void main(String[] arg) {
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();

	    if (loader == null) {
	    	loader = ProcessExec.class.getClassLoader();
	    }
	    URL url = loader.getResource("knp.properties");
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(new File(url.getFile())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			doService("私は歩きます。", props, 5000);
		} catch (ProcessFailedException e) {
			e.printStackTrace();
		}
		try {
			String test = "-100DA000";
			
			Matcher m = Pattern.compile(PATTERN_DEPENDENCY).matcher(test);
			m.matches();
			System.out.println(m.group(1));
			System.out.println(m.group(2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
