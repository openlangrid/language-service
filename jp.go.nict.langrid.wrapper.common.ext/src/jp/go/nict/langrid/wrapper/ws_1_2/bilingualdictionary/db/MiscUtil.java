package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MiscUtil {
	/**
	 * ログ用の時刻を作成
	 */
	public static String getCurrentTime() {
		String time;
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd" + "\t" + "EE" + "\t"
				+ "HH:mm:ss.SSS" + "\t" + "zz");
		time = df.format(date);
		return time;
	}

	public static String getCurrentShortTime() {
		String time = null;
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd" + "_"
				+ "HH_mm_ss_SSS" + "_" + "zz");
		time = df.format(date);
		return time;
	}
	
}
