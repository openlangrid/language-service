/*
 * $Id: TreeTaggerLinuxCommandEngine.java 27492 2010-04-13 05:09:11Z Takao Nakaguchi $
 *
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
package jp.go.nict.langrid.treetagger.engine.linux.command;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.bg;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.it;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.nl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ru;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.io.FileUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.engine.ProcessFailedException;
import jp.go.nict.langrid.treetagger.engine.Tag;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;
import jp.go.nict.langrid.treetagger.engine.UnsupportedLanguageException;

public class TreeTaggerLinuxCommandEngine
implements TreeTaggerEngine
{
	public TreeTaggerLinuxCommandEngine(
			File treeTaggerHome, File workDirectory, Language language)
	throws UnsupportedLanguageException
	{
		this.workDirectory = workDirectory;
		this.command = 
			treeTaggerHome + "/cmd/tree-tagger-"
			+ getLanguageString(language); 
	}

	public Tag[] tag(String sentence)
	throws ProcessFailedException
	{
		File tempDir = workDirectory;
		if(!tempDir.exists()){
			if(!tempDir.mkdirs()){
				throw new ProcessFailedException(
						"failed to create temporary directory: "
						+ tempDir);
			}
		}
		File inputFile = null;
		try{
			inputFile = FileUtil.createUniqueFile(tempDir, "input");
			writeInputFile(inputFile, sentence);
			return runTreeTagger(inputFile);
		} catch(InterruptedException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		} catch(IOException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		} finally{
			if(inputFile != null) inputFile.delete();
		}
	}

	/**
	 * 解析する言語を，TreeTagger用の文字列に変換する
	 * @param argLanguage 入力言語
	 * @return TreeTagger用言語コード
	 * @throws UnsupportedLanguageException TreeTaggerが扱えない言語が指定されてた
	 */
	private String getLanguageString(Language language)
	throws UnsupportedLanguageException{
		String l = languages.get(language);
		if(l != null){
			return l;
		} else{
			throw new UnsupportedLanguageException(language);
		}
	}

	/**
	 * 入力ファイルを作成する
	 * @param inputFile 入力ファイル
	 * @param contents 書き込む内容
	 * @throws IOException ファイルの入出力で例外が発生した
	 */
	private void writeInputFile(File inputFile, String sentence)
	throws IOException{
		OutputStream fout = new FileOutputStream(inputFile);
		try{
			Writer w = new OutputStreamWriter(fout, ENCODING);
			w.write(sentence);
			w.close();
		} finally{
			fout.close();
		}
	}

	/**
	 * TreeTaggerを実行する
	 * @param scriptFile TreeTagger用スクリプトファイル
	 * @throws IOException 入出力で例外が発生した
	 * @throws InterruptedException 
	 */
	private Tag[] runTreeTagger(File inputFile)
	throws InterruptedException, IOException
	{
		List<Tag> ret = new ArrayList<Tag>();
		Process process = null;
		try{
			process = new ProcessBuilder(
					command
					, inputFile.getAbsolutePath()
					).start();
			InputStream in = process.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, ENCODING));
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				StringTokenizer tokens = new StringTokenizer(line, "\t");
				if(tokens.countTokens() != 3) {
					logger.warning("invalid tokens: " + line);
					continue;
				}
				String word = tokens.nextToken();
				String tag = tokens.nextToken();
				String lemma = tokens.nextToken();
				if (lemma.equals("<unknown>")) {
					lemma = word;
				}				
				ret.add(new Tag(word, tag, lemma));
			}
			return ret.toArray(new Tag[]{});
		} finally{
			if(process != null){
				process.destroy();
			}
		}
	}

	private File workDirectory;
	private String command;

	private static final String ENCODING = "UTF-8";
	private static Map<Language, String> languages
		= new HashMap<Language, String>();
	private static Logger logger = Logger.getLogger(
			TreeTaggerLinuxCommandEngine.class.getName());
	static{
		languages.put(bg, "bulgarian");
		languages.put(de, "german");
		languages.put(en, "english");
		languages.put(es, "spanish");
		languages.put(fr, "french");
		languages.put(it, "italian");
		languages.put(nl, "dutch");
		languages.put(pt, "portuguese");
		languages.put(ru, "russian");
	}
}
