/*
 * $Id: HTML_DataUpdater.java 25906 2008-07-15 04:46:14Z murakami $
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
package jp.go.nict.langrid.wrapper.common.cache;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;

import jp.go.nict.langrid.repository.Storage;
import jp.go.nict.langrid.repository.StorageUtil;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServiceConfigurationException;
import jp.go.nict.langrid.wrapper.common.ContentTracer;
import jp.go.nict.langrid.wrapper.common.ContentUtil;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.MetaDataFile;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.paralleltext.HTML_ServiceDescription;
import jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.BilingualDictionaryServicePartIntegrater;
import jp.go.nict.langrid.wrapper.ws_1_2.paralleltext.ParallelTextServicePartIntegrater;

/**
 * 
 * HTMLで提供されるサービスの対訳データを、ローカルファイルに保存する。
 * 
 * SubserviceIntegraterを呼び出し、iniファイルで指定する切り出し方法に基づいて対訳ペアを切り出す。
 * 結果がCSV形式の文字列で返されるので、それを元に.datを生成する。
 *   
 * @author $Author: murakami $
 * @version $Revision: 25906 $
 */
public class HTML_DataUpdater {
	
	//rdf内,日付のYYYYとMMとDDの間の区切り文字
	private static String separator = "-";
	//キャッシュと異なる、正当性がチェックされていないデータの頭文字列
	private static String unchecked = "Unchecked";
	//キャッシュと異なる、正当性がチェックされたデータの頭文字列
	private static String checked = "Checked";
	
