/*
 * $Id: JTreeTaggerProcessor.java 27012 2009-02-27 06:31:12Z kamiya $
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
package jp.go.nict.langrid.wrapper.chasen;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.TestCase;

public class ChasenTest extends TestCase {
	public ChasenTest(){
		test = new Chasen();
		test.setChasenPath("C:\\Program Files\\ChaSen\\chasen.exe");
		test.setChasenEncoding("Shift_JIS");
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「文字列」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze1a() throws Exception{
		Morpheme[] ret = test.analyze("ja", "本日は快晴です．\n改行しました．");
		assertEquals("本日", ret[0].getWord());
		assertEquals("は", ret[1].getWord());
		assertEquals("快晴", ret[2].getWord());
		assertEquals("です", ret[3].getWord());
		assertEquals("．", ret[4].getWord());
		assertEquals("改行", ret[5].getWord());
		assertEquals("し", ret[6].getWord());
		assertEquals("まし", ret[7].getWord());
		assertEquals("た", ret[8].getWord());
		assertEquals("．", ret[9].getWord());
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「文字列」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze1b() throws Exception
	{
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[0].getWord(), "私");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[1].getWord(), "は");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[2].getWord(), "アイアーン");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[3].getWord(), "の");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[4].getWord(), "メンバー");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[5].getWord(), "です");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[6].getWord(), "．");
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「文字列」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze1c() throws Exception
	{
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[0].getWord(), "とても");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[1].getWord(), "賢い");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[2].getWord(), "友人");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[3].getWord(), "は");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[4].getWord(), "高橋");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[5].getWord(), "と");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[6].getWord(), "いう");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[7].getWord(), "．");
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「原型」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze2a() throws Exception
	{
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[0].getLemma(), "本日");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[1].getLemma(), "は");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[2].getLemma(), "快晴");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[3].getLemma(), "です");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[4].getLemma(), "．");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[5].getLemma(), "改行");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[6].getLemma(), "する");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[7].getLemma(), "ます");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[8].getLemma(), "た");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[9].getLemma(), "．");
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「原型」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze2b() throws Exception
	{
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[0].getLemma(), "私");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[1].getLemma(), "は");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[2].getLemma(), "アイアーン");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[3].getLemma(), "の");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[4].getLemma(), "メンバー");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[5].getLemma(), "です");
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[6].getLemma(), "．");
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「原型」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze2c() throws Exception
	{
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[0].getLemma(), "とても");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[1].getLemma(), "賢い");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[2].getLemma(), "友人");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[3].getLemma(), "は");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[4].getLemma(), "高橋");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[5].getLemma(), "と");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[6].getLemma(), "いう");
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[7].getLemma(), "．");
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3a() throws Exception
	{
		assertEquals(this.test.analyze("ja","本日は快晴です．")[0].getPartOfSpeech(), "noun.common"); // 本日 -> 名詞-副詞可能
		assertEquals(this.test.analyze("ja","本日は快晴です．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","本日は快晴です．")[2].getPartOfSpeech(), "noun.common"); // 快晴 -> 名詞-一般
		assertEquals(this.test.analyze("ja","本日は快晴です．")[3].getPartOfSpeech(), "other"); // です -> 助動詞
		assertEquals(this.test.analyze("ja","本日は快晴です．")[4].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3b() throws Exception
	{
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[0].getPartOfSpeech(), "noun.other"); // 私 -> 名詞-代名詞-一般
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[2].getPartOfSpeech(), "unknown"); // アイアーン -> 未知語
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[3].getPartOfSpeech(), "other"); // の -> 助詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[4].getPartOfSpeech(), "noun.common"); // メンバー -> 名詞-一般
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[5].getPartOfSpeech(), "other"); // です -> 助動詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[6].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3c() throws Exception
	{
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[0].getPartOfSpeech(), "adverb"); // とても -> 副詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[1].getPartOfSpeech(), "adjective"); // 賢い -> 形容詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[2].getPartOfSpeech(), "noun.common"); // 友人 -> 名詞-一般
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[3].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[4].getPartOfSpeech(), "noun.proper"); // 高橋 -> 名詞-固有名詞-人名-姓
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[5].getPartOfSpeech(), "other"); // と -> 助詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[6].getPartOfSpeech(), "verb"); // いう -> 動詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[7].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3d() throws Exception
	{
		assertEquals(this.test.analyze("ja","日本はこうなった．")[0].getPartOfSpeech(), "noun.proper"); // 日本 -> 名詞-固有名詞-地域-国
		assertEquals(this.test.analyze("ja","日本はこうなった．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[2].getPartOfSpeech(), "adverb"); // こう -> 副詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[3].getPartOfSpeech(), "verb"); // なっ -> 動詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[4].getPartOfSpeech(), "other"); // た -> 助動詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[5].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3e() throws Exception
	{
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[0].getPartOfSpeech(), "noun.other"); // それ -> 名詞-代名詞-一般
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[2].getPartOfSpeech(), "other"); // この -> 連体詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[3].getPartOfSpeech(), "noun.common"); // 部品 -> 名詞-一般
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[4].getPartOfSpeech(), "other"); // の -> 助詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[5].getPartOfSpeech(), "noun.common"); // 一部 -> 名詞-副詞可能
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[6].getPartOfSpeech(), "other"); // だ -> 助動詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[7].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3f() throws Exception
	{
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[0].getPartOfSpeech(), "noun.other"); // 二 -> 名詞-数
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[1].getPartOfSpeech(), "noun.other"); // 千 -> 名詞-数
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[2].getPartOfSpeech(), "noun.other"); // 年 -> 名詞-接尾-助数詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[3].getPartOfSpeech(), "noun.common"); // 前 -> 名詞-副詞可能
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[4].getPartOfSpeech(), "other"); // から -> 助詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[5].getPartOfSpeech(), "verb"); // あっ -> 動詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[6].getPartOfSpeech(), "other"); // た -> 助動詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[7].getPartOfSpeech(), "noun.other"); // こと -> 名詞-非自立-一般
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[8].getPartOfSpeech(), "other"); // を -> 助詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[9].getPartOfSpeech(), "verb"); // 知る -> 動詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[10].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3g() throws Exception
	{
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[0].getPartOfSpeech(), "noun.proper"); // 鈴木 -> 名詞-固有名詞-人名-姓
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[1].getPartOfSpeech(), "noun.other"); // さん -> 名詞-接尾-人名
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[2].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[3].getPartOfSpeech(), "noun.other"); // 協力 -> 名詞-サ変接続
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[4].getPartOfSpeech(), "noun.other"); // 的 -> 名詞-接尾-形容動詞語幹
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[5].getPartOfSpeech(), "other"); // な -> 助動詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[6].getPartOfSpeech(), "noun.common"); // 人 -> 名詞-一般
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[7].getPartOfSpeech(), "other"); // だ -> 助動詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[8].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3h() throws Exception
	{
		assertEquals(this.test.analyze("ja","自由を宣言する．")[0].getPartOfSpeech(), "noun.common"); // 自由 -> 名詞-形容動詞語幹
		assertEquals(this.test.analyze("ja","自由を宣言する．")[1].getPartOfSpeech(), "other"); // を -> 助詞
		assertEquals(this.test.analyze("ja","自由を宣言する．")[2].getPartOfSpeech(), "noun.other"); // 宣言 -> 名詞-サ変接続
		assertEquals(this.test.analyze("ja","自由を宣言する．")[3].getPartOfSpeech(), "verb"); // する -> 動詞
		assertEquals(this.test.analyze("ja","自由を宣言する．")[4].getPartOfSpeech(), "other"); // ． -> 記号
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （返ってくる形態素の個数が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze4() throws Exception
	{
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．").length, 7);
	}

	/**
	 * テキストの入力ミスに対して，エラーを出すかどうか確認します．
	 * （テキストに空文字列が入力された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testDoAnalyze5() throws Exception
	{
		try
		{
			this.test.analyze("ja", "");
			assertTrue(false);
		}
		catch(InvalidParameterException e)
		{
			assertTrue(true);
		}
	}
	
	/**
	 * テキストの入力ミスに対して，エラーを出すかどうか確認します．
	 * （テキストにnullが入力された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testDoAnalyze6() throws Exception
	{
		try
		{
			this.test.analyze("ja", null);
			assertTrue(false);
		}
		catch(InvalidParameterException e)
		{
			assertTrue(true);
		}
	}

	/**
	 * 言語コードの指定ミスに対して，エラーを出すかどうか確認します．
	 * （未定義の言語コードが利用された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testDoAnalyze7() throws Exception
	{
		try
		{
			this.test.analyze("jj", "本日は快晴です．");
			assertTrue(false);
		}
		catch(InvalidParameterException e)
		{
			assertTrue(true);
		}
	}
	
	/**
	 * 言語コードの指定ミスに対して，エラーを出すかどうか確認します．
	 * （言語コードに空文字列が利用された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testDoAnalyze8() throws Exception
	{
		try
		{
			this.test.analyze("", "本日は快晴です．");
			assertTrue(false);
		}
		catch(InvalidParameterException e)
		{
			assertTrue(true);
		}
	}
	
	/**
	 * 言語コードの指定ミスに対して，エラーを出すかどうか確認します．
	 * （言語コードにnullが利用された場合）
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testDoAnalyze9() throws Exception
	{
		try
		{
			this.test.analyze(null, "本日は快晴です．");
			assertTrue(false);
		}
		catch(InvalidParameterException e)
		{
			assertTrue(true);
		}
	}

	/**
	 * 対応している言語以外の言語コードを指定した場合，エラーを出すかどうか確認します．
	 * @throws Exception 想定していない例外が発生した
	 */
	public void testDoAnalyze10() throws Exception
	{
		try
		{
			this.test.analyze("ee", "本日は快晴です．");
			assertTrue(false);
		}
		catch(UnsupportedLanguageException e)
		{
			assertTrue(true);
		}
	}

	public void testDoAnalyze11_sahen() throws Exception{
		Morpheme[] ret = test.analyze("ja", "放射能に汚染された作物。");
		assertEquals("noun.other", ret[2].getPartOfSpeech());
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
			this.test.analyze("zh", "本日は快晴です．");
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

	private Chasen test;
}
