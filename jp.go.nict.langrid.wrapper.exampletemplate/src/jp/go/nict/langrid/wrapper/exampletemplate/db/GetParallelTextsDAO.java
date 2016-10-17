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
import jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank;
/**
 * <#if locale="ja">
 * データベース検索クラス
 * <#elseif locale="en">
 * </#if>
 */
public class GetParallelTextsDAO {
	private static final String TABLE_NAME = "parallel_text";
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
	 * 翻訳先言語コード
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String targetLang;
	/**
	 * <#if locale="ja">
	 * 一致条件
	 * <#elseif locale="en">
	 * </#if>
	 */
	private MatchingMethod matchingMethod;
	/**
	 * <#if locale="ja">
	 * 条件列
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String whereColumn;
	/**
	 * <#if locale="ja">
	 * 条件値
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String whereValue;
	/**
	 * <#if locale="ja">
	 * 曖昧検索語句
	 * <#elseif locale="en">
	 * </#if>
	 */
	private static final char PERCENT = '%';
	private static final String REPLACE_TARGET = "<CHANGE_SENTENCE>";
	private static final String REPLACEMENT = "";
	
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * @param headLang 現在の言語コード
	 * @param targetLang 翻訳する言語コード
	 * @param matchingMethod 一致条件
	 */
	public GetParallelTextsDAO(String headLang, String targetLang, MatchingMethod matchingMethod, ConnectionManager manager) {
		this.sourceLang = headLang;
		this.targetLang = targetLang;
		this.matchingMethod = matchingMethod;
		this.manager = manager;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（対訳IDを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 */
	public Collection<ParallelText> getParallelText(String exampleId) throws SQLException {
		this.whereColumn = DataBaseUtil.getColumnName("example_id");
		this.whereValue = exampleId;
		
		return executeQuery(createSQL());
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（対訳IDを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 */
	public Collection<ParallelText> getParallelText(String exampleId, Collection<FilledBlank> fillBlanks) throws SQLException {
		Collection<ParallelText> results = getParallelText(exampleId);
		if (fillBlanks == null || fillBlanks.size() == 0) {
			return results;
		}
		return replacementWord(results, fillBlanks);
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（sourceを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 */
	public Collection<ParallelText> getParallelTexts(String source) throws SQLException {
		this.whereColumn = "LOWER(" + DataBaseUtil.getColumnName(sourceLang) + ")";
		this.whereValue = source.toLowerCase();
		
		return executeQuery(createSQL());
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（sourceを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 */
	public Collection<ParallelText> getParallelTexts(String source, Collection<FilledBlank> fillBlanks) throws SQLException {
		Collection<ParallelText> results = getParallelTexts(source);
		if (fillBlanks == null || fillBlanks.size() == 0) {
			return results;
		}
		return replacementWord(results, fillBlanks);
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき部分を埋める
	 * @param collection
	 * @param fillBlanks
	 * @return
	 * </#if>
	 */
	protected Collection<ParallelText> replacementWord(
			Collection<ParallelText> collection, Collection<FilledBlank> fillBlanks) {
		String regxTmp = "<PID_NO=\\\"{0}\\\">[0-9,]+</PID>";
		
		for (ParallelText parallelText : collection) {
			String sTxt = parallelText.getSource();
			String tTxt = parallelText.getTarget();
			for (FilledBlank fillBlank : fillBlanks) {
				if (fillBlank == null || fillBlank.getWordId() == null || fillBlank.getPId() == null) 
					continue;
				String regx = MessageFormat.format(regxTmp, fillBlank.getPId());
				ParallelText word = getWord(fillBlank.getWordId());
				if (word == null) {
					continue;
				}
				sTxt = sTxt.replaceAll(regx, word.getSource());
				tTxt = tTxt.replaceAll(regx, word.getTarget());
				parallelText.setSource(sTxt);
				parallelText.setTarget(tTxt);
			}
		}
		return collection;
	}
	
	/*
	 * SQL文を実行し、ParalellText配列に格納して返却
	 */
	private Collection<ParallelText> executeQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<ParallelText> collection = new ArrayList<ParallelText>();
			while (rs.next()) {
				String head = rs.getString(sourceLang);
				String target = rs.getString(targetLang);
				
				head = head.replace(REPLACE_TARGET, REPLACEMENT);
				target = target.replace(REPLACE_TARGET, REPLACEMENT);
				
				ParallelText text = new ParallelText(head, target);
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
		String headLangColumn = DataBaseUtil.getColumnName(sourceLang);
		String targetLangColumn = DataBaseUtil.getColumnName(targetLang);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(headLangColumn);
		buf.append(", ");
		buf.append(targetLangColumn);
		buf.append(" FROM \"");
		buf.append(TABLE_NAME);
		buf.append("\" WHERE ");
		buf.append(whereColumn);
		if (matchingMethod.equals(MatchingMethod.COMPLETE)) {
			buf.append(" = ''{0}'' ");
		} else {
			buf.append(" LIKE ''{0}'' ");
			if (matchingMethod.equals(MatchingMethod.PARTIAL)) {
				whereValue = PERCENT + whereValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.PREFIX)) {
				whereValue = whereValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.SUFFIX)) {
				whereValue = PERCENT + whereValue.toLowerCase();
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
		String sql = MessageFormat.format(buf.toString(), whereValue);
		logger.info("createSQL()=" + sql);
		return sql;
	}
	
	/*
	 * word検索用SQL文を生成
	 */
	private String createSQLforWord(String wordId) {
		String headLangColumn = DataBaseUtil.getColumnName(sourceLang);
		String targetLangColumn = DataBaseUtil.getColumnName(targetLang);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(headLangColumn);
		buf.append(", ");
		buf.append(targetLangColumn);
		buf.append(" FROM \"");
		buf.append("applicable_word");
		buf.append("\" WHERE ");
		buf.append(" \"word_id\" = ''{0}''");

		String sql = MessageFormat.format(buf.toString(), wordId);
		logger.info("createSQLforWord()=" + sql);
		return sql;
	}
	
	/*
	 * word取得メソッド
	 */
	private ParallelText getWord(String wordId) {
		try {
			Collection<ParallelText> c = executeQuery(createSQLforWord(wordId));
			if (c != null && c.size() == 1) {
				return c.toArray(new ParallelText[0])[0];
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, ExceptionUtil.getMessageWithStackTrace(e));
		}
		return null;
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
	
	protected Logger logger = Logger.getLogger(GetParallelTextsDAO.class.getName());
}
