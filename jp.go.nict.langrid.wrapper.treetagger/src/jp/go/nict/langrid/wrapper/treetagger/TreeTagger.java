/*
 * $Id: TreeTagger.java 29283 2012-03-29 07:23:12Z kamiya $
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
package jp.go.nict.langrid.wrapper.treetagger;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.bg;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.es;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.fr;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.it;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.nl;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.pt;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ru;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.unknown;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.lang.ExceptionUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.treetagger.engine.Tag;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;
import jp.go.nict.langrid.treetagger.engine.UnsupportedLanguageException;
import jp.go.nict.langrid.treetagger.engine.linux.command.TreeTaggerLinuxCommandEngine;
import jp.go.nict.langrid.wrapper.common.util.posmap.GlobPosMapper;
import jp.go.nict.langrid.wrapper.common.util.posmap.PosMapper;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

/**
 * TreeTaggerのラッパーです。
 * @author $Author: kamiya $
 * @version $Revision: 29283 $
 */
public class TreeTagger
extends AbstractMorphologicalAnalysisService
{
	/**
	 * コンストラクタ。
	 */
	public TreeTagger() {
		super.setSupportedLanguageCollection(Arrays.asList(
				bg, nl, en, de, it, fr, ru, es, pt));
		treeTaggerHome = new File(getInitParameterString(
			"treeTaggerHome", "/usr/local/treetagger"
			));
	}

	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
	throws InvalidParameterException, ProcessFailedException
	{
		if(bootupException != null){
			throw bootupException;
		}
		if(engines.size() == 0) {
			init();
		}
		
		try{
			TreeTaggerEngine engine = getEngine(language);
			if(engine == null){
				throw new jp.go.nict.langrid.service_1_2.UnsupportedLanguageException(
						"language", language.getCode());
			}

			PosMapper posMap = langPosMapMap.get(language);
			if(posMap == null){
				throw new ProcessFailedException(
						"posmap not found for " + language);
			}

			Tag[] tags = null;
			try{
				tags = engine.tag(text);
			} catch(jp.go.nict.langrid.treetagger.engine.ProcessFailedException e){
				if(engine instanceof TreeTaggerClientEngine){
					logger.log(Level.SEVERE
							, "failed to invoke client engine. switching to command engine."
							, e);
					engine = newLinuxCommandEngine(
							treeTaggerHome, getWorkDirectory()
							, language);
					tags = engine.tag(text);
				} else{
					throw e;
				}
			}
			List<Morpheme> morphemes = new ArrayList<Morpheme>();
			for(Tag t : tags){
				if(t.lemma.equals("@card@")){
					t.lemma = t.word;
				} else if(t.lemma.endsWith("@ord@")){
					t.lemma = t.word;
				}
				PartOfSpeech pos = posMap.get(t.tag);
				if(pos != null){
					morphemes.add(new Morpheme(t.word, t.lemma, pos));
				} else{
					morphemes.add(new Morpheme(t.word, t.lemma, unknown));
				}
			}
			return morphemes;
		} catch(Throwable t){
			logger.log(Level.SEVERE, "failed to execute process.", t);
			throw new ProcessFailedException(t);
		}
	}

	private void init(){
		File work = getWorkDirectory();
		for(Language l : getSupportedLanguageCollection()){
			String engine = engineCommand.containsKey(l.getCode() + "-engine")
				? engineCommand.get(l.getCode() + "-engine")
				: "command";
			try{
				if(engine.equals("command")){
					engines.put(l, newLinuxCommandEngine(
							treeTaggerHome, work, l));
					logger.info(l + ": command");
				} else{
					String[] values = engine.split(":");
					if(values[0].equals("server")){
						int port = Integer.parseInt(values[1]);
						engines.put(l, new TreeTaggerClientEngine(
								l, port));
					}
					logger.info(l + ": server");
				}
			} catch(UnsupportedLanguageException e){
				logger.warning("failed to initialize engine for language: "
						+ l + "  type: " + engine);
			} catch(NumberFormatException e){
				logger.warning("failed to initialize engine for language: "
						+ l + "  type: " + engine);
			}
		}
	}

	private TreeTaggerEngine newLinuxCommandEngine(
			File treeTaggerHome, File workDirectory
			, Language language)
	throws UnsupportedLanguageException
	{
		return new TreeTaggerLinuxCommandEngine(
				treeTaggerHome, workDirectory, language);
	}

	public void setTreeTaggerHome(File treeTaggerHome) {
		this.treeTaggerHome = treeTaggerHome;
	}
	
	public void setEngineCommand(Map<String, String> engineCommand) {
		this.engineCommand = engineCommand;
	}

	private synchronized TreeTaggerEngine getEngine(Language lang){
		if(!initialized){
			init();
			initialized = true;
		}
		return engines.get(lang);
	}

	private boolean initialized;
	private File treeTaggerHome;
	private Map<String, String> engineCommand = new HashMap<String, String>();
	private static Map<Language, PosMapper> langPosMapMap = new HashMap<Language, PosMapper>();
	private static Map<Language, TreeTaggerEngine> engines = new HashMap<Language, TreeTaggerEngine>();
	private static ProcessFailedException bootupException = null;
	private static Logger logger = Logger.getLogger(TreeTagger.class.getName());

	static{
		try{
			Class<TreeTagger> clazz = TreeTagger.class;
			langPosMapMap.put(bg, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/bulgarian.posmap.glob")));
			langPosMapMap.put(nl, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/dutch.posmap.glob")));
			langPosMapMap.put(en, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/english.posmap.glob")));
			langPosMapMap.put(fr, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/french.posmap.glob")));
			langPosMapMap.put(de, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/german.posmap.glob")));
			langPosMapMap.put(it, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/italian.posmap.glob")));
			langPosMapMap.put(ru, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/russian.posmap.glob")));
			langPosMapMap.put(es, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/spanish.posmap.glob")));
			langPosMapMap.put(pt, new GlobPosMapper(
					clazz.getResourceAsStream("/posmap/portugues.posmap.glob")));
		} catch(IOException e){
			logger.log(Level.SEVERE, "failed to boot service.", e);
			bootupException	= new ProcessFailedException(
					ExceptionUtil.getMessageWithStackTrace(e)
					);
		}
	}
}
