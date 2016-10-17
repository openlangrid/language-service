/*
 * $Id: BilingualDictionaryDao.java 79 2010-04-22 05:47:32Z t-nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2008 NICT Language Grid Project.
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

import java.io.PrintStream;
import java.sql.Connection;
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
import jp.go.nict.langrid.wrapper.common.db.dao.entity.Translation;
import jp.go.nict.langrid.wrapper.common.db.dao.table.BasicEntityTable;
import jp.go.nict.langrid.wrapper.common.db.dao.table.SequenceTable;

public class BilingualDictionaryDao
extends BasicEntityDao<Translation, BasicEntityTable<Translation>>{
	public BilingualDictionaryDao(ConnectionManager connectionManager
			, DbDictionary dbDictionary, String tableName, int maxCount){
		super(connectionManager
				, new BasicEntityTable<Translation>(
						dbDictionary, tableName + "_translations", maxCount)
				, new SequenceTable(tableName + "_seq")
				);
	}

	public Collection<Translation> searchTranslations(
			Language headLang, Language[] targetLangs
			, String headWord, MatchingMethod matchingMethod
			)
			throws DaoException
	{
		return search(headLang, targetLangs
				, headWord, matchingMethod
				, new ResultSetToTranslationTransformer(targetLangs.length + 1)
				);
	}

	public long addTranslation(Language[] languages, String[] translations)
	throws DaoException{
		return insert(languages, translations);
	}

	public boolean deleteTranslation(long id)
	throws DaoException{
		return delete(id);
	}

	public void updateTranslation(long id, Language[] languages, String[] translations)
	throws DaoException{
		update(id, languages, translations);
	}

	public void dump(PrintStream ps)
	throws DaoException{
		Connection c = getConnection();
		try{
			String query = String.format(
					"select * from \"%s\""
					, getTable().getTableName()
					);
			ResultSet rs = c.createStatement().executeQuery(query);
			try{
				if(rs.next()){
					int n = rs.getMetaData().getColumnCount();
					for(int i = 0; i < n; i++){
						ps.print("  |  " + rs.getMetaData().getColumnName(i + 1));
					}
					ps.println("  |");
					do{
						for(int i = 0; i < n; i++){
							ps.print("  |  " + rs.getString(i + 1));
						}
						ps.println("  |");
					} while(rs.next());
				}
			} finally{
				rs.close();
			}
			closeConnection(c);
		} catch(SQLException e){
			closeConnection(c, e);
			throw new DaoException(e);
		}
	}

	private static class ResultSetToTranslationTransformer
	implements Transformer<ResultSet, Translation>{
		public ResultSetToTranslationTransformer(int translationCount){
			this.translationCount = translationCount;
		}
		public Translation transform(ResultSet value)
				throws TransformationException {
			try{
				int index = 1;
				long id = value.getLong(index++);
				String[] translations = new String[translationCount];
				for(int i = 0; i < translationCount; i++){
					translations[i] = value.getString(index++);
				}
				return new Translation(
						id, translations
						, CalendarUtil.createFromMillis(value.getTimestamp(index).getTime())
						, CalendarUtil.createFromMillis(value.getTimestamp(index + 1).getTime())
						);
			} catch(SQLException e){
				throw new TransformationException(e);
			}
		}
		private int translationCount;
	}
}