	/**
	 * 実際の.dat、.rdfの書き出し処理
	 * /work/packageName/fileName_YYYYMMDD_xx.dat　などのファイルを作成
	 * @param datStr datファイルの内容
	 * @param rdfStr rdfファイルの内容
	 * @param s dat,rdfファイルの存在するストレージ
	 * @param fileName dat,rdfファイルの基本ファイル名
	 * @return 最新のdatの内容が更新された場合1，その他の場合は0
	 * @throws ProcessFailedException 
	 * 		サービスの構築に失敗した（ページが存在しない、など）
	 * @throws IOException 
	 * 		ファイルの操作に関する例外
	 */
	public static int update(String datStr,String rdfStr,Storage s,String fileName)
			throws ProcessFailedException, IOException {
		
		//Checked.dat/rdfが存在する場合、
		//まずそれを最新のキャッシュに追加する
		if(s.entityExists(checked+".dat")&&s.entityExists(checked+".rdf")){
			
			String checkedDat="",checkedRdf="";
			
			try{
			//当該ファイルが存在した場合、これを読み取り、ファイル自体を削除する
			checkedDat = StorageUtil.loadString(s,checked+".dat",
					Charset.forName("UTF-8").newDecoder());
			checkedRdf = StorageUtil.loadString(s,checked+".rdf",
					Charset.forName("UTF-8").newDecoder());
			s.deleteEntity(checked+".dat");
			s.deleteEntity(checked+".rdf");
			}catch(IOException e){
				//複数プロセスでdeleteEntryとloadString、deleteEntryが競合した.
				//しようとしていた作業を、他のプロセスが行ってくれるので処理を中止．
				return 1;
			}
			
			//当該ファイルの内容を引数として、updateを呼び出す
			//前処理として、ファイルパスを未指定の状態に戻す
			checkedDat = checkedDat.replace(checked+".rdf","{rdfPath}");
			checkedRdf = checkedRdf.replace(checked+".dat","{datPath}");
			
			//Checkedからキャッシュへの追加処理である事を表すフラグを付与し、
			//updateを呼び出し
			update("flag=checked\n"+checkedDat,checkedRdf,s,fileName);
			
		}else{
			//当該ファイルが存在しなかった場合
			//そのまま次の処理に移行する
		}

		/**
		 * 最新のdatに更新の必要が無かった場合の処理
		 */
		if(datStr.startsWith("flag=upToDate\n"))
			return 0;
		
		/**
		 * Checked.dat/rdf が存在しなかった場合の処理
		 */
		String oldRDFStr = "";
		String oldDatStr = "";

		// 最新のrdfから作成日時を取り出し、それに基づき新しいrdf、datとして複製する
		// 同日に複数のファイルが作成されるのに対応するため、_00といった数値を末尾に付与する。
		// 重複を避けるため、rdfのreplacesも参照する
		// 最新のrdfが存在する場合、しない場合で場合分け
		if(s.entityExists(fileName+".rdf")&&s.entityExists(fileName+".dat")){
			oldRDFStr = StorageUtil.loadString(s,fileName+".rdf",
			Charset.forName("UTF-8").newDecoder());
			oldDatStr = StorageUtil.loadString(s,fileName+".dat",
			Charset.forName("UTF-8").newDecoder());
		}else{
			// 最新のrdfが存在しない場合
			// およびdatが欠落している場合
			// 単純に.dat及び.rdfファイルを生成
			
			// 先頭にフラグが存在した場合，除去
			if(datStr.startsWith("flag=checked\n"))
				datStr = datStr.replaceAll("flag=checked\n","");

			// .rdfの内容を生成
			rdfStr = refineMetaDataStr(rdfStr,fileName+".dat","","");
			// .rdfファイルを生成
			StorageUtil.storeString(s,fileName+".rdf",rdfStr,
					Charset.forName("UTF-8").newEncoder());
			// .datの内容を生成
			datStr = refineDataStr(datStr,fileName+".rdf");
			// .datファイルを生成
			StorageUtil.storeString(s,fileName+".dat",datStr,
					Charset.forName("UTF-8").newEncoder());

			return 1;
		}
		
		//datStrとキャッシュを比較しする
		if(oldDatStr.replaceFirst("metadata=\".+?\"","").equals(datStr.replaceFirst("metadata=\".+?\"",""))){
			//最新の取得データがキャッシュと同一だった場合
			//rdfのcreatedみ更新
			oldRDFStr = oldRDFStr.replaceFirst(
					"<dcterms:created>.+?</dcterms:created>",
					"<dcterms:created>"+makeDateStr()+"</dcterms:created>");
			// .rdfファイルを更新
			StorageUtil.storeString(s,fileName+".rdf",oldRDFStr,
					Charset.forName("UTF-8").newEncoder());
			
			return 0;
		}
		
		//最新の取得データがキャッシュと異なった場合、
		//Unchecked.dat/rdfを作成or更新する
		//ただし、Checkedの分岐からの呼び出しの場合は作成しない
		if(!datStr.startsWith("flag=checked\n")){
			//datファイルを生成
			datStr = datStr.replace("{rdfPath}",unchecked+".rdf");
			StorageUtil.storeString(s,unchecked+".dat",datStr,
					Charset.forName("UTF-8").newEncoder());
			//rdfファイルを生成
			rdfStr = rdfStr.replace("{datPath}",unchecked+".dat");
			StorageUtil.storeString(s,unchecked+".rdf",rdfStr,
					Charset.forName("UTF-8").newEncoder());
			
			return 0;
		}
		
		//Checkedのフラグを消去
		datStr = datStr.replace("flag=checked\n","");
		
		// 現在最新のrdfおよびdatの、更新後のファイル名を取得
		String oldFileName = fileName+"_"+makeFileNameFromRDFString(oldRDFStr);

		
		// 更新後の.rdfファイル(例 Phar_20060702_00.rdf)を生成
		// isReplacedByを設定
		// aboutも設定
		oldRDFStr = oldRDFStr.replaceAll(
				"<dcterms:isReplacedBy>(.*?)</dcterms:isReplacedBy>",
				"<dcterms:isReplacedBy>" + fileName+".rdf"
						+ "</dcterms:isReplacedBy>");
		oldRDFStr = oldRDFStr.replaceAll(
				"<rdf:Description rdf:about=\\\"(.*?)\\\">",
				"<rdf:Description rdf:about=\\\"" + oldFileName+ ".dat" + "\\\">");
		StorageUtil.storeString(s,oldFileName+".rdf",oldRDFStr,
				Charset.forName("UTF-8").newEncoder());

		// 更新後の.datファイルを生成
		// metadataを更新
		oldDatStr = oldDatStr.replaceAll("metadata=\\\"(.*?)\\\"",
				"metadata=\\\"" + oldFileName + ".rdf" + "\\\"");
		StorageUtil.storeString(s,oldFileName+".dat",oldDatStr,
				Charset.forName("UTF-8").newEncoder());
		
		// 最新の.rdfファイルを作成
		// .rdfの内容を生成
		rdfStr = refineMetaDataStr(rdfStr,fileName+".dat",oldFileName+".rdf","");
		StorageUtil.storeString(s,fileName+".rdf",rdfStr,
				Charset.forName("UTF-8").newEncoder());

		//最新の.datファイルを生成
		// .datの内容を生成
		datStr = refineDataStr(datStr,fileName+".rdf");
		StorageUtil.storeString(s,fileName+".dat",datStr,
				Charset.forName("UTF-8").newEncoder());
		
		// もう一段階古いrdfファイルのisReplacedByを更新
		String twoOldRDFAdr = getParamFromRDFString(oldRDFStr, "replaces");
		if (twoOldRDFAdr.length() != 0){
			String twoOldRDFStr = 
				StorageUtil.loadString(s,twoOldRDFAdr,
				Charset.forName("UTF-8").newDecoder());
			
			twoOldRDFStr = twoOldRDFStr.replaceAll(
					"<dcterms:isReplacedBy>(.*?)</dcterms:isReplacedBy>",
					"<dcterms:isReplacedBy>" + oldFileName+ ".rdf"
							+ "</dcterms:isReplacedBy>");

			StorageUtil.storeString(s,twoOldRDFAdr,twoOldRDFStr,
					Charset.forName("UTF-8").newEncoder());
		}

		return 1;
	}

