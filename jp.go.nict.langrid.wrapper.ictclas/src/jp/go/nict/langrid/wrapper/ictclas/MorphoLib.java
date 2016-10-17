/*
 * $Id: MorphoLib.java 26987 2009-02-20 05:55:23Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.ictclas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.ContentTracer;
import jp.go.nict.langrid.wrapper.common.ContentUtil;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_Action;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_Content;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_Resource;
import jp.go.nict.langrid.wrapper.common.iniFileUtil.morphologicalanalysis.Webapp_ServiceDescription;
 
/**
 * 形態素解析のラッパーのメイン処理部分を実装．
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26987 $
 */
public class MorphoLib {

	/**
	 * Webアプリケーションで提供される形態素解析のanalyzeの実装
	 * 与えられた要求と，iniファイルを表すオブジェクトから解析，切り出しを実行．
	 * @param language 解析対象の言語
	 * @param text 解析対象の文字列
	 * @param sd iniファイルに対応するオブジェクト
	 * @return 解析結果
	 * @throws ProcessFailedException
	 */
	protected static Collection<Morpheme> doAnalyzeImpl_WebApp(
			Language language, String text, Webapp_ServiceDescription sd)
			throws ProcessFailedException {
		//返値を格納する変数
		Collection<Morpheme> rtn = new ArrayList<Morpheme>();
		
		//ServiceDescriptionから、各種変数を取得
		Webapp_Resource pair = sd.getResources(language);
		Webapp_Action action = pair.getAction();
		Webapp_Content content = pair.getContent();
		String url = action.getUrl();
		String method = action.getMethod().toUpperCase();
		String sourceCharset = content.getSourceCharSet();
		String targetCharset = content.getTargetCharSet();
		
		//Webアプリケーションに投げるクエリを作成
		String query;
		
		try {
			query = action.getQuery().replaceAll("\\$\\{source\\}", URLEncoder.encode(text,sourceCharset));
		} catch (UnsupportedEncodingException e) {
			throw new ProcessFailedException("Invalid source charset "
					+ sourceCharset);
		}
		
		//クエリを用いて、cgiから結果コンテンツを取得
		String result = ContentUtil.getCGI(url, method, query, targetCharset);
		
		//コンテンツから、形態素が順番に全て文字列で格納されたコレクションを得る		
		Iterator itr = ContentTracer.trimRegexMorphological(
				result,content.getScraper().getRegexes(),content.getScraper().getIgnores()).iterator();

		while(itr.hasNext()){
			String[] aPart = (String[])itr.next();
						
			rtn.add(new Morpheme(aPart[0],aPart[1],strToPartOfSpeech(aPart[2],pair.getPartOfSpeech())));
			
		}
		
		return rtn;
	}
	
	/**
	 * 実行形式ファイルで提供される形態素解析のanalyzeの実装
	 * 与えられた要求と，iniファイルを表すオブジェクトから解析，切り出しを実行．
	 * 暫定的に不恰好な呼び出しになっているので，
	 * 実行ファイルの形態素解析のiniの仕様を決定し次第修正．
	 * @param command 実行ファイル実行用のコマンド
	 * @param language 解析対象の言語
	 * @param text 解析対象の文字列
	 * @param sd iniファイルに対応するオブジェクト
	 * @return 解析結果
	 * @throws ProcessFailedException
	 */
	public static Collection<Morpheme> doAnalyzeImpl_executable(
			String command, Language language, String text, Webapp_ServiceDescription sd)
	throws ProcessFailedException {
		//返値を格納する変数
		Collection<Morpheme> rtn = new ArrayList<Morpheme>();
		
		//ServiceDescriptionから、各種変数を取得
		Webapp_Resource pair = sd.getResources(language);
		Webapp_Content content = pair.getContent();
		
		//実行ファイルを実行
		String result = ">>TODO implement doAnalyzeImpl_executable" +
				"\n commend = "+command;

		try {
			ProcessBuilder pb = new ProcessBuilder(command, text);
			Process p = pb.start();

			InputStream is = p.getInputStream(); // まずHAMがstderrに吐いたメッセージを拾う
			BufferedReader brerr = new BufferedReader(new InputStreamReader(is));
			while (brerr.readLine() != null) {
				System.out.println(result);
			}
			p.destroy();

		} catch (IOException e) {
			throw new ProcessFailedException(e.toString());
		}
		
		System.out.println(result);
		
		//コンテンツから、形態素が順番に全て文字列で格納されたコレクションを得る		
		Iterator itr = ContentTracer.trimRegexMorphological(
				result,content.getScraper().getRegexes(),content.getScraper().getIgnores()).iterator();
			
		while(itr.hasNext()){
			String[] aPart = (String[])itr.next();
						
			rtn.add(new Morpheme(aPart[0],aPart[1],strToPartOfSpeech(aPart[2],pair.getPartOfSpeech())));
			
		}
		
		printMorList(rtn);
		
		return rtn;
	}

	
	public static void printMorList(Collection<Morpheme> list){
		Iterator itr = list.iterator();
		Morpheme aMor;
		System.out.println(">>printMorList");
		while(itr.hasNext()){
			aMor = (Morpheme)itr.next();
			System.out.println("w:"+aMor.getWord()+"\tl:"+aMor.getLemma()+"\tp:"+aMor.getPartOfSpeech().getExpression());
		}
	}

