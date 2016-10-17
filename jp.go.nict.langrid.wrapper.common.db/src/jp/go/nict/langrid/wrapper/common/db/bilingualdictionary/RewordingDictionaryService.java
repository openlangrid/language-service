/*
 * $Id: RewordingDictionaryService.java 409 2011-08-25 03:12:59Z t-nakaguchi $
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

import java.sql.SQLException;
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
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.commons.ws.param.ServiceContextParameterContext;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
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
public class RewordingDictionaryService
extends AbstractBilingualDictionaryWithLongestMatchSearchService{
	public static enum Direction{
		SPECIAL_TO_GENERAL,
		GENERAL_TO_SPECIAL
	}
	/**
	 * 
	 * 
	 */
	public RewordingDictionaryService() {
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
		try{
			manager = new ConnectionManager(conParams);
		} catch(NamingException e){
			setStartupException(e);
			logger.log(Level.SEVERE, "failed to initialize database connection.", e);
		}
		Language l = null;
		try{
			l = new Language(language);
		} catch(InvalidLanguageTagException e){
			throw new RuntimeException(e);
		}
		setSupportedLanguagePairs(Arrays.asList(new LanguagePair(l, l)));
		if(supportedMatchingMethods != null){
			setSupportedMatchingMethods(new HashSet<MatchingMethod>(
					Arrays.asList(supportedMatchingMethods)));
		} else{
			setSupportedMatchingMethods(MINIMUM_MATCHINGMETHODS);
		}
	}

	@Override
	protected Collection<Translation> doSearch(Language headLang,
			Language targetLang, String headword, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		checkConnectionValid();
		try {
			headLang   = LanguageCodeUtil.substituteLanguage(headLang);
			targetLang = LanguageCodeUtil.substituteLanguage(targetLang);
			Pair<Language, Language> pair = getInternalLanguage(headLang, targetLang);

			DictionaryDataBase ddb = new DictionaryDataBase(
					tableName, manager
					, conParams.dbDictionary, getMaxResults()
					);
			Collection<Translation> ret = ddb.getTranslation(
					pair.getFirst(), pair.getSecond()
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
		headLang   = LanguageCodeUtil.substituteLanguage(headLang);
		targetLang = LanguageCodeUtil.substituteLanguage(targetLang);
		Pair<Language, Language> pair = getInternalLanguage(headLang, targetLang);
		if(useQCAlgorithm){
			DictionaryDataBase ddb = new DictionaryDataBase(
					tableName, manager
					, conParams.dbDictionary, getMaxResults()
					);
			try{
				return QueryCentricSearch.search(
						ddb, pair.getFirst(), pair.getSecond()
						, morphemes, isCacheSearchResult());
			} catch(SQLException e){
				throw new ProcessFailedException(e);
			}
		} else{
			return super.doSearchLongestMatchingTerms(
					pair.getFirst(), pair.getSecond(), morphemes);
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

	private void checkConnectionValid() throws ProcessFailedException{
		if(manager == null){
			throw new ProcessFailedException("failed to connect database.");
		}
	}

	private Pair<Language, Language> getInternalLanguage(Language headLang, Language targetLang)
	throws ProcessFailedException{
		try{
			if(direction.equals(Direction.SPECIAL_TO_GENERAL)){
				headLang = new Language("x-" + headLang.getCode() + "-special");
				targetLang = new Language("x-" + targetLang.getCode() + "-general");
			} else{
				headLang = new Language("x-" + headLang.getCode() + "-general");
				targetLang = new Language("x-" + targetLang.getCode() + "-special");
			}
			return Pair.create(headLang, targetLang);
		} catch(InvalidLanguageTagException e){
			throw new ProcessFailedException(e);
		}
	}

	private ConnectionParameters conParams = new ConnectionParameters();

	private ConnectionManager manager;

	@Parameter(required=true)
	private String tableName;

	@Parameter(required=true)
	private String language;

	@Parameter(defaultValue="date")
	private String dateColumnName;

	@Parameter(required=true)
	private Direction direction;

	@Parameter
	private MatchingMethod[] supportedMatchingMethods;

	@Parameter(defaultValue="true")
	private boolean useQCAlgorithm;

	private static Logger logger = Logger.getLogger(
			RewordingDictionaryService.class.getName()
			);
}
