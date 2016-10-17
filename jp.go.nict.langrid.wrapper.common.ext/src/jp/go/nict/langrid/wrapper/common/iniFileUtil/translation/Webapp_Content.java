/*
 * $Id: Webapp_Content.java 25906 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil.translation;

import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;

import org.json.JSONObject;

/**
 * Webアプリケーションを用いた翻訳サービスのiniファイル.default(resources).contentに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_Content {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * sourceCharSetの表記に対応
	 */
	private String sourceCharSet = "";

	/**
	 * targetCharSetの表記に対応
	 */
	private String targetCharSet = "";

	/**
	 * scraperの表記に対応
	 */
	private Webapp_Scraper scraper = new Webapp_Scraper();

	
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
	public Webapp_Content(JSONObject jObj, Webapp_Default defaultSource) throws ServiceConfigurationException {
		try {
			//実際のキー毎の選択処理
			//(*は欠落を許すもの)
			//sourceCharSet*
			//targetCharSet*
			//scraper*

			//sourceCharSetの切り出し
			if(jObj.has("sourceCharSet"))
				sourceCharSet = jObj.getString("sourceCharSet");
			else if(defaultSource != null && defaultSource.getContent()!=null)
				sourceCharSet = defaultSource.getContent().getSourceCharSet();

			//targetCharSetの切り出し
			if(jObj.has("targetCharSet"))
				targetCharSet = jObj.getString("targetCharSet");
			else if(defaultSource != null && defaultSource.getContent()!=null)
				sourceCharSet = defaultSource.getContent().getTargetCharSet();

			//scraperの切り出し
			if(jObj.has("scraper"))
				scraper
				= new Webapp_Scraper(jObj.getJSONObject("scraper"),defaultSource);
			else if(defaultSource != null && defaultSource.getContent()!=null)
				scraper = defaultSource.getContent().getScraper();
			
		} catch (Exception e) {
			throw new ServiceConfigurationException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public Webapp_Content() {
	}

	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/

	/**
	 * @return sourceCharSet を戻します。
	 */
	public String getSourceCharSet() {
		return sourceCharSet; 
	}

	/**
	 * @return targetCharSet を戻します。
	 */
	public String getTargetCharSet() {
		return targetCharSet; 
	}

	/**
	 * @return scraper を戻します。
	 */
	public Webapp_Scraper getScraper() {
		return scraper; 
	}

}
