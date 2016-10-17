/*
 * $Id: HTML_Content.java 25906 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil.paralleltext;

import java.util.ArrayList;

import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * HTMLからの用例対訳のiniファイル.default(resource).contentに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class HTML_Content {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * charSetの表記に対応
	 */
	private String charSet = "";

	/**
	 * adressesの表記に対応
	 */
	private String[] adresses = {};

	/**
	 * scraperの表記に対応
	 */
	private HTML_Scraper scraper = new HTML_Scraper();

	
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @param defaultsource 
	 * @throws ServiceConfigurationException 
	 */
	public HTML_Content(JSONObject jObj, HTML_Default defaultsource) throws ServiceConfigurationException {
		try {
			//実際のキー毎の選択処理
			//(*は欠落を許すもの)
			//charSet*
			//adresses*
			//scraper*

			//charSetの切り出し
			if(jObj.has("charSet"))
				charSet = jObj.getString("charSet");
			else{
				if (defaultsource!=null)
					charSet = defaultsource.getContent().getCharSet();
			}
			
			//adressesの切り出し
			if (jObj.has("adresses")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("adresses");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				adresses = alString.toArray(new String[0]);
			}else{
				if (defaultsource != null && defaultsource.getContent()!=null)
					adresses = defaultsource.getContent().getAdresses();
			}

			//scraperの切り出し
			if(jObj.has("scraper"))
				scraper = new HTML_Scraper(jObj.getJSONObject("scraper"),defaultsource);
			else
				if (defaultsource !=null && defaultsource.getContent()!=null)
					scraper = defaultsource.getContent().getScraper();
			
		} catch (Exception e) {
			throw new ServiceConfigurationException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public HTML_Content() {
	}

	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/

	/**
	 * @return charSet を戻します。
	 */
	public String getCharSet() {
		return charSet; 
	}
	
	
	/**
	 * @return adresses を戻します。
	 */
	public String[] getAdresses() {
		return adresses;
	}

	/**
	 * @return scraper を戻します。
	 */
	public HTML_Scraper getScraper() {
		return scraper; 
	}

}
