/*
 * $Id: Local_ServiceDescription.java 25906 2008-07-15 04:46:14Z murakami $
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

import java.io.InputStream;

import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.Resource;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.ServiceDescription;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ローカルにデータを持つ標準隣接応答対サービスのiniファイル対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Local_ServiceDescription extends ServiceDescription {
	
	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/
	
	/**
	 * 
	 */
	private Local_Default defaultSource ; 

	/***************************************************************************
	 コンストラクタ関係
	 **************************************************************************/
	
	/**
	 * @param inputStream .iniファイルを読み込むストリーム
	 * コンストラクタは上位のコンストラクタをそのまま呼び出す
	 * @throws ServiceConfigurationException 
	 */
	public Local_ServiceDescription(InputStream inputStream)
	throws ProcessFailedException {
		super(inputStream);
	}

	@Override
	protected void initDefault(JSONObject joDef) throws ProcessFailedException {
		defaultSource = new Local_Default(joDef);
				
	}

	@Override
	protected void initResources(JSONArray jaSrc) throws ProcessFailedException {
		//Resourceの記述はこのフォーマットでは存在しないので省略
	}

	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/
	
	@Override
	public Local_Default getDefault() {
		return defaultSource;
	}

	@Override
	protected Resource[] getResources() {
		//Resourceの記述はこのフォーマットでは存在しないのでNULLを返す
		return null;
	}

}
