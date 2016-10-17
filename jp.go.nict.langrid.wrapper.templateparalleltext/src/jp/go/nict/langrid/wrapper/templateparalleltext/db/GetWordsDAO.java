package jp.go.nict.langrid.wrapper.templateparalleltext.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Choice;

public class GetWordsDAO {
	
	/**
	 * <#if locale="ja"> データベースマネージャ <#elseif locale="en"> </#if>
	 */
	private ConnectionManager manager;
	
	/**
	 * <#if locale="ja"> 現在の言語コード <#elseif locale="en"> </#if>
	 */
	private String sourceLang;
	
	private String tableNameDomains;
	private String tableNameWords;	
	private String tableNameDomainsWords;

	private String columnNameDomainId = "domain_id";
	private String columnNameWordId = "word_id";
	
	public static final String REGEX_PART_CHOICES = "(domains|choices)=\"([0-9]+(?:,[0-9]+)*|.+(?:\\|.+)*)\"";
	
	public static final String REGEX_PART_VALUE = "type=\"(text|int|float|date|time)\"( min=\".+?\")?( max=\".+?\")?";
	
	public GetWordsDAO(String sourceLang, ConnectionManager manager) {
		this(sourceLang, manager, "domains", "words", "domains_words");
	}
	
	public GetWordsDAO(String sourceLang, ConnectionManager manager, 
			String tnDomains, String tnWords, String tnDomainsWords) {
		this.sourceLang = sourceLang;
		this.manager = manager;
		this.tableNameDomains = tnDomains;
		this.tableNameWords = tnWords;
		this.tableNameDomainsWords = tnDomainsWords;
	}
	
	/*
	 * word検索用SQL文を生成
	 */
	public Collection<Choice> findWords(String domainIds) throws SQLException {
		String domains = DataBaseUtil.getColumnName(this.tableNameDomains);
		String domains_words = DataBaseUtil.getColumnName(this.tableNameDomainsWords);
		String words = DataBaseUtil.getColumnName(this.tableNameWords);
		String domain_id = DataBaseUtil.getColumnName(this.columnNameDomainId);
		String word_id = DataBaseUtil.getColumnName(this.columnNameWordId);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(domains + "." + domain_id).append(", ");
		buf.append(words + "." + word_id).append(", ");
		buf.append(words + "." + this.sourceLang);
		buf.append(" FROM ");
		buf.append(domains);
		buf.append(" INNER JOIN " + domains_words + " ON " + domains + "."
				+ domain_id + " = " + domains_words + "." + domain_id);
		buf.append(" INNER JOIN " + words + " ON " + domains_words + "."
				+ word_id + " = " + words + "." + word_id);
		buf.append(" WHERE ");
		buf.append(domains + "." + domain_id + " in ({0}) ");

		String sql = MessageFormat.format(buf.toString(),
				ArrayUtil.join(toEscapedText(domainIds.split(",")), ","));
		logger.info("findWordCategorys()=" + sql);
		return executeWordsQuery(sql);
	}

	/*
	 * word検索SQLを実行
	 */
	private Collection<Choice> executeWordsQuery(String sql)
			throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<Choice> collection = new ArrayList<Choice>();

			while (rs.next()) {
				String wordId = rs.getString(this.columnNameWordId);
				String domain = rs.getString(this.sourceLang);
				collection.add(new Choice(wordId, domain));
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

	private String[] toEscapedText(String[] params) {
		return ArrayUtil.collect(params, new Transformer<String, String>() {
			@Override
			public String transform(String value)
					throws TransformationException {
				return "'" + value + "'";
			}
		});
	}
	
	protected Logger logger = Logger.getLogger(GetWordsDAO.class.getName());
}
