/*
 * $Id:DictionaryService.java 3369 2008-02-23 18:42:55 +0900 (木, 02 23 2008 sagawa $
 *
 * Copyright (C) 2008 Language Grid Assocation.
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
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
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;


import java.util.ArrayList;
import java.util.Date;

import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntrySearchResult;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import java.util.Calendar;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntrySearchCondition;
import jp.go.nict.langrid.service_1_2.foundation.Order;



/**
 * ユーザ辞書サービス処理分岐元サービス
 * @author $Author:sagawa $
 * @version $Revision:3369 $
 */

public class DictionaryService {

	private DictionaryReference dictReference;

	private DictionaryEdit dictionaryEdit;

	private DictionaryCommon dictCommon;

	private boolean result;

	/**
	 * コンストラクタ
	 */
	public DictionaryService() {
		dictCommon = new DictionaryCommon();
		dictReference = new DictionaryReference(dictCommon);
		dictionaryEdit = new DictionaryEdit(dictCommon);
	}

	/**
	 * 新規辞書を作成する
	 * @param dictionary 辞書名
	 * @return createDictionary 処理結果
	 * @throws Exception　例外発生
	 */		
	public boolean createDictionary(String dictionary) throws Exception {
		return dictionaryEdit.createDictionary(dictionary);
	}

	/**
	 * 辞書に言語を追加
	 * @param dictionary 辞書名
	 * @param language   追加言語名
	 * @return addLanguage 処理結果
	 * @throws Exception　例外発生
	 */		
	public boolean addLanguage(String dictionary, String language) throws Exception {
		return dictionaryEdit.addSupportedLanguage(dictionary, language);
	}

	/**
	 * 辞書から言語を削除
	 * @param dictionary 辞書名
	 * @param language   削除言語名
	 * @return deleteLanguage 処理結果
	 * @throws Exception　例外発生
	 */		
	public boolean deleteLanguage(String dictionary, String language)
		throws Exception {
		return dictionaryEdit.deleteSupportedLanguage(dictionary, language);
	}

	/**
	 * 辞書削除
	 * @param dictionary 辞書名
	 * @return deleteDictionary 処理結果
	 * @throws Exception　例外発生
	 */		
	public boolean deleteDictionary(String dictionary)throws Exception {
		return dictionaryEdit.deleteDictionary(dictionary);
	}


	/**
	 * 指定辞書の言語リスト
	 * @param dictionary 辞書名
	 * @return getSupportedLanguageByDictionary 言語リスト
	 * @throws Exception　例外発生
	 */		
	public String[] getSupportedLanguageByDictionary(String dictionary) throws Exception {
		return dictReference.getSupportedLanguage(dictionary);
	}

	/**
	 * 言語指定の辞書データ
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @return getDictionaryByLang 辞書データ
	 * @throws Exception　例外発生
	 */
	public DictionaryData getDictionaryByLang(String dictionary,
			String[] language)throws Exception {
		return dictReference.getDictionaryByLang(dictionary, language);
	}

	/**
	 * 新規用語の追加
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @param term       用語
	 * @return addTerm   処理結果
	 * @throws Exception　例外発生
	 */	
	public boolean addTerm(String dictionary, String language, String term) 
	throws Exception
	,UnsupportedLanguageException,InvalidLanguageTagException
	{
		String[] l = new String[] { language };
		String[] t = new String[] { term };
		return addTerm(dictionary, l, t);
	}

	/**
	 * 新規用語の追加
	 * @param dictionary 辞書名
	 * @param language   言語リスト
	 * @param term       用語リスト
	 * @return addTerm   処理結果
	 * @throws Exception　例外発生
	 */	
	public boolean addTerm(String dictionary, String[] language, String[] term) 
		throws Exception 
		,UnsupportedLanguageException
		{
		result = dictionaryEdit.addTerm(dictionary, language, term);
		return result;
	}

	/**
	 * 用語の削除
	 * @param dictionary 辞書名
	 * @param termID     用語ID
	 * @return addTerm   処理結果
	 * @throws Exception　例外発生
	 */	
	public boolean deleteTermById(String dictionary, int termId) throws Exception {
		return deleteTermById(dictionary, new int[] { termId });
	}

