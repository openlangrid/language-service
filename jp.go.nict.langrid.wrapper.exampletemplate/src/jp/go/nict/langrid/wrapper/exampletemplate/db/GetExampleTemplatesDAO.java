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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.exampletemplate.service.Blank;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate;
import jp.go.nict.langrid.wrapper.exampletemplate.service.WordCategory;
/**
 * <#if locale="ja">
 * データベース検索クラス
 * <#elseif locale="en">
 * </#if>
 */
public class GetExampleTemplatesDAO {
	/**
	 * <#if locale="ja">
	 * 最大結果件数
	 * <#elseif locale="en">
	 * </#if>
	 */
	private static final int LIMIT = 10;
	/**
	 * <#if locale="ja">
	 * 開始オフセット
	 * <#elseif locale="en">
	 * </#if>
	 *
	private int OFFSET = 0;
	*/
	/**
	 * <#if locale="ja">
	 * データベースマネージャ
	 * <#elseif locale="en">
	 * </#if>
	 */
	private ConnectionManager manager;
	/**
	 * <#if locale="ja">
	 * 現在の言語コード
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String sourceLang;
	/**
	 * <#if locale="ja">
	 * 一致条件
	 * <#elseif locale="en">
	 * </#if>
	 */
	private MatchingMethod matchingMethod;
	/**
	 * <#if locale="ja">
	 * ID条件列
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String whereIdsColumn;
	/**
	 * <#if locale="ja">
	 * ID条件値
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String whereIdsValue;
	/**
	 * <#if locale="ja">
	 * 曖昧検索列
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String whereTextColumn;
	/**
	 * <#if locale="ja">
	 * 曖昧検索値
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String whereTextValue;
	/**
	 * <#if locale="ja">
	 * 曖昧検索語句
	 * <#elseif locale="en">
	 * </#if>
	 */
	private static final char PERCENT = '%';
//	private Connection con;
	
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * @param headLang 現在の言語コード
	 * @param targetLang 翻訳する言語コード
	 * @param matchingMethod 一致条件
	 */
	public GetExampleTemplatesDAO(
			String headLang, MatchingMethod matchingMethod, 
			ConnectionManager manager) {
		this.sourceLang = headLang;
		this.matchingMethod = matchingMethod;
		this.manager = manager;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例リストの取得（カテゴリID指定）
	 * @param categoryIds
	 * @return
	 * @throws SQLException
	 * </#if>
	 */
	public Collection<ExampleTemplate> getExampleTemplatesByCategoryIds(
			String[] categoryIds) throws SQLException {
		this.whereIdsColumn = "\"category_id\"";
		StringBuilder sb = new StringBuilder();
		for (String id : categoryIds) {
			sb.append("'");
			sb.append(id);
			sb.append("'");
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		this.whereIdsValue = sb.toString();
		
		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE \"parallel_text_category_relation\".");
		sql.append(whereIdsColumn);
		sql.append(" IN (" + whereIdsValue + ")");
		
		Collection<ExampleTemplate> results = executeQuery(sql.toString());
		// *** 穴あき部分の選択情報検索
		for (ExampleTemplate template : results) {
			template.setBlanks(findBlanks(template.getExample()));
		}
		return results;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例リストの取得（用例ID指定）
	 * @param exampleIds
	 * @return
	 * @throws SQLException
	 * </#if>
	 */
	public Collection<ExampleTemplate> getExampleTemplateByExampleIds(
			String[] exampleIds) throws SQLException {
		this.whereIdsColumn = "\"example_id\"";
		StringBuilder sb = new StringBuilder();
		for (String id : exampleIds) {
			sb.append("'");
			sb.append(id);
			sb.append("'");
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		this.whereIdsValue = sb.toString();
		
		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE \"parallel_text_category_relation\".");
		sql.append(whereIdsColumn);
		sql.append(" IN (" + whereIdsValue + ")");
		
		Collection<ExampleTemplate> results = executeQuery(sql.toString());
		// *** 穴あき部分の選択情報検索
		for (ExampleTemplate template : results) {
			template.setBlanks(findBlanks(template.getExample()));
		}
		return results;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例リストの取得（テキスト曖昧検索）
	 * @param text
	 * @return
	 * @throws SQLException
	 * </#if>
	 */
	public Collection<ExampleTemplate> getExampleTemplatesByText(
			String text) throws SQLException {
		this.whereTextColumn = DataBaseUtil.getColumnName(this.sourceLang);
		this.whereTextValue = text;
		
		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE \"parallel_text\".");
		sql.append(whereTextColumn);
		if (matchingMethod.equals(MatchingMethod.COMPLETE)) {
			sql.append(" = ''{0}'' ");
		} else {
			sql.append(" LIKE ''{0}'' ");
			if (matchingMethod.equals(MatchingMethod.PARTIAL)) {
				whereTextValue = PERCENT + whereTextValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.PREFIX)) {
				whereTextValue = whereTextValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.SUFFIX)) {
				whereTextValue = PERCENT + whereTextValue.toLowerCase();
			}
		}
		
		String query = MessageFormat.format(sql.toString(), whereTextValue);
		Collection<ExampleTemplate> results = executeQuery(query);
		// *** 穴あき部分の選択情報検索
		for (ExampleTemplate template : results) {
			template.setBlanks(findBlanks(template.getExample()));
		}
		return results;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例リストの取得（カテゴリID指定、テキスト曖昧検索）
	 * @param categoryIds
	 * @param text
	 * @return
	 * @throws SQLException
	 * </#if>
	 */
	public Collection<ExampleTemplate> getExampleTemplates(
			String[] categoryIds, String text) throws SQLException {
		this.whereIdsColumn = "\"category_id\"";
		StringBuilder sb = new StringBuilder();
		for (String id : categoryIds) {
			sb.append("'");
			sb.append(id);
			sb.append("'");
			sb.append(", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		this.whereIdsValue = sb.toString();
		
		this.whereTextColumn = DataBaseUtil.getColumnName(this.sourceLang);
		this.whereTextValue = text;
		
		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE \"parallel_text_category_relation\".");
		sql.append(whereIdsColumn);
		sql.append(" IN (" + whereIdsValue + ")");
		sql.append(" AND \"parallel_text\".");
		sql.append(whereTextColumn);
		if (matchingMethod.equals(MatchingMethod.COMPLETE)) {
			sql.append(" = ''{0}'' ");
		} else {
			sql.append(" LIKE ''{0}'' ");
			if (matchingMethod.equals(MatchingMethod.PARTIAL)) {
				whereTextValue = PERCENT + whereTextValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.PREFIX)) {
				whereTextValue = whereTextValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.SUFFIX)) {
				whereTextValue = PERCENT + whereTextValue.toLowerCase();
			}
		}
		
		String query = MessageFormat.format(sql.toString(), whereTextValue);
		Collection<ExampleTemplate> results = executeQuery(query);
		// *** 穴あき部分の選択情報検索
		for (ExampleTemplate template : results) {
			template.setBlanks(findBlanks(template.getExample()));
		}
		
		return results;
	}
	
