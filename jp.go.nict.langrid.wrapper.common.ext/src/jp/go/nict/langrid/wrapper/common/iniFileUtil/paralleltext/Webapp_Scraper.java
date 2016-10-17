/*
 * $Id: Webapp_Scraper.java 25906 2008-07-15 04:46:14Z murakami $
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
 * Webアプリケーションを用いた用例対訳サービスのiniファイル.default(resources).content.scraperに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_Scraper {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * typeの表記に対応
	 */
	private String type = "";

	/**
	 * ignoresの表記に対応
	 */
	private String[] ignores = {};

	/**
	 * regexesの表記に対応
	 */
	private String[] regexes = {};

	
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @param defaultSource defaultを表すオブジェクト
	 * @throws ServiceConfigurationException 
	 */
	public Webapp_Scraper(JSONObject jObj, Webapp_Default defaultSource) throws ServiceConfigurationException {
		try {

				//実際のキー毎の選択処理
				//順に
				//type
				//ignores
				//regexes

				if (jObj.has("type")){
					type = jObj.getString("type");
				}else if (defaultSource !=null
						&& defaultSource.getContent()!=null
						&& defaultSource.getContent().getScraper()!=null)
					type = defaultSource.getContent().getScraper().getType();

				//ignoresの切り出し
				if (jObj.has("ignores")) {
					ArrayList<String> alString = new ArrayList<String>();
					JSONArray buffArray = jObj.getJSONArray("ignores");
					int loop = buffArray.length();
					for(int i=0;i<loop;i++){
						alString.add(buffArray.getString(i));
					}
					ignores = alString.toArray(new String[0]);
				}else if (defaultSource.getContent()!=null
							&& defaultSource.getContent().getScraper()!=null)
					ignores = defaultSource.getContent().getScraper().getIgnores();
				
				//regexesの切り出し
				if (jObj.has("regexes")) {
					ArrayList<String> alString = new ArrayList<String>();
					JSONArray buffArray = jObj.getJSONArray("regexes");
					int loop = buffArray.length();
					for(int i=0;i<loop;i++){
						alString.add(buffArray.getString(i));
					}
					regexes = alString.toArray(new String[0]);
				}else if (defaultSource !=null
						&& defaultSource.getContent()!=null
						&& defaultSource.getContent().getScraper()!=null)
					regexes = defaultSource.getContent().getScraper().getRegexes();
		} catch (Exception e) {
			throw new ServiceConfigurationException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public Webapp_Scraper() {
	}

	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/

	/**
	 * @return type を戻します。
	 */
	public String getType() {
		return type; 
	}

	/**
	 * @return regexes を戻します。
	 */
	public String[] getRegexes() {
		return regexes; 
	}

	/**
	 * @return ignores を戻します。
	 */
	public String[] getIgnores() {
		return ignores;
	}

}
