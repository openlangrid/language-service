/*
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

import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.TestCase;

public class ICTCLASTest extends TestCase {
	
	/*public void testAnalyze() throws LanguageNotUniquelyDecidedException,
	UnsupportedLanguageException, AnalysisFailedException, InvalidParameterException,
	ServiceConfigurationException, UnknownException {
		test.analyze("zh-Hans", "他去那里");
		test.analyze("zh-Hans", "你好 ! 我 是 三洋 电机 的 铃木 , 请问 刘 先生 在 吗 ?");
	}*/
	
	/**
	 * テスト用オブジェクト
	 */
	private ICTCLAS test = new ICTCLAS();

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「文字列」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testAnalyze_Normal1_zh_Hans() throws Exception{
		Morpheme[] morphs = test.analyze("zh-Hans", "他去那里");
		assertEquals("他", morphs[0].getWord());
		assertEquals("去", morphs[1].getWord());
		assertEquals("那里", morphs[2].getWord());		
	}

	//FIXME lemmaをサポートしていない場合は現在文字列を返している。
	//例外を返すべきか？
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「原型」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testAnalyze_Normal2_zh_Hans() throws Exception{
		Morpheme[] morphs = test.analyze("zh-Hans", "他去那里");
		assertEquals("", morphs[0].getLemma());
		assertEquals("", morphs[1].getLemma());
		assertEquals("", morphs[2].getLemma());
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testAnalyze_Normal3_zh_Hans() throws Exception{
		Morpheme[] morphs = test.analyze("zh-Hans", "他去那里");
		assertEquals("noun.other", morphs[0].getPartOfSpeech());
		assertEquals("verb", morphs[1].getPartOfSpeech());
		assertEquals("noun.other", morphs[2].getPartOfSpeech());
		
		Morpheme[] morphs2 = test.analyze("zh-Hans",
				"你好 ! 我 是 三洋 电机 的 铃木 , 请问 刘 先生 在 吗 ?");
		assertEquals("other", morphs2[0].getPartOfSpeech()); //你好がotherか
		assertEquals("noun.proper", morphs2[4].getPartOfSpeech()); // 三洋がnoun.properか
		assertEquals("noun.common", morphs2[5].getPartOfSpeech()); // 电机がnoun.commonか
		assertEquals("other", morphs2[6].getPartOfSpeech()); //的がotherか
		
		Morpheme[] morphs3 = test.analyze("zh-Hans", "你太早点儿来");
		assertEquals("adverb", morphs3[1].getPartOfSpeech()); //太がadverbか
		assertEquals("adjective", morphs3[2].getPartOfSpeech()); //早がadjectiveか
		
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （返ってくる形態素の個数が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testAnalyze_Normal4_zh_Hans() throws Exception{
		assertEquals(test.analyze("zh-Hans", "他去那里").length, 3);
	}
	
	/**
	 * テキストの入力ミスに対して，エラーを出すかどうか確認します．
	 * （テキストに空文字列が入力された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testAnalyze_Abnormal1() throws Exception{
		try{
			this.test.analyze("zh-Hans", "");
			fail();
		} catch(InvalidParameterException e){
			assertTrue(true);
		}
	}

	/**
	 * テキストの入力ミスに対して，エラーを出すかどうか確認します．
	 * （テキストにnullが入力された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testAnalyze_Abnormal2() throws Exception{
		try{
			test.analyze("zh-Hans", null);
			fail();
		} catch(InvalidParameterException e){
			assertTrue(true);
		}
	}

	/**
	 * 言語コードの指定ミスに対して，エラーを出すかどうか確認します．
	 * （未定義の言語コードが利用された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testAnalyze_Abnormal3() throws Exception{
		try{
			test.analyze("jj", "他去那里");
			fail();
		} catch(InvalidParameterException e){
			assertTrue(true);
		}
	}

	/**
	 * 言語コードの指定ミスに対して，エラーを出すかどうか確認します．
	 * （言語コードに空文字列が利用された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testAnalyze_Abnormal4() throws Exception{
		try{
			test.analyze("", "他去那里");
			fail();
		} catch(InvalidParameterException e){
			assertTrue(true);
		}
	}

	/**
	 * 言語コードの指定ミスに対して，エラーを出すかどうか確認します．
	 * （言語コードにnullが利用された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testAnalyze_Abnormal5() throws Exception{
		try	{
			this.test.analyze(null, "他去那里");
			fail();
		} catch(InvalidParameterException e){
			assertTrue(true);
		}
	}

	/**
	 * 対応している言語以外の言語コードを指定した場合，エラーを出すかどうか確認します．
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testAnalyze_Abnormal6() throws Exception{
		try{			
			this.test.analyze("ee", "他去那里");
			fail();
		} catch(UnsupportedLanguageException e){
			assertTrue(true);
		}
	}

	/**
	 * 略記した言語コードに対応する言語が複数あった場合，エラーを出すかどうか確認します．
	 * 注：zh-Hansとzh-Hantを同時にサポートする場合などは，
	 * 　　このメソッドのコメントアウトを解除してください．
	 * @throws Exception 想定していない例外が発生した
	 */
	/*public void testDoAnalyze11() throws Exception
	{
		try
		{
			this.test.analyze("zh", "I have a pen.");
			assertTrue(false);
		}
		catch(LanguageNotUniquelyDecidedException e)
		{
			assertTrue(true);
		}
	}*/

	/**
	 * 言語コードの略記形が，正しく使えるかどうか確認します．
	 * 注：zh-Hansなどをサポートする場合は，
	 * 　　このメソッドのコメントアウトを解除してください．
	 * @throws Exception 想定していない例外が発生した
	 */
	/*public void testDoAnalyze12() throws Exception
	{
		assertEquals(this.test.analyze("zh","")[0].getWord(), "");
	}*/
}
