/*
 * $Id: AbstractExampleTemplateService.java 2009/02/23 koyama $
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

package jp.go.nict.langrid.wrapper.exampletemplate.ws;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.lang.ExceptionUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguagePairNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguagePairException;
import jp.go.nict.langrid.service_1_2.UnsupportedMatchingMethodException;
import jp.go.nict.langrid.service_1_2.paralleltext.ParallelText;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.service_1_2.util.validator.LanguageValidator;
import jp.go.nict.langrid.service_1_2.util.validator.MatchingMethodValidator;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateService;
import jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank;
import jp.go.nict.langrid.wrapper.ws_1_2.AbstractLanguagePairService;

/**
 * 穴あき用例サービス
 * @author koyama
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractExampleTemplateService extends AbstractLanguagePairService implements ExampleTemplateService {
	
	/**
	 * <#if locale="ja">
	 * デフォルトコンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractExampleTemplateService() {
		super();
	}
	/**
	 * <#if locale="ja">
	 * サービスコンテキストを引数にとるコンストラクタ。
	 * @param serviceContext サービスコンテキスト
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractExampleTemplateService(ServiceContext serviceContext){
		super(serviceContext);
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateService#getExampleTemplates(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
	 */
	public ExampleTemplate[] getExampleTemplates(String language, String text,
			String matchingMethod, String[] categoryIds)
			throws AccessLimitExceededException, InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		Language l = new LanguageValidator("language", language).notNull().trim().notEmpty().getLanguage();
//		String txt = new StringValidator("text", text).notNull().trim().notEmpty().getValue();
		MatchingMethod mm = new MatchingMethodValidator(
				"matchingMethod", matchingMethod
				)
				.notNull().trim().notEmpty().getMatchingMethod(matchingMethods);
		acquireSemaphore();
		try{
			return doExampleTempates(l, text, mm, categoryIds).toArray(new ExampleTemplate[]{});
		} catch(InvalidParameterException e){
			throw e;
		} catch(ProcessFailedException e){
			throw e;
		} catch(Throwable t){
			logger.log(Level.SEVERE, "unknown error occurred.", t);
			throw new ProcessFailedException(
					ExceptionUtil.getMessageWithStackTrace(t)
					);
		} finally{
			releaseSemaphore();
		}
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateService#getExampleTemplatesByExampled(java.lang.String, java.lang.String[])
	 */
	public ExampleTemplate[] getExampleTemplatesByExampled(String language,
			String[] exampleIds) throws AccessLimitExceededException,
			InvalidParameterException, LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		Language l = new LanguageValidator("language", language).notNull().trim().notEmpty().getLanguage();
		acquireSemaphore();
		try{
			return doExampleTemplatesByExampled(l, exampleIds).toArray(new ExampleTemplate[]{});
		} catch(InvalidParameterException e){
			throw e;
		} catch(ProcessFailedException e){
			throw e;
		} catch(Throwable t){
			logger.log(Level.SEVERE, "unknown error occurred.", t);
			throw new ProcessFailedException(
					ExceptionUtil.getMessageWithStackTrace(t)
					);
		} finally{
			releaseSemaphore();
		}
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateService#getParallelTexts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, jp.go.nict.langrid.wrapper.exampletemplate.service.FilledBlank[])
	 */
	public ParallelText[] getParallelTexts(String sourceLang,
			String targetLang, String source, String matchingMethod,
			String exampleId, FilledBlank[] filledBlanks)
			throws AccessLimitExceededException, InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		Language sourceL = new LanguageValidator("sourceLang", sourceLang).notNull().trim().notEmpty().getLanguage();
		Language targetL = new LanguageValidator("targetLang", targetLang).notNull().trim().notEmpty().getLanguage();
//		String txt = new StringValidator("source", source).notNull().trim().notEmpty().getValue();
		MatchingMethod mm = new MatchingMethodValidator(
				"matchingMethod", matchingMethod
				)
				.notNull().trim().notEmpty().getMatchingMethod(matchingMethods);
		acquireSemaphore();
		try{
			return doParallelTexts(sourceL, targetL, source, mm, exampleId, filledBlanks).toArray(new ParallelText[]{});
		} catch(InvalidParameterException e){
			throw e;
		} catch(ProcessFailedException e){
			throw e;
		} catch(Throwable t){
			logger.log(Level.SEVERE, "unknown error occurred.", t);
			throw new ProcessFailedException(
					ExceptionUtil.getMessageWithStackTrace(t)
					);
		} finally{
			releaseSemaphore();
		}
	}

	/**
	 * <#if locale="ja">
	 * 指定された検索語，言語コード，カテゴリID，マッチング方法で穴あき用例リストの検索を行う。
	 * @param language 元言語。RFC3066準拠
	 * @param text 対訳を探す文章
	 * @param matchingMethod 検索方法
	 * @param categoryIds 用例カテゴリID
	 * @return 検索結果が格納された配列．存在しない場合空配列．
	 * @throws InvalidParameterException headLang，targetLang，matchingMethodのいずれかがnullまたは空文字列．headLang，targetLangがRFC3066に準拠していない．matchingMethodが規定されている以外の文字列
	 * @throws ProcessFailedException 何らかの原因で対訳の検索に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<ExampleTemplate> doExampleTempates(Language language, String text,
			MatchingMethod matchingMethod, String[] categoryIds)
	throws InvalidParameterException, ProcessFailedException;
	
	/**
	 * <#if locale="ja">
	 * 指定された用例IDで穴あき用例リストの検索を行う。
	 * @param language 元言語。RFC3066準拠
	 * @param exampleIds 用例ID
	 * @return 検索結果が格納された配列．存在しない場合空配列．
	 * @throws InvalidParameterException headLang，targetLang，matchingMethodのいずれかがnullまたは空文字列．headLang，targetLangがRFC3066に準拠していない．matchingMethodが規定されている以外の文字列
	 * @throws ProcessFailedException 何らかの原因で対訳の検索に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<ExampleTemplate> doExampleTemplatesByExampled(Language language, String[] exampleIds)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 指定された検索語、マッチング方法で穴あき用例対訳の検索を行う。
	 * @param sourceLang 元言語。RFC3066準拠
	 * @param targetLang 対訳を探す言語。RFC3066準拠
	 * @param source 対訳を探す文章
	 * @param matchingMethod 検索方法
	 * @param exampleId 用例ID
	 * @param filledBlanks 穴埋め情報クラス
	 * @throws InvalidParameterException headLang，targetLang，matchingMethodのいずれかがnullまたは空文字列．headLang，targetLangがRFC3066に準拠していない．matchingMethodが規定されている以外の文字列
	 * @throws ProcessFailedException 何らかの原因で対訳の検索に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<ParallelText> doParallelTexts(Language sourceLang, Language targetLang, String source,
			MatchingMethod matchingMethod, String exampleId, FilledBlank[] filledBlanks)
	throws InvalidParameterException, ProcessFailedException;

	private static Logger logger = Logger.getLogger(
			AbstractExampleTemplateService.class.getName()
			);
	private Set<MatchingMethod> matchingMethods = MINIMUM_MATCHINGMETHODS;

}
