/*
 * $Id: ExampleTemplateUtilService.java 2009/03/01 koyama $
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

package jp.go.nict.langrid.wrapper.exampletemplate.service;

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

/**
 * @author koyama
 * @author $Author$
 * @version $Revision$
 */
public interface ExampleTemplateUtilService {
	/**
	 * <#if locale="ja">
	 * 入力支援用穴あき用例取得用．センテンスごとに区切られた状態で返す．
	 * @param language 元言語。RFC3066準拠
	 * @param exampleTemplate 穴あき用例対訳クラス
	 * @return セパレート結果が格納された配列。存在しない場合空配列
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguagePairNotUniquelyDecidedException 複数の言語対候補が見つかった
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 検索処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguagePairException 指定された言語対はサポートされていない
	 * @throws UnsupportedMatchingMethodException 指定されたマッチング方法はサポートされていない
	 * <#elseif locale="en">
	 * </#if>
	 */
	SeparateExample[] separateExamples(
		String language, ExampleTemplate[] exampleTemplates
		)
		throws AccessLimitExceededException, InvalidParameterException
		, LanguagePairNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException, ServerBusyException
		, ServiceNotActiveException, ServiceNotFoundException
		, UnsupportedLanguagePairException, UnsupportedMatchingMethodException
		;
	
	/**
	 * <#if locale="ja">
	 * 穴あき用例マージを行う．< br>
	 * 同一階層に同じセンテンスが来た場合はマージ処理を行う．<br>
	 * センテンス毎に親子関係を構築し、セグメントの子要素を連続して取得可能なリスト状態として返す．
	 * @param separateExamples マージ結果セグメント配列
	 * @return 検索結果が格納された配列。存在しない場合空配列
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguagePairNotUniquelyDecidedException 複数の言語対候補が見つかった
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 検索処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguagePairException 指定された言語対はサポートされていない
	 * @throws UnsupportedMatchingMethodException 指定されたマッチング方法はサポートされていない
	 * <#elseif locale="en">
	 * </#if>
	 */
	Segment[] margeExamples(SeparateExample[] separateExamples)
		throws AccessLimitExceededException, InvalidParameterException
		, LanguagePairNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException, ServerBusyException
		, ServiceNotActiveException, ServiceNotFoundException
		, UnsupportedLanguagePairException, UnsupportedMatchingMethodException
		;

}
