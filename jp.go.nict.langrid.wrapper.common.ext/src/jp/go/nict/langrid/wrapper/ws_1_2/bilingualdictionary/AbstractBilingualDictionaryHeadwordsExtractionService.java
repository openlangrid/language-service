/*
 * $Id:AbstractBilingualDictionaryService.java 1495 2006-10-12 18:42:55 +0900 (木, 12 10 2006) nakaguchi $
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
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
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
import jp.go.nict.langrid.service_1_2.bilingualdictionary.BilingualDictionaryHeadwordsExtractionService;
import jp.go.nict.langrid.service_1_2.util.validator.LanguagePairValidator;
import jp.go.nict.langrid.service_1_2.util.validator.LanguageValidator;
import jp.go.nict.langrid.service_1_2.util.validator.StringValidator;

/**
 * <#if locale="ja">
 * extractをサポートした対訳辞書の基底クラス。
 * <#elseif locale="en">
 * </#if>
 * @author $Author: nakaguchi $
 * @version $Revision: 26972 $
 */
public abstract class AbstractBilingualDictionaryHeadwordsExtractionService
extends AbstractBilingualDictionaryService
implements BilingualDictionaryHeadwordsExtractionService
{
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * 引数を取りません。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractBilingualDictionaryHeadwordsExtractionService(){
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * 対応する言語対の集合を引数にとります。
	 * @param context サービスコンテキスト
	 * @param supportedPairs 対応する言語対の集合
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractBilingualDictionaryHeadwordsExtractionService(
			ServiceContext context
			, Collection<LanguagePair> supportedPairs){
		super(context);
		setSupportedLanguagePairs(supportedPairs);
	}

	public String[] extract(String headLang, String targetLang, String text)
	throws AccessLimitExceededException, InvalidParameterException,
	LanguagePairNotUniquelyDecidedException, NoAccessPermissionException,
	NoValidEndpointsException, ProcessFailedException,
	ServerBusyException, ServiceNotActiveException,
	ServiceNotFoundException, UnsupportedLanguagePairException
	{

//正規コード		
//		LanguagePair pair = new LanguagePairValidator(
//				new LanguageValidator("headLang", headLang)
//				, new LanguageValidator("targetLang", targetLang)
//				).notNull().trim().notEmpty().getUniqueLanguagePair(
//						getSupportedLanguagePairCollection());

		
//暫定(ワークフロー連携の為)
		LanguagePair pair = new LanguagePairValidator(
				new LanguageValidator("headLang", headLang)
				, new LanguageValidator("targetLang", targetLang)
				).notNull().trim().notEmpty().getLanguagePair();
		
		try
		{
			pair = new LanguagePairValidator(
					new LanguageValidator("headLang", headLang)
					, new LanguageValidator("targetLang", targetLang)
					).notNull().trim().notEmpty().getUniqueLanguagePair(
							getSupportedLanguagePairCollection());
		}
		catch (Exception e)
		{
			return new String[]{};
		}
//暫定 END

		
		text = new StringValidator("headWord", text)
				.notNull().trim().notEmpty().getValue();

		acquireSemaphore();
		try{
			return doExtract(pair.getSource(), pair.getTarget(), text)
				.toArray(new String[]{});
		} catch(InvalidParameterException e){
			throw e;
		} catch(ProcessFailedException e){
			throw e;
		} catch(Throwable t){
			logger.log(Level.SEVERE, "unknown error occurred.", t);
			throw new ProcessFailedException(t);
		} finally{
			releaseSemaphore();
		}
	}

	protected abstract Collection<String> doExtract(
			Language headLang, Language targetLang, String text)
	throws InvalidParameterException, ProcessFailedException;

	private static Logger logger = Logger.getLogger(
			AbstractBilingualDictionaryHeadwordsExtractionService.class.getName()
			);
}
