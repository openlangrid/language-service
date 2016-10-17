/*
 * $Id: Webapp_Default.java 25906 2008-07-15 04:46:14Z murakami $
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

import java.util.ArrayList;
import java.util.Collection;

import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.Default;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.IniFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Webアプリケーションを用いた用例対訳サービスのiniファイル.defaultに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_Default extends Default {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * langPathの表記に対応
	 */
	private Collection<LanguagePair> langPath ;

	/**
	 * actionの表記に対応
	 */
	private Webapp_Action action = new Webapp_Action();

	/**
	 * contentの表記に対応
	 */
	private Webapp_Content content = new Webapp_Content();

	
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/

	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @throws ProcessFailedException 
	 */
	public Webapp_Default(JSONObject jObj) throws ProcessFailedException {
		try{
			//実際のキー毎の選択処理
			//(*は欠落を許すもの)
			//langPath
			//action
			//content

			//langPathの切り出し
			ArrayList<String> alString = new ArrayList<String>();
			JSONArray buffArray = jObj.getJSONArray("langPath");
			int loop = buffArray.length();
			for(int i=0;i<loop;i++){
				alString.add(buffArray.getString(i));
			}
			langPath = IniFunctions.stringsToLanguagePairs(
					alString.toArray(new String[0]));

			//actionの切り出し
			action = new Webapp_Action(jObj.getJSONObject("action"),null);

			//contentの切り出し
			content = new Webapp_Content(jObj.getJSONObject("content"),null);
			
		}catch(Exception e){
			throw new ProcessFailedException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public Webapp_Default(){
	}

		
	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/
	/**
	 * @return langPath を戻します。
	 */
	public Collection<LanguagePair> getLangPath() {
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

}
