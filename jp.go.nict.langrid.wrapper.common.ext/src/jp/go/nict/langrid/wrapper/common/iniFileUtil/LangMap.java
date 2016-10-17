/*
 * $Id: LangMap.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.common.iniFileUtil;

import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;

import org.json.JSONObject;

/**
 * iniファイル.langMapに対応
 * 言語コードと、資源ごとの言語名表現の対応付けを保持
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class LangMap {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * origの表記に対応
	 */
	private String orig = "";

	/**
	 * mappedの表記に対応
	 */
	private String mapped = "";

	
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @throws ServiceConfigurationException 
	 */
	public LangMap(JSONObject jObj) throws ServiceConfigurationException {
		try {
			//実際のキー毎の選択処理
			//順に
			//orig
			//mapped

			orig = jObj.getString("orig");

			mapped = jObj.getString("mapped");

		} catch (Exception e) {
			throw new ServiceConfigurationException(e.toString());
		}
	}

	/**
	 * デフォルトコンストラクタ
	 */
	public LangMap(){
	}

		
	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/

	/**
	 * @return orig を戻します。
	 */
	public String getOrig() {
		return orig; 
	}

	/**
	 * @return mapped を戻します。
	 */
	public String getMapped() {
		return mapped; 
	}

}
