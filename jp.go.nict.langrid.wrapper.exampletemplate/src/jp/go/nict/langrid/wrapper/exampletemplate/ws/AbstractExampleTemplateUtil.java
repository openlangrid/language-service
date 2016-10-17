/*
 * $Id: AbstraxtExampleTemplateUtil.java 2009/03/01 koyama $
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
import jp.go.nict.langrid.service_1_2.util.validator.LanguageValidator;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateUtilService;
import jp.go.nict.langrid.wrapper.exampletemplate.service.Segment;
import jp.go.nict.langrid.wrapper.exampletemplate.service.SeparateExample;
import jp.go.nict.langrid.wrapper.ws_1_2.AbstractService;

/**
 * 穴き用例対訳複合サービス用ユーティリティサービス
 * @author koyama
 * @author $Author$
 * @version $Revision$
 */
public abstract class AbstractExampleTemplateUtil extends AbstractService
		implements ExampleTemplateUtilService {

	/**
	 * コンストラクタ。
	 */
	public AbstractExampleTemplateUtil() {
		super();
	}

	/**
	 * コンストラクタ。
	 * @param serviceContext
	 */
	public AbstractExampleTemplateUtil(ServiceContext serviceContext) {
		super(serviceContext);
	}

	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateUtilService#margeExamples(jp.go.nict.langrid.wrapper.exampletemplate.service.SeparateExample[])
	 */
	public Segment[] margeExamples(SeparateExample[] separateExamples)
			throws AccessLimitExceededException, InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		acquireSemaphore();
		try{
			return doMargeExamples(separateExamples).toArray(new Segment[]{});
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
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplateUtilService#separateExamples(java.lang.String, jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate[])
	 */
	public SeparateExample[] separateExamples(String language,
			ExampleTemplate[] exampleTemplates)
			throws AccessLimitExceededException, InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException,
			UnsupportedMatchingMethodException {
		Language l = new LanguageValidator("language", language).notNull().trim().notEmpty().getLanguage();
		acquireSemaphore();
		try{
			return doSeparateExamples(l, exampleTemplates).toArray(new SeparateExample[]{});
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
	 * 入力支援用穴あき用例取得用．センテンスごとに区切られた状態で返す．
	 * @param language 元言語。RFC3066準拠
	 * @param exampleTemplate 穴あき用例対訳クラス
	 * @return セパレート結果が格納された配列。存在しない場合空配列
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<SeparateExample> doSeparateExamples(Language language, ExampleTemplate[] exampleTemplates)
		throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 穴あき用例マージを行う．< br>
	 * 同一階層に同じセンテンスが来た場合はマージ処理を行う．<br>
	 * センテンス毎に親子関係を構築し、セグメントの子要素を連続して取得可能なリスト状態として返す．
	 * @param separateExamples マージ結果セグメント配列
	 * @return 検索結果が格納された配列。存在しない場合空配列
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	protected abstract Collection<Segment> doMargeExamples(SeparateExample[] separateExamples)
		throws InvalidParameterException, ProcessFailedException;
	
	private static Logger logger = Logger.getLogger(
			AbstractExampleTemplateUtil.class.getName()
			);
}
