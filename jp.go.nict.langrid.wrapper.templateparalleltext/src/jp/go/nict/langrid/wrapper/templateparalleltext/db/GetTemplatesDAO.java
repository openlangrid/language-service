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
package jp.go.nict.langrid.wrapper.templateparalleltext.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.service_1_2.Category;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Choice;
import jp.go.nict.langrid.service_1_2.templateparalleltext.ChoiceParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Template;
import jp.go.nict.langrid.service_1_2.templateparalleltext.ValueParameter;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

/**
 * <#if locale="ja"> データベース検索クラス <#elseif locale="en"> </#if>
 */
public class GetTemplatesDAO {
	/**
	 * <#if locale="ja"> 最大結果件数 <#elseif locale="en"> </#if>
	 */
	// private static final int LIMIT = 10;
	/**
	 * <#if locale="ja"> 開始オフセット <#elseif locale="en"> </#if>
	 * 
	 * private int OFFSET = 0;
	 */
	/**
	 * <#if locale="ja"> データベースマネージャ <#elseif locale="en"> </#if>
	 */
	private ConnectionManager manager;
	/**
	 * <#if locale="ja"> 現在の言語コード <#elseif locale="en"> </#if>
	 */
	private String sourceLang;
	/**
	 * <#if locale="ja"> 一致条件 <#elseif locale="en"> </#if>
	 */
	private MatchingMethod matchingMethod;
	/**
	 * <#if locale="ja"> ID条件列 <#elseif locale="en"> </#if>
	 */
	private String whereIdsColumn;
	/**
	 * <#if locale="ja"> ID条件値 <#elseif locale="en"> </#if>
	 */
	private String whereIdsValue;
	/**
	 * <#if locale="ja"> 曖昧検索列 <#elseif locale="en"> </#if>
	 */
	private String whereTextColumn;
	/**
	 * <#if locale="ja"> 曖昧検索値 <#elseif locale="en"> </#if>
	 */
	private String whereTextValue;
	/**
	 * <#if locale="ja"> 曖昧検索語句 <#elseif locale="en"> </#if>
	 */
	
	private GetWordsDAO wordsDao;

	private String tableNameTemplates;
	private String tableNameCategories;
	private String tableNameCategoriesTemplates;

	private String columnNameTemplateId = "template_id";
	private String columnNameCategoryId = "category_id";

	private static final char PERCENT = '%';
	
	public static final String REGEX_PART_CHOICES = "(domains|choices)=\"([0-9]+(?:,[0-9]+)*|[^\"]+(?:\\|[^\"]+)*)\""; 
	
	public static final String REGEX_PART_VALUE = "type=\"(text|int|float|date|time)\"( min=\".+?\")?( max=\".+?\")?";
	
	

	// public static final String CHOICE_PARAM_REGEX =
	// "<param id=\"([0-9]+)\"\\s+("
	// + REGEX_PART_DOMAINS + ")\\s*/?>";
	public static final String CHOICE_PARAM_REGEX = "<param id=\"([0-9]+)\"\\s+"
			+ REGEX_PART_CHOICES + ".+?/?>";
	public static final String VALUE_PARAM_REGEX = "<param id=\"([0-9]+)\"\\s+"
			+ REGEX_PART_VALUE + ".+?/?>";
	
	public static final String MIN_MAX_REGEX = "(min|max)=\"(.*?)\"";
	
	// private Connection con;

	/**
	 * <#if locale="ja"> コンストラクタ
	 * 
	 * @param sourceLang
	 *            現在の言語コード
	 * @param targetLang
	 *            翻訳する言語コード
	 * @param matchingMethod
	 *            一致条件
	 */
	public GetTemplatesDAO(String sourceLang, MatchingMethod matchingMethod,
			ConnectionManager manager) {
		this(sourceLang, matchingMethod, manager, 
				"templates", "categories", "categories_templates", 
				new GetWordsDAO(sourceLang, manager));
	}
	
	public GetTemplatesDAO(String sourceLang, MatchingMethod matchingMethod,
			ConnectionManager manager, String tnTemplates, String tnCategories, String tnCategoriesTemplates,
			GetWordsDAO wordsDao) {
		this.sourceLang = sourceLang;
		this.matchingMethod = matchingMethod;
		this.manager = manager;
		this.tableNameTemplates = tnTemplates;
		this.tableNameCategories = tnCategories;
		this.tableNameCategoriesTemplates = tnCategoriesTemplates;
		this.wordsDao = wordsDao;
	}

