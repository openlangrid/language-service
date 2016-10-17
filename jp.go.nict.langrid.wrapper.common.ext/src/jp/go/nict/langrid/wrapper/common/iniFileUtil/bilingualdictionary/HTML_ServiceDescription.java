/*
 * $Id: HTML_ServiceDescription.java 25906 2008-07-15 04:46:14Z murakami $
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

import java.io.InputStream;
import java.util.ArrayList;

import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.ServiceDescription;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * HTMLからの用例対訳のiniファイルに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class HTML_ServiceDescription extends ServiceDescription {
	
	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * defaultの表記に対応
	 */
	private HTML_Default defaultSource ;

	/**
	 * resourcesの表記に対応
	 */
	private HTML_Resource[] resources ; 
	
	
	/***************************************************************************
	 コンストラクタ関係
	 **************************************************************************/
	/**
	 * _ServiceDescriptionのコンストラクタを呼び出す
	 * @param inputStream .iniファイルを読み込むストリーム
	 * @throws ServiceConfigurationException
	 */
	public HTML_ServiceDescription(InputStream inputStream) throws ProcessFailedException {
		super(inputStream);
	}

	/**
	 * デフォルトコンストラクタ
	 */
	public HTML_ServiceDescription() {
	}

	@Override
	protected void initDefault(JSONObject joDef) throws ProcessFailedException {
		defaultSource = new HTML_Default(joDef);
	}

	@Override
	protected void initResources(JSONArray jaSrc) throws ProcessFailedException {
		ArrayList<HTML_Resource> alResources = new ArrayList<HTML_Resource>();
		int loop = jaSrc.length();
		
		if(loop==0){
			HTML_Resource buffObject  = 
				new HTML_Resource(new JSONObject(),defaultSource);
			alResources.add(buffObject);
		}
		
		for(int i=0;i<loop;i++){
			try {
				HTML_Resource buffObject  = new HTML_Resource(jaSrc.getJSONObject(i),
					defaultSource);
				alResources.add(buffObject);
			} catch(Exception e) {
				throw new ProcessFailedException(e.toString());
			}
		}
		
		resources = alResources.toArray(new HTML_Resource[0]);
		
	}

	
	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/
	@Override
	public HTML_Default getDefault() {
		return defaultSource;
	}

	@Override
	public HTML_Resource[] getResources() {
		return resources;
	}
}
