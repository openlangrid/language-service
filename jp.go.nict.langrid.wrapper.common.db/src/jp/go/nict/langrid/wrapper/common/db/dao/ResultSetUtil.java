package jp.go.nict.langrid.wrapper.common.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import jp.go.nict.langrid.commons.util.CalendarUtil;

public class ResultSetUtil {
	public static String[] getValues(
			ResultSet resultSet, int index, int count)
	throws SQLException{
		String[] values = new String[count];
		for(int i = 0; i < count; i++){
			values[i] = resultSet.getString(i + index);
		}
		return values;
	}

	public static Calendar getCalendar(
			ResultSet resultSet, int index)
	throws SQLException{
		return CalendarUtil.createFromMillis(
				resultSet.getTimestamp(index).getTime());		
	}
}
