package jp.go.nict.langrid.wrapper.common.db.dao.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AbstractTable {
	public AbstractTable(String tableName){
		this.tableName = tableName;
	}

	public String getTableName(){
		return tableName;
	}

	public void clear(Connection connection)
	throws SQLException{
		PreparedStatement pst = connection.prepareStatement(
				String.format(clear, tableName)
				);
		try{
			pst.executeUpdate();
		} finally{
			pst.close();
		}
	}

	private String tableName;
	private static String clear = "DELETE FROM \"%s\"";
}
