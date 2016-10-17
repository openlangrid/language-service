/*
 * $Id: Webapp_PartOfSpeech.java 25906 2008-07-15 04:46:14Z murakami $
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

import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Webアプリケーションを用いた用例対訳サービスのiniファイル.default(resources).contentに対応
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class Webapp_PartOfSpeech {

	/***************************************************************************
	 様々なパラメータに対応する変数を保持
	 **************************************************************************/

	/**
	 * noun_commonの表記に対応
	 */
	private String[] noun_common = {};

	/**
	 * noun_properの表記に対応
	 */
	private String[] noun_proper = {};

	/**
	 * noun_otherの表記に対応
	 */
	private String[] noun_other = {};

	/**
	 * nounの表記に対応
	 */
	private String[] noun = {};

	/**
	 * verbの表記に対応
	 */
	private String[] verb = {};

	/**
	 * adjectiveの表記に対応
	 */
	private String[] adjective = {};

	/**
	 * adverbの表記に対応
	 */
	private String[] adverb = {};
	
	/**
	 * otherの表記に対応
	 */
	private String[] other = {};
	
	/**
	 * unknownの表記に対応
	 */
	private String[] unknown = {};

		
	/***************************************************************************
	 コンストラクタ
	 **************************************************************************/
	/**
	 * コンストラクタ
	 * JSONObjectオブジェクトを受け取り、各フィールドを埋める
	 * @param jObj 処理対象のJSONObjectオブジェクト
	 * @param defaultSource 
	 * @throws ServiceConfigurationException 
	 */
	public Webapp_PartOfSpeech(JSONObject jObj, Webapp_Default defaultSource) throws ServiceConfigurationException {
		try {
			//実際のキー毎の選択処理
			//(*は欠落を許すもの)
			//noun_common*
			//noun_proper*
			//noun_other*
			//noun*
			//verb*
			//adjective*
			//adverb*
			//other*
			//unknown*
			
			//noun_commonの切り出し
			if (jObj.has("noun_common")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("noun_common");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				noun_common = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getNoun_common();
			
			//の切り出し
			if (jObj.has("noun_proper")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("noun_proper");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				noun_proper = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getNoun_proper();
			
			//noun_otherの切り出し
			if (jObj.has("noun_other")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("noun_other");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				noun_other = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getNoun_other();
			
			//nounの切り出し
			if (jObj.has("noun")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("noun");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				noun = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getNoun();
			
			//verbの切り出し
			if (jObj.has("verb")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("verb");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				verb = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getVerb();
			
			//adjectiveの切り出し
			if (jObj.has("adjective")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("adjective");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				adjective = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getAdjective();
			
			//adverbの切り出し
			if (jObj.has("adverb")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("adverb");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				adverb = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getAdverb();
			
			//otherの切り出し
			if (jObj.has("other")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("other");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				other = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getOther();
			
			//の切り出し
			if (jObj.has("unknown")) {
				ArrayList<String> alString = new ArrayList<String>();
				JSONArray buffArray = jObj.getJSONArray("unknown");
				int loop = buffArray.length();
				for(int i=0;i<loop;i++){
					alString.add(buffArray.getString(i));
				}
				unknown = alString.toArray(new String[0]);
			}else if(defaultSource!=null && defaultSource.getPartOfSpeech()!=null)
				noun_common = defaultSource.getPartOfSpeech().getUnknown();

		} catch (Exception e) {
			throw new ServiceConfigurationException(e.toString());
		}
	}
	
	/**
	 * デフォルトコンストラクタ
	 */
	public Webapp_PartOfSpeech() {
	}

	/***************************************************************************
	 各種変数に対応するGetter
	 **************************************************************************/

	/**
	 * @return adjective を戻します。
	 */
	public String[] getAdjective() {
		return adjective;
	}

	/**
	 * @return adverb を戻します。
	 */
	public String[] getAdverb() {
		return adverb;
	}

	/**
	 * @return noun を戻します。
	 */
	public String[] getNoun() {
		return noun;
	}

	/**
	 * @return noun_common を戻します。
	 */
	public String[] getNoun_common() {
		return noun_common;
	}

	/**
	 * @return noun_other を戻します。
	 */
	public String[] getNoun_other() {
		return noun_other;
	}

	/**
	 * @return noun_proper を戻します。
	 */
	public String[] getNoun_proper() {
		return noun_proper;
	}

	/**
	 * @return unknown を戻します。
	 */
	public String[] getUnknown() {
		return unknown;
	}

	/**
	 * @return verb を戻します。
	 */
	public String[] getVerb() {
		return verb;
	}

	/**
	 * @return other を戻します。
	 */
	public String[] getOther() {
		return other;
	}
}