	/**
	 * メタデータ情報を記述するrdfファイルの内容を生成
	 * 全資源で共通
	 * @param mdf メタデータ情報を保持するオブジェクト
	 * @return rdfファイルの内容
	 * @throws IOException メタデータの骨格を現すrdfSkelton.datが見つからなかった
	 */
	private static String makeMetaDataStr(MetaDataFile mdf) throws IOException {
		//メタデータ記述を格納する文字列
		String buff = ContentUtil.getLocal(HTML_DataUpdater.class
				.getResourceAsStream("rdfSkelton.dat"));

		//対訳データの作成者を記述
		buff = buff.replace("{creator}", mdf.getCreator());

		//対訳データを対訳サービスとして提供する組織を記述
		buff = buff.replace("{publisher}", mdf.getPublisher());

		//Web上の元データへの参照を記述
		buff = buff.replace("{source}", mdf.getSource());

		//作成日を記述
		buff = buff.replace("{created}", makeDateStr());

		return buff;

	}
	
	/**
	 * datファイルの欠落パラメータを追加する
	 * @param datStr 元となるdatの文字列
	 * @param rdfFileName rdfファイルの名前
	 * @return パラメータを追加した後のdatの文字列
	 */
	private static String refineDataStr(String datStr,String rdfFileName){
		String buff = datStr.replace("{rdfPath}",rdfFileName);
		return buff;
	}
	
	/**
	 * rdfファイルの欠落パラメータを埋める
	 * @param rdfStr 元となるrdfの文字列
	 * @param datFileName datファイルの名前
	 * @param replaces メタデータ項目 replaces に格納する文字列
	 * @param isReplacedBy メタデータ項目 isReplacedBy に格納する文字列
	 * @return パラメータを埋めた後のrdfの文字列
	 */
	private static String refineMetaDataStr(String rdfStr,String datFileName,String replaces,String isReplacedBy){
		String buff = rdfStr.replace("{datPath}", datFileName);
		buff = buff.replace("{replaces}", replaces);
		buff = buff.replace("{isReplacedBy}", isReplacedBy);
		
		return buff;
	}

	/**
	 * YYYY_MM_DDの形式で現在の日付を表す文字列を生成
	 * （ただし _ はセパレータを表す任意の文字列）
	 * @return YYYY_MM_DDの形式での現在の日付
	 */
	private static String makeDateStr() {
		//YYYYMMDD形式で日付を格納する文字列
		Calendar cal = Calendar.getInstance();
		StringBuffer buff = new StringBuffer();

		buff.append(cal.get(Calendar.YEAR));

		buff.append(separator);

		if (cal.get(Calendar.MONTH) < 9)
			buff.append(0);
		buff.append(cal.get(Calendar.MONTH) + 1);

		buff.append(separator);

		if (cal.get(Calendar.DATE) < 10)
			buff.append(0);
		buff.append(cal.get(Calendar.DATE));

		return new String(buff);
	}

