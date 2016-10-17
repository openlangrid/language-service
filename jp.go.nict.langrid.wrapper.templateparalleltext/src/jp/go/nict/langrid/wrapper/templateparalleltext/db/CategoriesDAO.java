
package jp.go.nict.langrid.wrapper.templateparalleltext.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import jp.go.nict.langrid.service_1_2.Category;

import org.apache.commons.lang.StringEscapeUtils;
/**
 * <#if locale="ja">
 * データベース検索クラス
 * <#elseif locale="en">
 * </#if>
 */
public class CategoriesDAO {
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
	private String tableName = "categories";
	/**
	 * <#if locale="ja">
	 * 穴あき用例カテゴリID列名
	 * <#elseif locale="en">
	 * </#if>
	 */
	private String categoryId = "category_id";

	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * @param headLang 現在の言語コード
	 * @param targetLang 翻訳する言語コード
	 * @param matchingMethod 一致条件
	 */
	public CategoriesDAO(ConnectionManager manager, String tableName) {
		this.manager = manager;
		this.tableName = tableName;
	}
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（対訳IDを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Collection<Category> listCategories(String language) throws SQLException {
		String categoryIdColumn = DataBaseUtil.getColumnName(categoryId);
		String exampleCategoryColumn = DataBaseUtil.getColumnName(language);
		
		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(StringEscapeUtils.escapeSql(categoryIdColumn));
		buf.append(", ");
		buf.append(exampleCategoryColumn);
		buf.append(" FROM \"");
		buf.append(tableName);
		buf.append("\" order by ");
		buf.append(StringEscapeUtils.escapeSql(categoryIdColumn));

		String sql = buf.toString();
		logger.info("createSQL()=" + sql);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<Category> collection = new ArrayList<Category>();
			while (rs.next()) {
				String id = rs.getString(categoryId);
				String en = rs.getString(language);
				Category text = new Category(id, en);
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

	/**
	 * <#if locale="ja">
	 * 穴あき用例対訳の取得（対訳IDを指定）
	 * @param exampleId
	 * @return
	 * @throws SQLException
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String[] getCategoryNames(String cid, String[] languages)
	throws SQLException {
		StringBuilder buf = new StringBuilder();
		buf.append(" SELECT ");
		buf.append(StringEscapeUtils.escapeSql(categoryId));
		for(String l : languages){
			buf.append(",").append(DataBaseUtil.getColumnName(l));
		}
		buf.append(" FROM \"").append(tableName).append("\" where \"")
			.append(categoryId).append("\"='").append(
					StringEscapeUtils.escapeSql(cid)).append("'");

		String sql = buf.toString();
		logger.info("createSQL()=" + sql);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				String[] ret = new String[languages.length];
				for(int i = 0; i < ret.length; i++){
					ret[i] = rs.getString(i + 2);
				}
				return ret;
			} else{
				return null;
			}
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

	protected Logger logger = Logger.getLogger(CategoriesDAO.class.getName());
}
