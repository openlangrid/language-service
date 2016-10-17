//Copyright 2009 Carnegie-Mellon University
//Licensed under the Apache License, Version 2.0 (the "License"); 
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, 
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied. See the License for the
//specific language governing permissions and limitations
//under the License.

package edu.cmu.lti.jawjaw.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for communicating with the database
 * @author Hideki Shima
 *
 */
public class SQL {

	private static volatile SQL instance;
	private static Connection connection;
	
	/**
	 * Private constructor 
	 */
	private SQL(){
		try {
			createSQLConnection() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String dbUrl = "jdbc:sqlite:./data/wnjpn-0.9.db";

	public static void setDBPath(String dbPath){
		dbUrl = "jdbc:sqlite:" + dbPath;
	}

	/**
	 * Singleton pattern
	 * @return singleton object
	 */
	public static SQL getInstance(){
		synchronized (SQL.class) {
			try {
				if ( instance==null || connection==null || connection.isClosed() ) {
					instance = new SQL();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	/**
	 * Creates a jdbc connection to a sqlite3 db
	 * @throws Exception
	 */
	private static void createSQLConnection() throws Exception {
		String driver = "org.sqlite.JDBC";
		
		// Classload the driver
		Class.forName( driver );
						
//		String url = "jdbc:sqlite:./data/wnjpn-0.9.db";
		
		// Open cannection
		connection = DriverManager.getConnection( dbUrl );
	}
	
	public String execute( String sql ) {
		StringBuilder sb = new StringBuilder();
		PreparedStatement ps = getPreparedStatement( sql );
		try {
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public void execute( PreparedStatement ps ) throws SQLException {
		ps.execute();
	}
	
	public ResultSet executeSelect( PreparedStatement statement ) {
		ResultSet rs = null;
		try {
			rs = statement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public void executeInsert( PreparedStatement statement ) {
		try {
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void executeBatch( PreparedStatement statement ) {
		
		try {
			connection.setAutoCommit(false);
			statement.executeBatch();
			connection.setAutoCommit(true);
		} catch (SQLException e) {			
			e.printStackTrace();
		}
	}
	
	public PreparedStatement getPreparedStatement( String sql ){
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement( sql );
		} catch (Exception e) {
			e.getStackTrace();
		}
		return ps;
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			if ( connection != null ) {
				connection.close();
			}
		} catch ( SQLException e ) {
			// connection close failed.
			e.printStackTrace();
		}
	}
		
}
