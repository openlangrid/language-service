/*
 * $Id: ConnectionManager.java 4596 2009-01-23 09:22:05Z kkadowaki $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.exampletemplate.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * <#if locale="ja">
 * データベース接続管理クラス。
 * java.sql.DataSourceを作成し、それを介してjava.sql.Connectionオブジェクトを作成する。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 * @author $Author: kkadowaki $
 * @version $Revision: 4596 $
 */
public class ConnectionManager {
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * <#elseif locale="en">
	 * </#if>
	 */
	public ConnectionManager(
			String driverClassName, String connectionUrl
			, String userName, String password
			, int maxActive, int maxIdle, int maxWait
			, int maxPSActive)
	{
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(driverClassName);
		bds.setUrl(connectionUrl);
		bds.setUsername(userName);
		bds.setPassword(password);
		bds.setMaxActive(maxActive);
		bds.setMaxIdle(maxIdle);
		bds.setMaxWait(maxWait);
		if(maxPSActive != -1){
			bds.setMaxOpenPreparedStatements(maxPSActive);
		}
		this.dataSource = bds;
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * <#elseif locale="en">
	 * </#if>
	 */
	public ConnectionManager(String jndiName)
	throws NamingException{
		this.dataSource = (DataSource)new InitialContext(System.getProperties()).lookup(jndiName);
	}

	/**
	 * <#if locale="ja">
	 * データベースコネクションを返します。
	 * @return データベースコネクション
	 * @throws SQLException データベースコネクション作成時例外
	 * <#elseif locale="en">
	 * </#if>
	 */
	public synchronized Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	private DataSource dataSource;
}
