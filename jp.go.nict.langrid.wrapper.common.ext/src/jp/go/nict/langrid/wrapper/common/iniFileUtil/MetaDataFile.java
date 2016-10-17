/*
 * $Id: MetaDataFile.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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
 * iniファイル.update.metaDataFileに対応
 * .rdf生成時の情報を保持
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public class MetaDataFile {

	/***********************************************************************
	 * 様々なパラメータに対応する変数を保持
	 **********************************************************************/

	/**
	 * creatorの記述に対応
	 */
	private String creator = "";

	/**
	 * publisherの記述に対応
	 */
	private String publisher = "";

	/**
	 * sourceの記述に対応
	 */
	private String source = "";	

	/***********************************************************************
	 * コンストラクタ関係
	 **********************************************************************/
	/**
	 * コンストラクタ JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * 資源の種類に依存する部分については別途抽象関数を用意し、呼び出す
	 * 
	 * @param jObj
	 *            処理対象のJSONObjectオブジェクト
	 * @throws ServiceConfigurationException
	 */
	public MetaDataFile(JSONObject jObj) throws ServiceConfigurationException  {
		try {			

			// 実際のキー毎の選択処理
			// creator
			// publisher
			// source
			
			// creatorの切り出し
			creator = jObj.getString("creator");

			// publisherの切り出し
			publisher = jObj.getString("publisher");

			// sourceの切り出し
			source = jObj.getString("source");
			
		} catch (Exception e) {
			throw new ServiceConfigurationException(e.toString());
		}
	}	

	/**
	 * デフォルトコンストラクタ
	 */
	public MetaDataFile() {
	}
	

	/***********************************************************************
	 * 各種変数に対応するGetter
	 **********************************************************************/

	/**
	 * @return creator を戻します。
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @return publisher を戻します。
	 */
	public String getPublisher() {
		return publisher;
	}

	/**
	 * @return source を戻します。
	 */
	public String getSource() {
		return source;
	}
}
