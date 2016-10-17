/*
 * $Id: Webapp_Resource.java 25906 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis;

import java.util.ArrayList;
import java.util.Collection;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.IniFunctions;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.Resource;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Webアプリケーションを用いた用例対訳サービスのiniファイル.defaultに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_Resource extends Resource {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * langPathの表記に対応
	 */
	private Collection<Language> langPath ;

	/**
	 * actionの表記に対応
	 */
	private Webapp_Action action = new Webapp_Action();

	/**
	 * contentの表記に対応
	 */
	private Webapp_Content content = new Webapp_Content();

	/**
	 * partOfSpeechの表記に対応
	 */
	private Webapp_PartOfSpeech partOfSpeech = new Webapp_PartOfSpeech();
		
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @throws ServiceConfigurationException 
	 */
	public Webapp_Resource(JSONObject jObj,Webapp_Default defaultSource) throws ServiceConfigurationException {
		try{
			//実際のキー毎の選択処理
			//(*は欠落を許すもの)
			//*langPath
			//*action
			//*content

			//langPath*の切り出し
			if (jObj.has("langPath")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("langPath");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				langPath = IniFunctions.stringsToLanguages(
						alString.toArray(new String[0]));
			}else if(defaultSource!=null)
				langPath = defaultSource.getLangPath();

			//actionの切り出し
			if(jObj.has("action"))
				action = new Webapp_Action(jObj.getJSONObject("action"),null);
			else if(defaultSource != null)
				action = defaultSource.getAction();

			//contentの切り出し
			if(jObj.has("content"))
				content = new Webapp_Content(jObj.getJSONObject("content"),null);
			else if(defaultSource != null)
				content = defaultSource.getContent();
			

			// partOfSpeechの切り出し
			if(jObj.has("partOfSpeech"))
				partOfSpeech = new Webapp_PartOfSpeech(jObj.getJSONObject("partOfSpeech"), null);
			else if(defaultSource != null)
				partOfSpeech = defaultSource.getPartOfSpeech();
			
		}catch(Exception e){
			throw new ServiceConfigurationException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public Webapp_Resource(){
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
	 * @return action を戻します。
	 */
	public Webapp_Action getAction() {
		return action; 
	}

	/**
	 * @return content を戻します。
	 */
	public Webapp_Content getContent() {
		return content; 
	}

	/**
	 * @return partOfSpeech を戻します。
	 */
	public Webapp_PartOfSpeech getPartOfSpeech() {
		return partOfSpeech;
	}

}
