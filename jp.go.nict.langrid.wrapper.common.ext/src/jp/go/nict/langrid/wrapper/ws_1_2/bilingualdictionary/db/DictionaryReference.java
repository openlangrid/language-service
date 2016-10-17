/*
 * $Id:DictionaryReference.java 3369 2008-02-21 18:16:22 +0900 (土, 02 21 2008) sagawa $
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

import static jp.go.nict.langrid.service_1_2.typed.MatchingMethod.REGEX;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Term;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntry;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntrySearchCondition;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntrySearchResult;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.foundation.Order;
import jp.go.nict.langrid.service_1_2.foundation.typed.OrderDirection;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

/**
 * ユーザ辞書サービス(read系)のライブラリ
 * @author $Author:sagawa $
 * @version $Revision:3369 $
 */

public class DictionaryReference {

	private DictionaryCommon dictCommon;

	private String userMngTable;

	private String dictMngTable;

	/**
	 * コンストラクタ
	 * 
	 * @param dc 
	 */
	public DictionaryReference(DictionaryCommon dc) {
		dictCommon = dc;

		this.userMngTable = dictCommon.UserManagerTable;
		this.dictMngTable = dictCommon.DictManagerTable;
	}

	/**
	 * 辞書管理テーブルから辞書名をキーに辞書テーブル名を取得する．
	 * @param dictionary 辞書名
	 * @return 辞書テーブル名
	 * @throws SQLException　SQL実行時例外
	 * @throws Exception    その他の例外
	 */			
	public String getDictionaryTable(String dictionary) throws SQLException,Exception {
		String dicttable = null;

		Statement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT dicttable FROM " + dictMngTable
					+ " WHERE dictname = '" + dictionary + "';";
			
			rs = sqlstmt.executeQuery(query);
			
			if (rs.next()) {
				dicttable = rs.getString("dicttable");
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
		  dictCommon.releaseConnection(rs, sqlstmt, sqlcon);
		}
		return dicttable;
	}

