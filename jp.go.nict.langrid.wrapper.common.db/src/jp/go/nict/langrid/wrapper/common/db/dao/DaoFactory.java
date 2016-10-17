/*
 * $Id: DaoFactory.java 266 2010-10-03 10:33:59Z t-nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.common.db.dao;

import java.util.Properties;

import javax.naming.NamingException;

import jp.go.nict.langrid.commons.parameter.ParameterLoader;
import jp.go.nict.langrid.commons.parameter.ParameterRequiredException;
import jp.go.nict.langrid.commons.parameter.PropertiesParameterContext;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.ConnectionParameters;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class DaoFactory {
	public static DaoFactory createDaoFactory(Properties properties)
	throws DaoException
	{
		ConnectionManager manager;
		ConnectionParameters p = new ConnectionParameters();
		try{
			new ParameterLoader().load(p, new PropertiesParameterContext(properties));
		} catch(TransformationException e){
			throw new DaoException(e);
		} catch(ParameterRequiredException e){
			throw new DaoException(e);
		}
		try{
			manager = new ConnectionManager(p);
		} catch(NamingException e){
			throw new DaoException(e);
		}
		return new DaoFactory(manager);
	}

	public BilingualDictionaryDao createBilingualDictionaryDao(String tableName)
	throws DaoException{
		return new BilingualDictionaryDao(
				manager, dbDictionary, tableName, maxCount);
	}

	public MetaDao createMetaDao()
	throws DaoException{
		return new MetaDao(manager);
	}

	private DaoFactory(ConnectionManager manager){
		this.manager = manager;
	}

	private ConnectionManager manager;
	private DbDictionary dbDictionary = DbDictionary.POSTGRESQL;
	private int maxCount = 100;
}