	/**
	 * rdfファイルの生成時には、１つ前のrdfファイルが改名される。
	 * そのために、改名後のファイル名を生成。
	 * @param rdfStr １つ前のrdfファイルの内容
	 * @return 次に生成するrdfファイルの名前
	 */
	private static String makeFileNameFromRDFString(String rdfStr) {
		
		// createdから-を除いたものを取得
		String date = ContentTracer.trimRegex(rdfStr,
				"<dcterms:created>(.+?)</dcterms:created>").get(0)[0];
		date = date.replace("-", "");

		// replacesを取得
		String replaces = ContentTracer.trimRegex(rdfStr,
				"<dcterms:replaces>(.*?)</dcterms:replaces>").get(0)[0];

		if (replaces.matches(".*?" + date + "_[0-9]+.rdf")) {
						
			// 既に同日にデータが作成されていた場合の処理
			int num = new Integer(replaces.split("[_.]")[2]) + 1;
						
			String numStr;
			if (num < 10) {
				numStr = "0" + num;
			} else {
				numStr = String.valueOf(num);
			}

			return date + "_" + numStr;
		} else {
			
			// 同日にデータが作成されていなかった場合の処理
			return date + "_00";
		}
	}

	/**
	 * rdfファイルから　dcterms:要素名　タグで囲まれる要素を取得
	 * @param rdfStr 取得対象のrdfファイルの内容文字列
	 * @param param 巣得したい属性名
	 * @return 要素の文字列
	 */
	private static String getParamFromRDFString(String rdfStr, String param) {
		//paramを取得
		try {
			return ContentTracer.trimRegex(rdfStr,
					"<dcterms:" + param + ">(.*?)</dcterms:" + param + ">")
					.get(0)[0];
		} catch (IndexOutOfBoundsException e) {
			return "";
		}
	}


	/**
	 * 最新のrdfの作成日を調べ、更新の必要が有るかをチェック
	 * @param s rdfの存在するストレージ
	 * @param fileName rdfのファイル名
	 * @return 更新の必要が無ければtrue、有ればfalse
	 * @throws ServiceConfigurationException rdfファイルの取得に失敗
	 */
	private static boolean isRdfUpToDate(Storage s,String fileName) 
	throws ProcessFailedException
	{

		//filename.rdf、filename.datが存在しない場合も、
		//rdfが最新でない場合と同様に扱う
		if(!s.entityExists(fileName+".rdf")||!s.entityExists(fileName+".dat"))
			return false;
		
		String oldRDFStr="";
		try {
			oldRDFStr = StorageUtil.loadString(s,fileName+".rdf",
					Charset.forName("UTF-8").newDecoder());
		} catch (IOException e) {
			throw new ProcessFailedException(e.toString());
		}
		
		//更新頻度を変えたい場合、以下を変更する
		
		//rdfの作成月と、現在の月が同じ場合、更新処理をしない
		//rdfの作成月の取得
		String createdM = getParamFromRDFString(oldRDFStr,"created").split(separator)[1];
		//現在の月の取得
		Calendar cal = Calendar.getInstance();
		Integer nowM = cal.get(Calendar.MONTH)+1;
		//比較
		if(createdM.equals(nowM.toString()))
			return true;
		else
			return false;
	}
	
	/************************************************************
	 *						用例対訳に固有の処理     			*
	 ************************************************************/
	
	/**
	 * メタデータファイルの場所、対訳情報を記述するdatファイルの内容を生成
	 * 用例対訳サービス用
	 * @param ssi 対訳情報を返すオブジェクト
	 * @return datファイルの内容
	 */
	private static String makeDataStr_Parallel(ParallelTextServicePartIntegrater ssi) {
		//対訳データ記述を格納する文字列
		StringBuffer buff = new StringBuffer();
		
		//メタデータに関する部分を仮に記述
		buff.append("metadata=\"{rdfPath}\"\n");

		//本体部分を記述
		//重複するペアを許容する
		//buff.append(ssi.getResultsAsCSV());
		//重複するペアをまとめる
		buff.append(ssi.getResultsAsCSV_single());

		return new String(buff);
	}

