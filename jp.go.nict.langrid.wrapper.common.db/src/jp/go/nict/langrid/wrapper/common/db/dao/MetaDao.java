/*
 * $Id: MetaDao.java 409 2011-08-25 03:12:59Z t-nakaguchi $
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

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.dao.table.PgClassTable;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
public class MetaDao extends AbstractDao{
	/**
	 * 
	 * 
	 */
	public MetaDao(ConnectionManager manager){
		super(manager);
	}

	/**
	 * 
	 * 
	 */
	public boolean isAdjacencyPairTableAvailable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			boolean exists =
				isTablesExist(c, tableNamePrefix
						, "_firstTurns", "_secondTurns"
						, "_categories", "_category_firstTurn")
				&& sys.isSequenceExist(c, tableNamePrefix + "_seq");
			commitTransaction(c);
			return exists;
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public boolean isBilingualDictionaryTableAvailable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			boolean exists =
				sys.isTableExist(c, tableNamePrefix + "_translations")
				&& sys.isSequenceExist(c, tableNamePrefix + "_seq");
			commitTransaction(c);
			return exists;
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public boolean isParallelTextTableAvailable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			boolean exists =
				sys.isTableExist(c, tableNamePrefix + "_texts")
				&& sys.isSequenceExist(c, tableNamePrefix + "_seq");
			commitTransaction(c);
			return exists;
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public boolean isParallelTextWithIdTableAvailable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			boolean exists =
				isTablesExist(c, tableNamePrefix
						, "_texts", "_categories", "_category_text")
				&& sys.isSequenceExist(c, tableNamePrefix + "_seq");
			commitTransaction(c);
			return exists;
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void createAdjacencyPairTable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			createBasicEntityTable(c, tableNamePrefix + "_firstTurns", languages);
			createChildEntityTable(c, tableNamePrefix + "_secondTurns"
					, tableNamePrefix + "_firstTurns", languages);
			createBasicEntityTable(c, tableNamePrefix + "_categories", languages);
			createMappingTable(c, tableNamePrefix + "_category_firstTurn"
					, tableNamePrefix + "_categories", "categoryId"
					, tableNamePrefix + "_firstTurns", "firstTurnId");
			createSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void createBilingualDictionaryTable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			createBasicEntityTable(c, tableNamePrefix + "_translations", languages);
			createSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void createParallelTextTable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			createBasicEntityTable(c, tableNamePrefix + "_texts", languages);
			createSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void createParallelTextWithIdTable(
			String tableNamePrefix, Language[] languages)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			createBasicEntityTable(c, tableNamePrefix + "_texts", languages);
			createBasicEntityTable(c, tableNamePrefix + "_categories", languages);
			createMappingTable(c, tableNamePrefix + "_category_text"
					, tableNamePrefix + "_categories", "categoryId"
					, tableNamePrefix + "_texts", "parallelTextId");
			createSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void dropAdjacencyPairTable(String tableNamePrefix)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			dropTable(c, tableNamePrefix + "_category_firstTurn");
			dropTable(c, tableNamePrefix + "_categories");
			dropTable(c, tableNamePrefix + "_secondTurns");
			dropTable(c, tableNamePrefix + "_firstTurns");
			dropSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void dropParallelTextWithIdTable(String tableNamePrefix)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			dropTable(c, tableNamePrefix + "_category_text");
			dropTable(c, tableNamePrefix + "_categories");
			dropTable(c, tableNamePrefix + "_texts");
			dropSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void dropBilingualDictionaryTable(String tableNamePrefix)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			dropTable(c, tableNamePrefix + "_translations");
			dropSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	/**
	 * 
	 * 
	 */
	public void dropParallelTextTable(String tableNamePrefix)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			dropTable(c, tableNamePrefix + "_texts");
			dropSequence(c, tableNamePrefix + "_seq");
			commitTransaction(c);
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	public void createBilingualDictionaryDumpSQL(
			String tableName, Writer writer)
	throws DaoException
	{
		PrintWriter w = new PrintWriter(writer);
		Connection c = getConnection();
		try{
			String query = String.format(
					selectQueryFormat
					, tableName
					);
			Statement s = c.createStatement();
			try{
				ResultSet rs = c.createStatement().executeQuery(query);
				try{
					w.print("create table \"");
					w.print(tableName);
					w.print("\"(");
					printCreateTableSQL(rs.getMetaData(), w);
					w.println(");");
					while(rs.next()){
						w.print("insert into \"");
						w.print(tableName);
						w.print("\" values (");
						printInsertSQL(rs, rs.getMetaData().getColumnCount(), w);
						w.println(", current_timestamp, current_timestamp);");
					}
					w.flush();
				} finally{
					rs.close();
				}
			} finally{
				s.close();
			}
			closeConnection(c);
		} catch(SQLException e){
			closeConnection(c, e);
			throw new DaoException(e);
		}
	}

	public static void createBilingualDictionaryDumpSQL(
			String tableName, Language[] languages
			, Iterable<BilingualDictionaryElement> elements, Writer writer)
	{
		PrintWriter w = new PrintWriter(writer);
		w.print("create table \"");
		w.print(tableName);
		w.print("\"(");
		printCreateTableSQL(languages, w);
		w.println(");");
		for(BilingualDictionaryElement e : elements){
			w.print("insert into \"");
			w.print(tableName);
			w.print("\" values (");
			printInsertSQL(e, w);
			w.println(", current_timestamp, current_timestamp);");
		}
		w.flush();
	}

	private boolean isTablesExist(
			Connection c, String prefix, String... suffixes)
	throws SQLException{
		for(String s : suffixes){
			if(!sys.isTableExist(c, prefix + s)) return false;
		}
		return true;
	}

	private void createBasicEntityTable(
			Connection c, String tableName
			, Language[] languages)
	throws SQLException{
		QueryUtil.executeUpdate(c, String.format(
				createBasicEntitiesQueryFormat
				, tableName, QueryUtil.toColumnDefinitions(languages)
				));
	}

	private void createChildEntityTable(
			Connection c, String tableName, String parentTableName
			, Language[] languages)
	throws SQLException{
		QueryUtil.executeUpdate(c, String.format(
				createChildEntitiesQueryFormat
				, tableName, QueryUtil.toColumnDefinitions(languages)
				, parentTableName
				));
	}

	private void createMappingTable(
			Connection c, String tableName
			, String firstTableName, String firstTableIdColumnName
			, String secondTableName, String secondTableIdColumnName)
	throws SQLException{
		QueryUtil.executeUpdate(c, String.format(
				createMappingQueryFormat
				, tableName
				, firstTableName, firstTableIdColumnName
				, secondTableName, secondTableIdColumnName
				));
	}

	private void createSequence(
			Connection c, String sequenceName)
	throws SQLException{
		QueryUtil.executeUpdate(c, String.format(
				createSequenceQueryFormat
				, sequenceName
				));
	}

	private void dropTable(Connection c, String tableName)
	throws SQLException{
		QueryUtil.executeUpdate(c, String.format(
				dropTableQueryFormat
				, tableName
				));
	}

	private void dropSequence(Connection c, String tableName)
	throws SQLException{
		QueryUtil.executeUpdate(c, String.format(
				dropSequenceQueryFormat
				, tableName
				));
	}

	private static void printCreateTableSQL(Language[] languages, PrintWriter writer){
		for(int i = 0; i < languages.length; i++){
			writer.print("\"");
			writer.print(languages[i]);
			writer.print("\" text, ");
		}
		writer.println("createdDateTime timestamp, updatedDateTime timestamp");
	}

	private static void printInsertSQL(BilingualDictionaryElement element, PrintWriter writer){
		String[] hw = element.getHeadWords();
		boolean first = true;
		for(String w : hw){
			if(first){
				first = false;
			} else{
				writer.write(", ");
			}
			writer.write("'");
			writer.write(w);
			writer.write("'");
		}
	}

	private static void printCreateTableSQL(ResultSetMetaData metaData, PrintWriter writer)
	throws SQLException{
		StringBuilder b = new StringBuilder();
		for(int i = 1; i <= metaData.getColumnCount(); i++){
			if(b.length() > 0){
				b.append(", ");
			}
			b.append("\"");
			b.append(metaData.getColumnName(i));
			b.append("\" ");
			b.append(metaData.getColumnTypeName(i));
		}
		writer.write(b.toString());
	}

	private static void printInsertSQL(ResultSet result, int columnCount, PrintWriter writer)
	throws SQLException{
		StringBuilder b = new StringBuilder();
		for(int i = 1; i <= columnCount; i++){
			if(b.length() > 0){
				b.append(", ");
			}
			b.append("'");
			b.append(result.getString(i));
			b.append("'");
		}
		writer.write(b.toString());
	}

	private static PgClassTable sys = new PgClassTable();

	private static String createBasicEntitiesQueryFormat =
		"CREATE TABLE \"%s\"" +
		" (\"entityId\" bigint PRIMARY KEY, %s" +
		", \"createdDateTime\" timestamp DEFAULT CURRENT_TIMESTAMP" +
		", \"updatedDateTime\" timestamp DEFAULT CURRENT_TIMESTAMP)";
	private static String createChildEntitiesQueryFormat =
		"CREATE TABLE \"%s\"" +
		" (\"entityId\" bigint PRIMARY KEY, \"parentId\" bigint, %s" +
		", \"createdDateTime\" timestamp DEFAULT CURRENT_TIMESTAMP" +
		", \"updatedDateTime\" timestamp DEFAULT CURRENT_TIMESTAMP" +
		", FOREIGN KEY(\"parentId\") REFERENCES \"%s\"(\"entityId\")" +
		" )";
	private static String createMappingQueryFormat =
		"CREATE TABLE \"%s\"" +
		" (\"%3$s\" bigint REFERENCES \"%2$s\"(\"entityId\")" +
		", \"%5$s\" bigint REFERENCES \"%4$s\"(\"entityId\")" +
		", primary key(\"%3$s\", \"%5$s\"))";
	private static String createSequenceQueryFormat =
		"CREATE SEQUENCE \"%s\"";
	private static String dropTableQueryFormat =
		"DROP TABLE \"%s\"";
	private static String dropSequenceQueryFormat =
		"DROP SEQUENCE \"%s\"";
	private static String selectQueryFormat =
		"select * from \"%s\"";
}