	/**
	 * <#if locale="ja"> 穴あき用例リストの取得（カテゴリID指定）
	 * 
	 * @param categoryIds
	 * @return
	 * @throws SQLException
	 *             </#if>
	 */
	public Collection<Template> getTemplatesByCategoryIds(String[] categoryIds)
			throws SQLException {

		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE ");
		sql.append(createConditionForCategoryIds(categoryIds));

		return executeQuery(sql.toString());
	}

	/**
	 * <#if locale="ja"> 穴あき用例リストの取得（用例ID指定）
	 * 
	 * @param templateIds
	 * @return
	 * @throws SQLException
	 *             </#if>
	 */
	public Collection<Template> getTemplatesByTemplateIds(String[] templateIds)
			throws SQLException {

		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE ");
		sql.append(createConditionForTemplateIds(templateIds));

		return executeQuery(sql.toString());
	}

	/**
	 * <#if locale="ja"> 穴あき用例リストの取得（用例ID指定）
	 * 
	 * @param templateIds
	 * @return
	 * @throws SQLException
	 *             </#if>
	 */
	public Template getTemplateByTemplateId(String templateId)
			throws SQLException {
		Collection<Template> results = this
				.getTemplatesByTemplateIds(new String[] { templateId });
		Template[] templates = results.toArray(new Template[0]);
		return (templates.length > 0) ? templates[0] : null;
	}

	/**
	 * <#if locale="ja"> 穴あき用例リストの取得（テキスト曖昧検索）
	 * 
	 * @param text
	 * @return
	 * @throws SQLException
	 *             </#if>
	 */
	public Collection<Template> getTemplatesByText(String text)
			throws SQLException {

		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE ");
		sql.append(createConditionForTemplateText(text));

		return executeQuery(sql.toString());
	}

	/**
	 * <#if locale="ja"> 穴あき用例リストの取得（カテゴリID指定、テキスト曖昧検索）
	 * 
	 * @param categoryIds
	 * @param text
	 * @return
	 * @throws SQLException
	 *             </#if>
	 */
	public Collection<Template> getTemplates(String[] categoryIds, String text)
			throws SQLException {

		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE ");
		sql.append(createConditionForCategoryIds(categoryIds));
		sql.append(" AND ");
		sql.append(createConditionForTemplateText(text));

		return executeQuery(sql.toString());
	}

	/*
	 * SQL文を実行し、ExampleTemplate配列に格納して返却
	 */
	private Collection<Template> executeQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = manager.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			Collection<Template> collection = new ArrayList<Template>();

			Map<String, List<Category>> templateIdCategoryIds = new HashMap<String, List<Category>>();
			Map<String, String> templateIdTemplate = new HashMap<String, String>();
			while (rs.next()) {
				String templateId = rs.getString(this.columnNameTemplateId);

				if (!templateIdCategoryIds.containsKey(templateId)) {
					templateIdCategoryIds.put(templateId,
							new ArrayList<Category>());
				}

				templateIdCategoryIds.get(templateId).add(
						new Category(rs.getString(this.columnNameCategoryId),
								rs.getString("categoryName")));
				templateIdTemplate.put(templateId, rs.getString("template"));
			}