	/**
	 * .datや.rdfの書き出しの前処理、具体的には
	 *  .datの内容の生成
	 *  .rdfの内容の生成
	 *  共通の処理の呼び出し
	 * を行う
	 * 用例対訳サービス用
	 * @param sd iniファイルに対応するオブジェクト
	 * @param s 呼び出し元の扱うストレージ
	 * @param fileName 作成するファイルの名前
	 * @return 最新のdatの内容が更新された場合1，その他の場合は0
	 * @throws ProcessFailedException 
	 * @throws IOException 
	 */
	public static int update_Parallel(HTML_ServiceDescription sd,Storage s,String fileName)
	throws ProcessFailedException, IOException
	{
		String datStr="";
		String rdfStr="";
		
		if(!isRdfUpToDate(s,fileName)){
			//rdfファイルの更新が必要だった場合の処理
			//ssiを作成
			ParallelTextServicePartIntegrater ssi = new ParallelTextServicePartIntegrater(sd);
			//.datの内容(.rdfに関する部分以外)を生成
			datStr = makeDataStr_Parallel(ssi);
			//.rdfの内容(.dat、replaces、isReplacedByに関する部分以外)を生成
			rdfStr = makeMetaDataStr(sd.getMetaDataFile());
		}else{
			//rdfファイルに更新が必要なかった場合、updateメソッドにそれを知らせるため
			//datStrにフラグ文字列を格納
			datStr="flag=upToDate\n";
		}
		
		try{			
			//共通処理の呼び出し
			return update(datStr,rdfStr,s,fileName);
		}catch(IOException e){
			throw new ProcessFailedException(e.toString());
		}
		
	}
	
	/************************************************************
	 *						対訳辞書に固有の処理     			*
	 ************************************************************/
	
	/**
	 * メタデータファイルの場所、対訳情報を記述するdatファイルの内容を生成
	 * 用例対訳サービス用
	 * @param ssi 対訳情報を返すオブジェクト
	 * @return datファイルの内容
	 */
	private static String makeDataStr_BilingDict(BilingualDictionaryServicePartIntegrater ssi) {
		//対訳データ記述を格納する文字列
		StringBuffer buff = new StringBuffer();

		//メタデータに関する部分を仮に記述

		buff.append("metadata=\"{rdfPath}\"\n");
		
		//本体部分を記述
		//重複するペアをまとめる
		buff.append(ssi.getResultsAsCSV_single());

		return new String(buff);
	}
	
	
	/**
	 * .datや.rdfの書き出しの前処理、具体的には
	 *  .datの内容の生成
	 *  .rdfの内容の生成
	 *  共通の処理の呼び出し
	 * を行う
	 * 用例対訳サービス用
	 * @param sd iniファイルに対応するオブジェクト
	 * @param s 呼び出し元の扱うストレージ
	 * @param fileName 作成するファイルの名前
	 * @return 最新のdatの内容が更新された場合1，その他の場合は0
	 * @throws ProcessFailedException 
	 * @throws IOException 
	 */
	public static int update_BilingDict(jp.go.nict.langrid.wrapper.common.iniFileUtil.bilingualdictionary.HTML_ServiceDescription sd
			,Storage s,String fileName)
	throws IOException, ProcessFailedException
	{		
		String datStr="";
		String rdfStr="";
		
		if (!isRdfUpToDate(s, fileName)) {
			//rdfファイルの更新が必要だった場合の処理
			// ssiを作成
			BilingualDictionaryServicePartIntegrater ssi = new BilingualDictionaryServicePartIntegrater(sd);
			// .datの内容(.rdfに関する部分以外)を生成
			datStr = makeDataStr_BilingDict(ssi);
			//.rdfの内容(.dat、replaces、isReplacedByに関する部分以外)を生成
			rdfStr = makeMetaDataStr(sd.getMetaDataFile());
		}else{
			//rdfファイルに更新が必要なかった場合、updateメソッドにそれを知らせるため
			//datStrにフラグ文字列を格納
			datStr="flag=upToDate\n";
		}
		
		try{
			//共通処理の呼び出し
			return update(datStr,rdfStr,s,fileName);
		}catch(IOException e){
			throw new ProcessFailedException(e.toString());
		}
		
	}

	/************************************************************
	 *						getter,setter						*
	 ************************************************************/

}
