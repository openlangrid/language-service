package jp.go.nict.langrid.wrapper.juman;

import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.wrapper.juman.Juman;
import jp.go.nict.langrid.wrapper.ws_1_2.AbstractService;
import junit.framework.TestCase;

public class JumanTest extends TestCase {
	{
		AbstractService.setCurrentServiceContext(new LocalServiceContext());
		System.setProperty("jumanPath", "C:\\Program Files\\juman\\juman");
		System.setProperty("jumanEncoding", "Windows-31J");
		test = new Juman();
	}

	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「文字列」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze1a() throws Exception
	{
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[0].getWord(), "本日");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[1].getWord(), "は");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[2].getWord(), "快晴");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[3].getWord(), "です");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[4].getWord(), "．");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[5].getWord(), "改行");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[6].getWord(), "し");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[7].getWord(), "ました");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[8].getWord(), "．");
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
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[3].getLemma(), "だ");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[4].getLemma(), "．");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[5].getLemma(), "改行");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[6].getLemma(), "する");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[7].getLemma(), "ます");
		assertEquals(this.test.analyze("ja","本日は快晴です．\n改行しました．")[8].getLemma(), "．");
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
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[5].getLemma(), "だ");
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
		assertEquals(this.test.analyze("ja","本日は快晴です．")[0].getPartOfSpeech(), "noun.common"); // 本日 -> 名詞：時相名詞
		assertEquals(this.test.analyze("ja","本日は快晴です．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","本日は快晴です．")[2].getPartOfSpeech(), "noun.common"); // 快晴 -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","本日は快晴です．")[3].getPartOfSpeech(), "other"); // です -> 判定詞
		assertEquals(this.test.analyze("ja","本日は快晴です．")[4].getPartOfSpeech(), "other"); // ． -> 特殊
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3b() throws Exception
	{
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[0].getPartOfSpeech(), "noun.common"); // 私 -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[2].getPartOfSpeech(), "unknown"); // アイアーン -> 未定義語
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[3].getPartOfSpeech(), "other"); // の -> 助詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[4].getPartOfSpeech(), "noun.common"); // メンバー -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[5].getPartOfSpeech(), "other"); // です -> 判定詞
		assertEquals(this.test.analyze("ja","私はアイアーンのメンバーです．")[6].getPartOfSpeech(), "other"); // ． -> 特殊
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
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[2].getPartOfSpeech(), "noun.common"); // 友人 -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[3].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[4].getPartOfSpeech(), "noun.proper"); // 高橋 -> 名詞：人名
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[5].getPartOfSpeech(), "other"); // と -> 助詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[6].getPartOfSpeech(), "verb"); // いう -> 動詞
		assertEquals(this.test.analyze("ja","とても賢い友人は高橋という．")[7].getPartOfSpeech(), "other"); // ． -> 特殊
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3d() throws Exception
	{
		assertEquals(this.test.analyze("ja","日本はこうなった．")[0].getPartOfSpeech(), "noun.proper"); // 日本 -> 名詞：地名
		assertEquals(this.test.analyze("ja","日本はこうなった．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[2].getPartOfSpeech(), "adverb"); // こう -> 指示詞：副詞形態指示詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[3].getPartOfSpeech(), "verb"); // なった -> 動詞
		assertEquals(this.test.analyze("ja","日本はこうなった．")[4].getPartOfSpeech(), "other"); // ． -> 特殊
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3e() throws Exception
	{
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[0].getPartOfSpeech(), "noun.other"); // それ -> 指示詞：名詞形態指示詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[1].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[2].getPartOfSpeech(), "other"); // この -> 指示詞：連体詞形態指示詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[3].getPartOfSpeech(), "noun.common"); // 部品 -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[4].getPartOfSpeech(), "other"); // の -> 助詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[5].getPartOfSpeech(), "noun.common"); // 一部 -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[6].getPartOfSpeech(), "other"); // だ -> 判定詞
		assertEquals(this.test.analyze("ja","それはこの部品の一部だ．")[7].getPartOfSpeech(), "other"); // ． -> 特殊
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3f() throws Exception
	{
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[0].getPartOfSpeech(), "noun.other"); // 二千 -> 名詞：数詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[1].getPartOfSpeech(), "noun.other"); // 年 -> 接尾辞：名詞性名詞助数辞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[2].getPartOfSpeech(), "noun.other"); // 前 -> 接尾辞：名詞性名詞接尾辞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[3].getPartOfSpeech(), "other"); // から -> 助詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[4].getPartOfSpeech(), "verb"); // あった -> 動詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[5].getPartOfSpeech(), "noun.other"); // こと -> 名詞：形式名詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[6].getPartOfSpeech(), "other"); // を -> 助詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[7].getPartOfSpeech(), "verb"); // 知る -> 動詞
		assertEquals(this.test.analyze("ja","二千年前からあったことを知る．")[8].getPartOfSpeech(), "other"); // ． -> 特殊
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3g() throws Exception
	{
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[0].getPartOfSpeech(), "noun.proper"); // 鈴木 -> 名詞：人名
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[1].getPartOfSpeech(), "noun.other"); // さん -> 接尾辞：名詞性名詞接尾辞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[2].getPartOfSpeech(), "other"); // は -> 助詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[3].getPartOfSpeech(), "noun.common"); // 協力 -> 名詞：サ変名詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[4].getPartOfSpeech(), "adjective"); // 的な -> 接尾辞：形容詞性名詞接尾辞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[5].getPartOfSpeech(), "noun.common"); // 人 -> 名詞：普通名詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[6].getPartOfSpeech(), "other"); // だ -> 判定詞
		assertEquals(this.test.analyze("ja","鈴木さんは協力的な人だ．")[7].getPartOfSpeech(), "other"); // ． -> 特殊
	}
	
	/**
	 * 正しい入力に対して，意図した結果を返すか確認します．
	 * （各形態素の「品詞」が正しいかどうか）
	 * @throws Exception 想定していない例外が発生した
	 */	
	public void testDoAnalyze3h() throws Exception
	{
		assertEquals(this.test.analyze("ja","自由を宣言する．")[0].getPartOfSpeech(), "adjective"); // 自由 -> 形容詞
		assertEquals(this.test.analyze("ja","自由を宣言する．")[1].getPartOfSpeech(), "other"); // を -> 助詞
		assertEquals(this.test.analyze("ja","自由を宣言する．")[2].getPartOfSpeech(), "noun.common"); // 宣言 -> 名詞：サ変名詞
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

	private Juman test;
}
