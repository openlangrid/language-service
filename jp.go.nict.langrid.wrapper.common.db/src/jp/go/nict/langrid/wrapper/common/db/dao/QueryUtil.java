package jp.go.nict.langrid.wrapper.common.db.dao;

import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.COMPLETE;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PARTIAL;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.PREFIX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.REGEX;
import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.SUFFIX;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.transformer.LanguageToCodeStringTransformer;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;

public class QueryUtil {
	public static int executeUpdate(Connection connection, String query)
	throws SQLException{
		if(showSql.get()){
			System.out.println(query);
		}
		Statement s = connection.createStatement();
		try{
			return s.executeUpdate(query);
		} finally{
			s.close();
		}
	}

	public static PreparedStatement prepareStatement(
			Connection connection, String queryFormat, Object... args)
	throws SQLException{
		String query = String.format(queryFormat, args);
		if(showSql.get()){
			System.out.println(query);
		}
		return connection.prepareStatement(query);
	}

	public static String toColumnNames(Language... languages){
		return 
			"\""
			+ StringUtil.join(languages, l2code, "\", \"")
			+ "\""
			;
	}

	public static String toColumnSelects(String tableNameAlias, Language... languages){
		return 
			tableNameAlias + ".\""
			+ StringUtil.join(languages, l2code, "\", " + tableNameAlias + ".\"")
			+ "\""
			;
	}

	public static String toColumnDefinitions(Language[] languages){
		return 
			"\""
			+ StringUtil.join(languages, l2code, "\" text, \"")
			+ "\" text"
			;
	}

	public static String toColumnAssignes(Language... languages){
		return 
			"\""
			+ StringUtil.join(languages, l2code, "\"=?, \"")
			+ "\"=?"
			;
	}

	public static Pair<String, String> makeOperationAndMatchingValue(
			String value, MatchingMethod method, DbDictionary dictionary){
		String operation = " = ?"; // COMPLETE MATCH
		String matchingValue = value.toLowerCase(Locale.ENGLISH);
		if (method.equals(COMPLETE)) {
		} else if(method.equals(REGEX) && dictionary != null){
			switch(dictionary){
				case POSTGRESQL:
					operation = " ~ ? ";
					break;
				case MYSQL:
					operation = " REGEXP ? ";
					break;
				default:
					operation = " = ? and 0=1";
					break;
			}
		} else {
			operation = " LIKE ? ";
			if (method.equals(PARTIAL)) {
				matchingValue = '%' + matchingValue + '%';
			} else if (method.equals(PREFIX)) {
				matchingValue = matchingValue + '%';
			} else if (method.equals(SUFFIX)) {
				matchingValue = '%' + matchingValue;
			}
		}
		return Pair.create(operation, matchingValue);
	}

	public static void setShowSqlEnabled(boolean enabled){
		showSql.set(enabled);
	}

	private static Transformer<Language, String> l2code
			= new LanguageToCodeStringTransformer();
	private static ThreadLocal<Boolean> showSql = new ThreadLocal<Boolean>(){
		protected Boolean initialValue() {
			return false;
		}
	};
}
