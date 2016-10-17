/*
 * $Id: ICTCLAS.java 28009 2010-08-09 10:25:33Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.ictclas;

import static jp.go.nict.langrid.language.IANALanguageTags.zh_Hans;
import static jp.go.nict.langrid.language.LangridLanguageTags.zh_CN;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.zh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.lang.ExceptionUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.wrapper.common.ContentTracer;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.IniFunctions;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_Content;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_Resource;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_ServiceDescription;
import jp.go.nict.langrid.wrapper.common.util.WorkFileUtil;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

/**
 * University of pennsylvania 提供のhindi語の形態素解析
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 28009 $
 */
public class ICTCLAS extends AbstractMorphologicalAnalysisService {
	/**
	 * コンストラクタ。
	 */
	public ICTCLAS() {
		Set<Language> langs = new HashSet<Language>(
				IniFunctions.integrateLanguages(sd)
				);
		langs.add(zh);
		langs.add(zh_CN);
		setSupportedLanguageCollection(langs);
		if(exceptionFlag != null){
			setStartupException(exceptionFlag);
		}
	}

	@Override
	protected Collection<jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme> doAnalyze(
			Language language, String text)
	throws InvalidParameterException, ProcessFailedException
	{
		// 対応言語コード判定
		if (languageMap.get(language) == null) {
			throw new UnsupportedLanguageException("language", language.toString());
		}
		
		language = zh_Hans;

		// 返値を格納する変数
		Collection<Morpheme> rtn = new ArrayList<Morpheme>();

		// ServiceDescriptionから、各種変数を取得
		Webapp_Resource pair = sd.getResources(language);
		Webapp_Content content = pair.getContent();

		// 実行ファイルを実行
		String result;

		try {
			File engineFile = prepareEngine(getWorkFile("engine"));
			ProcessBuilder pb = new ProcessBuilder(
					engineFile.getAbsolutePath());
			pb.directory(engineFile.getParentFile());
			Process p = pb.start();
			//　引数として分解対象の文字列を入力
			OutputStream os = p.getOutputStream();
			try{
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						os,"GB2312"));
				bw.write(text+"\n");
				bw.flush();

				InputStream is = p.getInputStream();
				BufferedReader brerr = new BufferedReader(new InputStreamReader(
						is,"GB2312"));

				//　１行の結果のみに対応
				result = brerr.readLine();

				// デバッグ用
				//System.out.println(result);
			} finally{
				os.close();
				p.waitFor();
			}
		} catch (Exception e) {
			throw new ProcessFailedException(
					ExceptionUtil.getMessageWithStackTrace(e)
					);
		}

		//コンテンツから、形態素が順番に全て文字列で格納されたコレクションを得る		
		Iterator<?> itr = ContentTracer.trimRegexMorphological(
				result
				, content.getScraper().getRegexes()
				, content.getScraper().getIgnores()
				).iterator();

		while(itr.hasNext()){
			String[] aPart = (String[])itr.next();
			String word = aPart[0];
			String lemma = aPart[1];
			String pos = aPart[2];
			if(lemma.length() == 0){
				lemma = word;
			}
			rtn.add(new Morpheme(
					word, lemma
					, MorphoLib.strToPartOfSpeech(pos, pair.getPartOfSpeech())
					));
		}

		return rtn;
	}

	/**
	 * Hamのengineの実装を流用
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	private static synchronized File prepareEngine(File directory)
		throws IOException, ServiceConfigurationException
	{
		if(System.getProperty("os.name").indexOf("Windows") != -1){
			throw new ServiceConfigurationException(
					"This wrapper of ICTCLAS runs only on linux."
					);
		}

		String engineName = "ictclasbin";
		File engine = new File(directory, engineName);
		if(engine.exists()) return engine;

		// ICTCLASの実行に必要なリソース
		List<String> resources = new ArrayList<String>(Arrays.asList(new String[]{
				engineName
				, "BigramDict.dct", "coreDict.dct", "lexical.ctx"
				, "nr.ctx", "nr.dct", "ns.ctx"
				, "ns.dct", "tr.ctx", "tr.dct"}));

		WorkFileUtil.storeResources(ICTCLAS.class, resources, directory);
		try{
			Runtime.getRuntime().exec(
					"chmod ugo+x " + engine.getAbsolutePath()
					).waitFor();
		} catch(Throwable t){
			logger.log(Level.WARNING, "failed to add execution flag.", t);
		}
		return engine;
	}

	static{
		// .iniファイルから設定を読み込み
		try {
			sd = new Webapp_ServiceDescription(
					ICTCLAS.class.getResourceAsStream("resource.ini"));
		} catch (ProcessFailedException e) {
			exceptionFlag = e;
		}
	}

	/**
	 * .iniファイルに記述された設定を格納します．
	 */
	private static Webapp_ServiceDescription sd;

	/**
	 *static節内で発生した例外のフラグです．
	 */
	private static ProcessFailedException exceptionFlag;

	private static Logger logger = Logger.getLogger(ICTCLAS.class.getName());
	
	private static Map<Language, Language> languageMap = new HashMap<Language, Language>();
	
	// 翻訳対応言語コード
	static {
		languageMap.put(zh, zh_CN);
		languageMap.put(zh_Hans, zh_CN);
		languageMap.put(zh_CN, zh_CN);
	}
}
