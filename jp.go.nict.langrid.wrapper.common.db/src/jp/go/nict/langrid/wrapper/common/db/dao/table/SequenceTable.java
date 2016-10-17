package jp.go.nict.langrid.wrapper.common.db.dao.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SequenceTable {
	public SequenceTable(String sequenceName){
		this.sequenceQuery = String.format(
				generateSequence, sequenceName
				);
	}

	public long next(Connection connection)
	throws SQLException{
		Statement st = connection.createStatement();
		try{
			ResultSet r = st.executeQuery(sequenceQuery);
			try{
				r.next();
				return r.getLong(1);
			} finally{
				r.close();
			}
		} finally{
			st.close();
		}
	}

	private String sequenceQuery;
	private static String generateSequence = "select nextval('\"%s\"')";
}
