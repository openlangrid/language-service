/*
 * $Id: ParallelTextWithIdDao.java 266 2010-10-03 10:33:59Z t-nakaguchi $
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.Category;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.ParallelTextWithId;
import jp.go.nict.langrid.wrapper.common.db.dao.table.BasicEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.CategoryTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.MappingTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.SequenceTable;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class ParallelTextWithIdDao
extends CategorizedEntityDao<ParallelTextWithId, BasicEntityTable<ParallelTextWithId>>
{
	/**
	 * 
	 * 
	 */
	public ParallelTextWithIdDao(
			ConnectionManager connectionManager
			, DbDictionary dbDictionary
			, String tableName, int maxCount){
		super(connectionManager
				, new BasicEntityTable<ParallelTextWithId>(
						dbDictionary, tableName + "_texts", maxCount)
				, new CategoryTable(
						dbDictionary, tableName + "_categories", maxCount)
				, new MappingTable(tableName + "_category_text"
						, "categoryId", "parallelTextId")
				, new SequenceTable(tableName + "_seq")
				);
	}

	public Collection<ParallelTextWithId> searchParallelTextWithIds(
			Language sourceLang, final Language[] targetLangs
			, String matchingValue, MatchingMethod matchingMethod
			, long[] categoryIds)
	throws DaoException{
		return searchEntitiesOfCategories(sourceLang, targetLangs
				, matchingValue, matchingMethod, categoryIds
				, new Transformer<ResultSet, ParallelTextWithId>(){
			public ParallelTextWithId transform(ResultSet value)
					throws TransformationException {
				try{
					int index = 1;
					long id = value.getLong(index++);
					String[] texts = ResultSetUtil.getValues(
							value, index, targetLangs.length + 1);
					index += targetLangs.length + 1;
					return new ParallelTextWithId(
							id, texts, new Category[]{}
							, ResultSetUtil.getCalendar(value, index)
							, ResultSetUtil.getCalendar(value, index + 1)
							);
				} catch(SQLException e){
					throw new TransformationException(e);
				}
			}
		});
	}

	public long addParallelTextWithId(Language[] languages, String[] texts)
	throws DaoException{
		return insert(languages, texts);
	}

	public boolean deleteParallelTextWithId(long id)
	throws DaoException{
		return delete(id);
	}

	public boolean updateParallelTextWithId(long id, Language[] languages, String[] texts)
	throws DaoException{
		return update(id, languages, texts);
	}

	public void updateParallelTextWithIdCategories(long parallelTextWithIdId, long[] categoryIds)
	throws DaoException{
		updateEntityCategories(parallelTextWithIdId, categoryIds);
	}
}
