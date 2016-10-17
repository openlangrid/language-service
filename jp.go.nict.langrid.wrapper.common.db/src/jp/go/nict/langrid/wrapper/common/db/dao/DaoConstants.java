/*
 * $Id: DaoConstants.java 266 2010-10-03 10:33:59Z t-nakaguchi $
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

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class DaoConstants {
	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JNDI_DATASOURCE
			= "connection.dataSource";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_DRIVER_CLASS
			= "connection.driverClassName";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_CONNECTION_URL
			= "connection.url";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_USERNAME
			= "connection.username";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_PASSWORD
			= "connection.password";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_MAX_ACTIVE
			= "connection.maxActive";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_MAX_IDLE
			= "connection.maxIdle";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_MAX_WAIT
			= "connection.maxWait";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_JDBC_MAX_STATEMENT_ACTIVE
			= "connection.maxStatementActive";

	/**
	 * 
	 * 
	 */
	public static final String PARAMETER_DBDICTIONARY
			= "connection.dbdictionary";

	/**
	 * 
	 * 
	 */
	public static final String VALUE_DBDICTIONARY_DERBY
			= "derby";

	/**
	 * 
	 * 
	 */
	public static final String VALUE_DBDICTIONARY_POSTGRESQL
			= "postgresql";
}
