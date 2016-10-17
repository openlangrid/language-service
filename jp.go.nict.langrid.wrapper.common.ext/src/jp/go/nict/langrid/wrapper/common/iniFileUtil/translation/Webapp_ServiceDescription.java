/*
 * $Id: Webapp_ServiceDescription.java 25906 2008-07-15 04:46:14Z murakami $
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

import java.io.InputStream;
import java.util.ArrayList;

import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.ServiceDescription;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Webアプリケーションを用いた翻訳サービスのiniファイルに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_ServiceDescription extends ServiceDescription {
	
	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * defaultの表記に対応
	 */
	private Webapp_Default defaultSource ;
 
	/**
	 * resourcesの表記に対応
	 */
	private Webapp_Resource[] resources ; 
	
	
	/***************************************************************************
	 コンストラクタ関係
	 **************************************************************************/
	/**
	 * ServiceDescriptionのコンストラクタを呼び出す
	 * @param inputStream .iniファイルを読み込むストリーム
	 * @throws ServiceConfigurationException
	 */
	public Webapp_ServiceDescription(InputStream inputStream) throws ProcessFailedException {
		super(inputStream);
	}

	@Override
	protected void initDefault(JSONObject joDef) throws ProcessFailedException {
		defaultSource = new Webapp_Default(joDef);
	}

	@Override
	protected void initResources(JSONArray jaSrc) throws ProcessFailedException {
		ArrayList<Webapp_Resource> alResources = new ArrayList<Webapp_Resource>();
		int loop = jaSrc.length();
		for(int i=0;i<loop;i++){
			try {
				Webapp_Resource buffObject  = 
					new Webapp_Resource(jaSrc.getJSONObject(i),defaultSource);
				alResources.add(buffObject);
			} catch(Exception e) {
				throw new ProcessFailedException(e.toString());
			}
		}
		
		resources = alResources.toArray(new Webapp_Resource[0]);
		
	}

	
	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/
	@Override
	public Webapp_Default getDefault() {
		return defaultSource;
	}

	@Override
	public Webapp_Resource[] getResources() {
		return resources;
	}
	
	/***************************************************************************
	 その他のメソッド
	 **************************************************************************/

	/**
	 * 言語コードを1つ以上持つ配列を受け取り、
	 * その配列と、langPath(順序も考慮)が一致するような Trans_Webapp_Resource を返す
	 * 
	 * @param aPair 検索対象の言語コードのペア
	 * @return 結果を格納したArrayList
	 */
	public Webapp_Resource getResources(LanguagePair aPair) {

		for (int i = 0; i < resources.length; i++) {

			if(resources[i].getLangPath().contains(aPair))
				return resources[i];
		}
		return null;
	}
}