	public static PartOfSpeech strToPartOfSpeech(String string, Webapp_PartOfSpeech wp) 
		throws ProcessFailedException{
		
		//stringがnoun.commonに相当するかのチェック
		for(int i=0;i<wp.getNoun_common().length;i++){
			if(string.matches(wp.getNoun_common()[i]))
				return PartOfSpeech.noun_common;
		}
		
		//stringがnoun.properに相当するかのチェック
		for(int i=0;i<wp.getNoun_proper().length;i++){
			if(string.matches(wp.getNoun_proper()[i]))
				return PartOfSpeech.noun_proper;
		}
		
		//stringがnoun.otherに相当するかのチェック
		for(int i=0;i<wp.getNoun_other().length;i++){
			if(string.matches(wp.getNoun_other()[i]))
				return PartOfSpeech.noun_other;
		}

		//stringがnounに相当するかのチェック
		for(int i=0;i<wp.getNoun().length;i++){
			if(string.matches(wp.getNoun()[i]))
				return PartOfSpeech.noun;
		}
		
		//stringがverbに相当するかのチェック
		for(int i=0;i<wp.getVerb().length;i++){
			if(string.matches(wp.getVerb()[i]))
				return PartOfSpeech.verb;
		}
		
		//stringがadjectiveに相当するかのチェック
		for(int i=0;i<wp.getAdjective().length;i++){
			if(string.matches(wp.getAdjective()[i]))
				return PartOfSpeech.adjective;
		}
		
		//stringがadverbに相当するかのチェック
		for(int i=0;i<wp.getAdverb().length;i++){
			if(string.matches(wp.getAdverb()[i]))
				return PartOfSpeech.adverb;
		}
		
		//stringがotherに相当するかのチェック
		for(int i=0;i<wp.getOther().length;i++){
			if(string.matches(wp.getOther()[i]))
				return PartOfSpeech.other;
		}
		
		//stringがunknownに相当するかのチェック
		for(int i=0;i<wp.getUnknown().length;i++){
			if(string.matches(wp.getUnknown()[i]))
				return PartOfSpeech.unknown;
		}
		
		//partOfSpeechが特定出来なかった場合、例外を投げる
		throw new ProcessFailedException(string+" can't be identified.");
	}
	
	//テスト記述用の、ツール的関数
	public static void datToTestCode(String datname, Webapp_PartOfSpeech wp) 
	throws ProcessFailedException{
		String content="";
		try {
			content = ContentUtil.getLocal(datname);
		} catch (Exception e) {
			throw new ProcessFailedException(e.toString());
		}
		
		//資源ごとの処理
		String[] contentArr = content.split("\n");
		ArrayList<String[]> result = new ArrayList<String[]>();
		HashSet<String> set = new HashSet<String>();
		
		
		int i=0;
		while(true){
			if(i>contentArr.length-2)
				break;
				
			String[] buff = new String[3];
			buff[0]=contentArr[i++];
			buff[1]=contentArr[i++];
			buff[2]=strToPartOfSpeech(buff[1],wp).toString();
			
			if(!set.contains(buff[1])){
				result.add(buff);
				set.add(buff[1]);
			}
		}		
		
		for(int j=0;j<result.size();j++){
			System.out.println("\t// "+result.get(j)[1]+" が "+result.get(j)[2]+" と出力されているかのテスト");
			System.out.println(
					"\tassertEquals(this.test.analyze(\"hi\",\""+result.get(j)[0]+"\")"
					+"[0].getPartOfSpeech(), \""+result.get(j)[2]+"\");");
		}
		
		System.out.println("----------------------------");
	}
}
