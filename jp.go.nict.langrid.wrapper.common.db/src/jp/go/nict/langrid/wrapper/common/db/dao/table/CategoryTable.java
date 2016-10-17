package jp.go.nict.langrid.wrapper.common.db.dao.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.CalendarUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;
import jp.go.nict.langrid.wrapper.common.db.dao.QueryUtil;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.Category;

public class CategoryTable
extends BasicEntityTable<Category>{
	public CategoryTable(
			DbDictionary dbDictionary, String tableName
			, int maxCount
			){
		super(dbDictionary, tableName, maxCount);
	}

	public Collection<Category> list(Connection connection, Language[] languages)
	throws SQLException{
		PreparedStatement s = QueryUtil.prepareStatement(
				connection, listCategories
				, getTableName()
				, QueryUtil.toColumnNames(languages)
				);
		try{
			List<Category> ret = new ArrayList<Category>();
			ResultSet rs = s.executeQuery();
			try{
				ResultSetToCategoryTransformer t = new ResultSetToCategoryTransformer(
						languages.length);
				while (rs.next()) {
					ret.add(t.transform(rs));
				}
			} finally{
				rs.close();
			}
			return ret;
		} finally {
			s.close();
		}
	}

	public Collection<Category> search(
			Connection connection
			, Language matchingLanguage, Language[] subLanguages
			, String matchingValue, MatchingMethod matchingMethod
			)
	throws SQLException{
		return search(connection, matchingLanguage, subLanguages
				, matchingValue, matchingMethod
				, new ResultSetToCategoryTransformer(subLanguages.length + 1)
				);
	}

	public long[] searchCategoryIds(
			Connection connection
			, Language language
			, String[] categories
			)
	throws SQLException{
		PreparedStatement s = QueryUtil.prepareStatement(
				connection, selectCategoryIds
				, getTableName()
				, language.getCode()
				, StringUtil.repeatedString("?", categories.length, ", ")
				);
		try{
			for(int i = 0; i < categories.length; i++){
				s.setString(i + 1, categories[i]);
			}
			List<Long> ret = new ArrayList<Long>();
			ResultSet rs = s.executeQuery();
			try{
				while (rs.next()) {
					ret.add(rs.getLong(1));
				}
			} finally{
				rs.close();
			}
			long[] r = new long[ret.size()];
			for(int i = 0; i < ret.size(); i++){
				r[i] = ret.get(i);
			}
			return r;
		} finally {
			s.close();
		}
	}

	// 1 - categoryTableName, 2 - language
	private static String listCategories =
		"SELECT \"entityId\", %2$s, \"createdDateTime\", \"updatedDateTime\"" +
		" FROM \"%1$s\"";
	// 1 - categoryTableName, 2 - language, 3 - categories
	private static String selectCategoryIds =
		"SELECT \"entityId\"" +
		" FROM \"%1$s\"" +
		" WHERE LOWER(\"%2$s\") IN (%3$s)";

	private static class ResultSetToCategoryTransformer
	implements Transformer<ResultSet, Category>{
		public ResultSetToCategoryTransformer(int valueLength){
			this.valueLength = valueLength;
		}
		public Category transform(ResultSet value)
			throws TransformationException {
			try{
				int index = 1;
				long id = value.getLong(index++);
				String[] categories = new String[valueLength];
				for(int i = 0; i < valueLength; i++){
					categories[i] = value.getString(index++);
				}
				return new Category(
						id, categories
						, CalendarUtil.createFromMillis(value.getTimestamp(index).getTime())
						, CalendarUtil.createFromMillis(value.getTimestamp(index + 1).getTime())
						);
			} catch(SQLException e){
				throw new TransformationException(e);
			}
		}
		private int valueLength;
	};
}
