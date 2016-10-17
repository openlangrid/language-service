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
import jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWord;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleCategory;
import jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank;
/**
 * <#if locale="ja">
 * データベース検索クラス
 * <#elseif locale="en">
 * </#if>
 */
public class GetApplicableWordsDAO {
	/**
	 * <#if locale="ja">
	 * データベースマネージャ
	 * <#elseif locale="en">
	 * </#if>
	 */
	private ConnectionManager manager;
	/**
	 * <#if locale="ja">
	 * 穴あき用例カテゴリID列名
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String wordId = "word_id";
	/**
	 * <#if locale="ja">
	 * 穴あき用例カテゴリ列名
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String headLang ;
	private String ids;
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * @param headLang 現在の言語コード
	 * @param targetLang 翻訳する言語コード
	 * @param matchingMethod 一致条件
	 */
	public GetApplicableWordsDAO(String headLang, ConnectionManager manager) {
		this.headLang = headLang;
		this.manager = manager;
	}
	
	public Collection<ApplicableWord> getApplicableWords(String[] categoryIds) throws SQLException {
		StringBuffer sb = new StringBuffer();
		for (String id : categoryIds) {
			sb.append("'" + id + "'");
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		ids = sb.toString();
		
		return executeQuery(createSQL());
	}
	
	/*
	 * SQL文を実行し、ParalellText配列に格納して返却
	 */
	private Collection<ApplicableWord> executeQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<ApplicableWord> collection = new ArrayList<ApplicableWord>();
			while (rs.next()) {
				String id = rs.getString(wordId);
				String word = rs.getString(headLang);
				ApplicableWord text = new ApplicableWord(id, word);
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
		String headLangColumn = DataBaseUtil.getColumnName(headLang);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT");
		buf.append("  \"applicable_word\".\"word_id\", ");
		buf.append("  \"applicable_word\".");
		buf.append(headLangColumn);
		buf.append(" FROM ");
		buf.append("  \"applicable_word_category_relation\"");
		buf.append("  LEFT JOIN ");
		buf.append("  \"applicable_word\"");
		buf.append("  ON \"applicable_word_category_relation\".\"word_id\"");
		buf.append("   = \"applicable_word\".\"word_id\"");
		buf.append(" WHERE");
		buf.append("  \"applicable_word_category_relation\".\"category_id\" IN ({0})");		
		
//		buf.append(" SELECT ");
//		buf.append(wordIdColumn);
//		buf.append(", ");
//		buf.append(headLangColumn);
//		buf.append(" FROM \"");
//		buf.append(TABLE_NAME + "\" ");
//		buf.append(" WHERE ");
//		buf.append(wordIdColumn);
//		buf.append(" IN ({0})");
//		buf.append(" order by ");
//		buf.append(wordIdColumn);
		
		String sql = MessageFormat.format(buf.toString(), ids);
		logger.info("createSQL()=" + sql);
		return sql;
	}
	protected Logger logger = Logger.getLogger(GetApplicableWordsDAO.class.getName());
}
