package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.util.ArrayList;

/*
 * コミュニティ翻訳用String関数群
 */
public class StringUtil {

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列結合
	 */
	public static String append(String language, String str1, String str2) {
		String result = "";
		if (separator(language)) {
			result = appendDiscontinuous(str1, str2); // 分かち書きの言語を処理する
		} else {
			result = appendContinuous(str1, str2);
		}

		return result;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列配列結合
	 */
	public static String appendArray(String language, String[] list) {
		String result = "";
		if (separator(language)) {
			for (int i = 0; i < list.length; i++) {
				result = appendDiscontinuous(result, list[i]);
			}
		} else {
			for (int i = 0; i < list.length; i++) {
				result = appendContinuous(result, list[i]);
			}
		}
		return result;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 言語コードにより結合方法を分岐させる
	 */
	private static boolean separator(String lang) {
		String[] separation = new String[] { "en", "de", "fr", "it", "es" };
		for (int i = 0; i < separation.length; i++) {
			if (separation[i].equals(lang)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列配列と文字列配列の結合
	 */
	public static String[] appendArrayArray(String language, String[] str1,
			String[] str2) {
		ArrayList<String> temp = new ArrayList<String>();

		// 要素の数を揃える
		int max = Math.max(str1.length, str2.length);
		String[] temp1 = new String[max];
		String[] temp2 = new String[max];

		for (int i = 0; i < max; i++) {
			if (i < str1.length) {
				temp1[i] = str1[i];
			} else {
				temp1[i] = "";
			}
		}

		for (int i = 0; i < max; i++) {
			if (i < str2.length) {
				temp2[i] = str2[i];
			} else {
				temp2[i] = "";
			}
		}

		if (language.contains("en") || language.contains("fr")
				|| language.contains("it") || language.contains("de")
				|| language.contains("es")) {
			// 分かち書きの言語を処理する
			for (int i = 0; i < max; i++) {
				temp.add(appendDiscontinuous(temp1[i], temp2[i]));
			}
		} else {
			for (int i = 0; i < max; i++) {
				temp.add(appendContinuous(temp1[i], temp2[i]));
			}
		}
		String[] result = new String[temp.size()];
		temp.toArray(result);

		return result;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 分かち書きの言語処理（英語、仏語）
	 * 
	 * 間に半角スペースを挿入する。 名詞複数形の所有格に対応
	 */
	private static String appendDiscontinuous(String str1, String str2) {
		String result = "";
		String before = "";
		String after = "";
		String beforeApostrophe = "";

		if ((!str1.equals("") && !str2.equals(""))) {
			if (str1.length() > 1) {
				beforeApostrophe = str1.substring(str1.length() - 2, str1
						.length());
			}
			before = str1.substring(str1.length() - 1, str1.length());
			after = str2.substring(0, 1);
			if (beforeApostrophe.equals("'s")) {
				result = str1 + ' ' + str2;
			} else {
				if (before.equals("'") || after.equals("'")
						|| after.equals("?") || after.equals("!")
						|| after.equals(".")) {
					result = str1 + str2;
				} else {
					result = str1 + ' ' + str2;
				}
			}
		} else {
			if (str1.equals("") && !str2.equals("")) {
				result = str2;
			} else if (!str1.equals("") && str2.equals("")) {
				result = str1;
			}
		}

		return result;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 分かち書きでない言語処理
	 * 
	 * 直接結合する。
	 */
	private static String appendContinuous(String str1, String str2) {
		String result = "";
		result = str1 + str2;
		return result;
	}

	/**
	 * ++WebService++ 2006/11/07 前後の空白を削除
	 */
	public static String spaceTriming(String text) {
		return text.trim();
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 全角を半角に変換
	 * 
	 * 英数記号
	 */
	public static String full2HalfSize(String text) {
		String result = "";
		String hankaku = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String zenkaku = "０１２３４５６７８９ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ";
		char ch;
		for (int j = 0; j < text.length(); j++) {
			ch = text.charAt(j);
			for (int i = 0; i < zenkaku.length(); i++) {
				if (ch == zenkaku.charAt(i)) {
					ch = hankaku.charAt(i);
				}
			}
			result = result + ch;
		}

		// 半角ピリオドを二重にする
		result = result.replace(".", " ..");
		return result;
	}

	/**
	 * 2006.11.07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列ソート
	 */
	public static String[] stringSort(String sourceLang, String[] sourceList,
			String order) {
		String[] result = null;
		if (sourceLang.contains("en") || sourceLang.contains("de")
				|| sourceLang.contains("fr")) {
			result = StringUtil.wordSort(sourceList, order);
		} else {
			result = StringUtil.lengthSort(sourceList, order);
		}
		return result;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列語数基準ソート
	 * 
	 * @order asc=昇順 ,desc=降順
	 * 
	 */
	public static String[] wordSort(String[] list, String order) {
		String swap = "";
		int sortorder = 0;
		if (order.equals("desc")) {
			sortorder = 1;
		} else if (order.equals("asc")) {
			sortorder = 2;
		}

		for (int i = 0; i < list.length - 1; i++) {
			for (int n = i + 1; n < list.length; n++) {
				if (sortorder == 1) {
					if (wordcount(list[i]) < wordcount(list[n])) {// 要素の比較
						swap = list[i];// 要素の交換
						list[i] = list[n];
						list[n] = swap;
					}
				} else if (sortorder == 2) {
					if (wordcount(list[i]) > wordcount(list[n])) {// 要素の比較
						swap = list[i];// 要素の交換
						list[i] = list[n];
						list[n] = swap;
					}
				}
			}
		}
		return list;
	}

	/**
	 * 2006/10/13
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 分かち書き（英語など）の語数を返す．
	 */
	private static int wordcount(String source) {
		int num = source.split(" ").length;
		return num;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列長基準ソート
	 * 
	 * @order asc=昇順 ,desc=降順
	 */
	public static String[] lengthSort(String[] list, String order) {
		String swap = "";
		int sortorder = 0;
		if (order.equals("desc")) {
			sortorder = 1;
		} else if (order.equals("asc")) {
			sortorder = 2;
		}

		for (int i = 0; i < list.length - 1; i++) {
			for (int n = i + 1; n < list.length; n++) {
				if (sortorder == 1) {
					if (list[i].length() < list[n].length()) {// 要素の比較
						swap = list[i];// 要素の交換
						list[i] = list[n];
						list[n] = swap;
					}
				} else if (sortorder == 2) {
					if (list[i].length() > list[n].length()) {// 要素の比較
						swap = list[i];// 要素の交換
						list[i] = list[n];
						list[n] = swap;
					}
				}
			}
		}
		return list;
	}

	// 配列の重複を削除する
	public static String[] stripDup(String[] text) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < text.length; i++) {
			list.add(text[i]);
		}
		for (int i = 0; i < list.size(); i++) {
			String temp = list.get(i);
			for (int n = i + 1; n < list.size(); n++) {
				if (temp.equals(list.get(n))) {
					list.remove(n);
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public static int[] stripDup(int[] data) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < data.length; i++) {
			list.add(data[i]);
		}
		for (int i = 0; i < list.size(); i++) {
			int temp = list.get(i);
			for (int n = i + 1; n < list.size(); n++) {
				if (temp == list.get(n)) {
					list.remove(n);
				}
			}
		}

		return intArray2Int(list);
	}

	/**
	 * ArrayList<Integer>をint[]に変換する
	 */
	public static int[] intArray2Int(ArrayList<Integer> data) {
		int[] array = new int[data.size()];
		for (int i = 0; i < data.size(); i++) {
			array[i] = data.get(i);
		}
		return array;
	}

	/**
	 * String配列中のNULLを""にする．
	 */
	public static String[] Null2Empty(String[] list) {
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			if (list[i] == null) {
				temp.add("");
			} else {
				temp.add(list[i]);
			}
		}
		return temp.toArray(new String[temp.size()]);
	}

	public static int getMax(int[] list) {
		int max = 0;
		for (int i = 0; i < list.length; i++) {
			max = Math.max(max, list[i]);
		}
		return max;
	}

	/**
	 * 2006/07/27
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * 文字列整形：正規表現用
	 * 
	 */
	public static String trimRegexString(String source) {
		String trimTerm = "";
		trimTerm = source.replace("(", "\\(");
		trimTerm = trimTerm.replace(")", "\\)");
		trimTerm = trimTerm.replace("[", "\\[");
		trimTerm = trimTerm.replace("]", "\\]");
		trimTerm = trimTerm.replace("'", "\\'");
		trimTerm = trimTerm.replace("'", "\\'");
		trimTerm = trimTerm.replace("\\", "\\\\");
		trimTerm = trimTerm.replace("?", "\\?");
		return trimTerm;
	}

	/**
	 * 2006/11/07
	 * 
	 * @author Tomohiro Shigenobu
	 * 
	 * SQL用に文字列を整形する
	 * 
	 * @param term
	 * @return
	 */
	public static String conversionSQL(String str) {
		str = str.replace("'", "\\'");
		str = str.replace("(", "\\(");
		str = str.replace(")", "\\)");
		str = str.replace("[", "\\[");
		str = str.replace("]", "\\]");
		str = str.replace(".", "\\.");
		str = str.replace(":", "\\:");
		str = str.replace("-", "\\-");
		return str;
	}
}