	/**
	 * 辞書管理テーブルから辞書名をキーにログテーブル名を取得する．
	 * @param dictionary 辞書名
	 * @return ログテーブル名
	 * @throws Exception    その他の例外
	 */			
	public String getLogTable(String dictionary) throws Exception {
		String logtable = null;
		
		Statement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT logtable FROM " + dictMngTable
					+ " WHERE dictname = '" + dictionary + "';";
			rs = sqlstmt.executeQuery(query);
			if (rs.next()) {
				logtable = rs.getString("logtable");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
		return logtable;
	}

	/**
	 * 
	 * 
	 * @sort : 文字列の長さでソート 昇順asc 降順desc
	 */

	/**
	 * 指定言語の用語リスト取得
	 * @param dictionary 辞書名
	 * @param headLang   1番目の言語
	 * @param targetLang 2番目の言語
	 * @return 用語リスト
	 * @throws Exception その他の例外
	 */				
	public String[] getTermByLang(String dictionary, String headLang, String targetLang) throws Exception
	{
		return getTermByLang(dictionary, headLang,targetLang, null);
	}

	/**
	 * 指定言語の用語リスト取得
	 * @param dictionary 辞書名
	 * @param headLang   1番目の言語
	 * @param targetLang 2番目の言語
	 * @param sort       昇順(asc)・降順(desc)
	 * @return 用語リスト
	 * @throws Exception その他の例外
	 */				
	public String[] getTermByLang(String dictionary,
			String headLang, String targetLang,String text) throws Exception
	{
		String[] termList = null;

		Statement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;			
		
		try {
			
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "";
			query = "SELECT " + headLang + "," + targetLang + " FROM ";
			if (text.length() > 30)
			{query += getDictionaryTable(dictionary) + ";";}
			else
			{query += getDictionaryTable(dictionary) + " Where length(" + headLang + ") <= " + text.length() + ";";}
			rs = sqlstmt.executeQuery(query);
	        
	        ArrayList<String> list = new ArrayList<String>();
	        //rs.setFetchSize(rs.getFetchSize());
			while (rs.next()) {
				if ((rs.getString(headLang) != null) && (rs.getString(targetLang) != null)) {

					/*Deleted by m.gotou 2008/03/04
					if (text.contains(rs.getString(headLang).toLowerCase())) */
					if (text.toLowerCase().contains(rs.getString(headLang).toLowerCase()))
					{						
						
						boolean uniq =true;
						for (int i = 0; i < list.size(); ++i)
						{
							if (list.get(i).equals(rs.getString(headLang)))
							{
								uniq = false;
								break;
							}
						}
							
						if (uniq)
						{list.add(rs.getString(headLang));}
					}
				} else {
					//list.add("");
				}
			}

			termList = (String[]) list.toArray(new String[list.size()]);
			return termList;			
			
		} catch (Exception e) {
			throw e;
		} finally{	        
	        dictCommon.releaseConnection(rs, sqlstmt, sqlcon);
		}
	}



	/**
	 * 辞書データ検索前方・後方・部分一致・正規表現
	 * @param dictionary 辞書名
	 * @param headLang   検索条件となる言語
	 * @param targetLang 取得先となる言語
	 * @param headWord　　 検索用語
	 * @param searchMethod　検索条件(前方・後方・部分・正規表現一致)
	 * @return preSearch 検索結果
	 * @throws Exception その他の例外
	 */					
	public Translation[] preSearch(String dictionary,
			String headLang, String targetLang, String headWord,MatchingMethod matchingMethod) throws Exception {
		Connection sqlcon = null;
		PreparedStatement sqlstmt = null;
		ResultSet rs = null;

		try {
			sqlcon = dictCommon.getConnection();

			//Mode start by t.nakaguchi 2008/8/4 カラム名"エスケープ、クエリの発行を1回だけにする
			String query = createPreSearchQuery(
					headLang, targetLang, getDictionaryTable(dictionary)
					, matchingMethod.equals(REGEX) ? "~" : "ILIKE"
					);
			sqlstmt = sqlcon
					.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			switch(matchingMethod){
				case PREFIX:
					sqlstmt.setString(1, headWord + "%");
					break;
				case SUFFIX:
					sqlstmt.setString(1, "%" + headWord);
					break;
				case PARTIAL:
				default:
					sqlstmt.setString(1, "%" + headWord + "%");
					break;
				case REGEX:
					sqlstmt.setString(1,headWord);
			}
			rs = sqlstmt.executeQuery();

			List<Translation> result = new ArrayList<Translation>();
			Translation prevTrans = null;
			while (rs.next()) {
				String hw = rs.getString(headLang);
				String tw = rs.getString(targetLang);
				if(
						prevTrans != null
						&& prevTrans.getHeadWord().equals(hw))
				{
					prevTrans.setTargetWords(ArrayUtil.append(
							prevTrans.getTargetWords()
							, tw));
				} else{
					prevTrans = new Translation();
					prevTrans.setHeadWord(hw);
					prevTrans.setTargetWords(new String[]{tw});
					result.add(prevTrans);
				}
			}
			return result.toArray(new Translation[]{});
			//Mode end by t.nakaguchi 2008/8/4 カラム名"エスケープ、クエリの発行を1回だけにする
		} catch (SQLException e) {
			//Mode start by t.nakaguchi 2008/8/4 例外を返すように
			throw new ProcessFailedException(e);
			//Mode end by t.nakaguchi 2008/8/4 例外を返すように
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}
	
	/**
	 * 辞書データ完全一致検索
	 * @param dictionary 辞書名
	 * @param headLang   検索条件となる言語
	 * @param targetLang 取得先となる言語
	 * @param headWord　　 検索用語
	 * @return preSearch 検索結果
	 * @throws Exception その他の例外
	 */						
	public Translation[] completeSearch(String dictionary, String headLang,
			String targetLang, String headWord) throws Exception {
		
		String[] targetWord = new String[0];
		ArrayList<String> list = new ArrayList<String>();		

		PreparedStatement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;		

		try {			
			sqlcon = dictCommon.getConnection();
			//Mod start by t.nakaguchi 2008/08/04 カラム名"エスケープ)
			String query = "SELECT distinct \"" + targetLang + "\" FROM "
					+ getDictionaryTable(dictionary) + " WHERE " + headLang
					//Mod start by t.harato 2008/02/27 検索条件-修正)
					//+ " ILIKE ? ORDER BY priority asc;";
					+ " ILIKE ? and (" + targetLang + " is not null and " + targetLang + " <> '')"
					;
			        //Mod end by t.harato 2008/02/27 検索条件-修正)
			//Mod end by t.nakaguchi 2008/08/04 カラム名"エスケープ)
			sqlstmt = sqlcon
					.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);	
			sqlstmt.setString(1, headWord);
			rs = sqlstmt.executeQuery();
            int rec_cnt =0;
			while (rs.next()) {
				rec_cnt += 1;
				if (rs.getString(targetLang) != null) {
					list.add(rs.getString(targetLang));
				}
			}

			targetWord = (String[]) list.toArray(new String[list.size()]);
			if (rec_cnt !=0)
			{
				Translation t2= new Translation();
				t2.setHeadWord(headWord);
				t2.setTargetWords(targetWord);
		        Translation[] t1=new Translation[]{t2};
				return t1;						
			}
			else
			{
				return new Translation[]{};
			}
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}

	
	
	/**
	 * 辞書データ検索
	 * 存在すれば複数のデータを返す
	 * 検索方法から処理を分岐
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
		Translation[] t1 = null;
			if (searchMethod.equals(MatchingMethod.COMPLETE))
			{
				t1 =  completeSearch(dictionary,headLang,targetLang,headWord);
			}
			else
			{
				t1 =  preSearch(dictionary,headLang,targetLang,headWord,searchMethod);
			}
			return t1;
		}

	/**
	 * 辞書データ検索
	 * 指定条件で辞書を検索する
	 * 
	 * @param dictionary  辞書名
	 * @param startIndex Index開始位置
	 * @param maxCount   最大取得件数
	 * @param targetLangs 取得先となる言語リスト
	 * @param conditions　  検索条件リスト
	 * @param orders      　　　ソート条件リスト
	 * @return termentrysearch 検索結果
	 * @throws Exception その他の例外
	 */						
	public TermEntrySearchResult termentrysearch(String dictionary,
			int startIndex, int maxCount,String[] targetLangs,TermEntrySearchCondition[] conditions,Order[] orders) throws Exception {

		TermEntrySearchResult list = new TermEntrySearchResult();		
		PreparedStatement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;
		
		try {			
			sqlcon = dictCommon.getConnection();
			
			String query = "SELECT ";
			//Add start by t.harato 2008/02/27 (件数取得処理-追加)
			String countquery = "SELECT count(*) FROM "
				+ getDictionaryTable(dictionary);
			//Add end   by t.harato 2008/02/27 (件数取得処理-追加)
			int i = 0;
			for (String targetLang:targetLangs)
			{
				i +=1;
				if (i != 1)
				{
					query += "," + targetLang;
				}
				else
				{
					query += targetLang;
				}
			}

			query += ",id,priority FROM "
				+ getDictionaryTable(dictionary);			
		   
			i = 0;
			if (conditions!=null)
			{				
				for (TermEntrySearchCondition condition:conditions)
				{
					if (condition!=null)
					{
		    			i+=1;
						MatchingMethod matchingmethod = getMatchingMethodfromString(condition.getMatchingMethod().toUpperCase());
		                if (i != 1)
			            //Mod start  by t.harato 2008/05/07 (検索条件-修正)
//		                {query += " and " + condition.getLanguage();
//		                //Add start by t.harato 2008/02/27 (件数取得処理-追加)
//		                countquery += " and " + condition.getLanguage();}
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
//		                else
//		                {query += " WHERE " + condition.getLanguage();
//		                //Add start by t.harato 2008/02/27 (件数取得処理-追加)
//		                countquery += " WHERE " + condition.getLanguage();}
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
//		                if (matchingmethod == MatchingMethod.PREFIX)
//						{query +=" like '" + condition.getText() + "%'";
//						//Add start by t.harato 2008/02/27 (件数取得処理-追加)
//						countquery +=" like '" + condition.getText() + "%'";}
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
//		                else if (matchingmethod == MatchingMethod.SUFFIX)
//						{query +=" like '%" + condition.getText() + "'";			
//						//Add start by t.harato 2008/02/27 (件数取得処理-追加)
//						countquery +=" like '%" + condition.getText() + "'";}			
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
//						else if (matchingmethod == MatchingMethod.PARTIAL)
//						{query +=" like '%" + condition.getText() + "%'";
//						//Add start by t.harato 2008/02/27 (件数取得処理-追加)
//						countquery +=" like '%" + condition.getText() + "%'";}
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
//						else if (matchingmethod == MatchingMethod.REGEX)
//						{query +=" ='~" + condition.getText() + "'";
//						//Add start by t.harato 2008/02/27 (件数取得処理-追加)
//						countquery +=" ='~" + condition.getText() + "'";}
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
//						else
//						{query +=" ='" + condition.getText() + "'";										
//						//Add start by t.harato 2008/02/27 (件数取得処理-追加)
//						countquery +=" ='" + condition.getText() + "'";}										
//		                //Add end   by t.harato 2008/02/27 (件数取得処理-追加)
		                {query += " and " + condition.getLanguage();
		                countquery += " and " + condition.getLanguage();}
		                else
		                {query += " WHERE " + condition.getLanguage();
		                countquery += " WHERE " + condition.getLanguage();}
		                if (matchingmethod == MatchingMethod.PREFIX)
						{query +=" Ilike '" + condition.getText() + "%'";
						countquery +=" Ilike '" + condition.getText() + "%'";}
		                else if (matchingmethod == MatchingMethod.SUFFIX)
						{query +=" Ilike '%" + condition.getText() + "'";			
						countquery +=" Ilike '%" + condition.getText() + "'";}			
		            	else if (matchingmethod == MatchingMethod.PARTIAL)
						{query +=" Ilike '%" + condition.getText() + "%'";
						countquery +=" Ilike '%" + condition.getText() + "%'";}
		            	else if (matchingmethod == MatchingMethod.REGEX)
						{query +=" ='~" + condition.getText() + "'";
						countquery +=" ='~" + condition.getText() + "'";}
		            	else
						{query +=" ILIKE '" + condition.getText() + "'";										
						countquery +=" ILIKE '" + condition.getText()+ "'";}										
		                //Mod end  by t.harato 2008/05/07 (検索条件-修正)
		                
					}
				}				
			}
			//Delete start by t.harato 2008/04/21 (検索条件-削除)
//			//Add start by t.harato 2008/02/28 (検索条件-追加)
//			for (String targetLang:targetLangs)
//			{
//				if (i == 0)
//				{query += " WHERE (" + targetLang + " is not null and " + targetLang + " <> '') ";
//				}
//				else
//				{query += " and (" + targetLang + " is not null and " + targetLang + " <> '') ";
//				}
//				i += 1;
//			}
//			//Add end by t.harato 2008/02/28 (検索条件-追加)
			//Delete end by t.harato 2008/04/21 (検索条件-削除)
			i = 0;
			if (orders != null)
			{
				for (Order order:orders)
				{
					if (order != null)
					{
						i+=1;
						if (i ==1)
						{query += " ORDER BY " + order.getFieldName() + " " + replaceOrderDirection2postgreSQL(order) +";";}
						else
						{query += " ," + order.getFieldName() + " " + replaceOrderDirection2postgreSQL(order)+";";}						
					}
				}				
			}
			//Delete start by t.harato 2008/05/01 (検索条件-削除)
			//query += " limit " + maxCount +  ";";
			//Delete end by t.harato 2008/05/01 (検索条件-削除)
			sqlstmt = sqlcon
					.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);			
			rs = sqlstmt.executeQuery();
			ArrayList<TermEntry> termentrylist = new ArrayList<TermEntry>();	
			
			//StartIndexの位置までtermentrylist.add(null)
			//delete start by t.harato 2008/04/30 (検索処理-削除)
			//for (; i <= startIndex; i++)
			//{
			//	termentrylist.add(null);
			//}
			//delete end by t.harato 2008/04/30 (検索処理-削除)
			
			//add start by t.harato 2008/05/01 (検索処理-追加)
			int count = 0;
			//add end by t.harato 2008/05/01 (検索処理-追加)
			i = 0;
			while (rs.next()) {
				//add start by t.harato 2008/04/30 (検索処理-追加)
				if (i >= startIndex){
			    //add end by t.harato 2008/04/30 (検索処理-追加)
			    	
					//add start by t.harato 2008/05/01 (検索処理-追加)
					if (count == maxCount || maxCount == 0){
						break;
					}
					//add end by t.harato 2008/05/01 (検索処理-追加)
					TermEntry termentry = new TermEntry();
					termentry.setId(rs.getInt("id"));
					ArrayList<Term> termlist = new ArrayList<Term>();	
					for (String targetLang:targetLangs)
					{
						Term term = new Term();
						term.setLanguage(targetLang);				
						if (rs.getString(targetLang) != null) {
							term.setText(rs.getString(targetLang));
	                    }
						if (rs.getString("priority") != null) {
							term.setPriority(rs.getInt("priority"));
						}				
						termlist.add(term);
					}
					termentry.setTerms(termlist.toArray(new Term[termlist.size()]));
					termentrylist.add(termentry);
					count += 1;
					
			    //add start by t.harato 2008/04/30 (検索処理-追加)
				}
				i += 1;
				//add end by t.harato 2008/04/30 (検索処理-追加)
			}

			//Add start by t.harato 2008/02/27 (件数取得処理-追加)
			sqlstmt = sqlcon
			.prepareStatement(countquery, ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = sqlstmt.executeQuery();
            int totalcount=0;
			while (rs.next()) {
				totalcount = rs.getInt("count");
			}
			//Add end   by t.harato 2008/02/27 (件数取得処理-追加)
			list.setTotalCount(totalcount);
			list.setElements(termentrylist.toArray(new TermEntry[termentrylist.size()]));

			return list;		
		
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}		
	
	
	public String replaceOrderDirection2postgreSQL(Order order) throws Exception
	{
		try
		{
		if (OrderDirection.valueOf(order.getDirection().toUpperCase()).equals(OrderDirection.ASCENDANT))
		{ return "asc";}
		else if (OrderDirection.valueOf(order.getDirection().toUpperCase()).equals(OrderDirection.DESCENDANT))
		{ return "desc";}
		else
		{ throw new Exception("order direction only accept 'ASCENDANT' or 'DESCENDANT'");}
		}
		catch (Exception e)
		{throw new Exception("order direction only accept 'ASCENDANT' or 'DESCENDANT'");}
	}

	
	public MatchingMethod getMatchingMethodfromString(String matchingmethod) throws Exception
	{
		try
		{return MatchingMethod.valueOf(matchingmethod);}
		catch (Exception e)
		{throw new Exception(matchingmethod + " is Invalid MatchingMethod.");}
	}
	
	/**
	 * 辞書名から利用可能な言語リストを取得する．
	 * 
	 * @param dictionary 辞書名
	 * @return getSupportedLanguage 言語リスト
	 * @throws UnsupportedLanguageException サポートされない言語
	 * @throws Exception その他の例外
	 */							
	public String[] getSupportedLanguage(String dictionary) 
		throws Exception,UnsupportedLanguageException {
		String[] language = null;

		
		Statement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;				
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT language FROM " + dictMngTable
					+ " WHERE dictname = '" + dictionary + "';";

			
			rs = sqlstmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getObject("language") != null) {
					language = (String[])rs.getArray("language").getArray();
				}
			}
			return language;		

		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);
		}
	}

	/**
	 * 言語指定で辞書データを返す．
	 * 
	 * @param dictionary 辞書名
	 * @param language 言語リスト
	 * @return getDictionaryByLang 辞書データ
	 * @throws Exception その他の例外
	 */							
	public DictionaryData getDictionaryByLang(String dictionary,
			String[] language) throws Exception {

		ArrayList<Integer> idArray = new ArrayList<Integer>();
		ArrayList<Integer> priorityArray = new ArrayList<Integer>();

		DictionaryData dictData = new DictionaryData();
		dictData.setLanguage(language);

		String langlist = "";
		for (int i = 0; i < language.length; i++) {
			langlist += "," + language[i];
		}


		Statement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;						
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT id,priority" + langlist + " FROM "
					+ getDictionaryTable(dictionary)
					+ " ORDER BY priority ASC;";
			rs = sqlstmt.executeQuery(query);
			while (rs.next()) {
				idArray.add(rs.getInt("id"));
				priorityArray.add(rs.getInt("priority"));
			}

			int[] id = StringUtil.intArray2Int(idArray);
			int[] priority = StringUtil.intArray2Int(priorityArray);

			dictData.setTermId(id);
			dictData.setPriorityId(priority);
			dictData.setPriorityById(priority, id);

			for (int i = 0; i < language.length; i++) {
				rs.beforeFirst();
				ArrayList<String> temp = new ArrayList<String>();
				while (rs.next()) {
					temp.add(rs.getString(language[i]));
				}
				String[] term = (String[]) temp
						.toArray(new String[temp.size()]);
				term = StringUtil.Null2Empty(term);
				dictData.setTermById(language[i], id, term);
			}
			return dictData;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}


	/** DELETED by h.sagawa 2008/02/12 処理不要
	 * ▲▲▲ ユーザ管理テーブルより利用(Read)可能な辞書リストを返す． 辞書管理テーブルに存在しない辞書は，ユーザ管理より削除する．
	public String[] getReadableDictionary(String user) throws Exception {
		DictionaryEdit dictEdit = new DictionaryEdit(dictCommon);
		String[] readableList = null;

		Statement sqlstmt = null;
		ResultSet rs = null;
		Connection sqlcon = null;								
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT readlist FROM " + userMngTable
					+ " WHERE userid = '" + user + "';";
			rs = sqlstmt.executeQuery(query);

			if (rs.next()) {
				Jdbc3Array temp = (Jdbc3Array) rs.getObject("readlist");
				readableList = (String[]) temp.getArray();
			}
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean exist = false;
		for (int i = 0; i < readableList.length; i++) {
			// 辞書の存在を確認し，無ければユーザ管理より削除
			if (!existTable(readableList[i])) {
				dictEdit.deleteDicionaryInfo(readableList[i], user);
				exist = true;
			}
		}
		if (exist) {
			readableList = this.getReadableDictionary(user);
		}
		return readableList;
	}
    */

	/**
	 * 辞書データのIDの最大値を取得する
	 * 
	 * @param dictionary 辞書名
	 * @return maxID IDの最大値
	 * @throws Exception その他の例外
	 */							
	public int maxID(String dictionary) throws Exception {
		int maxid = 0;
		Connection sqlcon =null;
		Statement sqlstmt = null;
		ResultSet rs = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT MAX(id) FROM "
					+ getDictionaryTable(dictionary) + ";";
			rs = sqlstmt.executeQuery(query);
			rs.next();
            maxid = rs.getInt(1);
    		return maxid;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}

	}

	/**
	 * Priorityの最大値を取得する． 
	 * 
	 * @param dictionary 辞書名
	 * @return maxPriority Priorityの最大値
	 * @throws Exception その他の例外
	 */								
	public int maxPriority(String dictionary)throws Exception {
		int maxpriority = 0;

		Connection sqlcon =null;
		Statement sqlstmt = null;
		ResultSet rs = null;		
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT MAX(priority) FROM "
					+ getDictionaryTable(dictionary) + ";";
			rs = sqlstmt.executeQuery(query);
			rs.next();
			maxpriority = rs.getInt(1);
			return maxpriority;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}


	/**
	 * 指定辞書の用語IDリストを取得する
	 * 
	 * @param dictionary 辞書名
	 * @return getTermID 用語IDリスト
	 * @throws Exception その他の例外
	 */									
	public int[] getTermID(String dictionary) throws Exception {
		int[] termID = null;
		ArrayList<Integer> temp = new ArrayList<Integer>();

		Connection sqlcon =null;
		Statement sqlstmt = null;
		ResultSet rs = null;		
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT id FROM " + getDictionaryTable(dictionary)
					+ " ORDER BY priority ASC;";
			rs = sqlstmt.executeQuery(query);

			while (rs.next()) {
				temp.add(rs.getInt("id"));
			}
			termID = StringUtil.intArray2Int(temp);
			return termID;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}

	}

	/**
	 * テーブルの存在を確認
	 * 
	 * @param dictionary 辞書名
	 * @return existTable true:存在する false:存在しない
	 * @throws Exception その他の例外
	 */										
	public boolean existTable(String dictionary) throws Exception {

		Connection sqlcon =null;
		Statement sqlstmt = null;
		ResultSet rs = null;				
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT dictname FROM " + dictMngTable + ";";
			rs = sqlstmt.executeQuery(query);
			while (rs.next()) {
				if (dictionary.equalsIgnoreCase(rs.getString("dictname"))) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}

	/**
	 * 指定言語の存在を確認
	 * 
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @return existLanguage true:存在する false:存在しない
	 * @throws Exception その他の例外
	 */											
	public boolean existLanguage(String dictioary, String language) 
		throws Exception,UnsupportedLanguageException {

		if (language == null)
		{return false;}
		
		String[] supportedLanguage = getSupportedLanguage(dictioary);
		if (supportedLanguage != null) {
			for (int i = 0; i < supportedLanguage.length; i++) {
				if (language.equals(supportedLanguage[i])) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * 辞書の列名を取得
	 * 
	 * @param dictionary 辞書名
	 * @return getColumnList 列名リスト
	 * @throws Exception その他の例外
	 */					
	public String[] getColumnList(String dictionary) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		String[] columnList = null;

		Connection sqlcon =null;
		Statement sqlstmt = null;
		ResultSet rs = null;						
		
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT * FROM " + getDictionaryTable(dictionary)
					+ ";";
			rs = sqlstmt.executeQuery(query);
			// 登録されている列名の取得
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 0; i < meta.getColumnCount(); i++) {
				list.add(meta.getColumnName(i + 1));
			}
			columnList = list.toArray(new String[list.size()]);
			return columnList;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}

	/**
	 * 辞書データを全て返す
	 * 
	 * @param dictionary 辞書名
	 * @return getAllData 全てのデータ
	 * @throws Exception その他の例外
	 */					
	public String getAllData(String dictionary) throws Exception {
		String allData = "";
		String[] columnList = getColumnList(dictionary);

		for (int i = 0; i < columnList.length; i++) {
			allData += columnList[i] + "\t";
		}

		Connection sqlcon = dictCommon.getConnection();
		Statement sqlstmt = null;
		ResultSet rs = null;						
		try {
			sqlstmt = sqlcon.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT * FROM " + getDictionaryTable(dictionary)
					+ ";";
			rs = sqlstmt.executeQuery(query);

			while (rs.next()) {
				allData += "\n";
				for (int i = 0; i < columnList.length; i++) {
					allData += rs.getString(columnList[i]) + "\t";
				}
			}
			return allData;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
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
		Date lastupd = new Date();

		Connection sqlcon = null;
		Statement sqlstmt = null;
		ResultSet rs = null;								
		
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT lastupdate FROM " + dictMngTable
					+ " WHERE dictname = '" + dictionary + "';";
			
			rs = sqlstmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getObject("lastupdate") != null) {
					lastupd = (Date) rs.getObject("lastupdate");
				}
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastupd);
			return calendar;
		} catch (Exception e) {
			throw e;
		}finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
	}
	
	private String createPreSearchQuery(
			String headLang, String targetLang, String tableName
			, String matchMethod){
		return String.format(
				searchQueryFormat
				, headLang, targetLang, tableName, matchMethod
				);
	}

	private final String searchQueryFormat = 
			"select distinct \"%1$s\", \"%2$s\""
			+ " from %3$s"
			+ " where \"%1$s\" %4$s ? and \"%2$s\" is not null and \"%2$s\" <> ''"
			+ " order by \"%1$s\""
			;
}
