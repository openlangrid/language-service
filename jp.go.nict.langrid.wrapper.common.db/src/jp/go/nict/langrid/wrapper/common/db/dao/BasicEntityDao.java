/*
 * $Id: BasicEntityDao.java 266 2010-10-03 10:33:59Z t-nakaguchi $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
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
package jp.go.nict.langrid.wrapper.common.db.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.BasicEntity;
import jp.go.nict.langrid.wrapper.common.db.dao.table.BasicEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.SequenceTable;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public abstract class BasicEntityDao<T extends BasicEntity
, U extends BasicEntityTable<T>>
extends AbstractDao{
	public BasicEntityDao(
			ConnectionManager connectionManager
			, U table, SequenceTable sequenceTable){
		super(connectionManager);
		this.table = table;
		this.sequenceTable = sequenceTable;
	}

	protected void clear()
	throws DaoException{
		Connection con = getConnection();
		try{
			table.clear(con);
			closeConnection(con);
		} catch (SQLException e) {
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	protected Collection<T> search(
			Language matchingLanguage, Language[] subLanguages
			, String matchingValue, MatchingMethod matchingMethod
			, Transformer<ResultSet, T> transformer)
	throws DaoException{
		Connection con = getConnection();
		try{
			Collection<T> ret = table.search(con, matchingLanguage, subLanguages
					, matchingValue, matchingMethod, transformer);
			closeConnection(con);
			return ret;
		} catch (SQLException e) {
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	protected long insert(Language[] languages, String[] values)
	throws DaoException{
		if(languages.length != values.length){
			throw new IllegalArgumentException(
					"length of languages and firstTurns must be same.");
		}
		Connection con = beginTransaction();
		try {
			long id = sequenceTable.next(con);
			table.insert(con, id, languages, values);
			commitTransaction(con);
			return id;
		} catch (SQLException e) {
			rollbackTransaction(con, e);
			throw new DaoException(e);
		}
	}

	protected boolean delete(long id)
	throws DaoException{
		Connection con = getConnection();
		try {
			boolean deleted = table.delete(con, id);
			closeConnection(con);
			return deleted;
		} catch (SQLException e) {
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	protected boolean update(long id, Language[] languages, String[] values)
	throws DaoException{
		if(languages.length != values.length){
			throw new IllegalArgumentException(
					"length of languages and values must be same.");
		}
		Connection con = getConnection();
		try {
			boolean updated = table.update(con, id, languages, values);
			closeConnection(con);
			return updated;
		} catch (SQLException e) {
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	protected BasicEntityTable<T> getTable(){
		return table;
	}

	protected SequenceTable getSequenceTable(){
		return sequenceTable;
	}

	private BasicEntityTable<T> table;
	private SequenceTable sequenceTable;
}
