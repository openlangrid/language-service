/*
 * $Id: ParallelTextDao.java 266 2010-10-03 10:33:59Z t-nakaguchi $
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
import jp.go.nict.langrid.commons.util.CalendarUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.common.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.common.db.DbDictionary;
import jp.go.nict.langrid.wrapper.common.db.dao.entity.ParallelText;
import jp.go.nict.langrid.wrapper.common.db.dao.table.BasicEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.SequenceTable;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class ParallelTextDao
extends BasicEntityDao<ParallelText, BasicEntityTable<ParallelText>>
{
	/**
	 * 
	 * 
	 */
	public ParallelTextDao(
			ConnectionManager connectionManager
			, DbDictionary dbDictionary
			, String tableName, int maxCount){
		super(connectionManager
				, new BasicEntityTable<ParallelText>(
						dbDictionary, tableName + "_texts", maxCount)
				, new SequenceTable(tableName + "_seq")
				);
	}

	public Collection<ParallelText> searchParallelTexts(
			Language sourceLang, Language[] targetLangs
			, String source, MatchingMethod matchingMethod)
	throws DaoException{
		return search(sourceLang, targetLangs
				, source, matchingMethod
				, new ParallelTextTransformer(targetLangs.length + 1)
				);
	}

	public long addParallelText(
			Language[] languages, String[] texts)
	throws DaoException{
		return insert(languages, texts);
	}

	public boolean deleteParallelText(long id)
	throws DaoException{
		return delete(id);
	}

	public boolean updateParallelText(long id, Language[] languages, String[] texts)
	throws DaoException{
		return update(id, languages, texts);
	}

	private static class ParallelTextTransformer implements Transformer<ResultSet, ParallelText>{
		public ParallelTextTransformer(int textCount){
			this.textCount = textCount;
		}
		public ParallelText transform(ResultSet value)
				throws TransformationException {
			try{
				int index = 1;
				long id = value.getLong(index++);
				String[] texts = new String[textCount];
				for(int i = 0; i < textCount; i++){
					texts[i] = value.getString(index++);
				}
				return new ParallelText(
						id, texts
						, CalendarUtil.createFromMillis(value.getTimestamp(index).getTime())
						, CalendarUtil.createFromMillis(value.getTimestamp(index + 1).getTime())
						);
			} catch(SQLException e){
				throw new TransformationException(e);
			}
		}
		private int textCount;
	}
}
