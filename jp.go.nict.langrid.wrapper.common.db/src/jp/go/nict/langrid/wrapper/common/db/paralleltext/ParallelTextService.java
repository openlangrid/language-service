/*
 * $Id: ParallelTextService.java 409 2011-08-25 03:12:59Z t-nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2009 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.common.db.paralleltext;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.COMPLETE;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PARTIAL;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PREFIX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.REGEX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.SUFFIX;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import jp.go.nict.langrid.commons.parameter.ParameterContext;
import jp.go.nict.langrid.commons.parameter.ParameterLoader;
import jp.go.nict.langrid.commons.parameter.ParameterRequiredException;
import jp.go.nict.langrid.commons.parameter.annotation.Parameter;
import jp.go.nict.langrid.commons.transformer.StringToEnumTransformer;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.ws.param.ServiceContextParameterContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.transformer.CodeStringToLanguageTransformer;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.ConnectionParameters;
import jp.go.nict.langrid.wrapper.common.db.DbParameterLoader;
import jp.go.nict.langrid.wrapper.common.db.DictionaryDataBase;
import jp.go.nict.langrid.wrapper.common.util.LanguageCodeUtil;
import jp.go.nict.langrid.wrapper.ws_1_2.paralleltext.AbstractParallelTextService;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author Kohei Kadowaki
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
public class ParallelTextService extends AbstractParallelTextService {
	/**
	 * 
	 * 
	 */
	public ParallelTextService() {
		// servlet-config.wsddからDBの情報を取得
		ParameterContext pc = new ServiceContextParameterContext(getServiceContext());

		try{
			ParameterLoader pl = new DbParameterLoader();
			pl.load(conParams, pc);
			pl.load(this, pc);
		} catch(TransformationException e){
			setStartupException(e);
		} catch(ParameterRequiredException e){
			setStartupException(e);
		}

		Collection<LanguagePair> languages = new ArrayList<LanguagePair>();
		if(languageColumnNames != null){
			setLanguages(languageColumnNames);
		}
		setSupportedLanguagePairs(languages);
		if(supportedMatchingMethods != null){
			setSupportedMatchingMethods(new HashSet<MatchingMethod>(
					Arrays.asList(supportedMatchingMethods)));
		} else{
			setSupportedMatchingMethods(MINIMUM_MATCHINGMETHODS);
		}
	}

	private void initDb(){
		if(conParams.driverName == null && conParams.jndiDataSourceName == null) return;
		try{
			manager = new ConnectionManager(conParams);
		} catch(NamingException e){
			setStartupException(e);
			logger.log(Level.SEVERE, "failed to initialize database connection.", e);
		}
	}

	public void setConnectionParameters(ConnectionParameters conParams) {
		this.conParams = conParams;
		initDb();
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public void setLanguageColumnNames(String languageColumnNames){
		this.languageColumnNames = languageColumnNames.split("\\s");  
		setLanguages(this.languageColumnNames);
	}

	public void setMatchingMethods(
			String matchingMethods
			) {
		this.supportedMatchingMethods = ArrayUtil.collect(
				matchingMethods.split(" ")
				, MatchingMethod.class
				, new StringToEnumTransformer<MatchingMethod>(MatchingMethod.class)
				);
		setSupportedMatchingMethods(new HashSet<MatchingMethod>(
				Arrays.asList(this.supportedMatchingMethods)));
	}

	public void setDateColumnName(String dateColumnName){
		this.dateColumnName = dateColumnName;
	}

	protected void setLanguages(String[] languages){
		this.languageColumnNames = languages;
		Collection<LanguagePair> langs = new ArrayList<LanguagePair>();
		LanguagePairUtil.addBidirectionalRoundrobinformedPairs(
				langs
				, ArrayUtil.collect(
						languageColumnNames
						, new CodeStringToLanguageTransformer())
				);
		setSupportedLanguagePairs(langs);
	}

	@Override
	protected Collection<ParallelText> doSearch(Language headLang,
			Language targetLang, String headword, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		checkConnectionValid();
		try {
			headLang   = LanguageCodeUtil.substituteLanguage(headLang);
			targetLang = LanguageCodeUtil.substituteLanguage(targetLang);
			
			DictionaryDataBase dictionary = new DictionaryDataBase(
					tableName
					, manager, conParams.dbDictionary
					, getMaxResults()
					);
			Collection<ParallelText> parallelTexts = dictionary.getParallelText(
					headLang, targetLang
					, headword, matchingMethod
					);
long t0, t1;
t0 = System.currentTimeMillis();
			// TODO: とってもアドホックだ、条件振り分け付きのTransformerがあれば、、、
			if (ja.matches(headLang)) {
				for (ParallelText pt : parallelTexts) {
					pt.setSource(StringScrapingJa.scrape(pt.getSource()));
				}
			}
			if (ja.matches(targetLang)) {
				for (ParallelText pt : parallelTexts) {
					pt.setTarget(StringScrapingJa.scrape(pt.getTarget()));
				}
			}
t1 = System.currentTimeMillis();
//logger.log(Level.INFO, "StringScraping.scrape process time is " + (t1 - t0) + "ms.");
			return parallelTexts;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ProcessFailedException(e);
		}
	}

	private void checkConnectionValid() throws ProcessFailedException{
		if(manager == null){
			throw new ProcessFailedException("failed to connect database.");
		}
	}

	/**
	 * 
	 * 
	 */
	private ConnectionManager manager;

	private ConnectionParameters conParams = new ConnectionParameters();
	/**
	 * 
	 * 
	 */
	@Parameter()
	public String tableName;

	/**
	 * 
	 * 
	 */
	@Parameter()
	public String[] languageColumnNames;

	/**
	 * 
	 * 
	 */
	@Parameter(defaultValue="date")
	public String dateColumnName;

	@Parameter(defaultValue="COMPLETE PARTIAL PREFIX SUFFIX REGEX")
	private MatchingMethod[] supportedMatchingMethods = {COMPLETE, PARTIAL, PREFIX, SUFFIX, REGEX};

	private static Logger logger = Logger.getLogger(
			ParallelTextService.class.getName()
			);
}
