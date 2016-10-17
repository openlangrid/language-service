/*
 * $Id: TreeTagger.java 3604 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.treetagger.client.typed;

/**
 * 通信ステータス用コード
 * @author $Author$
 * @version $Revision$
 */
public enum StatusCode{
	/**
	 * リクエスト時のコード
	 */
	REQUEST,
	
	/**
	 * レスポンス時のコード
	 */
	RESPONSE,
	
	/**
	 * 処理でエラーが起こった場合のコード
	 */
	PROCESSEERROR,

	/**
	 * リクエストが不正な場合のコード
	 */
	INVALIDREQUEST,
	
	/**
	 * レスポンスが不正な場合のコード
	 */
	INVALIDRESPONSE,
	
	/**
	 * リクエストの内容が不正な場合のコード
	 */
	INVALIDPARAMETER,
	
	/**
	 * ソケットをクローズするコールコード
	 */
	CLOSE;
}
