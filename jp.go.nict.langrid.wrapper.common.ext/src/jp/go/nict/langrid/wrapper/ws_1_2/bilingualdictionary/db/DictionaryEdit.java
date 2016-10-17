/*
 * $Id:DictionaryEdit.java 3421 2008-02-24 15:52:23 +0900 (日, 02 24 2008) sagawa $
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import jp.go.nict.langrid.language.util.LanguageUtil;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;


/**
 * ユーザ辞書サービス(write系)のライブラリ。
 * @author $Author:sagawa $
 * @version $Revision:3421 $
 */
public class DictionaryEdit {

	private DictionaryCommon dictCommon;
	
	private DictionaryReference dictReference =null;
	
	private String userMngTable;

	private String dictMngTable;
	
	
	/*
	 * コンストラクタ
	 */
	public DictionaryEdit(DictionaryCommon dc) {
		dictCommon = dc;

		this.userMngTable = dictCommon.UserManagerTable;
		this.dictMngTable = dictCommon.DictManagerTable;
	}

	/**
	 * 新規用語追加(複数登録)
	 * @param dictionary 辞書名
	 * @param language   追加言語リスト
	 * @param term       追加用語リスト
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */			
	public boolean addTerm(String dictionary, String[] language, String[] term) 
		throws Exception 
		,UnsupportedLanguageException
		{
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		PreparedStatement sqlstmt=null;
		Connection sqlcon=null;
		try {
			int id = dictReference.maxID(dictionary) + 1;
			int priority = 1;
			StringBuffer langQuery = new StringBuffer();
			StringBuffer termQuery = new StringBuffer();
			for (int i = 0; i < language.length; i++) {
				if (term[i] == null)
				{
					throw new InvalidParameterException("term","Can't Be Blank.");
				}		
				//delete start by t.harato 2008/05/07 (term登録処理-削除)
//				if (term[i].equals(""))
//				{
//					throw new InvalidParameterException("term","Can't Be Blank.");
//				}						
				//delete end by t.harato 2008/05/07 (term登録処理-削除)
				
				if (!dictReference.existLanguage(dictionary, language[i]))
				{
					throw new UnsupportedLanguageException("language",language[i].toString());
				}
				
				langQuery.append(language[i] + ",");
				termQuery.append("?,");

//				priority = Math.max(priority, dictReference
//						.maxPriority(dictionary) + 1);
			}
			String query = "INSERT INTO "
					+ dictReference.getDictionaryTable(dictionary)
					+ " (id ,priority, "
					+ langQuery.substring(0, langQuery.length() - 1) + ") "
					+ "VALUES(" + id + "," + priority + ","
					+ termQuery.substring(0, termQuery.length() - 1) + ")";
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			
			for (int i = 0; i < language.length; i++) {
				sqlstmt.setString(i + 1, term[i]);
			}
			sqlstmt.executeUpdate();

			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);
		}
		return true;
	}

	/**
     * 優先順位を設定する． 同期処理：もし同じIDが存在しない場合，そのIDは処理しない．但し，更新前に追加されている可能性があるため整合性をとる．．
	 * @param dictionary 辞書名
	 * @param termID     用語IDリスト
	 * @param priority   優先順位リスト
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */			
	public boolean setPriority(String dictionary, int[] termID, int[] priority) throws Exception {
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		Statement sqlstmt=null;
		Connection sqlcon=null;
		try {

			int[] pooledTermID = dictReference.getTermID(dictionary);
			ArrayList<Integer> tTermID = new ArrayList<Integer>();
			ArrayList<Integer> tPriority = new ArrayList<Integer>();

			for (int i = 0; i < termID.length; i++) {
				for (int n = 0; n < pooledTermID.length; n++) {
					if (termID[i] == pooledTermID[n]) {
						tTermID.add(termID[i]);
						tPriority.add(priority[i]);
						break;
					}
				}
			}

			// 差分の処理
			int[] ttPriority = StringUtil.intArray2Int(tPriority);
			int nextPriority = StringUtil.getMax(ttPriority) + 1;
			for (int i = 0; i < pooledTermID.length; i++) {
				if (!tTermID.contains(pooledTermID[i])) {
					tTermID.add(pooledTermID[i]);
					tPriority.add(++nextPriority);
				}
			}

			termID = StringUtil.intArray2Int(tTermID);
			priority = StringUtil.intArray2Int(tPriority);

			String dictionaryTable = dictReference
					.getDictionaryTable(dictionary);
			sqlcon = dictCommon.getConnection();

			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "";
			for (int i = 0; i < termID.length; i++) {
				query = "UPDATE " + dictionaryTable + " SET priority = '"
						+ priority[i] + "' WHERE id = '" + termID[i] + "';";

				sqlstmt.addBatch(query);
			}
			sqlstmt.executeBatch();
			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);
		}
		return true;
	}

	/** Deleted by h.sagawa 2008/02/24 処理不要
	 * ▲▲▲ ユーザ管理テーブルから辞書名を削除 ユーザからの直接呼び出しがないため権限不要
	public boolean deleteDicionaryInfo(String dictionary, String user) throws Exception{
		String[] readList, writeList, editList;
		String readLine = "", writeLine = "", editLine = "";
		Connection sqlcon=null;
		Statement sqlstmt = null;
		ResultSet rs = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT readlist,writelist,editlist FROM "
					+ userMngTable + " WHERE userid = '" + user + "';";
			rs = sqlstmt.executeQuery(query);

			if (rs.next()) {
				readList = (String[]) ((Jdbc3Array) rs.getObject("readlist"))
						.getArray();
				writeList = (String[]) ((Jdbc3Array) rs.getObject("writelist"))
						.getArray();
				editList = (String[]) ((Jdbc3Array) rs.getObject("editlist"))
						.getArray();

				for (int i = 0; i < readList.length; i++) {
					if (!readList[i].equals(dictionary)) {
						readLine += "," + readList[i];
					}
				}
				if (readLine.equals("")) {
					readLine = "{}";
				} else {
					readLine = "{" + readLine.substring(1) + "}";
				}

				for (int i = 0; i < writeList.length; i++) {
					if (!writeList[i].equals(dictionary)) {
						writeLine += "," + writeList[i];
					}
				}
				if (writeLine.equals("")) {
					writeLine = "{}";
				} else {
					writeLine = "{" + writeLine.substring(1) + "}";
				}

				for (int i = 0; i < editList.length; i++) {
					if (!editList[i].equals(dictionary)) {
						editLine += "," + editList[i];
					}
				}
				if (editLine.equals("")) {
					editLine = "{}";
				} else {
					editLine = "{" + editLine.substring(1) + "}";
				}

				query = "UPDATE " + userMngTable + " SET readlist = '"
						+ readLine + "', writelist = '" + writeLine
						+ "', editlist = '" + editLine + "' WHERE userid = '"
						+ user + "';";
				sqlstmt.executeUpdate(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
		return true;
	}
	 */



	/**
     * テーブルを論理削除(テーブル名を変更する)
	 * @param dictionary 辞書名
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */				
	public boolean deleteDictionary(String dictionary)throws Exception {
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			String dictionaryTable = dictReference
					.getDictionaryTable(dictionary);
			String logTable = dictReference.getLogTable(dictionary);

			String time = MiscUtil.getCurrentShortTime();

			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			// 辞書テーブル名変更
			String query = "ALTER TABLE " + dictionaryTable + " RENAME TO "
					+ dictionaryTable + "_" + time + ";";
			sqlstmt.executeUpdate(query);
			//[Begin]Deleted by h.sagawa 2008/02/24
			// ログテーブル名変更
			//query = "ALTER TABLE " + logTable + " RENAME TO " + logTable + "_"
			//		+ time + ";";
			//sqlstmt.executeUpdate(query);
			//[End]
			// 辞書管理テーブルから削除
			query = "DELETE FROM " + dictMngTable + " WHERE dictname = '"
					+ dictionary + "';";
			sqlstmt.executeUpdate(query);
            
			
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);			
		}
		return true;
	}

	/**
     * 新規辞書作成
	 * @param dictionary 辞書名
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */					
	public boolean createDictionary(String dictionary) throws Exception{
		DictionaryReference dictReference = new DictionaryReference(dictCommon);

		String dictTable = dictionary + "_dict";
		//Deleted by h.sahawa 2008/02/24
		//String logTable = dictionary + "_log";
		String dictColumn = "id INTEGER, priority INTEGER";
		//Deleted by h.sahawa 2008/02/24
		// ログの列に関しては要検討
		//String logColumn = "time TEXT, userid TEXT, type TEXT, data TEXT";

		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			// 同じ名前のテーブルの存在を確認
			if (dictReference.existTable(dictionary)) {
				throw new Exception("Table:" + dictionary + " has already existed.");
			}

			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "CREATE TABLE " + dictTable + "(" + dictColumn
					+ ");";
			sqlstmt.executeUpdate(query);

			//[Begin]Deleted by h.sagawa 2008/02/24
			// ログテーブルを作成
			//query = "CREATE TABLE " + logTable + "(" + logColumn + ");";
			//sqlstmt.executeUpdate(query);
			//[End]
			
			// 管理テーブルに名前を追加（作成したユーザのみに変更権がある）
			addDictionaryInfo(dictionary);
			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);			
		}
		return true;
	}

	
	/**
     * 管理テーブルに新規辞書データを追加
	 * @param dictionary 辞書名
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */						
	public boolean addDictionaryInfo(String dictionary) throws Exception{

		boolean result = false;
		//String[] readList, writeList, editList;
		//String readLine = null, writeLine = null, editLine = null;

		
		Connection sqlcon=null;
		Statement sqlstmt=null;
		ResultSet rs = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			// 辞書管理テーブルに追加
			String query = "INSERT INTO " + dictMngTable
					+ " (dictname, dicttable, logtable) " + "VALUES ('"
					+ dictionary + "', '" + dictionary + "_dict', '"
					+ dictionary + "_log');";
			sqlstmt.executeUpdate(query);
			/*[Begin] Deleted by h.sagawa 2008/02/24
			// ユーザ管理テーブルに追加 ＊空の場合の処理を追加すること
			query = "SELECT readlist,writelist,editlist FROM " + userMngTable
					+ " WHERE userid = '" + user + "';";
			rs = sqlstmt.executeQuery(query);

			if (rs.next()) {
				readList = (String[]) ((Jdbc3Array) rs.getObject("readlist"))
						.getArray();
				writeList = (String[]) ((Jdbc3Array) rs.getObject("writelist"))
						.getArray();
				editList = (String[]) ((Jdbc3Array) rs.getObject("editlist"))
						.getArray();

				readLine = "{";
				for (int i = 0; i < readList.length; i++) {
					readLine += readList[i] + ",";
				}
				readLine += dictionary + "}";

				writeLine = "{";
				for (int i = 0; i < writeList.length; i++) {
					writeLine += writeList[i] + ",";
				}
				writeLine += dictionary + "}";

				editLine = "{";
				for (int i = 0; i < editList.length; i++) {
					editLine += editList[i] + ",";
				}
				editLine += dictionary + "}";

				query = "UPDATE " + userMngTable + " SET readlist = '"
						+ readLine + "', writelist = '" + writeLine
						+ "', editlist = '" + editLine + "' WHERE userid = '"
						+ user + "';";
				sqlstmt.executeUpdate(query);
	
			}
			[End]*/

			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);			
		}
		return result;
	}

	/**
     * ID指定による辞書データ更新 同期処理：更新時に削除されていたら，新規登録扱いにする．
	 * @param dictionary 辞書名
	 * @param termID     用語ID
	 * @param language   言語リスト
	 * @param term       用語リスト
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */						
	public boolean updateTerm(String dictionary, int termID,
			String[] language, String[] term) throws Exception{
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		String dictionaryTable = dictReference.getDictionaryTable(dictionary);

		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			// TermIDの有無を確認する．無ければ新規登録する．
			int[] pooledTermID = dictReference.getTermID(dictionary);
			ArrayList<Integer> dTermID = new ArrayList<Integer>();
		    boolean dFlag = true;
			for (int n = 0; n < pooledTermID.length; n++) {
				int pTermID = pooledTermID[n];
					if (termID == pTermID) {
						dFlag = false;
						break;
					}
			}
			if (dFlag) {
				dTermID.add(termID);
			}
	
			// 存在しないIDが合った場合新規登録とする
			if (dTermID.size() > 0) {
				int[] aTermID = StringUtil.intArray2Int(dTermID);
				aTermID = StringUtil.stripDup(aTermID);
				for (int i = 0; i < aTermID.length; i++) {
					ArrayList<String> dLanguage = new ArrayList<String>();
					ArrayList<String> dTerm = new ArrayList<String>();
					
	//				for (int n = 0; n < termID.length; n++) {
					for (int n = 0; n < language.length; n++) {
						if (aTermID[i] == termID) {//
							dTerm.add(term[n]);
							dLanguage.add(language[n]);
					
						}
					}
					if (dTerm.size() > 0) {
						this.addTerm(dictionary, dLanguage
								.toArray(new String[dLanguage.size()]), dTerm
								.toArray(new String[dTerm.size()]));
	
					}
				}
			}


			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query, newTerm;
				if (!dTermID.contains(termID)) {
					for (int y = 0; y < language.length; y++) {
						//delete start by t.harato 2008/05/07 (term登録処理-削除)
//						if ((term[y] == null) || (term[y].equals("")))
//						{
//							throw new InvalidParameterException("term","Can't Be Blank.");
//						}
						//delete end by t.harato 2008/05/07 (term登録処理-削除)
						if (!dictReference.existLanguage(dictionary, language[y]))
						{
							throw new UnsupportedLanguageException("language",language.toString());
						}
						
					    newTerm = "'" + StringUtil.conversionSQL(term[y]) + "'";
						query = "UPDATE " + dictionaryTable + " SET " + language[y]
							+ " = " + newTerm + " WHERE id = '" + termID
							+ "';";
						sqlstmt.addBatch(query);
					}
				}
			sqlstmt.executeBatch();

			int[] termIDs = new int[] {termID};
			this.deleteNullLine(dictionary, termIDs);
			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);			
		}
		return true;
	}


	/**
     * Upadate後に，行が空になった場合の処理 空白行削除処理
	 * @param dictionary 辞書名
	 * @param termID     用語IDリスト
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */							
	public boolean deleteNullLine(String dictionary, int[] termId) throws Exception{
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		// 空行削除処理
		String[] supportLang = dictReference.getSupportedLanguage(dictionary);
		String dicionaryTable = dictReference.getDictionaryTable(dictionary);

		ArrayList<Integer> list = new ArrayList<Integer>();

		termId = StringUtil.stripDup(termId);
		Connection sqlcon=null;
		Statement sqlstmt=null;
		ResultSet rs = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			for (int i = 0; i < termId.length; i++) {
				String query = "SELECT * FROM " + dicionaryTable
						+ " WHERE id = '" + termId[i] + "';";
				rs = sqlstmt.executeQuery(query);
				if (rs.next()) {
					String temp = "";
					for (int n = 0; n < supportLang.length; n++) {
						temp += rs.getString(supportLang[n]);
					}
					if (temp.equals("")) {
						list.add(termId[i]);
					}
				}
			}

			if (list.size() > 0) {
				deleteTermById(dictionary, StringUtil.intArray2Int(list));
			}
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);
		}
		return true;
	}

	/**
     * 辞書データ削除
	 * @param dictionary 辞書名
	 * @param termID     用語IDリスト
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */							
	public boolean deleteTermById(String dictionary, int[] termid) throws Exception{
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			String check_id ="";
			ResultSet rs = null;
			String dicionaryTable = dictReference
					.getDictionaryTable(dictionary);
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			if (termid == null){
				throw new InvalidParameterException("termid","Can't Be Blank");
			}
			
			if (termid.length== 0){
				throw new InvalidParameterException("termid","Can't Be Blank");
			}
			
			
			for (int i = 0; i < termid.length; i++) {
				boolean flag = false;
				String sqlquery = "SELECT id FROM " + dicionaryTable + " WHERE id = "
				        + termid[i] + ";";
				rs = sqlstmt.executeQuery(sqlquery);
				while (rs.next()) {
					check_id = rs.getString("id");
					int id = Integer.parseInt(check_id);
					if (termid[i] == id){
						flag = true;
					}
				}
				if (flag == true)
				{
					String query = "DELETE FROM " + dicionaryTable + " WHERE id = "
					+ termid[i] + ";";
					sqlstmt.executeUpdate(query);
				}else{
					throw new InvalidParameterException("termid:" + termid[i] ,"Does not exist.");
				}
				
			}

		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);
		}
		return true;
	}

	/**
     * 対応言語追加
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */							
	public boolean addSupportedLanguage(String dictionary, String language)
		throws Exception,ProcessFailedException{
		DictionaryReference dictReference = new DictionaryReference(dictCommon);

		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			// 言語の存在を確認
			LanguageUtil.decodeLanguageArray(language);
			if (dictReference.existLanguage(dictionary, language)) {
			//	return false;
				throw new ProcessFailedException("'addLanguage Has Already exist.'");
			}
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			// 指定辞書テーブルに言語を追加
			String query = "ALTER TABLE "
					+ dictReference.getDictionaryTable(dictionary)
					+ " ADD COLUMN " + language + " TEXT;";
			sqlstmt.executeUpdate(query);
			// 辞書管理テーブルに言語を追加
			this.addLanguage(dictionary, language);
			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);
		}
		return true;
	}


	/**
	 * 辞書管理テーブルに新規言語を追加
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */								
	public void addLanguage(String dictionary, String language) throws Exception{
		String[] supportedLang = new String[0];
		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT language FROM " + dictMngTable
					+ " WHERE dictname = '" + dictionary + "';";
			ResultSet rs = sqlstmt.executeQuery(query);
			if (rs.next()) {
				if (rs.getObject("language") != null) {
					supportedLang = (String[])rs.getArray("language").getArray();
				}
			}
			String lang = "{";
			for (int i = 0; i < supportedLang.length; i++) {
				lang += supportedLang[i] + ",";
			}
			lang += language + "}";
			query = "UPDATE " + dictMngTable + " SET language = '" + lang
					+ "' WHERE dictname = '" + dictionary + "';";
			sqlstmt.executeUpdate(query);
			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);			
		}
	}

	/**
	 * 辞書の言語を削除
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */									
	public boolean deleteSupportedLanguage(String dictionary, String language
				)throws Exception {
		DictionaryReference dictReference = new DictionaryReference(dictCommon);

		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			// 言語の存在を確認
			if (!dictReference.existLanguage(dictionary, language)) {
				throw new ProcessFailedException("'deleteLanguage Does Not Exist.'");
			}
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			// 指定辞書テーブルから指定言語列を削除
			String query = "ALTER TABLE "
					+ dictReference.getDictionaryTable(dictionary)
					+ " DROP COLUMN " + language + ";";
			sqlstmt.executeUpdate(query);

			// 辞書管理テーブルから言語を削除
			this.deleteLanguage(dictionary, language);
			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);
		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);			
		}
		return true;
	}

	/**
	 * 辞書管理テーブルに新規言語を追加
	 * @param dictionary 辞書名
	 * @param language   言語
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */										
	public void deleteLanguage(String dictionary, String language)throws Exception {
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		String[] supportedLang = null;

		Connection sqlcon = null;
		Statement sqlstmt = null;
		ResultSet rs = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT language FROM " + dictMngTable
					+ " WHERE dictname = '"
					+ dictionary + "';";
					/*Deleted by m.gotou 2008/03/04
					+ dictReference.getDictionaryTable(dictionary) + "';";
					*/
			rs = sqlstmt.executeQuery(query);
			if (rs.next()) {
				supportedLang = (String[])rs.getArray("language").getArray();
			}
			StringBuffer lang = new StringBuffer();
			for (int i = 0; i < supportedLang.length; i++) {
				if (!supportedLang[i].equals(language)) {
					lang.append(supportedLang[i] + ",");
				}
			}

			/*Added by m.gotou 2008/03/04*/
			if(lang.length() == 0) lang.append(",");

			query = "UPDATE " + dictMngTable + " SET language = '{"
					+ lang.substring(0, lang.length() - 1).toString()
					+ "}' WHERE dictname = '" + dictionary + "';";
			sqlstmt.executeUpdate(query);

			// 辞書管理テーブルに最終更新日をセット
			this.setLastUpdate(dictionary);

		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(rs, sqlstmt, sqlcon);
		}
	}

	
	/*Deleted by h.sagawa 2008/02/28
	public void writeLog(String dictionary, String type, String user)throws Exception {
		DictionaryReference dictReference = new DictionaryReference(dictCommon);
		String logTable = dictReference.getLogTable(dictionary);
		
		// 時刻
		String time = MiscUtil.getCurrentTime();
		time = "'" + time + "'";

		// 名前
		if (user.equals("") || user == null) {
			user = "NULL";
		} else {
			user = "'" + user + "'";
		}

		// 種類
		if (type.equals("") || type == null) {
			type = "NULL";
		} else {
			type = "'" + type + "'";
		}
		
		// 辞書データ取得
		String data = dictReference.getAllData(dictionary);
		if (data.equals("") || data == null) {
			data = "NULL";
		} else {	
			data = "'" + StringUtil.conversionSQL(data) + "'";
		}
		
		
		// クエリ実行
		String query = "INSERT INTO " + logTable
				+ " (time, userid, type, data) "
				+ "VALUES(" + time + "," + user + "," + type + "," + data +")";

		Connection sqlcon = dictCommon.getConnection();
		PreparedStatement sqlstmt=null;
		try {
			sqlstmt = sqlcon
					.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			sqlstmt.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);
		}
	}
	*/

	/**
	 * 辞書管理テーブルの最終更新日をセット
	 * @param dictionary 辞書名
	 * @return 処理結果
	 * @throws Exception 例外発生
	 */	
	public void setLastUpdate(String dictionary)throws Exception {
		Connection sqlcon = null;
		Statement sqlstmt = null;
		try {
			sqlcon = dictCommon.getConnection();
			sqlstmt = sqlcon
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "UPDATE " + dictMngTable + " SET lastupdate = now()"
					+ " WHERE dictname = '" + dictionary + "';";
			sqlstmt.executeUpdate(query);
		} catch (Exception e) {
		   throw e;
		} finally{
			dictCommon.releaseConnection(sqlstmt, sqlcon);
		}
	}	
}
