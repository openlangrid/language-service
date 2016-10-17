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

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.EditableBilingualDictionaryService;
import jp.go.nict.langrid.wrapper.ws_1_2.AbstractService;

/**
 * <#if locale="ja">
 * 対訳辞書編集サービスの基底クラス。
 * <#elseif locale="en">
 * </#if>
 * @author $Author: nakaguchi $
 * @version $Revision: 26972 $
 */
public abstract class AbstractEditableBilingualDictionaryService
extends AbstractService
implements EditableBilingualDictionaryService
{
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractEditableBilingualDictionaryService(){
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param context サービスコンテキスト。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public AbstractEditableBilingualDictionaryService(ServiceContext context){
		super(context);
	}
}
