/*
 * $Id: ParallelTextWithIdService.java 266 2010-10-03 10:33:59Z t-nakaguchi $
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import jp.go.nict.langrid.commons.ws.param.ServiceContextParameterContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.Category;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelTextWithId;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.ConnectionParameters;
import jp.go.nict.langrid.wrapper.common.db.DataBaseUtil;
import jp.go.nict.langrid.wrapper.common.db.DbParameterLoader;
import jp.go.nict.langrid.wrapper.common.db.dao.DaoException;
import jp.go.nict.langrid.wrapper.common.db.dao.MetaDao;
import jp.go.nict.langrid.wrapper.common.db.dao.ParallelTextWithIdDao;
import jp.go.nict.langrid.wrapper.ws_1_2.paralleltext.AbstractParallelTextWithIdService;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class ParallelTextWithIdService
extends AbstractParallelTextWithIdService {
	/**
	 * 
	 * 
	 */
	public ParallelTextWithIdService() {
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

		Language[] languages = Util.toLanguages(languageColumnNames).toArray(new Language[]{});
		setSupportedLanguagePairs(Util.toRoundrobinPairs(languages));
		setSupportedMatchingMethods(Util.toMatchingMethods(supportedMatchingMethods));

		dao = new ParallelTextWithIdDao(
				manager, conParams.dbDictionary
				, tableName, getMaxResults()
				);

		try{
			MetaDao md = new MetaDao(manager);
			if(!md.isParallelTextWithIdTableAvailable(tableName, languages)){
				md.createParallelTextWithIdTable(tableName, languages);
			} else if(recreateTables){
				md.dropParallelTextWithIdTable(tableName);
				md.createParallelTextWithIdTable(tableName, languages);
			}
		} catch(DaoException e){
			setStartupException(e);
		}
	}

	ParallelTextWithIdDao getDao(){
		return dao;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<ParallelTextWithId> doSearchParallelTexts(
			Language sourceLang, Language targetLang
			, String matchingValue, MatchingMethod matchingMethod
			, String[] categoryIds
			)
	throws InvalidParameterException, ProcessFailedException {
		checkConnectionValid();
		try {
			List<Long> cids = new ArrayList<Long>();
			for(String s : categoryIds){
				try{
					cids.add(Long.valueOf(s));
				} catch(NumberFormatException e){
				}
			}
			if(categoryIds.length > 0 && cids.size() == 0){
				return Collections.EMPTY_LIST;
			}
			return CollectionUtil.collect(
					dao.searchParallelTextWithIds(sourceLang, new Language[]{targetLang, categoryLanguage}
						, matchingValue, matchingMethod
						, (long[])ArrayUtil.toPrimitiveArray(cids, Long.class))
					, new Transformer<jp.go.nict.langrid.wrapper.common.db.dao.entity.ParallelTextWithId, ParallelTextWithId>(){
						public ParallelTextWithId transform(
								jp.go.nict.langrid.wrapper.common.db.dao.entity.ParallelTextWithId value)
								throws TransformationException {
							return new ParallelTextWithId(
									String.valueOf(value.getId())
									, value.getTexts()[0]
									, value.getTexts()[1]
									, ArrayUtil.collect(value.getCategories(), c2c)
									);
						}						
					}
					);
		} catch (DaoException e) {
			logger.log(Level.WARNING, "failed to access DAO", e);
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected Collection<Category> doListCategories()
	throws ProcessFailedException {
		checkConnectionValid();
		try{
			return CollectionUtil.collect(
					dao.listCategories(new Language[]{categoryLanguage})
					, c2c);
		} catch (DaoException e) {
			logger.log(Level.WARNING, "failed to access DAO", e);
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

	private void checkConnectionValid() throws ProcessFailedException{
		if(manager == null){
			throw new ProcessFailedException("failed to connect database.");
		}
	}

	private ConnectionParameters conParams = new ConnectionParameters();

	private ConnectionManager manager;
	private ParallelTextWithIdDao dao;

	@Parameter(required=true)
	private String tableName;

	@Parameter(required=true)
	private String[] languageColumnNames;

	@Parameter(defaultValue="en")
	private Language categoryLanguage;

	@Parameter(defaultValue="COMPLETE PARTIAL PREFIX SUFFIX REGEX")
	private MatchingMethod[] supportedMatchingMethods; 

	@Parameter(defaultValue="false")
	private boolean recreateTables;

	private static Transformer<jp.go.nict.langrid.wrapper.common.db.dao.entity.Category, Category> c2c
		= new Transformer<jp.go.nict.langrid.wrapper.common.db.dao.entity.Category, Category>(){
		public Category transform(
				jp.go.nict.langrid.wrapper.common.db.dao.entity.Category value)
				throws TransformationException {
			return new Category(String.valueOf(value.getId())
					, value.getCategories()[value.getCategories().length - 1]);
		}
	};
	private static Logger logger = Logger.getLogger(
			AdjacencyPairService.class.getName()
			);
}