	/**
	 * 用語の削除
	 * @param dictionary 辞書名
	 * @param termID     用語IDリスト
	 * @return addTerm   処理結果
	 * @throws Exception　例外発生
	 */	
	public boolean deleteTermById(String dictionary, int termId[]) throws Exception {
		// log
		result = dictionaryEdit.deleteTermById(dictionary, termId);
		return result;
	}

	/**
	 * 用語の更新
	 * @param dictionary 　辞書名
	 * @param termID    　　　 用語ID
	 * @param language   　  言語
	 * @return updateTerm 処理結果
	 * @throws Exception　  例外発生
	 */	
	public boolean updateTerm(String dictionary, int termId, String language,
			String term) throws Exception {
		return updateTerm(dictionary, termId,
				new String[] { language }, new String[] { term });
	}

	/**
	 * 用語の更新
	 * @param dictionary 辞書名
	 * @param termID     用語ID
	 * @param language   言語リスト
	 * @return updateTerm   処理結果
	 * @throws Exception　例外発生
	 */	
	public boolean updateTerm(String dictionary, int termId,
			String[] language, String[] term) throws Exception {
		result = dictionaryEdit.updateTerm(dictionary, termId, language, term);
		return result;
	}

	/**
	 * 優先順位設定
	 * @param dictionary 　　辞書名
	 * @param termID    　　　　 用語IDリスト
	 * @param priority   　　　優先順位リスト
	 * @return setPriority 処理結果
	 * @throws Exception　    例外発生
	 */	
	public boolean setPriority(String dictionary, int[] termId, int[] priority) throws Exception {
		return dictionaryEdit.setPriority(dictionary, termId, priority);
	}

	/**
	 * 辞書データ検索
	 * 
	 * @param dictionary 辞書名
	 * @param headLang   検索条件となる言語
	 * @param targetLang 取得先となる言語
	 * @param headWord　　 検索用語
	 * @param searchMethod　検索条件(完全・前方・後方・部分・正規表現一致)
	 * @return preSearch 検索結果
	 * @throws Exception その他の例外
	 */			
	public Translation[] search(String dictionary, String headLang,
			String targetLang, String headWord,MatchingMethod searchMethod) throws Exception {

		
		return dictReference.search(dictionary, headLang, targetLang, headWord,
				searchMethod);
	}

	/**
	 * 辞書データ検索
	 * 指定条件で辞書を検索する
	 * 
	 * @param dictionary 辞書名
	 * @param startIndex Index開始位置
	 * @param maxCount   最大取得件数
	 * @param targetLangs 取得先となる言語リスト
	 * @param MatchingCondition　検索条件リスト
	 * @param orders            ソート条件リスト
	 * @return termentrysearch 検索結果
	 * @throws Exception その他の例外
	 */						
	public TermEntrySearchResult termentrysearch(String dictionary,int startIndex, int maxCount,String[] targetLangs,TermEntrySearchCondition[] conditions,Order[] orders) throws Exception {
		return dictReference.termentrysearch(dictionary,startIndex, maxCount, targetLangs, conditions, orders);
	}
	
	
	/**
	 * 専門用語出現判定（文字列マッチング） 
	 * 処理高速化1:文字数が３０文字以下の場合はDBにlength指定
	 * 処理高速化2:文字列マッチング処理をレコードカーソル取得Loop内で実行
	 * @param dictionary 辞書名
	 * @param headLang   検索条件となる言語
	 * @param targetLang 取得先となる言語
	 * @param text        　　 検索用語
	 * @return extract   検索結果
	 * @throws Exception その他の例外
	 */
	public String[] extract(String dictionary, String headLang, String targetLang, String text) throws Exception
	{
		return dictReference.getTermByLang(dictionary,headLang,targetLang,text);
	}

	
	/**
	 * 指定辞書の最終更新日を取得
	 * 
	 * @param dictionary 辞書名
	 * @return getLastUpdate 最終更新日
	 * @throws Exception その他の例外
	 */					
	public Calendar getLastUpdate(String dictionary) throws Exception
	{
		return dictReference.getLastUpdate(dictionary);
	}

}
