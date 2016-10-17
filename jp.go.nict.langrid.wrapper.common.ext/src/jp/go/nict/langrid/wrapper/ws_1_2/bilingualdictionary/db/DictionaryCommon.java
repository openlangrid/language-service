/*
 * $Id:DictionaryCommon.java 3411 2008-02-12 18:42:55 +0900 (木, 12 10 2006) sagawa $
 *
 * Copyright (C) 2008 Language Grid Assocation.
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
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
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.dbcp.BasicDataSource;

public class DictionaryCommon {
	/** データベース接続のためのURL */
//	private static String dburl = "jdbc:postgresql://127.0.0.1:5432/";
	private static String dburl = "jdbc:postgresql://192.168.0.2:5432/"; // langrid
//	private static String dburl = "jdbc:postgresql://localhost:5427/"; // trans

	/** データベース接続のためのユーザ名 */
	//private static String dbuser = "fujihara"; // NICT 京大
	//private static String dbuser = "postgres"; // Local	
	private static String dbuser = "langrid"; // Trans

	/** データベース接続のためのパスワード */
	//private static String dbpassword = "Cd1kdLdk";
	//private static String dbpassword = "fujihara"; //NICT 京大
	//private static String dbpassword = "manager"; //Local
	private static String dbpassword = ""; //Trans

	/** コネクションプール機能を持つDataSource */
	private static BasicDataSource dataSource;

	/** Database name * */
	private static String dbname = "CommunityDictionary"; // NICT 京大
	//private static String dbname = "kawasaki-dict"; // local
	//private static String dbname = "LanguageResources"; //Trans
	
	public String UserManagerTable = "usermanager";
	
	public String DictManagerTable = "dictionarymanager";

	/**
	 * コンストラクタ
	 */
	public DictionaryCommon() throws RuntimeException{

		try {
			
			dataSource = new BasicDataSource();
	
			dataSource.setDriverClassName("org.postgresql.Driver");
			
			dataSource.setUsername(dbuser);
			
			dataSource.setPassword(dbpassword);
			
			dataSource.setUrl(dburl+dbname);
			
			dataSource.setDefaultAutoCommit(true);
			
			dataSource.setDefaultReadOnly(false);
			
			dataSource.setMaxActive(200);
			
			//dataSource.setMinEvictableIdleTimeMillis(5000);
			
			dataSource.setPoolPreparedStatements(true);

			dataSource.setMaxIdle(0);
			
			//dataSource.setMinIdle(0);
			
			dataSource.setNumTestsPerEvictionRun(20);
			dataSource.setTimeBetweenEvictionRunsMillis(1000);
			
			//dataSource.setMaxWait(10000);
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * SQL接続
	 * 
	 * @throws ConnectSQLFailure
	 * @throws SQLException,Exception
	 */

	//[Begin] Added Exception type by h.sagawa 2008/02/08
	public Connection getConnection() throws SQLException,Exception {
		Connection sqlcon = null;
		try {
			sqlcon = dataSource.getConnection();
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return sqlcon;
	}	
	//public Connection getConnection() {
	//	Connection sqlcon = null;
	//	try {
	//		sqlcon = dataSource.getConnection();
	//	} catch (SQLException e) {
	//		e.printStackTrace();
	//		System.out.print(e.getMessage());
	//		// throw new ConnectSQLFailure();
	//	}
	//	return sqlcon;
	//}
	//[End]

	/**
	 * SQL接続解除 コネクションをコネクションプールに返却します。
	 * 
	 * @param sqlstmt SQLステートメント
	 * @param sqlcon SQLコネクション
	 * @throws ReleaseSQLFailure
	 * @throws SQLException,Exception
	 */
	//[Begin] Added Exception type by h.sagawa 2008/02/08
	public void releaseConnection(Statement sqlstmt, Connection sqlcon) throws SQLException,Exception  {
		try {
			if (sqlstmt != null) {
				sqlstmt.close();
			}
			if (sqlcon != null)
				sqlcon.close();
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	//public void releaseConnection(Statement sqlstmt, Connection sqlcon)  {
	//	try {
	//		if (sqlstmt != null) {
	//			sqlstmt.close();
	//		}
	//		if (sqlcon != null)
	//			sqlcon.close();
	//	} catch (SQLException e) {
	//		e.printStackTrace();
	//		System.out.print(e.getMessage());
	//		// throw new ReleaseSQLFailure();
	//	}
	//}	
	//[End]
	
	
	/**
	 * SQL接続解除 コネクションをコネクションプールに返却します。
	 * 
	 * @param rs 結果セットオブジェクト
	 * @param sqlstmt SQLステートメント
	 * @param sqlcon SQLコネクション
	 * @throws ReleaseSQLFailure
	 * @throws SQLException,Exception
	 */	
	public void releaseConnection(ResultSet rs, Statement sqlstmt,
			Connection sqlcon) throws SQLException,Exception {
		try {
			if (rs != null) {
				rs.close();
			}
			if (sqlstmt != null) {
				sqlstmt.close();
			}
			if (sqlcon != null)
				sqlcon.close();
		} catch (SQLException e) {
          throw e;
		} catch (Exception e) {
	      throw e;		
		}
	}	
}
