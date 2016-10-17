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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
/**
 * <#if locale="ja">
 * データベース検索クラス
 * <#elseif locale="en">
 * </#if>
 */
public class DictionaryDataBase {
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
	 * 翻訳対象文字列
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String headword;
	/**
	 * <#if locale="ja">
	 * 現在の言語コード
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String headLang;
	/**
	 * <#if locale="ja">
	 * 翻訳先言語コード
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String targetLang;
	/**
	 * <#if locale="ja">
	 * テーブル名称
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String tableName;
	/**
	 * <#if locale="ja">
	 * データベース列
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String[] columns;
	
	/**
	 * <#if locale="ja">
	 * 一致条件
	 * <#elseif locale="en">
	 * </#if>
	 */
	private MatchingMethod matchingMethod;
	/**
	 * <#if locale="ja">
	 * 曖昧検索語句
	 * <#elseif locale="en">
	 * </#if>
	 */
	private static final char PERCENT = '%';
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * @param tableName テーブル名称
	 * @param columns テーブルカラム
	 * @param headLang 現在の言語コード
	 * @param targetLang 翻訳する言語コード
	 * @param headword 翻訳対象メッセージ
	 * @param matchingMethod 一致条件
	 * <#elseif locale="en">
	 * </#if>
	 */
	public DictionaryDataBase(String tableName, String[] columns, ConnectionManager manager, String headLang, String targetLang, String headword, MatchingMethod matchingMethod) {
		this.tableName = tableName;
		this.columns = columns;
		this.manager = manager;
		this.headLang = headLang;
		this.targetLang = targetLang;
		this.headword = headword;
		this.matchingMethod = matchingMethod;
	}
	/**
	 * <#if locale="ja">
	 * 翻訳結果を返します。
	 * @return 翻訳結果
	 * @throws SQLException 
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Collection<Translation> getTranslation() throws SQLException {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			headLang = DataBaseUtil.checkColumn(headLang, columns);
			targetLang = DataBaseUtil.checkColumn(targetLang, columns);
			Collection<Translation> collection = new ArrayList<Translation>();
			con = manager.getConnection();
			pst = con.prepareStatement(createSQL());
			int i = 1;
			pst.setString(i++, headword);
			rs = pst.executeQuery();
			Translation current = new Translation();
			Set<String> currentTargetWords = new HashSet<String>();
			while (rs.next()) {
				String head = rs.getString(headLang);
				String target = rs.getString(targetLang);
				if(head == null || head.trim().length() == 0) continue;
				if(target == null || target.trim().length() == 0) continue;

				if(!head.equals(current.getHeadWord())){
					if(current.getHeadWord() != null){
						current.setTargetWords(currentTargetWords.toArray(new String[]{}));
						collection.add(current);
					}
					current = new Translation(head, new String[]{});
					currentTargetWords.clear();
				}
				currentTargetWords.add(target);
			}
			if(current.getHeadWord() != null){
				current.setTargetWords(currentTargetWords.toArray(new String[]{}));
				collection.add(current);
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
			if (pst != null) {
				try {
					pst.close();
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
	/**
	 * <#if locale="ja">
	 * 翻訳結果を返します。
	 * @return 翻訳結果
	 * @throws SQLException 
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Collection<ParallelText> getParallelText() throws SQLException {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			headLang = DataBaseUtil.checkColumn(headLang, columns);
			targetLang = DataBaseUtil.checkColumn(targetLang, columns);
			Collection<ParallelText> collection = new ArrayList<ParallelText>();
			con = manager.getConnection();
			pst = con.prepareStatement(createSQL());
			int i = 1;
			pst.setString(i++, headword);
			rs = pst.executeQuery();
			while (rs.next()) {
				String head = rs.getString(headLang);
				String target = rs.getString(targetLang);
				ParallelText text = new ParallelText(head, target);
				collection.add(text);
//				System.out.println(head + "," + target);
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
			if (pst != null) {
				try {
					pst.close();
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

	/**
	 * <#if locale="ja">
	 * SQL文を作成します
	 * @return 作成後SQL
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String createSQL() {
		String headLangColumn = DataBaseUtil.getColumnName(headLang);
		String targetLangColumn = DataBaseUtil.getColumnName(targetLang);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(headLangColumn);
		buf.append(", ");
		buf.append(targetLangColumn);
		buf.append(" FROM \"");
		buf.append(tableName);
		buf.append("\" WHERE ");
		buf.append(" LOWER(");
		buf.append(headLangColumn);
		buf.append(")");
		if (matchingMethod.equals(MatchingMethod.COMPLETE)) {
			buf.append(" = ? ");
		} else {
			buf.append(" LIKE ? ");
			if (matchingMethod.equals(MatchingMethod.PARTIAL)) {
				headword = PERCENT + headword.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.PREFIX)) {
				headword = headword.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.SUFFIX)) {
				headword = PERCENT + headword.toLowerCase();
			}
		}
		buf.append(" order by ");
		buf.append(headLangColumn);
/*
		buf.append(" OFFSET ");
		buf.append(OFFSET);
		buf.append(" LIMIT ");
		buf.append(LIMIT);
*/
		return buf.toString();
	}
	public static void main(String[] arg) {
		try {
			Language sourceLang = new Language("ja");
			Language targetLang = new Language("en");
			String headword = "翻訳精";
			String tableName = "local_dictionary";
			String[] columns = new String[]{"ja", "en", "ko", "zh-CN"};
			String driverName = "org.postgresql.Driver";
			String url = "jdbc:postgresql://localhost:5432/langrid";
			String username = "postgres";
			String password = "postgres";
			ConnectionManager manager = new ConnectionManager(
					driverName, url, username, password, 1, 1, 10000, -1);
			DictionaryDataBase dic = new DictionaryDataBase(tableName, columns, manager, sourceLang.getCode(), targetLang.getCode(), headword, MatchingMethod.PARTIAL);
			dic.getTranslation();
		} catch (InvalidLanguageTagException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
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
		return headLang;
	}

	/**
	 * <#if locale="ja">
	 * @param headLang the headLang to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setHeadLang(String headLang) {
		this.headLang = headLang;
	}

	/**
	 * <#if locale="ja">
	 * @return the targetLang
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getTargetLang() {
		return targetLang;
	}

	/**
	 * <#if locale="ja">
	 * @param targetLang the targetLang to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setTargetLang(String targetLang) {
		this.targetLang = targetLang;
	}

	/**
	 * <#if locale="ja">
	 * @return the tableName
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * <#if locale="ja">
	 * @param tableName the tableName to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
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
	 * @return the headword
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getHeadword() {
		return headword;
	}
	/**
	 * <#if locale="ja">
	 * @param headword the headword to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setHeadword(String headword) {
		this.headword = headword;
	}
	/**
	 * <#if locale="ja">
	 * @return the columns
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String[] getColumns() {
		return columns;
	}
	/**
	 * <#if locale="ja">
	 * @param columns the columns to set
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setColumns(String[] columns) {
		this.columns = columns;
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
}
