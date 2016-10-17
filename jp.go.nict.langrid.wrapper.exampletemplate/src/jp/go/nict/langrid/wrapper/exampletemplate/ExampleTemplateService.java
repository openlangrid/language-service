/*
 * $Id: ExampleTemplateService.java 2009/02/26 koyama $
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
import java.util.ArrayList;
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
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.exampletemplate.db.ConnectionManager;
import jp.go.nict.langrid.wrapper.exampletemplate.db.GetExampleTemplatesDAO;
import jp.go.nict.langrid.wrapper.exampletemplate.db.GetParallelTextsDAO;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate;
import jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank;
import jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateService;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 穴あき用例サービス
 * @author $Author$
 * @version $Revision$
 */
public class ExampleTemplateService extends AbstractExampleTemplateService {

	/**
	 * コンストラクタ
	 */
	public ExampleTemplateService() {
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
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateService#doExampleTempates(jp.go.nict.langrid.language.Language, java.lang.String, jp.go.nict.langrid.service_1_2.typed.MatchingMethod, java.lang.String[])
	 */
	@Override
	protected Collection<ExampleTemplate> doExampleTempates(Language language,
			String text, MatchingMethod matchingMethod, String[] categoryIds)
			throws InvalidParameterException, ProcessFailedException {
		GetExampleTemplatesDAO exampleTemplatesDAO = new GetExampleTemplatesDAO(
				language.getCode(), matchingMethod, connectionManager);
		try {
			if (categoryIds != null && categoryIds.length > 0 && StringUtils.isNotBlank(text)) {
				return exampleTemplatesDAO.getExampleTemplates(categoryIds, text);
			} else {
				if (categoryIds != null && categoryIds.length > 0) {
					return exampleTemplatesDAO.getExampleTemplatesByCategoryIds(categoryIds);
				} else if (StringUtils.isNotBlank(text)) {
					return exampleTemplatesDAO.getExampleTemplatesByText(text);
				} else {
					throw new InvalidParameterException("categoryIds and text", "is empty.");
				}
			}
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateService#doExampleTemplatesByExampled(jp.go.nict.langrid.language.Language, java.lang.String[])
	 */
	@Override
	protected Collection<ExampleTemplate> doExampleTemplatesByExampled(
			Language language, String[] exampleIds)
			throws InvalidParameterException, ProcessFailedException {
		if (exampleIds == null || exampleIds.length == 0) {
			throw new InvalidParameterException("exampleIds", "is empty.");
		}
		GetExampleTemplatesDAO exampleTemplatesDAO = new GetExampleTemplatesDAO(
				language.getCode(), MatchingMethod.COMPLETE, connectionManager);
		try {
			return exampleTemplatesDAO.getExampleTemplateByExampleIds(exampleIds);
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateService#doParallelTexts(jp.go.nict.langrid.language.Language, jp.go.nict.langrid.language.Language, java.lang.String, jp.go.nict.langrid.service_1_2.typed.MatchingMethod, java.lang.String, jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank[])
	 */
	@Override
	protected Collection<ParallelText> doParallelTexts(Language sourceLang,
			Language targetLang, String source, MatchingMethod matchingMethod,
			String exampleId, FilledBlank[] filledBlanks)
			throws InvalidParameterException, ProcessFailedException {
		// Parameter check now!!
		
		GetParallelTextsDAO parallelTextsDAO = new GetParallelTextsDAO(
				sourceLang.getCode(), targetLang.getCode(), matchingMethod, connectionManager);
		try {
			if (StringUtils.isNotBlank(exampleId)) {
				if (MatchingMethod.COMPLETE != matchingMethod) {
					logger.log(Level.WARNING, "exampleId指定検索で、matchingMethodがCOMPLETE以外が指定されていたので、強制的にCOMPLETEに置き換えました。");
					parallelTextsDAO.setMatchingMethod(MatchingMethod.COMPLETE);
				}
				if (filledBlanks == null) {
					return parallelTextsDAO.getParallelText(exampleId);
				} else {
					return parallelTextsDAO.getParallelText(exampleId, Arrays.asList(filledBlanks));
				}
			} else {
				if (filledBlanks == null) {
					return parallelTextsDAO.getParallelTexts(source);
				} else {
					return parallelTextsDAO.getParallelTexts(source, Arrays.asList(filledBlanks));
				}
			}
		} catch (SQLException e) {
			throw new ProcessFailedException(e);
		}
	}

	/* データベース接続管理クラス */
	protected ConnectionManager connectionManager = null;
	/* java.util.Logger */
	private Logger logger = Logger.getLogger(ExampleTemplateService.class.getName());
}
