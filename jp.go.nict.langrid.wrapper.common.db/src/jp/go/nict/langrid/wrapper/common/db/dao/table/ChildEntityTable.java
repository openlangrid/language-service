package jp.go.nict.langrid.wrapper.common.db.dao.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;
import jp.go.nict.langrid.wrapper.common.db.dao.QueryUtil;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.ChildEntity;

public class ChildEntityTable<T extends ChildEntity>
extends AbstractTable{
	public ChildEntityTable(
			DbDictionary dbDictionary, String tableName
			, int maxCount
			){
		super(tableName);
		this.dbDict = dbDictionary;
		this.maxCount = maxCount;
	}

	public DbDictionary getDbDictionary(){
		return dbDict;
	}

	public int getMaxCount(){
		return maxCount;
	}

	public Collection<T> search(
			Connection connection, long parentId
			, Language matchingLanguage, Language[] subLanguages
			, String matchingValue, MatchingMethod matchingMethod
			, Transformer<ResultSet, T> transformer)
	throws SQLException{
		Pair<String, String> opAndVal = QueryUtil.makeOperationAndMatchingValue(
				matchingValue, matchingMethod, getDbDictionary()
				);
		List<T> ret = new ArrayList<T>();
		PreparedStatement s = QueryUtil.prepareStatement(
				connection, selectEntities
				, getTableName()
				, matchingLanguage.getCode()
				, subLanguages.length > 0 ?
						", " + QueryUtil.toColumnNames(subLanguages)
						: ""
				, opAndVal.getFirst()
				, maxCount);
		try{
			s.setString(1, opAndVal.getSecond());
			ResultSet rs = s.executeQuery();
			try{
				while (rs.next()) {
					ret.add(transformer.transform(rs));
				}
			} finally{
				rs.close();
			}
			return ret;
		} finally {
			s.close();
		}
	}

	public void insert(
			Connection connection, long id, long parentId, Language[] languages, String[] values)
	throws SQLException
	{
		if(languages.length != values.length){
			throw new IllegalArgumentException(
					"length of languages and firstTurns must be same.");
		}
		PreparedStatement s = QueryUtil.prepareStatement(
				connection, insertEntity, getTableName()
				, QueryUtil.toColumnNames(languages)
				, StringUtil.repeatedString("?", values.length, ", ")
				);
		try{
			int index = 1;
			s.setLong(index++, id);
			s.setLong(index++, parentId);
			for(String v : values){
				s.setString(index++, v);
			}
			s.executeUpdate();
		} finally{
			s.close();
		}
	}

	public boolean delete(Connection connection, long id, long parentId)
	throws SQLException{
		PreparedStatement pst = connection.prepareStatement(
				String.format(deleteEntity, getTableName())
				);
		try{
			pst.setLong(1, id);
			pst.setLong(2, parentId);
			int affected = pst.executeUpdate();
			if(affected > 1){
				throw new SQLException("more than 1 rows affected");
			}
			return affected == 1;
		} finally {
			pst.close();
		}
	}

	public int deleteChildrenOf(Connection connection, long parentId)
	throws SQLException{
		PreparedStatement pst = connection.prepareStatement(
				String.format(deleteChildren, getTableName())
				);
		try{
			pst.setLong(1, parentId);
			return pst.executeUpdate();
		} finally {
			pst.close();
		}
	}

	public void update(
			Connection connection, long id, long parentId
			, Language[] languages, String[] values)
	throws SQLException
	{
		if(languages.length != values.length){
			throw new IllegalArgumentException(
					"length of languages and values must be same.");
		}
		PreparedStatement s = QueryUtil.prepareStatement(
				connection, updateEntity, getTableName()
				, QueryUtil.toColumnAssignes(languages)
				);
		try{
			int index = 1;
			for(String v : values){
				s.setString(index++, v);
			}
			s.setLong(index++, id);
			s.setLong(index++, parentId);
			s.executeUpdate();
		} finally{
			s.close();
		}
	}

	private DbDictionary dbDict;
	private int maxCount;

	// 1 - tableName
	// 2 - sourceLanguage, 3 - targetLanguages
	// 4 - operator and parameter(?)
	// 5 - maxCount
	private static String selectEntities =
		"SELECT \"entityId\", \"%2$s\"%3$s, \"createdDateTime\", \"updatedDateTime\"" +
		" FROM \"%1$s\"" +
		" WHERE LOWER(\"%2$s\") %4$s" +
		" ORDER BY LOWER(\"%2$s\")" +
		" LIMIT %5$d";
	private static String insertEntity
			= "INSERT INTO \"%s\" (\"entityId\", \"parentId\", %s) VALUES (?, ?, %s)";
	private static String deleteEntity
			= "DELETE FROM \"%s\" WHERE \"entityId\"=? AND \"parentId\"=?";
	private static String deleteChildren
			= "DELETE FROM \"%s\" WHERE \"parentId\"=?";
	private static String updateEntity
			= "UPDATE \"%s\" SET %s, \"updatedDateTime\"=CURRENT_TIMESTAMP" +
			  " WHERE \"entityId\"=? AND \"parentId\"=?";
}