			for (String templateId : templateIdTemplate.keySet()) {
				String template = templateIdTemplate.get(templateId);
				List<Category> categories = templateIdCategoryIds
						.get(templateId);

				Template exampleTemplate = new Template(templateId, template,
						findChoiceParameters(template), findValueParameters(template),
						categories.toArray(new Category[0]));
				collection.add(exampleTemplate);

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
	protected String createSelectSQL() {
		String templates = DataBaseUtil.getColumnName(this.tableNameTemplates);
		String categories = DataBaseUtil.getColumnName(this.tableNameCategories);
		String categoriesTemplates = DataBaseUtil.getColumnName(this.tableNameCategoriesTemplates);
		
		String sourceLang = DataBaseUtil.getColumnName(this.sourceLang);
		String templateId = DataBaseUtil.getColumnName(this.columnNameTemplateId);
		String categoryId = DataBaseUtil.getColumnName(this.columnNameCategoryId);

		StringBuffer buf = new StringBuffer();
		buf.append(" SELECT ");
		buf.append(categoriesTemplates + "." + templateId + ",");
		buf.append(categoriesTemplates + "." + categoryId + ",");
		buf.append(categories + "." + sourceLang + " as categoryName,");
		buf.append(templates + "." + sourceLang + " as template");
		buf.append(" FROM " + categoriesTemplates);
		buf.append(" INNER JOIN " + categories);
		buf.append(" ON " + categoriesTemplates + "." + categoryId);
		buf.append(" = " + categories + "." + categoryId);
		buf.append(" INNER JOIN " + templates);
		buf.append(" ON " + categoriesTemplates + "." + templateId);
		buf.append(" = " + templates + "." + templateId);

		return buf.toString();
	}

	protected String createConditionForCategoryIds(String[] categoryIds) {
		this.whereIdsColumn = DataBaseUtil.getColumnName(this.columnNameCategoryId);
		this.whereIdsValue = ArrayUtil.join(toEscapedText(categoryIds), ",");
		String categories_templates = DataBaseUtil
				.getColumnName(this.tableNameCategoriesTemplates);
		return "( " + categories_templates + "." + whereIdsColumn + " IN ("
				+ whereIdsValue + ") )";
	}

	protected String createConditionForTemplateIds(String[] templateIds) {
		this.whereIdsColumn = DataBaseUtil.getColumnName(this.columnNameTemplateId);
		this.whereIdsValue = ArrayUtil.join(toEscapedText(templateIds), ",");
		String templates = DataBaseUtil.getColumnName(this.tableNameTemplates);
		return "( " + templates + "." + whereIdsColumn + " IN ("
				+ whereIdsValue + ") )";
	}

	protected String createConditionForTemplateText(String text) {
		this.whereTextColumn = DataBaseUtil.getColumnName(this.sourceLang);
		this.whereTextValue = text;
		String templates = DataBaseUtil.getColumnName(this.tableNameTemplates);

		StringBuffer sql = new StringBuffer();
		sql.append("( lower(" + templates + "." + whereTextColumn + ")");
		if (matchingMethod.equals(MatchingMethod.COMPLETE)) {
			sql.append(" = ''{0}'' ");
		} else {
			sql.append(" LIKE ''{0}'' ");
			if (matchingMethod.equals(MatchingMethod.PARTIAL)) {
				whereTextValue = PERCENT + whereTextValue.toLowerCase()
						+ PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.PREFIX)) {
				whereTextValue = whereTextValue.toLowerCase() + PERCENT;
			} else if (matchingMethod.equals(MatchingMethod.SUFFIX)) {
				whereTextValue = PERCENT + whereTextValue.toLowerCase();
			}
		}
		sql.append(")");
		return MessageFormat.format(sql.toString(), whereTextValue);
	}

	/*
	 * Choice Parameter情報を検索
	 */
	protected ChoiceParameter[] findChoiceParameters(String text)
			throws SQLException {
		Collection<ChoiceParameter> collection = new ArrayList<ChoiceParameter>();
		Pattern pattern = Pattern.compile(CHOICE_PARAM_REGEX);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {

			String paramId = matcher.group(1);
			String kind = matcher.group(2);
			String value = matcher.group(3);

			if (kind.equals("domains")) {
				ChoiceParameter cp = new ChoiceParameter(paramId, 
						this.wordsDao.findWords(value).toArray(new Choice[0]));
				collection.add(cp);
			} else if (kind.equals("choices")) {

				Collection<Choice> choices = new ArrayList<Choice>();
				for (String choiceVal : value.split("\\|")) {
					Choice c = new Choice(String.valueOf(choices.size() + 1),
							choiceVal);
					choices.add(c);
				}

				collection.add(new ChoiceParameter(paramId, choices
						.toArray(new Choice[0])));
			}
		}

		return collection.toArray(new ChoiceParameter[0]);
	}

	/*
	 * Value Parameter情報を検索
	 */
	protected ValueParameter[] findValueParameters(String text)
			throws SQLException {
		Collection<ValueParameter> collection = new ArrayList<ValueParameter>();
		Pattern pattern = Pattern.compile(VALUE_PARAM_REGEX);
		Pattern pattern2 = Pattern.compile(MIN_MAX_REGEX);
		
		Matcher matcher = pattern.matcher(text);
		
		while (matcher.find()) {
			String paramId = matcher.group(1);
			String type = matcher.group(2);
			String min = null;
			String max = null;
			
			for(int i : new int[]{3, 4}) {
				if(matcher.group(i) != null) {
					Matcher matcher2 = pattern2.matcher(matcher.group(i));
					matcher2.find();
					if(matcher2.group(1).startsWith("min")) {
						min = matcher2.group(2);
					} else {
						max = matcher2.group(2);
					}
				}
			}

			ValueParameter rParam = new ValueParameter(paramId, type, min, max);
			collection.add(rParam);
		}

		return collection.toArray(new ValueParameter[0]);
	}

