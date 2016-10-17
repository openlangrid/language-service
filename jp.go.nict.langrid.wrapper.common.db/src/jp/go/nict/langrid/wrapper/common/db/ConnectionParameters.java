/*
 * $Id: ConnectionParameters.java 409 2011-08-25 03:12:59Z t-nakaguchi $
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
package jp.go.nict.langrid.wrapper.common.db;

import jp.go.nict.langrid.commons.parameter.annotation.Parameter;
import jp.go.nict.langrid.commons.parameter.annotation.ParameterConfig;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
@ParameterConfig(prefix="connection.", loadAllFields=true)
public class ConnectionParameters {
	/**
	 * 
	 * 
	 */
	@Parameter(name="dataSource")
	public String jndiDataSourceName;

	/**
	 * 
	 * 
	 */
	@Parameter(name="driverClassName")
	public String driverName;

	/**
	 * 
	 * 
	 */
	public String url;

	/**
	 * 
	 * 
	 */
	public String username;

	/**
	 * 
	 * 
	 */
	public String password;

	/**
	 * 
	 * 
	 */
	@Parameter(defaultValue="5")
	public int maxActive = 5;

	/**
	 * 
	 * 
	 */
	@Parameter(defaultValue="3")
	public int maxIdle = 3;

	/**
	 * 
	 * 
	 */
	@Parameter(defaultValue="5000")
	public int maxWait = 5000;

	/**
	 * 
	 * 
	 */
	@Parameter(name="maxStatementActive", defaultValue="-1")
	public int maxPSActive = -1;

	/**
	 * 
	 * 
	 */
	public DbDictionary dbDictionary;

	public void setDataSourceName(String jndiDataSourceName) {
		this.jndiDataSourceName = jndiDataSourceName;
	}
	public void setDbDictionary(DbDictionary dbDictionary) {
		this.dbDictionary = dbDictionary;
	}
}
