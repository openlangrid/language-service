/*
 * $Id: AbstractDao.java 266 2010-10-03 10:33:59Z t-nakaguchi $
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
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public abstract class AbstractDao{
	public AbstractDao(
			ConnectionManager connectionManager
			){
		this.cm = connectionManager;
	}

	protected Connection getConnection()
	throws DaoException{
		try{
			return cm.getConnection();
		} catch(SQLException e){
			throw new DaoException(e);
		}
	}

	protected void closeConnection(Connection connection)
	throws DaoException{
		try{
			connection.close();
		} catch(SQLException e){
			throw new DaoException(e);
		}
	}

	protected void closeConnection(Connection connection, Throwable exception)
	throws DaoException{
		try{
			connection.close();
		} catch(SQLException e){
			log(exception);
			throw new DaoException(e);
		}
	}

	protected Connection beginTransaction()
	throws DaoException{
		Connection c = getConnection();
		try{
			c.setAutoCommit(false);
			return c;
		} catch(SQLException e){
			closeConnection(c, e);
			throw new DaoException(e);
		}
	}

	protected void commitTransaction(Connection connection)
	throws DaoException{
		try{
			connection.commit();
			closeConnection(connection);
		} catch(SQLException e){
			closeConnection(connection, e);
			throw new DaoException(e);
		}
	}

	protected void rollbackTransaction(Connection connection)
	throws DaoException{
		try{
			connection.rollback();
			closeConnection(connection);
		} catch(SQLException e){
			closeConnection(connection, e);
			throw new DaoException(e);
		}
	}

	protected void rollbackTransaction(Connection connection, Throwable exception)
	throws DaoException{
		try{
			connection.rollback();
			closeConnection(connection, exception);
		} catch(SQLException e){
			log(exception);
			closeConnection(connection, e);
			throw new DaoException(e);
		}
	}

	private static void log(Throwable t){
		logger.log(
				Level.WARNING
				, "exception hided because another exception occurred"
				, t);
	}

	private ConnectionManager cm;
	private static Logger logger = Logger.getLogger(AbstractDao.class.getName());
}
