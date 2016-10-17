/*
 * $Id: ApplicableWordService.java 2009/02/26 koyama $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
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

package jp.go.nict.langrid.wrapper.exampletemplate;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.th;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;

import jp.go.nict.langrid.commons.lang.ExceptionUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.exampletemplate.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.exampletemplate.db.GetApplicableWordsDAO;
import jp.go.nict.langrid.wrapper.exampletemplate.db.GetExampleTemplatesDAO;
import jp.go.nict.langrid.wrapper.exampletemplate.db.ListExampleCategoriesDAO;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWord;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleCategory;
import jp.go.nict.langrid.wrapper.exampletemplate.service.WordCategory;
import jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractApplicableWordService;

/**
 * 穴埋め単語サービス
 * @author $Author$
 * @version $Revision$
 */
public class ApplicableWordService extends AbstractApplicableWordService {

	/**
	 * コンストラクタ
	 */
	public ApplicableWordService() {
		if (languagePairInitializer())
			logger.info("Language pair setting successed.");
		if (connectionInitializer())
			logger.info("Database connection initialized successed.");
	}
	
	/**
	 * <#if locale="ja">
	 * サポートしている言語対を設定します。
	 * </#if>
	 */
	protected boolean languagePairInitializer() {
		super.setSupportedLanguagePairs(Arrays.asList(
				new LanguagePair(en, ja),
				new LanguagePair(en, th)));
		return true;
	}
	
	/**
	 * <#if locale="ja">
	 * データベース接続管理クラスを生成
	 * </#if>
	 */
	protected boolean connectionInitializer() {
		String jndiDataSourceName = getServiceContext().getInitParameter("connection.dataSource");
		try {
			this.connectionManager = new ConnectionManager(
					"java:comp/env/" + jndiDataSourceName);
			return true;
		} catch (NamingException e) {
			logger.log(Level.SEVERE, "failed to find data source: " + jndiDataSourceName, e);
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractApplicableWordService#doApplicableWordTranslation(jp.go.nict.langrid.language.Language, jp.go.nict.langrid.language.Language, java.lang.String, jp.go.nict.langrid.service_1_2.typed.MatchingMethod, java.lang.String)
	 */
	@Override
	protected Collection<WordCategory> doApplicableWordTranslation(
			Language sourceLang, Language targetLang, String source,
			MatchingMethod matchingMethod, String wordId)
			throws InvalidParameterException, ProcessFailedException {
		GetExampleTemplatesDAO exampleTemplatesDAO = new GetExampleTemplatesDAO(
				sourceLang.getCode(), matchingMethod, connectionManager);
		try {
			return exampleTemplatesDAO.findWordCategorys(wordId);
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	
	/*
	 * (非 Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractApplicableWordService#doListExampleCategories()
	 */
	@Override
	protected Collection<ExampleCategory> doListExampleCategories()
			throws InvalidParameterException, ProcessFailedException {
		ListExampleCategoriesDAO categoriesDAO = new ListExampleCategoriesDAO(connectionManager);
		try {
			return categoriesDAO.listExampleCategories();
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}



	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractApplicableWordService#doApplicableWords(jp.go.nict.langrid.language.Language, java.lang.String[])
	 */
	@Override
	protected Collection<ApplicableWord> doApplicableWords(Language language,
			String[] categoryIds) throws InvalidParameterException,
			ProcessFailedException {
		if (categoryIds == null || categoryIds.length == 0) 
			throw new InvalidParameterException("categoryIds", "is empty.");
		GetApplicableWordsDAO applicableWordsDAO = new GetApplicableWordsDAO(language.getCode(), connectionManager);
		try {
			return applicableWordsDAO.getApplicableWords(categoryIds);
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}
	
	/* データベース接続管理クラス */
	protected ConnectionManager connectionManager = null;
	/* java.util.Logger */
	private Logger logger = Logger.getLogger(ApplicableWordService.class.getName());

}
