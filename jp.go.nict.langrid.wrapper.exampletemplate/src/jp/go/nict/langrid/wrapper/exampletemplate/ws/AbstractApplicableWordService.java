/*
 * $Id: AbstractApplicableWordService.java 2009/02/23 koyama $
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
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.service_1_2.util.validator.LanguageValidator;
import jp.go.nict.langrid.service_1_2.util.validator.MatchingMethodValidator;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWord;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWordService;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleCategory;
import jp.go.nict.langrid.wrapper.exampletemplate.service.WordCategory;
import jp.go.nict.langrid.wrapper.ws_1_2.AbstractLanguagePairService;

/**
 * 穴埋め単語サービス
 * @author koyama
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractApplicableWordService extends AbstractLanguagePairService implements ApplicableWordService {
	/**
	 * <#if locale="ja">
	 * デフォルトコンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractApplicableWordService() {
		super();
	}
	/**
	 * <#if locale="ja">
	 * サービスコンテキストを引数にとるコンストラクタ。
	 * @param serviceContext サービスコンテキスト
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractApplicableWordService(ServiceContext serviceContext){
		super(serviceContext);
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWordService#getApplicableWords(java.lang.String, java.lang.String[])
	 */
	public ApplicableWord[] getApplicableWords(String language,
			String[] categoryIds) throws AccessLimitExceededException,
			InvalidParameterException, LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		Language l = new LanguageValidator("language", language).notNull().trim().notEmpty().getLanguage();
		acquireSemaphore();
		try{
			return doApplicableWords(l, categoryIds).toArray(new ApplicableWord[]{});
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

	/*
	 * (非 Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWordService#listExampleCategories()
	 */
	public ExampleCategory[] listExampleCategories()
			throws AccessLimitExceededException, InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		acquireSemaphore();
		try{
			return doListExampleCategories().toArray(new ExampleCategory[]{});
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
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ApplicableWordService#getApplicableWordTranslation(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public WordCategory[] getApplicableWordTranslation(String sourceLang,
			String targetLang, String source, String matchingMethod,
			String wordId) throws AccessLimitExceededException,
			InvalidParameterException, LanguagePairNotUniquelyDecidedException,
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
			return doApplicableWordTranslation(sourceL, targetL, source, mm, wordId).toArray(new WordCategory[]{});
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
	 * 指定されたカテゴリにより、穴埋め単語リストの検索を行う。
	 * @param language 元言語。RFC3066準拠
	 * @param categoryIds カテゴリID
	 * @return 検索結果が格納された配列．存在しない場合空配列．
	 * @throws InvalidParameterException headLang，targetLang，matchingMethodのいずれかがnullまたは空文字列．headLang，targetLangがRFC3066に準拠していない．matchingMethodが規定されている以外の文字列
	 * @throws ProcessFailedException 何らかの原因で対訳の検索に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<ApplicableWord> doApplicableWords(Language language, String[] categoryIds)
	throws InvalidParameterException, ProcessFailedException;
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例カテゴリテーブルに登録されている、全てのカテゴリデータを取得する。
	 * @return 検索結果が格納された配列．存在しない場合空配列．
	 * @throws InvalidParameterException headLang，targetLang，matchingMethodのいずれかがnullまたは空文字列．headLang，targetLangがRFC3066に準拠していない．matchingMethodが規定されている以外の文字列
	 * @throws ProcessFailedException 何らかの原因で対訳の検索に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<ExampleCategory> doListExampleCategories()
	throws InvalidParameterException, ProcessFailedException;
	
	/**
	 * <#if locale="ja">
	 * 指定された検索語、マッチング方法で穴埋め単語対訳の検索を行う。
	 * @param sourceLang 元言語。RFC3066準拠
	 * @param targetLang 対訳を探す言語。RFC3066準拠
	 * @param source 対訳を探す文章
	 * @param matchingMethod 検索方法
	 * @param wordId 穴埋め単語ID
	 * @return 検索結果が格納された配列．存在しない場合空配列．
	 * @throws InvalidParameterException headLang，targetLang，matchingMethodのいずれかがnullまたは空文字列．headLang，targetLangがRFC3066に準拠していない．matchingMethodが規定されている以外の文字列
	 * @throws ProcessFailedException 何らかの原因で対訳の検索に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<WordCategory> doApplicableWordTranslation(
			Language sourceLang, Language targetLang, String source, MatchingMethod matchingMethod, String wordId)
	throws InvalidParameterException, ProcessFailedException;

	private static Logger logger = Logger.getLogger(
			AbstractApplicableWordService.class.getName()
			);
	private Set<MatchingMethod> matchingMethods = MINIMUM_MATCHINGMETHODS;

}
