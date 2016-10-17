/*
 * $Id: Webapp_Action.java 25906 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary;

import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;

import org.json.JSONObject;

/**
 * Webアプリケーションを用いた用例対訳サービスのiniファイル.default(resources).actionに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_Action  {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * typeの表記に対応
	 */
	private String url = "";

	/**
	 * queryの表記に対応
	 */
	private String query = "";

	/**
	 * methodの表記に対応
	 */
	private String method = "";
	
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/

	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @param defaultSource 
	 * @throws ServiceConfigurationException 
	 */
	public Webapp_Action(JSONObject jObj, Webapp_Default defaultSource) throws ServiceConfigurationException {

		try{
			
			// 実際のキー毎の選択処理
			// (*は欠落を許すもの)
			// url*
			// query*
			// method*

			//urlの切り出し
			if(jObj.has("url"))
				url = jObj.getString("url");
			else if(defaultSource != null && defaultSource.getAction()!=null)
				url = defaultSource.getAction().getUrl();

			//queryの切り出し
			if(jObj.has("query"))
			query = jObj.getString("query");
			else if(defaultSource != null && defaultSource.getAction()!=null)
				query = defaultSource.getAction().getQuery();

			//methodの切り出し
			if(jObj.has("method"))
			method = jObj.getString("method");
			else if(defaultSource != null && defaultSource.getAction()!=null)
				method = defaultSource.getAction().getMethod();

		} catch(Exception e){
			throw new ServiceConfigurationException(e.toString());
		}
	}

	/**
	 * デフォルトコンストラクタ
	 */
	public Webapp_Action(){
	}

		
	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/
	/**
	 * @return type を戻します。
	 */
	public String getUrl() {
		return url; 
	}

	/**
	 * @return query を戻します。
	 */
	public String getQuery() {
		return query; 
	}

	/**
	 * @return method を戻します。
	 */
	public String getMethod() {
		return method; 
	}

}
