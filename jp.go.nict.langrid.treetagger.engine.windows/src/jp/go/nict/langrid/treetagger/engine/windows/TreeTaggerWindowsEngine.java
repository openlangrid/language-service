/*
 * $Id: TreeTaggerWindowsEngine.java 26539 2008-09-19 08:47:01Z nakaguchi $
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
package jp.go.nict.langrid.treetagger.engine.windows;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.it;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.commons.io.FileUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.engine.ProcessFailedException;
import jp.go.nict.langrid.treetagger.engine.Tag;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;
import jp.go.nict.langrid.treetagger.engine.UnsupportedLanguageException;
import jp.go.nict.langrid.treetagger.engine.WorkFileUtil;

/**
 * Windows版TreeTaggerを呼び出すクラス。
 * @author $Author: nakaguchi $
 * @version $Revision: 26539 $
 */
public class TreeTaggerWindowsEngine implements TreeTaggerEngine{
	/**
	 * コンストラクタ。
	 * @param workDirectory 入出力ファイルを作成する一時ディレクトリ
	 * @param language 形態素解析を行う言語
	 */
	public TreeTaggerWindowsEngine(File workDirectory, Language language)
		throws IOException, UnsupportedLanguageException
	{
		workDir = workDirectory;
		engineFile = prepareWindowsEngine(language, workDir);
	}

	/**
	 * 形態素解析を行う。
	 * @param sentence 形態素解析を行う文章
	 * @return 形態素解析結果
	 * @throws IOException 入出力に失敗した
	 */
	public Tag[] tag(String sentence)
	throws ProcessFailedException
	{
		try{
			return doTag(sentence);
		} catch(IOException e){
			throw new ProcessFailedException(e);
		}
	}

	private Tag[] doTag(String sentence)
	throws IOException
	{
		File tempDir = new File(workDir, "temp");
		tempDir.mkdirs();
		File outputFile = FileUtil.createUniqueFile(tempDir, "output");

		/**
		 * 返値
		 */
		List<Tag> tags = new ArrayList<Tag>();

		//形態素解析にかける文字列をtxtfileに入力
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile)));
		bw.write(sentence + nr);
		bw.close();

		String[] arg ={engineFile.toString(), workDir.toString(), outputFile.toString()};

		//Tree_taggerを実行
		Process p = Runtime.getRuntime().exec(
				arg, null
				);

		InputStream is = p.getInputStream();
//		InputStream eis = p.getErrorStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		//Tree_taggerの結果をMorphemeに代入
		String line = null;
		while((line = br.readLine()) != null){
			//文字列代入
			int lin = line.indexOf("\t",0);
			String word = line.substring(0,lin);

			//品詞代入
			int lin2 = line.indexOf("\t",lin+1);
			String partOfSpeech = line.substring(lin,lin2);

			//語頭の空白を削除(品詞)
			for(int k=0;k<partOfSpeech.length();k++){
				Pattern pattern = Pattern.compile("^\\s");
				Matcher matcher = pattern.matcher(partOfSpeech);
				partOfSpeech = matcher.replaceAll("");
			}

			//原型代入
			String original = line.substring(lin2,line.length());

			//語頭の空白を削除(原型)
			for(int k=0;k<original.length();k++){
				Pattern pattern = Pattern.compile("^\\s");
				Matcher matcher = pattern.matcher(original);
				original = matcher.replaceAll("");
			}

			tags.add(new Tag(word, partOfSpeech, original));
		}
/*
		BufferedReader ebr = new BufferedReader(new InputStreamReader(eis));
		String l = null;
		while((l = ebr.readLine()) != null){
			System.out.println("error: " + l);
		}
*/		outputFile.delete();

		return tags.toArray(new Tag[]{});
	}

	private File engineFile;

	private static synchronized File prepareWindowsEngine(
			Language language, File directory)
		throws IOException, UnsupportedLanguageException
	{
		String engineName = engineFiles.get(language);
		if(engineName == null){
			throw new UnsupportedLanguageException(language);
		}

		String resBase = "/"
			+ TreeTaggerWindowsEngine.class.getPackage().getName().replace(".", "/");
		String parBase = resBase + "/parameter";

		String pbin = resBase + "/bin";
		File binDir = new File(directory, "bin");

		File engine = new File(binDir, engineName);
		if(engine.exists()) return engine;

		WorkFileUtil.storeResources(
				TreeTaggerWindowsEngine.class
				, new ArrayList<String>(Arrays.asList(
						pbin + "/tree-tagger.exe"
						, pbin + "/train-tree-tagger.exe"
						, pbin + "/tag-english.bat"
						, pbin + "/tag-french.bat"
						, pbin + "/tag-german.bat"
						, pbin + "/tag-italian.bat"
						, pbin + "/tag-spanish.bat"
						))
				, binDir
				);

		String pcmd = resBase + "/cmd";
		File cmdDir = new File(directory, "cmd");
		WorkFileUtil.storeResources(
				TreeTaggerWindowsEngine.class
				, new ArrayList<String>(Arrays.asList(
						pcmd + "/mwl-lookup.perl"
						, pcmd + "/tokenize.pl"
						))
				, cmdDir
				);

		String plib = resBase + "/lib";
		File libDir = new File(directory, "lib");
		WorkFileUtil.storeResources(
				TreeTaggerWindowsEngine.class
				, new ArrayList<String>(Arrays.asList(
						plib + "/english-abbreviations"
						, plib + "/french-abbreviations"
						, plib + "/german-abbreviations"
						, plib + "/italian-abbreviations"
						, plib + "/spanish-abbreviations"
						, plib + "/spanish-mwls"
						, parBase + "/english.par"
						, parBase + "/french.par"
						, parBase + "/german.par"
						, parBase + "/italian.par"
						, parBase + "/spanish.par"
						))
				, libDir
				);

		return engine;
	}

	private File workDir;
	private static final String nr = "\r\n";
	private static Map<Language, String> engineFiles = new HashMap<Language, String>();
	static{
		engineFiles.put(en, "tag-english.bat");
		engineFiles.put(de, "tag-german.bat");
		engineFiles.put(it, "tag-italian.bat");
		engineFiles.put(es, "tag-spanish.bat");
		engineFiles.put(fr, "tag-french.bat");
//		enginefiles.put(bg, "tag-bulgarian.bat");
	}
}
