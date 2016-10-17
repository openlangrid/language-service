/*
 * $Id: AdjacencyPairService.java 409 2011-08-25 03:12:59Z t-nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2009 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.common.db.service;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.util.CollectionUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.commons.ws.param.ServiceContextParameterContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.transformer.CodeStringToLanguageTransformer;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.adjacencypair.AdjacencyPair;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.ConnectionParameters;
import jp.go.nict.langrid.wrapper.common.db.DataBaseUtil;
import jp.go.nict.langrid.wrapper.common.db.DbParameterLoader;
import jp.go.nict.langrid.wrapper.common.db.dao.AdjacencyPairDao;
import jp.go.nict.langrid.wrapper.common.db.dao.DaoException;
import jp.go.nict.langrid.wrapper.common.db.dao.MetaDao;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.Category;
import jp.go.nict.langrid.wrapper.ws_1_2.adjacencypair.AbstractAdjacencyPairService;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
public class AdjacencyPairService
extends AbstractAdjacencyPairService{
	/**
	 * 
	 * 
	 */
	public AdjacencyPairService() {
		init();
	}

	/**
	 * 
	 * 
	 */
	public AdjacencyPairService(ServiceContext serviceContext){
		super(serviceContext);
		init();
	}

	AdjacencyPairDao getDao(){
		return dao;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<AdjacencyPair> doSearch(String category,
			Language language, String firstTurn, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		checkConnectionValid();
		try {
			Category[] cats = new Category[]{};
			if(category != null && category.length() > 0){
				cats = dao.searchCategories(language, new Language[]{}, category)
					.toArray(new Category[]{});
				if(cats.length == 0) return Collections.EMPTY_LIST;
			}
			long[] cids = new long[cats.length];
			for(int i = 0; i < cats.length; i++){
				cids[i] = cats[i].getId();
			}
			return CollectionUtil.collect(
					dao.searchAdjacencyPairs(language, new Language[]{}, firstTurn, matchingMethod, cids)
					, new Transformer<jp.go.nict.langrid.wrapper.common.db.dao.entity.AdjacencyPair, AdjacencyPair>(){
						public AdjacencyPair transform(
								jp.go.nict.langrid.wrapper.common.db.dao.entity.AdjacencyPair value)
								throws TransformationException {
							String category = "";
							if(value.getCategories().length > 0){
								category = value.getCategories()[0].getCategories()[0];
							}
							String[] secondTurns = ArrayUtil.collect(
									value.getSecondTurns()
									, String.class
									, new Transformer<jp.go.nict.langrid.wrapper.common.db.dao.entity.AdjacencyPair.SecondTurn, String>(){
										public String transform(jp.go.nict.langrid.wrapper.common.db.dao.entity.AdjacencyPair.SecondTurn value) throws TransformationException {
											return value.getSecondTurns()[0];
										};
									});
							return new AdjacencyPair(
									category
									, value.getFirstTurns()[0]
									, secondTurns
									);
						}						
					}
					);
		} catch (DaoException e) {
			logger.log(Level.SEVERE, "failed to sql execute: " , e);
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected Calendar doGetLastUpdate() throws ProcessFailedException {
		checkConnectionValid();
		Date date = DataBaseUtil.getLastUpdate(
				"\"" + tableName + "_firstTurns\""
				, "\"updatedDateTime\"", manager);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	protected void setTableName(String tableName){
		this.tableName = tableName;
	}

	protected void setLanguages(String[] languages){
		this.languageColumnNames = languages;
		Collection<Language> langs = Arrays.asList(
				ArrayUtil.collect(
						languageColumnNames
						, new CodeStringToLanguageTransformer())
				);
		setSupportedLanguageCollection(langs);
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
		try{
			manager = new ConnectionManager(conParams);
		} catch(NamingException e){
			setStartupException(e);
			logger.log(Level.SEVERE, "failed to initialize database connection.", e);
		}
		setSupportedLanguageCollection(Util.toLanguages(languageColumnNames));
		if(supportedMatchingMethods != null){
			setSupportedMatchingMethods(new HashSet<MatchingMethod>(
					Arrays.asList(supportedMatchingMethods)));
		} else{
			setSupportedMatchingMethods(MINIMUM_MATCHINGMETHODS);
		}

		dao = new AdjacencyPairDao(manager, conParams.dbDictionary
				, tableName, getMaxResults());
		try{
			MetaDao md = new MetaDao(manager);
			Language[] langs = getSupportedLanguageCollection().toArray(new Language[]{});
			if(!md.isAdjacencyPairTableAvailable(tableName, langs)){
				md.createAdjacencyPairTable(tableName, langs);
			} else if(recreateTables){
				md.dropAdjacencyPairTable(tableName);
				md.createAdjacencyPairTable(tableName, langs);
			}
		} catch(DaoException e){
			setStartupException(e);
		}
	}

	private void checkConnectionValid() throws ProcessFailedException{
		if(manager == null){
			throw new ProcessFailedException("failed to connect database.");
		}
	}

	private ConnectionParameters conParams = new ConnectionParameters();

	private ConnectionManager manager;
	private AdjacencyPairDao dao;

	@Parameter(required=true)
	private String tableName;

	@Parameter(required=true)
	private String[] languageColumnNames;

	@Parameter(defaultValue="COMPLETE PARTIAL PREFIX SUFFIX REGEX")
	private MatchingMethod[] supportedMatchingMethods; 

	@Parameter(defaultValue="false")
	private boolean recreateTables;

	private static Logger logger = Logger.getLogger(
			AdjacencyPairService.class.getName()
			);
}
