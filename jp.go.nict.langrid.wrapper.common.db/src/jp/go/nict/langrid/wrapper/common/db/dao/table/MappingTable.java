package jp.go.nict.langrid.wrapper.common.db.dao.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.go.nict.langrid.wrapper.common.db.dao.QueryUtil;

public class MappingTable
extends AbstractTable{
	public MappingTable(
			String tableName
			, String firstIdColumnName
			, String secondIdColumnName
			){
		super(tableName);
		this.firstIdColumnName = firstIdColumnName;
		this.secondIdColumnName = secondIdColumnName;
	}

	public String getFirstIdColumnName(){
		return firstIdColumnName;
	}

	public String getSecondIdColumnName(){
		return secondIdColumnName;
	}

	public void insert(Connection connection, long firstId, long secondId)
	throws SQLException{
		PreparedStatement s = QueryUtil.prepareStatement(
				connection, insertRef, getTableName()
				, firstIdColumnName, secondIdColumnName
				);
		s.setLong(1, firstId);
		s.setLong(2, secondId);
		s.executeUpdate();
		s.close();
	}

	public void deleteForFirst(Connection connection, long id)
	throws SQLException{
		delete(connection, firstIdColumnName, id);
	}

	public void deleteForSecond(Connection connection, long id)
	throws SQLException{
		delete(connection, secondIdColumnName, id);
	}

	private void delete(Connection connection, String columnName, long id)
	throws SQLException{
		PreparedStatement pst = connection.prepareStatement(
				String.format(deleteRef, getTableName(), columnName)
				);
		try{
			pst.setLong(1, id);
			pst.executeUpdate();
		} finally {
			pst.close();
		}
	}

	private String firstIdColumnName;
	private String secondIdColumnName;

	private static String insertRef =
		"INSERT INTO \"%s\" (\"%s\", \"%s\")" +
		" VALUES (?, ?)";
	private static String deleteRef = "DELETE FROM \"%s\" WHERE \"%s\"=?";
}