	/*
	 * word検索用SQL文を生成
	 */
//	public Collection<Choice> findWords(String domainIds) throws SQLException {
//		String domains = DataBaseUtil.getColumnName(this.tableNameDomains);
//		String domains_words = DataBaseUtil.getColumnName(this.tableNameDomainsWords);
//		String words = DataBaseUtil.getColumnName(this.tableNameWords);
//		String domain_id = DataBaseUtil.getColumnName("domain_id");
//		String word_id = DataBaseUtil.getColumnName("word_id");
//
//		StringBuffer buf = new StringBuffer();
//		buf.append(" SELECT ");
//		buf.append(domains + "." + domain_id).append(", ");
//		buf.append(words + "." + word_id).append(", ");
//		buf.append(words + "." + this.sourceLang);
//		buf.append(" FROM ");
//		buf.append(domains);
//		buf.append(" INNER JOIN " + domains_words + " ON " + domains + "."
//				+ domain_id + " = " + domains_words + "." + domain_id);
//		buf.append(" INNER JOIN " + words + " ON " + domains_words + "."
//				+ word_id + " = " + words + "." + word_id);
//		buf.append(" WHERE ");
//		buf.append(domains + "." + domain_id + " in ({0}) ");
//
//		String sql = MessageFormat.format(buf.toString(),
//				ArrayUtil.join(toEscapedText(domainIds.split(",")), ","));
//		logger.info("findWordCategorys()=" + sql);
//		return executeWordsQuery(sql);
//	}
//
//	/*
//	 * word検索SQLを実行
//	 */
//	private Collection<Choice> executeWordsQuery(String sql)
//			throws SQLException {
//		Connection con = null;
//		Statement stmt = null;
//		ResultSet rs = null;
//
//		try {
//			con = manager.getConnection();
//			stmt = con.createStatement();
//			rs = stmt.executeQuery(sql);
//			Collection<Choice> collection = new ArrayList<Choice>();
//
//			while (rs.next()) {
//				String domainId = rs.getString("domain_id");
//				String domain = rs.getString(this.sourceLang);
//				collection.add(new Choice(domainId, domain));
//			}
//			return collection;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			throw e;
//		} finally {
//			if (rs != null) {
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					;
//				}
//			}
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					;
//				}
//			}
//			if (con != null) {
//				try {
//					con.close();
//				} catch (SQLException e) {
//					;
//				}
//			}
//		}
//	}

	private String[] toEscapedText(String[] params) {
		return ArrayUtil.collect(params, new Transformer<String, String>() {
			@Override
			public String transform(String value)
					throws TransformationException {
				return "'" + value + "'";
			}
		});
	}

	// ----------------------------------- 以下 Getter, Setter
	/**
	 * <#if locale="ja">
	 * 
	 * @return the headLang <#elseif locale="en"> </#if>
	 */
	public String getHeadLang() {
		return sourceLang;
	}

	/**
	 * <#if locale="ja">
	 * 
	 * @param headLang
	 *            the headLang to set <#elseif locale="en"> </#if>
	 */
	public void setHeadLang(String headLang) {
		this.sourceLang = headLang;
	}

	/**
	 * <#if locale="ja">
	 * 
	 * @return the mathingMethod <#elseif locale="en"> </#if>
	 */
	public MatchingMethod getMathingMethod() {
		return matchingMethod;
	}

	/**
	 * <#if locale="ja">
	 * 
	 * @param mathingMethod
	 *            the mathingMethod to set <#elseif locale="en"> </#if>
	 */
	public void setMathingMethod(MatchingMethod mathingMethod) {
		this.matchingMethod = mathingMethod;
	}

	/**
	 * <#if locale="ja">
	 * 
	 * @return the matchingMethod <#elseif locale="en"> </#if>
	 */
	public MatchingMethod getMatchingMethod() {
		return matchingMethod;
	}

	/**
	 * <#if locale="ja">
	 * 
	 * @param matchingMethod
	 *            the matchingMethod to set <#elseif locale="en"> </#if>
	 */
	public void setMatchingMethod(MatchingMethod matchingMethod) {
		this.matchingMethod = matchingMethod;
	}

	protected Logger logger = Logger.getLogger(GetTemplatesDAO.class.getName());
}
