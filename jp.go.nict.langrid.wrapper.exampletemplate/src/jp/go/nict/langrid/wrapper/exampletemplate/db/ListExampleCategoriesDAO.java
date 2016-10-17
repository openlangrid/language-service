/*
 * $Id: DictionaryDataBase.java 4596 2009-01-23 09:22:05Z kkadowaki $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.exampletemplate.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;

import jp.go.nict.langrid.commons.lang.ExceptionUtil;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleCategory;
import jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank;
/**
 * <#if locale="ja">
 * データベース検索クラス
 * <#elseif locale="en">
 * </#if>
 */
public class ListExampleCategoriesDAO {
	/**
	 * <#if locale="ja">
	 * データベースマネージャ
	 * <#elseif locale="en">
	 * </#if>
	 */
	private ConnectionManager manager;
	/**
	 * <#if locale="ja">
	 * テーブル名
	 * <#elseif locale="en">
	 * </#if>
	 */
	private static final String TABLE_NAME = "parallel_text_category";
	/**
	 * <#if locale="ja">
	 * 穴あき用例カテゴリID列名
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String categoryId = "category_id";
	/**
	 * <#if locale="ja">
	 * 穴あき用例カテゴリ列名
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String exampleCategory = "en";
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * @param headLang 現在の言語コード
	 * @param targetLang 翻訳する言語コード
	 * @param matchingMethod 一致条件
	 */
	public ListExampleCategoriesDAO(ConnectionManager manager) {
		this.manager = manager;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（対訳IDを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 */
	public Collection<ExampleCategory> listExampleCategories() throws SQLException {
		return executeQuery(createSQL());
	}
	
	/*
	 * SQL文を実行し、ParalellText配列に格納して返却
	 */
	private Collection<ExampleCategory> executeQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<ExampleCategory> collection = new ArrayList<ExampleCategory>();
			while (rs.next()) {
				String id = rs.getString(categoryId);
				String en = rs.getString(exampleCategory);
				ExampleCategory text = new ExampleCategory(id, en);
				collection.add(text);
			}
			return collection;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					;
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					;
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					;
				}
			}
		}
	}

	/*
	 * SQL文を生成
	 */
	private String createSQL() {
		String categoryIdColumn = DataBaseUtil.getColumnName(categoryId);
		String exampleCategoryColumn = DataBaseUtil.getColumnName(exampleCategory);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(categoryIdColumn);
		buf.append(", ");
		buf.append(exampleCategoryColumn);
		buf.append(" FROM \"");
		buf.append(TABLE_NAME);
		buf.append("\" order by ");
		buf.append(categoryIdColumn);
		
		String sql = buf.toString();
		logger.info("createSQL()=" + sql);
		return sql;
	}
	protected Logger logger = Logger.getLogger(ListExampleCategoriesDAO.class.getName());
}
