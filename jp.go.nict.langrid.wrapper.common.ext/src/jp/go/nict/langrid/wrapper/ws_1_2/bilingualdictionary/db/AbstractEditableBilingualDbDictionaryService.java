/*
 * $Id:AbstractBilingualDictionaryService.java 1 2008-01-23 18:42:55 +0900 (木, 01 23 2008) sagawa $
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
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import jp.go.nict.langrid.commons.transformer.ToStringTransformer;
import jp.go.nict.langrid.commons.util.CollectionUtil;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.LanguageNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.EditableBilingualDictionaryService;
import jp.go.nict.langrid.service_1_2.foundation.Order;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.AbstractEditableBilingualDictionaryService;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntrySearchResult;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Term;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntry;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.TermEntrySearchCondition;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;

/**
 * 辞書サービス(write系)の基底クラスです．
 * @author $Author:sagawa $
 * @version $Revision:1 $
 */
public abstract class AbstractEditableBilingualDbDictionaryService
	extends AbstractEditableBilingualDictionaryService implements EditableBilingualDictionaryService
{	
		
	private String dictionary = "";

	private DictionaryService dictService = null;
	
	private DictionaryCommon dictCommon;
	
	private DictionaryReference dictReference;		
	/**
	 * コンストラクタ。
	 * @param 辞書テーブル名
	 */
	public AbstractEditableBilingualDbDictionaryService(String dict)
	throws Exception
	{
		dictionary = dict;
		dictService = new DictionaryService();
		dictCommon = new DictionaryCommon();
		dictReference = new DictionaryReference(dictCommon);
		setSupportedMatchingMethods(new HashSet<MatchingMethod>(
				Arrays.asList(MatchingMethod.values())));
	}	
	//[End]		
	

	/**
	 * TermEntryを検索する．
	 * @param startIndex 開始インデックス
	 * @param maxCount 最大件数
	 * @param language
	 * @param conditions 検索条件
	 * @param orders ソート順
	 * @return 検索結果
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguageNotUniquelyDecidedException 対訳言語対候補が一意に求まらない（例：headLangもしくはtargetLangにzhを指定したときに，対応言語対にzh-Hansとzh-Hantが存在した場合）
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguageException 指定された言語対はサポートされていない
	 */		
	public TermEntrySearchResult searchTerm(int startIndex, int maxCount
		    , String[] language, TermEntrySearchCondition[] conditions
		    , Order[] orders
		    )
		throws AccessLimitExceededException, InvalidParameterException
		, LanguageNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException, UnsupportedLanguageException
		{
		try	
		{
			
//			new NumberValidator("startIndex",startIndex).notNegative().notNull().getValue();
//			new NumberValidator("maxCount",maxCount).notNegative().notNull().getValue();			
//			MatchingMethod[] mmethods = (MatchingMethod[]) matchingMethods.toArray(new MatchingMethod[matchingMethods.size()]);
//			jp.go.nict.langrid.service_1_2.foundation.typed.MatchingCondition[] mc = 
//				new MatchingConditionsValidator(
//					"MatchingConditions", 
//					conditions, 
//					mmethods).notNull().notEmpty().getMatchingConditions();
			
			if (language== null)
			{language = dictService.getSupportedLanguageByDictionary(dictionary);}
			if (language.length == 0)
			{language = dictService.getSupportedLanguageByDictionary(dictionary);}
//			for (String lang:language)
//			{String hw = new StringValidator("headWord", lang)
//			.notNull().trim().notEmpty().getValue();}

    		for (int i = 0; i < language.length;i++ ){
    			if (!dictReference.existLanguage(dictionary, language[i])) {
    				throw new UnsupportedLanguageException("condition.language",language[i]);
    			}
    		}
    		return dictService.termentrysearch(dictionary,
    				startIndex,maxCount,language,conditions,orders);   	
		}
		catch (UnsupportedLanguageException e) {
			throw e;
		} 
		catch (IllegalArgumentException e) {
			throw e;
	 	} catch (ProcessFailedException e){
	 		throw e;
	 	}
		catch (Exception e) {
			throw new ProcessFailedException(e.toString());
		}
	}

 	 
	 
	/**
	 * 対訳セットの追加（DBの1行分の追加）。
	 * 辞書の全言語分のデータが渡されなかった場合、渡されなかった言語の訳語として空文字列が格納される。
	 * @param terms 対訳セット
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguageNotUniquelyDecidedException 対訳言語対候補が一意に求まらない（例：headLangもしくはtargetLangにzhを指定したときに，対応言語対にzh-Hansとzh-Hantが存在した場合）
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguageException 指定された言語対はサポートされていない
	 */			
	 public void addTerm(Term[] terms)
		throws AccessLimitExceededException, InvalidParameterException
		, LanguageNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException, UnsupportedLanguageException
	 {
		 try
		 {	 

			 if (terms == null){
				 throw new InvalidParameterException("terms","'Can't Be Blank'");
			 }
			 if (terms.length == 0){
				 throw new InvalidParameterException("terms","'Can't Be Blank'");
			 }
			 
			 String[] language = new String[terms.length] ;
			 String[] text = new String[terms.length] ;
			 int i = 0;
			 

			 for (Term term:terms)
			 {
				 language[i] = term.getLanguage();
				 text[i] = term.getText();
				 i += 1;
			 }
			 dictService.addTerm(dictionary,language,text);	
		 } catch (UnsupportedLanguageException e) {
			 throw e;
		 } catch (InvalidParameterException e) {
			 throw e;
		 } catch (ProcessFailedException e){
		 	 throw e;
		 } catch (Exception e) {
		     throw new ProcessFailedException(e.toString());
		 }
	 }

	 /**
	 * TermEntryのidを使ってデータを削除する。
	 * @param termIds 削除する対訳のID
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguageNotUniquelyDecidedException 対訳言語対候補が一意に求まらない（例：headLangもしくはtargetLangにzhを指定したときに，対応言語対にzh-Hansとzh-Hantが存在した場合）
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguageException 指定された言語対はサポートされていない
	 */
	 public void deleteTerm(int[] termIds)
	 throws AccessLimitExceededException, InvalidParameterException
		, LanguageNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException, UnsupportedLanguageException
	 {
		 try
		 {
			 dictService.deleteTermById(dictionary, termIds);
		 }catch (ProcessFailedException e){
	 		throw e;
		 }
		 catch(Exception e)
		 {
			 throw new ProcessFailedException(e.toString());
		 }		 
	 }

	 /**
	 * 指定されたTermEntryのidと一致するDBのデータを上書きする。
	 * @param entries 対訳情報
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguageNotUniquelyDecidedException 対訳言語対候補が一意に求まらない（例：headLangもしくはtargetLangにzhを指定したときに，対応言語対にzh-Hansとzh-Hantが存在した場合）
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguageException 指定された言語対はサポートされていない
	 */
	 public void setTerm(TermEntry[] entries)
	 throws AccessLimitExceededException, InvalidParameterException
		, LanguageNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException, UnsupportedLanguageException
	 {	
		 try	
		 {
			 int[] id = new int[entries.length];
			 int i = 0;

           if (entries == null)
           {throw new InvalidParameterException("entries","'Can't Be Blank'");}			 
			 
		   if (entries.length == 0)
           {throw new InvalidParameterException("entries","'Can't Be Blank'");}
                      
			 for (TermEntry entry:entries)
			 {
				 id[i] = entry.getId();
				 Term[] terms = entry.getTerms();	
				 String[] language = new String[terms.length];
				 String[] text = new String[terms.length];

				 if (terms == null){
					 throw new InvalidParameterException("entry.terms","'Can't Be Blank'");
				 }
				 if (terms.length == 0){
					 throw new InvalidParameterException("entry.terms","'Can't Be Blank'");
				 }
				 
				 int j = 0;
				 for (Term term:terms)
				 {
					 language[j] =  term.getLanguage();
					 text[j] =  term.getText();
					 j += 1;
				 }
				 dictService.updateTerm(dictionary,id[i],language,text);
				 i +=1;
			 }
		 } catch (UnsupportedLanguageException e) {
			throw e;
		 } catch (InvalidParameterException e) {
			throw e;
		 } catch (ProcessFailedException e){
		 	throw e;
		 } catch (Exception e) {
			throw new ProcessFailedException(e.toString());
		 }
		 		 	 
	 }

	 /**
	 * 辞書の対応言語を追加する．
	 * @param languages 追加する言語
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 */
	 public void addLanguage(String[] languages)
	 throws AccessLimitExceededException, InvalidParameterException
		, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException
	 {
		 try
		 {
			 if (languages == null)
			 {throw new InvalidParameterException("languages","'Can't Be Blank'");} 
			 
			 if (languages.length == 0)
			 {throw new InvalidParameterException("languages","'Can't Be Blank'");} 			 
			 
			 
			 for (int i = 0; i < languages.length; i++)
			 {
				 if (languages[i]== null){
					 throw new InvalidParameterException("language","'Can't Be Blank'");
				 }else{
					 if (languages[i].equals("")){
						 throw new InvalidParameterException("language","'Can't Be Blank'");
					 }
				 }
				 String language = languages[i].toString();
				 dictService.addLanguage(dictionary, language);
			 }
		 } catch (InvalidParameterException e){
			 throw e;
		 } catch (ProcessFailedException e){
			 throw e;
		 } catch (Exception e){	
			 throw new ProcessFailedException(e.toString());
		 }		 	 	 
	 }

	/**
	 * 辞書の対応言語を削除する．
	 * @param languages 削除する言語
	 * @throws AccessLimitExceededException アクセス制限に違反した
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws LanguageNotUniquelyDecidedException 対訳言語対候補が一意に求まらない（例：headLangもしくはtargetLangにzhを指定したときに，対応言語対にzh-Hansとzh-Hantが存在した場合）
	 * @throws NoAccessPermissionException 呼び出しを行ったユーザに実行権限が無い
	 * @throws NoValidEndpointsException 有効なエンドポイントが無い
	 * @throws ProcessFailedException 処理に失敗した
	 * @throws ServerBusyException サーバが混雑していて処理が実行できない
	 * @throws ServiceNotActiveException サービスはアクティブではない
	 * @throws ServiceNotFoundException 指定されたサービスが見つからない
	 * @throws UnsupportedLanguageException 指定された言語対はサポートされていない
	 */
	 public void deleteLanguage(String[] languages)
	 throws AccessLimitExceededException, InvalidParameterException
		, LanguageNotUniquelyDecidedException, NoAccessPermissionException
		, NoValidEndpointsException, ProcessFailedException
		, ServerBusyException, ServiceNotActiveException
		, ServiceNotFoundException, UnsupportedLanguageException
	 {
		 try
		 {
			 if (languages == null)
			 {throw new InvalidParameterException("languages","'Can't Be Blank'");}
			 if (languages.length == 0)
			 {throw new InvalidParameterException("languages","'Can't Be Blank'");}
			 
			 for (int i = 0; i < languages.length; i++)
			 {
				if (languages[i]== null){
					throw new InvalidParameterException("language","'Can't Be Blank'");
				}
				if (languages[i].equals("")){
					throw new InvalidParameterException("language","'Can't Be Blank'");
				}
				String language = languages[i].toString();
				dictService.deleteLanguage(dictionary, language);
			 }
		 
	 	} catch (UnsupportedLanguageException e){
	 		throw e;
	 	} catch (InvalidParameterException e){
	 		throw e;
	 	} catch (ProcessFailedException e){
	 		throw e;
	 	} catch (Exception e){	
	 		throw new ProcessFailedException(e.toString());
	 	}		 	 	 	 	 
	 }		 	 	 	 

		//Add start by t.harato 2008/02/27 (件数取得処理-追加)
	 /**
	  * 辞書ラッパーが対応している検索手法を返す。
	  * @return 対応している検索方法の配列
	  */
	 public String[] getSupportedMatchingMethods()
		throws AccessLimitExceededException, NoAccessPermissionException,
		NoValidEndpointsException, ProcessFailedException,
		ServerBusyException, ServiceNotActiveException,
		ServiceNotFoundException {
		 return supportedMatchingMethods;
	 }
	 /**
	  * 対応する検索方法を設定します．
	  * @param supportedMatchingMethods 対応する検索方法の集合
	  */
	 protected void setSupportedMatchingMethods(Set<MatchingMethod> supportedMatchingMethods){
		 this.matchingMethods = supportedMatchingMethods;
		 this.supportedMatchingMethods = CollectionUtil.collect(
				 matchingMethods, new ToStringTransformer<MatchingMethod>()
		 ).toArray(new String[]{});
	 }
	 
	 
	 public Calendar getLastUpdate()
	 throws AccessLimitExceededException, NoAccessPermissionException
	 , NoValidEndpointsException, ProcessFailedException
	 , ServerBusyException, ServiceNotActiveException
	 , ServiceNotFoundException
	 {
		 try
		 {
			 return dictService.getLastUpdate(dictionary);
		 }
		 catch (Exception e)
		 {
			 throw new ProcessFailedException(e.toString());
		 }
	 }
	 public String[] listLanguage()
	 throws AccessLimitExceededException, NoAccessPermissionException
	 , NoValidEndpointsException, ProcessFailedException
	 , ServerBusyException, ServiceNotActiveException
	 , ServiceNotFoundException
	 {
		try
		{
			return dictService.getSupportedLanguageByDictionary(dictionary);
		}catch(Exception e){
			throw new ProcessFailedException(e.toString());
		}
	 }
	 private String[] supportedMatchingMethods;
	 //Add end by t.harato 2008/02/27 (件数取得処理-追加)
	private Set<MatchingMethod> matchingMethods;
}
