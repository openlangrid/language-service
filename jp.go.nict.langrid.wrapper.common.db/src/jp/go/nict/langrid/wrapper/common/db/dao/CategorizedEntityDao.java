/*
 * $Id: CategorizedEntityDao.java 409 2011-08-25 03:12:59Z t-nakaguchi $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2009 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.common.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.CalendarUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.CategorizedEntity;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.Category;
import jp.go.nict.langrid.wrapper.common.db.dao.table.BasicEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.CategoryTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.MappingTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.SequenceTable;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
public abstract class CategorizedEntityDao<T extends CategorizedEntity
, U extends BasicEntityTable<T>>
extends BasicEntityDao<T, U>{
	public CategorizedEntityDao(
			ConnectionManager connectionManager
			, U table, CategoryTable categoryTable
			, MappingTable mappingTable, SequenceTable sequenceTable){
		super(connectionManager, table, sequenceTable);
		this.categoryTable = categoryTable;
		this.mappingTable = mappingTable;
	}

	public Collection<Category> listCategories(
			Language[] languages)
	throws DaoException{
		Connection con = getConnection();
		try{
			Collection<Category> ret = categoryTable.list(
					con, languages);
			closeConnection(con);
			return ret;
		} catch(SQLException e){
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	public Collection<Category> searchCategories(
			Language language, Language[] subLanguages
			, String category)
	throws DaoException{
		Connection con = getConnection();
		try{
			Collection<Category> ret = categoryTable.search(
					con, language, subLanguages
					, category, MatchingMethod.COMPLETE
					);
			closeConnection(con);
			return ret;
		} catch(SQLException e){
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	public long[] searchCategoryIds(
			Language language, String[] categories)
	throws DaoException{
		Connection con = getConnection();
		try{
			long[] ret = categoryTable.searchCategoryIds(
					con, language, categories);
			closeConnection(con);
			return ret;
		} catch(SQLException e){
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	public long addCategory(Language[] languages, String[] categories)
	throws DaoException
	{
		Connection con = getConnection();
		try{
			long id = getSequenceTable().next(con);
			categoryTable.insert(con, id, languages, categories);
			closeConnection(con);
			return id;
		} catch(SQLException e){
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	public void deleteCategory(long categoryId)
	throws DaoException{
		Connection con = beginTransaction();
		try{
			mappingTable.deleteForFirst(con, categoryId);
			categoryTable.delete(con, categoryId);
			commitTransaction(con);
		} catch (SQLException e) {
			rollbackTransaction(con, e);
		}
	}

	public void updateCategory(long categoryId
			, Language[] languages, String[] categories)
	throws DaoException{
		Connection con = getConnection();
		try{
			categoryTable.update(con, categoryId, languages, categories);
			closeConnection(con);
		} catch(SQLException e){
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	protected Collection<T> searchEntitiesOfCategories(
			Language language, Language[] subLanguages
			, String matchingValue, MatchingMethod matchingMethod
			, long[] categoryIds, Transformer<ResultSet, T> transformer)
	throws DaoException{
		Connection con = getConnection();
		try{
			EntityCollector<T> collector = new EntityCollector<T>(
					transformer, subLanguages.length);
			Pair<String, String> opAndVal = QueryUtil.makeOperationAndMatchingValue(
					matchingValue, matchingMethod, getTable().getDbDictionary()
					);
			String categoryTables = "";
			String categoryConditions = "";
			if(categoryIds.length > 0){
				categoryTables = 
					", \"" + categoryTable.getTableName() + "\" cat_ct"
					+ ", \"" + mappingTable.getTableName() + "\"" +
							" cat_c2e";
				categoryConditions =
					" AND cat_ct.\"entityId\" IN ("
					+ StringUtil.repeatedString("?", categoryIds.length, ", ")
					+ ") AND e.\"entityId\" = cat_c2e.\"" + mappingTable.getSecondIdColumnName() + "\""
					+ " AND cat_c2e.\"" + mappingTable.getFirstIdColumnName() + "\"=cat_ct.\"entityId\"";
			}
			// 1 - entityName, 2 - categoryTableName
			// 3 - mappingTableName,
			// 4 - categoryIdColumnName(in mapping table)
			// 5 - entityIdColumnName(in mapping table)
			// 6 - language, 7 - subLanguages, 8 - category sub languages
			// 9 - matching operation and value
			// 10 - category from, 11 - category conditions
			// 12 - max count
			PreparedStatement s = QueryUtil.prepareStatement(
					con, selectEntitiesAndCategories
					, getTable().getTableName(), getCategoryTable().getTableName()
					, getMappingTable().getTableName()
					, getMappingTable().getFirstIdColumnName()
					, getMappingTable().getSecondIdColumnName()
					, language.getCode()
					, subLanguages.length > 0 ? ", " + QueryUtil.toColumnSelects("e", subLanguages) : ""
					, subLanguages.length > 0 ? ", " + QueryUtil.toColumnSelects("ct", subLanguages) : ""
					, opAndVal.getFirst()
					, categoryTables, categoryConditions
					, getTable().getMaxCount());
			s.setString(1, opAndVal.getSecond());
			for(int i = 0; i < categoryIds.length; i++){
				s.setLong(i + 2, categoryIds[0]);
			}
			try{
				ResultSet rs = s.executeQuery();
				try{
					while (rs.next()) {
						collector.collectWithCategory(rs.getLong(1), rs);
					}
				} catch(TransformationException e){
					throw new DaoException(e);
				} finally{
					rs.close();
				}
			} finally{
				s.close();
			}
			return collector.getEntities();
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			closeConnection(con);
		}
	}
	private static class EntityCollector<T extends CategorizedEntity>{
		public EntityCollector(
				Transformer<ResultSet, T> transformer
				, int targetLangLength){
			this.transformer = transformer;
			this.targetLangLength = targetLangLength;
		}

		public void collectWithCategory(long id, ResultSet resultSet) throws SQLException{
			T p = entities.get(id);
			if(p == null){
				try{
					entities.put(id, transformer.transform(resultSet));
				} catch(TransformationException e){
					if(e.getCause() instanceof SQLException){
						throw (SQLException)e.getCause();
					} else{
						throw e;
					}
				}
			}
			List<Category> cts = entityIdToCategories.get(id);
			if(cts == null){
				cts = new ArrayList<Category>();
				entityIdToCategories.put(id, cts);
			}
			cts.add(new Category(
					resultSet.getLong(targetLangLength + 5)
					, ResultSetUtil.getValues(resultSet, targetLangLength + 6, targetLangLength + 1)
					, CalendarUtil.createFromMillis(resultSet.getTimestamp(targetLangLength * 2 + 7).getTime())
					, CalendarUtil.createFromMillis(resultSet.getTimestamp(targetLangLength * 2 + 8).getTime())
					));
		}

		public Collection<T> getEntities(){
			Collection<T> ret = entities.values();
			for(T pt : ret){
				List<Category> cts = entityIdToCategories.get(pt.getId());
				if(cts != null){
					pt.setCategories(cts.toArray(new Category[]{}));
				} else{
					pt.setCategories(new Category[]{});
				}
			}
			return ret;
		}

		private Transformer<ResultSet, T> transformer;
		private int targetLangLength;
		private Map<Long, List<Category>> entityIdToCategories
				= new HashMap<Long, List<Category>>();
		private Map<Long, T> entities
				= new LinkedHashMap<Long, T>();
	}

	protected boolean delete(long id)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			mappingTable.deleteForSecond(c, id);
			boolean deleted = getTable().delete(c, id);
			commitTransaction(c);
			return deleted;
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	protected void updateEntityCategories(long entityId, long[] categoryIds)
	throws DaoException{
		Connection con = beginTransaction();
		try {
			mappingTable.deleteForSecond(con, entityId);
			for(long cid : categoryIds){
				mappingTable.insert(con, cid, entityId);
			}
			commitTransaction(con);
		} catch (SQLException e) {
			rollbackTransaction(con);
			throw new DaoException(e);
		}
	}

	protected CategoryTable getCategoryTable(){
		return categoryTable;
	}

	protected MappingTable getMappingTable(){
		return mappingTable;
	}

	private CategoryTable categoryTable;
	private MappingTable mappingTable;

	// 1 - entityName, 2 - categoryTableName
	// 3 - mappingTableName,
	// 4 - categoryIdColumnName(in mapping table)
	// 5 - entityIdColumnName(in mapping table)
	// 6 - language, 7 - subLanguages, 8 - category sub languages
	// 9 - matching operation and value
	// 10 - category from, 11 - category conditions
	// 12 - max count
	private static String selectEntitiesAndCategories =
		"SELECT e.\"entityId\", e.\"%6$s\"%7$s, e.\"createdDateTime\", e.\"updatedDateTime\"" +
		"   , ct.\"entityId\", ct.\"%6$s\"%8$s, ct.\"createdDateTime\", ct.\"updatedDateTime\"" +
		" FROM \"%1$s\" e, \"%2$s\" ct, \"%3$s\" m" +
	    "   %10$s" +
		" WHERE LOWER(e.\"%6$s\") %9$s AND e.\"entityId\" = m.\"%5$s\"" +
		"   AND m.\"%4$s\" = ct.\"entityId\"" +
		"   %11$s" +
		" ORDER BY e.\"%6$s\", ct.\"%6$s\"" +
		" LIMIT %12$d";
}
