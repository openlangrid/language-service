package jp.go.nict.langrid.wrapper.templateparalleltext;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.transformer.CodeStringToLanguageTransformer;
import jp.go.nict.langrid.language.transformer.LanguageToCodeStringTransformer;
import jp.go.nict.langrid.service_1_2.Category;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundChoiceParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.BoundValueParameter;
import jp.go.nict.langrid.service_1_2.templateparalleltext.Template;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.templateparalleltext.db.CategoriesDAO;
import jp.go.nict.langrid.wrapper.templateparalleltext.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.templateparalleltext.db.GetTemplatesDAO;
import jp.go.nict.langrid.wrapper.templateparalleltext.db.GetWordsDAO;
import jp.go.nict.langrid.wrapper.ws_1_2.templateparalleltext.AbstractTemplateParallelTextService;

import org.apache.commons.lang.StringUtils;

public class TemplateParallelTextService
extends AbstractTemplateParallelTextService{

	public TemplateParallelTextService(ServiceContext context){
		super(context);
		init();
	}
	public TemplateParallelTextService() {
		init();
	}

	public void setDataSourceName(String dataSourceName) {
		initConnection(dataSourceName);
	}

	public void setTableNamePrefix(String tableNamePrefix){
		this.tableNamePrefix = tableNamePrefix;
	}

	public void setLanguages(String languages){
		initLang(languages);
	}

	private void init(){
		if (initConnection(getServiceContext().getInitParameter("connection.dataSource")))
			logger.info("Database connection initialized successed.");
		tableNamePrefix = getInitParameterString("tableNamePrefix", "");
		initLang(getInitParameterString("languages", ""));
	}

	private void initLang(String lang){
		if(lang == null || lang.length() == 0) return;
		String[] langs = lang.split(" ");
		super.setSupportedLanguageCollection(Arrays.asList(
				ArrayUtil.collect(langs, new CodeStringToLanguageTransformer())
				));
	}

	/**
	 * <#if locale="ja"> データベース接続管理クラスを生成 </#if>
	 */
	private boolean initConnection(String dataSourceName) {
		if(dataSourceName == null) return false;
		try {
			this.connectionManager = new ConnectionManager("java:comp/env/"
					+ dataSourceName);

			return true;
		} catch (NamingException e) {
			logger.log(Level.SEVERE, "failed to find data source: "
					+ dataSourceName, e);
		}
		return false;
	}

	@Override
	protected Category[] doListTemplateCategories(Language language)
			throws InvalidParameterException, ProcessFailedException {
		CategoriesDAO categoriesDAO = new CategoriesDAO(
				connectionManager, getTableName("categories")
				);
		try {
			return categoriesDAO.listCategories(language.getCode()).toArray(new Category[0]);
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected String[] doGetCategoryNames(String categoryId, Language[] languages)
	throws InvalidParameterException, ProcessFailedException{
		CategoriesDAO categoriesDAO = new CategoriesDAO(
				connectionManager, getTableName("categories")
				);
		try {
			return categoriesDAO.getCategoryNames(
					categoryId, ArrayUtil.collect(languages, new LanguageToCodeStringTransformer())
					);
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected Template[] doSearchTemplates(Language language, String text,
			MatchingMethod matchingMethod, String[] categoryIds)
			throws InvalidParameterException, ProcessFailedException {

		GetTemplatesDAO templatesDAO = new GetTemplatesDAO(language.getCode(),
				matchingMethod, connectionManager, getTableName("templates"),
				getTableName("categories"),
				getTableName("categories_templates"), new GetWordsDAO(
						language.getCode(), connectionManager,
						getTableName("domains"), getTableName("words"),
						getTableName("domains_words")));
		try {
			if (categoryIds != null && categoryIds.length > 0
					&& StringUtils.isNotBlank(text)) {
				return templatesDAO.getTemplates(categoryIds, text).toArray(
						new Template[0]);
			} else if (categoryIds != null && categoryIds.length > 0) {
				return templatesDAO.getTemplatesByCategoryIds(categoryIds)
						.toArray(new Template[0]);
			} else if (StringUtils.isNotBlank(text)) {
				return templatesDAO.getTemplatesByText(text).toArray(
						new Template[0]);
			} else {
				throw new InvalidParameterException("categoryIds and text",
						"is empty.");
			}

		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected Template[] doGetTemplatesByTemplateId(Language language,
			String[] templateIds) throws InvalidParameterException,
			ProcessFailedException {
		if (templateIds == null || templateIds.length == 0) {
			throw new InvalidParameterException("templateIds", "is empty.");
		}

		GetTemplatesDAO templatesDAO = new GetTemplatesDAO(language.getCode(),
				MatchingMethod.COMPLETE, connectionManager,
				getTableName("templates"), getTableName("categories"),
				getTableName("categories_templates"), new GetWordsDAO(
						language.getCode(), connectionManager,
						getTableName("domains"), getTableName("words"),
						getTableName("domains_words")));
		try {
			return templatesDAO.getTemplatesByTemplateIds(templateIds).toArray(
					new Template[0]);
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected String doGenerateSentence(Language language, String templateId,
			BoundChoiceParameter[] boundChoiceParameters,
			BoundValueParameter[] boundValueParameters)
			throws InvalidParameterException, ProcessFailedException {

		GetTemplatesDAO dao = new GetTemplatesDAO(language.getCode(),
				MatchingMethod.COMPLETE, connectionManager,
				getTableName("templates"), getTableName("categories"),
				getTableName("categories_templates"), new GetWordsDAO(
						language.getCode(), connectionManager,
						getTableName("domains"), getTableName("words"),
						getTableName("domains_words")));

		try {
			Template template = dao.getTemplateByTemplateId(templateId);
			if (template == null) {
				return "";
			}
			TemplateBindingParameter tbp = new TemplateBindingParameter(
					template);

			if (template.getChoiceParameters() != null) {
				tbp.setBoundChoiceParameter(boundChoiceParameters);
			}

			if (template.getValueParameters() != null) {
				tbp.setBoundValueParameter(boundValueParameters);
			}

			return tbp.toStringBoundParameter();

		} catch (SQLException e) {

			throw new ProcessFailedException(e);

		}
	}

	private String getTableName(String baseName){
		return tableNamePrefix + tableNames.get(baseName);
	}

	/* データベース接続管理クラス */
	protected ConnectionManager connectionManager = null;
	private String tableNamePrefix;
	/* java.util.Logger */
	private Logger logger = Logger.getLogger(TemplateParallelTextService.class
			.getName());

	private static Map<String, String> tableNames = new HashMap<String, String>();
	static {
		tableNames.put("categories", "categories");
		tableNames.put("templates", "templates");
		tableNames.put("categories_templates", "categories_templates");
		tableNames.put("domains", "domains");
		tableNames.put("words", "words");
		tableNames.put("domains_words", "domains_words");
	}
}
