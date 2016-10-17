/*
 * $Id: Local_Default.java 25906 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil.adjacencypair;

import java.util.ArrayList;
import java.util.Collection;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.Default;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.IniFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ローカルにデータを持つ標準隣接応答対サービスのiniファイル.defaultに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Local_Default extends Default {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/
	
	/**
	 * langPathの表記に対応
	 */
	private Collection<Language> langPath ;
	
	/**
	 * filePathの表記に対応
	 */
	private String filePath = "";
	
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @throws ServiceConfigurationException
	 */
	public Local_Default(JSONObject jObj) throws ProcessFailedException  {
		try{
			
			//langPathの切り出し
			ArrayList<String> alString = new ArrayList<String>();
			JSONArray buffArray = jObj.getJSONArray("langPath");
			int loop = buffArray.length();
			for(int i=0;i<loop;i++){
				alString.add(buffArray.getString(i));
			}
			langPath = IniFunctions.stringsToLanguages(
					alString.toArray(new String[0]));
			
			//filePathの切り出し
			filePath = jObj.getString("filePath");
			
		}catch(Exception e){
			throw new ProcessFailedException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public Local_Default(){
	}
	
	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/
	
	/**
	* @return langPath を戻します。
	*/
	public Collection<Language> getLangPath() {
		return langPath; 
	}
		
	/**
	 * @return filePath を戻します。
	 */
	public String getFilePath() {
		return filePath;
	}

}
