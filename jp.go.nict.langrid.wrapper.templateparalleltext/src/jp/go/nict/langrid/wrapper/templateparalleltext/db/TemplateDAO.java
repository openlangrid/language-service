/*
 * $Id: TemplateDAO.java 28332 2011-01-21 10:00:57Z Takao Nakaguchi $
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

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.service_1_2.templateparalleltext.ValueParameter;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

/**
 * <#if locale="ja"> データベース検索クラス <#elseif locale="en"> </#if>
 */
public class TemplateDAO {
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
	
	private String tableNameTemplates;
	private String tableNameCategories;
	private String tableNameCategoriesTemplates;

	private String columnNameTemplateId = "template_id";
	private String columnNameCategoryId = "category_id";

	private static final char PERCENT = '%';
	
	public static final String REGEX_PART_CHOICES = "(domains|choices)=\"([0-9]+(?:,[0-9]+)*|.+(?:\\|.+)*)\"";
	
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
	 * コンストラクタ
	 * @param sourceLang
	 *            現在の言語コード
	 * @param targetLang
	 *            翻訳する言語コード
	 * @param matchingMethod
	 *            一致条件
	 */
	public TemplateDAO(ConnectionManager manager) {
		this(manager, "templates", "categories", "categories_templates");
	}
	
	public TemplateDAO(ConnectionManager manager, String tnTemplates
			, String tnCategories, String tnCategoriesTemplates) {
		this.manager = manager;
		this.tableNameTemplates = tnTemplates;
		this.tableNameCategories = tnCategories;
		this.tableNameCategoriesTemplates = tnCategoriesTemplates;
	}

	public long addTemplate(String ja, String en, String vi)
			throws SQLException {
/*
		StringBuffer sql = new StringBuffer();
		sql.append(createSelectSQL());
		sql.append(" WHERE ");
		sql.append(createConditionForCategoryIds(categoryIds));

		return executeQuery(sql.toString());
*/
		return -1;
	}

	public void setCategory(long templateId, String categoryId){
		
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
		sql.append("( " + templates + "." + whereTextColumn);
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


	private String[] toEscapedText(String[] params) {
		return ArrayUtil.collect(params, new Transformer<String, String>() {
			@Override
			public String transform(String value)
					throws TransformationException {
				return "'" + value + "'";
			}
		});
	}

	protected Logger logger = Logger.getLogger(TemplateDAO.class.getName());
}
