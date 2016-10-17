/*
 * $Id: DataBaseUtil.java 4596 2009-01-23 09:22:05Z kkadowaki $
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <#if locale="ja">
 * データベースユーティリティクラス
 * <#elseif locale="en">
 * </#if>
 *
 */
public class DataBaseUtil {

	public static String checkColumn(String targetColumn, String[] columns) throws SQLException {
		for (String column : columns) {
			if (targetColumn.toUpperCase().equals(column.toUpperCase())) {
				return column;
			}
		}
		throw new SQLException("this column not exists. " + targetColumn);
	}
	
	public static String getColumnName(String column) {
		if (column == null) {
			return "";
		}
		return ("\"" + column + "\"");
	}
	
	public static Date getLastUpdate(String tableName, String timestampColumn, ConnectionManager manager) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			con = manager.getConnection();
			pst = con.prepareStatement("SELECT MAX(" + timestampColumn + ") AS date FROM " + tableName);
			rs = pst.executeQuery();
			if (rs.next()) {
				Timestamp timestamp = rs.getTimestamp("date");
				return new Date(timestamp.getTime());
			}
			return new Date();
		} catch (SQLException e) {
			e.printStackTrace();
			return new Date();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	public static void main(String[] arg) {
		Date date = getLastUpdate(
				"local_dictionary"
				, ""
				, new ConnectionManager(
						"org.postgresql.Driver"
						, "jdbc:postgresql://localhost:5432/langrid"
						, "postgres"
						, "kado0852"
						, 1, 1, 10000, -1));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(date));
	}
}
