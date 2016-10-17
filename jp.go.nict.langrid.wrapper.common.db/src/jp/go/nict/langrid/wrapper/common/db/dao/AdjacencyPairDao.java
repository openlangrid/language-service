/*
 * $Id: AdjacencyPairDao.java 409 2011-08-25 03:12:59Z t-nakaguchi $
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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.AdjacencyPair;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.Category;
import jp.go.nict.langrid.wrapper.common.db.dao.table.BasicEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.CategoryTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.ChildEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.MappingTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.SequenceTable;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 409 $
 */
public class AdjacencyPairDao
extends CategorizedEntityDao<AdjacencyPair, BasicEntityTable<AdjacencyPair>> {
	/**
	 * 
	 * 
	 */
	public AdjacencyPairDao(
			ConnectionManager connectionManager
			, DbDictionary dbDictionary
			, String tableNamePrefix, int maxCount){
		super(connectionManager
				, new BasicEntityTable<AdjacencyPair>(
						dbDictionary, tableNamePrefix +"_firstTurns", maxCount)
				, new CategoryTable(
						dbDictionary, tableNamePrefix + "_categories", maxCount)
				, new MappingTable(
						tableNamePrefix + "_category_firstTurn"
						, "categoryId", "firstTurnId")
				, new SequenceTable(
						tableNamePrefix + "_seq")
				);
		this.secondTurnTable = new ChildEntityTable<AdjacencyPair.SecondTurn>(
				dbDictionary, tableNamePrefix +"_secondTurns", maxCount);
	}

	/**
	 * 
	 * 
	 */
	public Collection<AdjacencyPair> searchAdjacencyPairs(
			Language language, Language[] subLanguages
			, String matchingValue, MatchingMethod matchingMethod
			, long[] categoryIds)
	throws DaoException{
		Connection con = getConnection();
		try{
			long offset = 0;
			int limit = getTable().getMaxCount();
			AdjacencyPairCollector collector = new AdjacencyPairCollector(limit);
			Pair<String, String> opAndVal = QueryUtil.makeOperationAndMatchingValue(
					matchingValue, matchingMethod, getTable().getDbDictionary()
					);
			String categoryTables = "";
			String categoryConditions = "";
			if(categoryIds.length > 0){
				categoryTables = 
					", \"" + getCategoryTable().getTableName() + "\" cat_ct"
					+ ", \"" + getMappingTable().getTableName() + "\" cat_c2f";
				categoryConditions =
					" AND cat_ct.\"entityId\" IN ("
					+ StringUtil.repeatedString("?", categoryIds.length, ", ")
					+ ") AND ft.\"entityId\" = cat_c2f.\"firstTurnId\" AND cat_c2f.\"categoryId\"=cat_ct.\"entityId\"";
			}
			while(true){
				PreparedStatement s = QueryUtil.prepareStatement(
						con, selectFirstTurnsAndSecondTurns
						, getTable().getTableName()
						, secondTurnTable.getTableName()
						, language.getCode()
						, subLanguages.length > 0 ?
								", " + QueryUtil.toColumnSelects("ft", subLanguages)
								: ""
						, opAndVal.getFirst()
						, categoryTables, categoryConditions
						, limit
						, offset
						);
				int index = 1;
				s.setString(index++, opAndVal.getSecond());
				for(int i = 0; i < categoryIds.length; i++){
					s.setLong(index++, categoryIds[0]);
				}
				long lastFtId = -1;
				try{
					ResultSet rs = s.executeQuery();
					try{
						while (rs.next()) {
							int i = 1;
							long id = rs.getLong(i++);
							lastFtId = id;
							String[] values = ResultSetUtil.getValues(
									rs, i, subLanguages.length + 1);
							i += subLanguages.length + 1;
							Calendar fct = ResultSetUtil.getCalendar(rs, i++);
							Calendar fut = ResultSetUtil.getCalendar(rs, i++);
							long sid = rs.getLong(i++);
							String[] svalues = ResultSetUtil.getValues(
									rs, i, subLanguages.length + 1);
							i += subLanguages.length + 1;
							Calendar sct = ResultSetUtil.getCalendar(rs, i++);
							Calendar sut = ResultSetUtil.getCalendar(rs, i++);
							collector.collectWithSecondTurn(
									id, values, fct, fut
									, sid, svalues, sct, sut
									);
							if(!collector.hasAdjacencyPair(id)) break;
						}
					} finally{
						rs.close();
					}
				} finally{
					s.close();
				}
				if(!collector.hasAdjacencyPair(lastFtId)) break;
				offset += limit;
			}

			offset = 0;
			while(true){
				// 1 - firstTurnTableName, 2 - categoryTableName
				// 3 - mappingTableName,
				// 4 - categoryIdColumnName(in mapping table)
				// 5 - firstTurnIdColumnName(in mapping table)
				// 6 - language, 7 - subLanguages
				// 8 - matching operation and value
				// 9 - category from, 10 - category conditions
				// 11 - max count
				PreparedStatement s = QueryUtil.prepareStatement(
						con, selectFirstTurnsAndCategories
						, getTable().getTableName(), getCategoryTable().getTableName()
						, getMappingTable().getTableName()
						, "categoryId", "firstTurnId"
						, language.getCode()
						, subLanguages.length > 0 ?
								", " + QueryUtil.toColumnSelects("ft", subLanguages)
								: ""
						, opAndVal.getFirst()
						, categoryTables, categoryConditions
						, limit
						, offset);
				s.setString(1, opAndVal.getSecond());
				for(int i = 0; i < categoryIds.length; i++){
					s.setLong(i + 2, categoryIds[0]);
				}
				long lastFtId = -1;
				try{
					ResultSet rs = s.executeQuery();
					try{
						while (rs.next()) {
							int i = 1;
							long id = rs.getLong(i++);
							lastFtId = id;
							String[] values = ResultSetUtil.getValues(
									rs, i, subLanguages.length + 1);
							i += subLanguages.length + 1;
							Calendar fct = ResultSetUtil.getCalendar(rs, i++);
							Calendar fut = ResultSetUtil.getCalendar(rs, i++);
							long cid = rs.getLong(i++);
							String[] cvalues = ResultSetUtil.getValues(
									rs, i, subLanguages.length + 1);
							i += subLanguages.length + 1;
							Calendar cct = ResultSetUtil.getCalendar(rs, i++);
							Calendar cut = ResultSetUtil.getCalendar(rs, i++);
							collector.collectWithCategory(
									id, values, fct, fut
									, cid, cvalues, cct, cut
									);
							if(!collector.hasAdjacencyPair(id)) break;
						}
					} finally{
						rs.close();
					}
				} finally{
					s.close();
				}
				if(!collector.hasAdjacencyPair(lastFtId)) break;
				offset += limit;
			}
			return collector.getAdjacencyPairs();
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			closeConnection(con);
		}
	}
/*
	public AdjacencyPair add(
			Language language, String firstTurn, String[] secondTurns
			, long[] categoryIds)
	throws DaoException
	{
		Calendar now = Calendar.getInstance();
		Timestamp nowTs = new Timestamp(now.getTimeInMillis());
		Connection con = beginTransaction();
		try {
			long id = getSequenceTable().next(con);
			PreparedStatement s = QueryUtil.prepareStatement(
					con, insertFirstTurn, getTableName()
					, QueryUtil.toColumnNames(language)
					);
			s.setLong(1, id);
			s.setString(2, firstTurn);
			s.setTimestamp(3, nowTs);
			s.setTimestamp(4, nowTs);
			s.executeUpdate();
			s.close();
			AdjacencyPair ap = new AdjacencyPair(
					id, firstTurn, new AdjacencyPair.SecondTurn[]{}
					, new Category[]{}, now, now);

			AdjacencyPair.SecondTurn[] sts
					= new AdjacencyPair.SecondTurn[secondTurns.length];
			for(int i = 0; i < sts.length; i++){
				long secondTurnId = generateSequence(con);
				s = QueryUtil.prepareStatement(
						con, insertSecondTurn, getTableName()
						, QueryUtil.toColumnNames(language)
						);
				s.setLong(1, secondTurnId);
				s.setLong(2, id);
				s.setString(3, secondTurns[i]);
				s.setTimestamp(4, nowTs);
				s.setTimestamp(5, nowTs);
				s.executeUpdate();
				s.close();
				sts[i] = ap.new SecondTurn(
						secondTurnId, secondTurns[i], now, now
						);
			}
			ap.setSecondTurns(sts);

			List<Category> cts = new ArrayList<Category>();
			if(categoryIds.length > 0){
				s = QueryUtil.prepareStatement(
						con, selectCategories, getTableName()
						, language.getCode()
						, StringUtil.repeatedString("?", categoryIds.length, ", ")
						);
				for(int i = 0; i < categoryIds.length; i++){
					s.setLong(i + 1, categoryIds[i]);
				}
				ResultSet rs = s.executeQuery();
				while(rs.next()){
					cts.add(new Category(
							rs.getLong(1), rs.getString(2)
							, CalendarUtil.createFromMillis(rs.getTimestamp(3).getTime())
							, CalendarUtil.createFromMillis(rs.getTimestamp(4).getTime())
							));
				}
				rs.close();
				s.close();
				for(Category c : cts){
					s = QueryUtil.prepareStatement(
							con, insertCategory_FirstTurn, getTableName()
							);
					s.setLong(1, c.getId());
					s.setLong(2, id);
					s.executeUpdate();
					s.close();
				}
			}
			ap.setCategories(cts.toArray(new Category[]{}));

			con.commit();
			return ap;
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			try{
				con.setAutoCommit(true);
			} catch(SQLException e){
			}
			closeConnection(con);
		}
	}
*/
	public long addAdjacencyPair(Language language, String firstTurn
			, String[] secondTurns, long[] categoryIds)
	throws DaoException{
		Language[] l = {language};
		long id = addFirstTurn(l, new String[]{firstTurn});
		for(String s : secondTurns){
			addSecondTurn(id, l, new String[]{s});			
		}
		updateFirstTurnCategories(id, categoryIds);
		return id;
	}

	public long addFirstTurn(Language[] languages, String[] firstTurns)
	throws DaoException{
		return insert(languages, firstTurns);
	}

	public long addSecondTurn(long firstTurnId, Language[] languages, String[] secondTurns)
	throws DaoException{
		Connection con = beginTransaction();
		try {
			long id = getSequenceTable().next(con);
			secondTurnTable.insert(
					con, id, firstTurnId
					, languages, secondTurns);
			commitTransaction(con);
			return id;
		} catch (SQLException e) {
			rollbackTransaction(con, e);
			throw new DaoException(e);
		}
	}

	public void updateFirstTurn(long firstTurnId
			, Language[] languages, String[] firstTurns)
	throws DaoException{
		update(firstTurnId, languages, firstTurns);
	}

	public void updateSecondTurn(long firstTurnId
			, long secondTurnId, Language[] languages, String[] secondTurns)
	throws DaoException{
		if(languages.length != secondTurns.length){
			throw new IllegalArgumentException(
					"length of languages and firstTurns must be same.");
		}
		Connection con = getConnection();
		try {
			secondTurnTable.update(con, firstTurnId, secondTurnId
					, languages, secondTurns);
			closeConnection(con);
		} catch (SQLException e) {
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	public void updateFirstTurnCategories(long firstTurnId, long[] categoryIds)
	throws DaoException{
		updateEntityCategories(firstTurnId, categoryIds);
	}

	public boolean deleteFirstTurn(long firstTurnId)
	throws DaoException{
		Connection c = beginTransaction();
		try{
			getMappingTable().deleteForSecond(c, firstTurnId);
			secondTurnTable.deleteChildrenOf(c, firstTurnId);
			boolean deleted = getTable().delete(c, firstTurnId);
			commitTransaction(c);
			return deleted;
		} catch(SQLException e){
			rollbackTransaction(c, e);
			throw new DaoException(e);
		}
	}

	public boolean deleteSecondTurn(long firstTurnId, long secondTurnId)
	throws DaoException{
		Connection con = getConnection();
		try {
			boolean deleted = secondTurnTable.delete(con, secondTurnId, firstTurnId);
			closeConnection(con);
			return deleted;
		} catch (SQLException e) {
			closeConnection(con, e);
			throw new DaoException(e);
		}
	}

	private static class AdjacencyPairCollector{
		public AdjacencyPairCollector(int maxCount){
			this.maxCount = maxCount;
		}

		public boolean hasAdjacencyPair(long ftId){
			return aps.containsKey(ftId);
		}

		public void collectWithSecondTurn(
				long ftId, String[] ft, Calendar ftCt, Calendar ftUt
				, long stId, String[] st, Calendar stCt, Calendar stUt){
			AdjacencyPair ap = aps.get(ftId);
			if(ap == null){
				if(aps.size() >= maxCount) return;
				ap = new AdjacencyPair(
						ftId, ft, new AdjacencyPair.SecondTurn[]{}
						, new Category[]{}, ftCt, ftUt
						);
				aps.put(ftId, ap);
			}
			List<AdjacencyPair.SecondTurn> sts
				= ftIdToSecondTurns.get(ftId);
			if(sts == null){
				sts = new ArrayList<AdjacencyPair.SecondTurn>();
				ftIdToSecondTurns.put(ftId, sts);
			}
			sts.add(new AdjacencyPair.SecondTurn(
					stId, ftId, st, stCt, stUt
					));
		}

		public void collectWithCategory(
				long ftId, String ft[], Calendar ftCt, Calendar ftUt
				, long ctId, String ct[], Calendar ctCt, Calendar ctUt){
			AdjacencyPair ap = aps.get(ftId);
			if(ap == null){
				if(aps.size() >= maxCount) return;
				ap = new AdjacencyPair(
						ftId, ft, new AdjacencyPair.SecondTurn[]{}
						, new Category[]{}, ftCt, ftUt
						);
				aps.put(ftId, ap);
			}
			List<Category> cts
				= ftIdToCategories.get(ftId);
			if(cts == null){
				cts = new ArrayList<Category>();
				ftIdToCategories.put(ftId, cts);
			}
			cts.add(new Category(
					ctId, ct, ctCt, ctUt
					));
		}

		public Collection<AdjacencyPair> getAdjacencyPairs(){
			Collection<AdjacencyPair> ret = aps.values();
			for(AdjacencyPair ap : ret){
				List<AdjacencyPair.SecondTurn> sts = ftIdToSecondTurns.get(
						ap.getId()
						);
				if(sts != null){
					ap.setSecondTurns(sts.toArray(
							new AdjacencyPair.SecondTurn[]{}
							));
				} else{
					ap.setSecondTurns(new AdjacencyPair.SecondTurn[]{});
				}
				List<Category> cts = ftIdToCategories.get(
						ap.getId()
						);
				if(cts != null){
					ap.setCategories(cts.toArray(new Category[]{}));
				} else{
					ap.setCategories(new Category[]{});
				}
			}
			return ret;
		}

		private int maxCount;
		private Map<Long, List<AdjacencyPair.SecondTurn>> ftIdToSecondTurns
				= new HashMap<Long, List<AdjacencyPair.SecondTurn>>();
		private Map<Long, List<Category>> ftIdToCategories
				= new HashMap<Long, List<Category>>();
		private Map<Long, AdjacencyPair> aps
				= new LinkedHashMap<Long, AdjacencyPair>();
	}

	private ChildEntityTable<AdjacencyPair.SecondTurn> secondTurnTable;

	// 1 - firstTurnTableName, 2 - secondTurnTableName
	// 3 - language, 4 - subLanguages
	// 5 - matching operation and value
	// 6 - category from, 7 - category conditions
	// 8 - max count
	private static String selectFirstTurnsAndSecondTurns =
		"SELECT ft.\"entityId\", ft.\"%3$s\"%4$s, ft.\"createdDateTime\", ft.\"updatedDateTime\"" +
		"   , st.\"entityId\", st.\"%3$s\"%4$s, st.\"createdDateTime\", st.\"updatedDateTime\"" +
		" FROM \"%1$s\" ft, \"%2$s\" st" +
	    "   %6$s" +
		" WHERE LOWER(ft.\"%3$s\") %5$s AND ft.\"entityId\" = st.\"parentId\"" +
		"   %7$s" +
		" ORDER BY ft.\"%3$s\", st.\"%3$s\"" +
		" LIMIT %8$d OFFSET %9$d";

	// 1 - firstTurnTableName, 2 - categoryTableName
	// 3 - mappingTableName,
	// 4 - categoryIdColumnName(in mapping table)
	// 5 - firstTurnIdColumnName(in mapping table)
	// 6 - language, 7 - subLanguages
	// 8 - matching operation and value
	// 9 - category from, 10 - category conditions
	// 11 - max count
	private static String selectFirstTurnsAndCategories =
		"SELECT ft.\"entityId\", ft.\"%6$s\"%7$s, ft.\"createdDateTime\", ft.\"updatedDateTime\"" +
		"   , ct.\"entityId\", ct.\"%6$s\"%7$s, ct.\"createdDateTime\", ct.\"updatedDateTime\"" +
		" FROM \"%1$s\" ft, \"%2$s\" ct, \"%3$s\" m" +
	    "   %9$s" +
		" WHERE LOWER(ft.\"%6$s\") %8$s AND ft.\"entityId\" = m.\"%5$s\"" +
		"   AND m.\"%4$s\" = ct.\"entityId\"" +
		"   %10$s" +
		" ORDER BY ft.\"%6$s\", ct.\"%6$s\"" +
		" LIMIT %11$d OFFSET %12$d";
}
