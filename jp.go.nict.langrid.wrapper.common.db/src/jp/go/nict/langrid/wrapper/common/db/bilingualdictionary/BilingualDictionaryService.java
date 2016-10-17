/*
 * $Id: BilingualDictionaryService.java 409 2011-08-25 03:12:59Z t-nakaguchi $
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
package jp.go.nict.langrid.wrapper.common.db.bilingualdictionary;

import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.COMPLETE;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PARTIAL;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PREFIX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.SUFFIX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.REGEX;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.commons.ws.param.ServiceContextParameterContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.transformer.CodeStringToLanguageTransformer;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TranslationWithPosition;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.ConnectionParameters;
import jp.go.nict.langrid.wrapper.common.db.DataBaseUtil;
import jp.go.nict.langrid.wrapper.common.db.DbParameterLoader;
import jp.go.nict.langrid.wrapper.common.db.DictionaryDataBase;
import jp.go.nict.langrid.wrapper.common.util.LanguageCodeUtil;
import jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.AbstractBilingualDictionaryWithLongestMatchSearchService;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author Kohei Kadowaki
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
public class BilingualDictionaryService
extends AbstractBilingualDictionaryWithLongestMatchSearchService
{
	/**
	 * 
	 * 
	 */
	public BilingualDictionaryService() {
		init();
	}

	public BilingualDictionaryService(ServiceContext serviceContext){
		super(serviceContext);
		init();
	}


	private void init(){
		ParameterContext pc = new ServiceContextParameterContext(getServiceContext());
		try{
			ParameterLoader pl = new DbParameterLoader();
			pl.load(this, pc);
			pl.load(conParams, pc);
		} catch(TransformationException e){
			setStartupException(e);
		} catch(ParameterRequiredException e){
			setStartupException(e);
		}
		if(languageColumnNames != null){
			setLanguages(languageColumnNames);
		}
		if(supportedMatchingMethods != null){
			setSupportedMatchingMethods(new HashSet<MatchingMethod>(
					Arrays.asList(supportedMatchingMethods)));
		} else{
			setSupportedMatchingMethods(MINIMUM_MATCHINGMETHODS);
		}
		initDb();
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

	public void setUseQCAlgorithm(boolean useQCAlgorithm){
		this.useQCAlgorithm = useQCAlgorithm;
	}

	@Override
	protected Collection<Translation> doSearch(Language headLang,
			Language targetLang, String headword, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		checkConnectionValid();
		try {
			
			headLang   = LanguageCodeUtil.substituteLanguage(headLang);
			targetLang = LanguageCodeUtil.substituteLanguage(targetLang);
			
			DictionaryDataBase ddb = new DictionaryDataBase(
					tableName, manager
					, conParams.dbDictionary, getMaxResults()
					);
			Collection<Translation> ret = ddb.getTranslation(
					LanguageCodeUtil.substituteLanguage(headLang), 
					LanguageCodeUtil.substituteLanguage(targetLang)
					, headword, matchingMethod
					);
			return ret;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "failed to sql execute: " , e);
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected Collection<TranslationWithPosition> doSearchLongestMatchingTerms(
			Language headLang, Language targetLang, Morpheme[] morphemes)
			throws InvalidParameterException, ProcessFailedException {
		if(useQCAlgorithm){
			DictionaryDataBase ddb = new DictionaryDataBase(
					tableName, manager
					, conParams.dbDictionary, getMaxResults()
					);
			try{
				return QueryCentricSearch.search(ddb, headLang, targetLang, morphemes
						, isCacheSearchResult());
			} catch(SQLException e){
				throw new ProcessFailedException(e);
			}
		} else{
			return super.doSearchLongestMatchingTerms(headLang, targetLang, morphemes);
		}
	}

	@Override
	protected Calendar doGetLastUpdate() throws ProcessFailedException {
		checkConnectionValid();
		Date date = DataBaseUtil.getLastUpdate(
				tableName, dateColumnName, manager);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
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

	private void checkConnectionValid() throws ProcessFailedException{
		if(manager == null){
			throw new ProcessFailedException("failed to connect database.");
		}
	}

	private ConnectionParameters conParams = new ConnectionParameters();

	private ConnectionManager manager;

	@Parameter
	private String tableName;

	@Parameter
	private String[] languageColumnNames;

	@Parameter(defaultValue="date")
	private String dateColumnName = "date";

	@Parameter(defaultValue="COMPLETE PARTIAL PREFIX SUFFIX REGEX")
	private MatchingMethod[] supportedMatchingMethods = {
			COMPLETE, PARTIAL, PREFIX, SUFFIX, REGEX
			};

	@Parameter(defaultValue="true")
	private boolean useQCAlgorithm = true;

	private static Logger logger = Logger.getLogger(
			BilingualDictionaryService.class.getName()
			);
}
