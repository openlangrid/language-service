package jp.go.nict.langrid.wrapper.eijiro;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.ConnectionParameters;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;

import com.csvreader.CsvReader;

public class EijiroImporter {
	public static void main(String[] args) throws Exception{

		ConnectionParameters p = new ConnectionParameters();
		p.dbDictionary = DbDictionary.POSTGRESQL;
		p.driverName = "org.postgresql.Driver";
		p.url = "jdbc:postgresql:langrid-service";
		p.username = "postgres";
		p.password = "postgres";
		p.maxActive = 5;
		p.maxIdle = 1;
		p.maxPSActive = 5;
		p.maxWait = 5000;
		ConnectionManager m = new ConnectionManager(p);
		Connection con = m.getConnection();
		con.setAutoCommit(false);
		PreparedStatement s = con.prepareStatement(
				"insert into eijiro112" +
				" (en, ja, exp, \"level\", memory, modify, pron, filelink)" +
				"  values (?, ?, ?, ?, ?, ?, ?, ?)");

		CsvReader r = new CsvReader(new FileInputStream("data/eijiro112.csv"), Charset.forName("UTF-16"));
		r.readHeaders();
		int c = 0;
		while(r.readRecord()){
			if(c++ % 500 == 0){
				System.out.println((c - 1) + " records inserted.");
			}
			s.setString(1, r.get(0));
			s.setString(2, r.get(1));
			s.setString(3, r.get(2));
			s.setInt(4, Integer.valueOf(r.get(3)));
			s.setInt(5, Integer.valueOf(r.get(4)));
			s.setInt(6, Integer.valueOf(r.get(5)));
			s.setString(7, r.get(6));
			s.setString(8, r.getColumnCount() > 7 ? r.get(7) : "");
			s.executeUpdate();
		}
		con.commit();
		r.close();
		System.out.println("----------");
		showlines("data/eijiro112.csv");
	}

	private static void showlines(String resource) throws Exception{
		BufferedReader r = new BufferedReader(new InputStreamReader(
				new FileInputStream(resource), "UTF-16"));
		String line = null;
		int c = 0;
		while((line = r.readLine()) != null){
			if(c++ > 100) break;
			System.out.println(line);
		}
		r.close();
	}

	String[] separateCsv(String line){
		return null;
	}
}
