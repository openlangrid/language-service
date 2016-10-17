package jp.go.nict.langrid.wrapper.common.db.dao.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jp.go.nict.langrid.wrapper.common.db.dao.QueryUtil;

public class PgClassTable{
	public boolean isTableExist(Connection connection, String tableName)
	throws SQLException{
		PreparedStatement s = QueryUtil.prepareStatement(connection, tableExist);
		try{
			s.setString(1, tableName);
			ResultSet r = s.executeQuery();
			try{
				if(!r.next()){
					throw new SQLException("failed to get table existence.");
				}
				int c = r.getInt(1);
				if(c == 1) return true;
				if(c == 0) return false;
				throw new SQLException("more than one table found.");
			} finally{
				r.close();
			}
		} finally{
			s.close();
		}
	}

	public boolean isSequenceExist(Connection connection, String sequenceName)
	throws SQLException{
		PreparedStatement s = QueryUtil.prepareStatement(connection, sequenceExist);
		try{
			s.setString(1, sequenceName);
			ResultSet r = s.executeQuery();
			try{
				if(!r.next()){
					throw new SQLException("failed to get table existence.");
				}
				int c = r.getInt(1);
				if(c == 1) return true;
				if(c == 0) return false;
				throw new SQLException("more than one table found.");
			} finally{
				r.close();
			}
		} finally{
			s.close();
		}
	}

	private static final String tableExist =
		"SELECT count(relname) FROM pg_class c"
		+ " WHERE relname=? AND relkind='r'::\"char\"";
	private static final String sequenceExist =
		"SELECT count(relname) FROM pg_class c"
		+ " WHERE relname=? AND relkind='S'::\"char\"";
}