	/*
	 * SQL文を実行し、ExampleTemplate配列に格納して返却
	 */
	private Collection<ExampleTemplate> executeQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<ExampleTemplate> collection = new ArrayList<ExampleTemplate>();
			
			String tmpId = null;
			String tmp = null;
			List<String> categoryIds = new ArrayList<String>();
			while (rs.next()) {
				String exampleId = rs.getString("example_id");
				String example = rs.getString(sourceLang);
				String categoryId = rs.getString("category_id");
				if (tmpId != null && !tmpId.equals(exampleId)) {
					ExampleTemplate exampleTemplate = new ExampleTemplate(tmpId, tmp);
					exampleTemplate.setCategoryIds(categoryIds.toArray(new String[]{}));
					collection.add(exampleTemplate);
					categoryIds = new ArrayList<String>();
				}
				tmpId = exampleId;
				tmp = example;
				categoryIds.add(categoryId);
			}
			if (tmpId != null && categoryIds.size() > 0) {
				ExampleTemplate exampleTemplate = new ExampleTemplate(tmpId, tmp);
				exampleTemplate.setCategoryIds(categoryIds.toArray(new String[]{}));
				collection.add(exampleTemplate);
				categoryIds = new ArrayList<String>();
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
	 * SQLを生成（SELECT句のみ）
	 */
	private String createSelectSQL() {
		String headLangColumn = DataBaseUtil.getColumnName(sourceLang);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append(" SELECT ");
		buf.append(" \"parallel_text_category_relation\".\"example_id\",");
		buf.append(" \"parallel_text_category_relation\".\"category_id\",");
		buf.append(" \"parallel_text\".");
		buf.append(headLangColumn);
		buf.append(" FROM \"parallel_text_category_relation\"");
		buf.append(" LEFT JOIN \"parallel_text\" ");
		buf.append("ON \"parallel_text_category_relation\".\"example_id\" ");
		buf.append(" = \"parallel_text\".\"example_id\" ");
		
		return buf.toString();
	}
	
	/*
	 * Blank情報を検索
	 */
	private Blank[] findBlanks(String text) throws SQLException {
		Collection<Blank> collection = new ArrayList<Blank>();
		String regx = "<PID_NO=\\\"(\\d)\\\">([0-9,]+)</PID>";
		Pattern pattern = Pattern.compile(regx);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String pid = matcher.group(1);
			String wordCategorys = matcher.group(2);
			Blank blank = new Blank(pid, findWordCategorys(wordCategorys).toArray(new WordCategory[0]));
			collection.add(blank);
		}
		return collection.toArray(new Blank[0]);
	}
	
