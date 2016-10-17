/*
 * $Id: JTreeTaggerProcessor.java 27012 2009-02-27 06:31:12Z kamiya $
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
package jp.go.nict.langrid.wrapper.chasen;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.adjective;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.adverb;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun_common;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun_other;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun_proper;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.other;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.unknown;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.verb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.process.ProcessFacade;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

public class Chasen extends AbstractMorphologicalAnalysisService {
	public Chasen() {
		setChasenPath(getInitParameterString("chasenPath", "/usr/local/bin/chasen"));
		setChasenEncoding(getInitParameterString("chasenEncoding", "EUC-JP"));
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	public void setChasenPath(String chasenPath) {
		this.chasenPath = chasenPath;
	}

	public void setChasenEncoding(String chasenEncoding) {
		this.chasenEncoding = chasenEncoding;
		clEncoding = "e";
		try{
			Charset cs = Charset.forName(chasenEncoding);
			if(cs.equals(Charset.forName("EUC-JP"))){
				clEncoding = "e";
			} else if(cs.equals(Charset.forName("UTF-8"))){
				clEncoding = "w";
			} else if(cs.equals(Charset.forName("Shift-JIS"))){
				clEncoding = "s";
			} else if(cs.equals(Charset.forName("Windows-31J"))){
				clEncoding = "s";
			}
		} catch(IllegalArgumentException e){
		}
	}

	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		ProcessFacade p = new ProcessFacade(chasenPath, "-i", clEncoding);
		try{
			p.start();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					p.getOutputStream(), chasenEncoding));
			bw.write(text);
			bw.close();
			p.waitFor(getMaxWaitMillisForExternalProcess());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					p.getInputStream(), chasenEncoding));

			ArrayList<Morpheme> morphemes = new ArrayList<Morpheme>();

			String str = null;
			while((str = br.readLine()) != null){
				if(str.equals("EOS")) break;

				String[] analyzedMorpheme = str.split("\\t");
				String word = analyzedMorpheme[0];
				String lemma = analyzedMorpheme[2];
				if(lemma.equals("")) lemma = word;
				PartOfSpeech partOfSpeech = determinePartOfSpeech(analyzedMorpheme[3].split("-"));
				morphemes.add(new Morpheme(word,lemma,partOfSpeech));
			}
			return morphemes;
		} catch(TimeoutException e){
			String message = "process timed out: " + getMaxWaitMillisForExternalProcess();
			logger.warning(message + "  language: " + language + "  text: " + text);
			throw new ProcessFailedException(message);
		} catch (IOException e){
			logger.log(Level.SEVERE, "Wrapper Error: Process cannot start", e);
			throw new ProcessFailedException(e);
		} catch (InterruptedException e){
			logger.log(Level.SEVERE, "Wrapper Error: Process has been interrupted", e);
			throw new ProcessFailedException(e);
		} finally{
			try{
				p.close(1000);
			} catch(TimeoutException e){
				throw new ProcessFailedException(e);
			} catch(InterruptedException e){
				throw new ProcessFailedException(e);
			} catch(IOException e){
				throw new ProcessFailedException(e);
			}
		}
	}

	private static PartOfSpeech determinePartOfSpeech(String[] posInfo){
		PartOfSpeech ret = posMap.get(posInfo[0]);
		if(ret != null) return ret;
		if(posInfo.length > 1){
			ret = posDetailMap.get(posInfo[1]);
			if(ret != null) return ret;
			ret = posOtherMap.get(posInfo[0]);
			if(ret != null) return ret;
		}
		return other;
	}
	
	private String chasenPath = "/usr/local/bin/chasen";
	private String chasenEncoding = "EUC-JP";
	private String clEncoding = "e";
	
	private static Map<String, PartOfSpeech> posMap = new HashMap<String, PartOfSpeech>();
	private static Map<String, PartOfSpeech> posDetailMap = new HashMap<String, PartOfSpeech>();
	private static Map<String, PartOfSpeech> posOtherMap = new HashMap<String, PartOfSpeech>();
	private static Logger logger = Logger.getLogger(Chasen.class.getName());
	static{
		posMap.put("未知語", unknown);
		posMap.put("動詞", verb);
		posMap.put("形容詞", adjective);
		posMap.put("副詞", adverb);
		posDetailMap.put("一般", noun_common);
		posDetailMap.put("副詞可能", noun_common);
		posDetailMap.put("サ変接続", noun_other);
		posDetailMap.put("形容動詞語幹", noun_common);
		posDetailMap.put("固有名詞", noun_proper);
		posDetailMap.put("代名詞", noun_other);
		posDetailMap.put("数", noun_other);
		posDetailMap.put("接尾", noun_other);
		posDetailMap.put("非自立", noun_other);
		posOtherMap.put("名詞", noun_other);
		posOtherMap.put("指示詞", other);
		posOtherMap.put("接尾辞", other);
	}
}
