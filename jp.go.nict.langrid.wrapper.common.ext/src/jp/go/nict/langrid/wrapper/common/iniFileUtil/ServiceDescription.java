/*
 * $Id: ServiceDescription.java 26847 2008-12-12 08:37:57Z Takao Nakaguchi $
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

import java.io.InputStream;
import java.util.HashMap;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.nio.charset.CharsetUtil;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * iniファイルにの大元対応
 * 一般的な各項目を保持
 * ライブラリごとにこれをextendし、iniファイルの内容を保持するクラスを記述する
 * 
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26847 $
 */
public abstract class ServiceDescription {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * typeの表記に対応
	 */
	private String type = "";

	/**
	 * langmapの表記に対応
	 */
	private HashMap<String,String> langMap = new HashMap<String,String>();

	/**
	 * updateの記述に対応
	 */
	private MetaDataFile metaDataFile = new MetaDataFile();

	/**
	 * defaultの表記に対応
	 * サブクラスによるオーバーライドの必要あり
	 */
	@SuppressWarnings("unused")
	private Default defaultSource ;

	/**
	 * resourcesの表記に対応
	 * サブクラスによるオーバーライドの必要あり
	 */
	@SuppressWarnings("unused")
	private Resource[] resources ;

	
	/***************************************************************************
	 コンストラクタ関係
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * 資源の種類に依存する部分については別途抽象関数を用意し、呼び出す
	 * @param inputStream .iniファイルを読み込むストリーム
	 * @throws ProcessFailedException 
	 */
	public ServiceDescription(InputStream inputStream) throws ProcessFailedException  {
		try {
			//.iniファイルからのJSONObjectの生成
			if(inputStream ==null)
				throw new ProcessFailedException("ini file not found");
				
			JSONObject jObj = new JSONObject(StreamUtil.readAsString(inputStream, CharsetUtil.newUTF8Decoder()));
			
			// 実際のキー毎の選択処理(* 省略可能)
			// type
			// *langMap
			// *update
			// *default
			// resources

			// typeの切り出し
			type = jObj.getString("type");
			
			// langMapの切り出し
			if (jObj.has("langMap")) {
								
				JSONArray buffArray = jObj.getJSONArray("langMap");
								
				int loop = buffArray.length();
				for (int i = 0; i < loop; i++) {
					LangMap buffObject = new LangMap(buffArray.getJSONObject(i));
					langMap.put(buffObject.getOrig(),buffObject.getMapped());
				}
			}
			
			//metaDataFileの切り出し
			if (jObj.has("metaDataFile")) 
				metaDataFile = new MetaDataFile(jObj.getJSONObject("metaDataFile"));
			
			//defaultの切り出し
			if (jObj.has("default")) 
				initDefault(jObj.getJSONObject("default"));

			//resourcesの切り出し
			if (jObj.has("resources"))
				initResources(jObj.getJSONArray("resources"));
			else
				initResources(new JSONArray());
			
		} catch (Exception e) {
			throw new ProcessFailedException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public ServiceDescription() {
	}

	/**
	 * defaultを初期化するメソッド
	 * サブクラスによるオーバーライドの必要あり
	 * @param joDef defaultの記述に対応するJSONObject
	 * @throws ServiceConfigurationException
	 */
	protected abstract void initDefault(JSONObject joDef) 
	throws ProcessFailedException;

	/**
	 * resourcesを初期化するメソッド
	 * サブクラスによるオーバーライドの必要あり
	 * @param jaSrc sourcesの記述に対応するJSONArray
	 * @throws ServiceConfigurationException
	 */
	protected abstract void initResources(JSONArray jaSrc) 
	throws ProcessFailedException;

		
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
	 * @return langmap を戻します。
	 */
	public HashMap<String,String> getLangMap() {
		return langMap; 
	}
	
	/**
	 * @return metaDataFile を戻します。
	 */
	public MetaDataFile getMetaDataFile() {
		return metaDataFile;
	}

	/**
	 * @return default を戻します。
	 * サブクラスによりオーバーライドの必要あり
	 */
	protected abstract Default getDefault();

	/**
	 * @return resources を戻します。
	 * サブクラスによりオーバーライドの必要あり
	 */
	protected abstract Resource[] getResources();
}