	/*
	 * word検索用SQL文を生成
	 */
	public Collection<WordCategory> findWordCategorys(String wordIds) throws SQLException{
		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(" \"category_id\", \"en\" ");
		buf.append(" FROM \"");
		buf.append("applicable_word_category");
		buf.append("\" WHERE ");
		buf.append(" \"category_id\" in ({0})");

		String sql = MessageFormat.format(buf.toString(), wordIds);
		logger.info("findWordCategorys()=" + sql);
		return executeWordCategorysQuery(sql);
	}
	
	/*
	 * word検索SQLを実行
	 */
	private Collection<WordCategory> executeWordCategorysQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<WordCategory> collection = new ArrayList<WordCategory>();
			
			while (rs.next()) {
				String categoryId = rs.getString("category_id");
				String en = rs.getString("en");
				collection.add(new WordCategory(categoryId, en));
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

	//----------------------------------- 以下 Getter, Setter
	/**
	 * <#if locale="ja">
	 * @return the headLang
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getHeadLang() {
		return sourceLang;
	}

	/**
	 * <#if locale="ja">
	 * @param headLang the headLang to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setHeadLang(String headLang) {
		this.sourceLang = headLang;
	}

	/**
	 * <#if locale="ja">
	 * @return the mathingMethod
	 * <#elseif locale="en">
	 * </#if>
	 */
	public MatchingMethod getMathingMethod() {
		return matchingMethod;
	}

	/**
	 * <#if locale="ja">
	 * @param mathingMethod the mathingMethod to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setMathingMethod(MatchingMethod mathingMethod) {
		this.matchingMethod = mathingMethod;
	}
	/**
	 * <#if locale="ja">
	 * @return the matchingMethod
	 * <#elseif locale="en">
	 * </#if>
	 */
	public MatchingMethod getMatchingMethod() {
		return matchingMethod;
	}
	/**
	 * <#if locale="ja">
	 * @param matchingMethod the matchingMethod to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setMatchingMethod(MatchingMethod matchingMethod) {
		this.matchingMethod = matchingMethod;
	}
	
	protected Logger logger = Logger.getLogger(GetExampleTemplatesDAO.class.getName());
}
