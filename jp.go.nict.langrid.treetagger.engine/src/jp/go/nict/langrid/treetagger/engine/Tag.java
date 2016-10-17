/*
 * $Id: Tag.java 26402 2008-08-28 08:11:01Z nakaguchi $
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
package jp.go.nict.langrid.treetagger.engine;

/**
 * TreeTaggerが返す情報を格納するクラス。
 * @author $Author: nakaguchi $
 * @version $Revision: 26402 $
 */
public class Tag{
	/**
	 * コンストラクタ。
	 * @param word 元の語句
	 * @param tag TreeTaggerにより付加されるタグ
	 * @param lemma 語句の原型
	 */
	public Tag(String word, String tag, String lemma){
		this.word = word;
		this.tag = tag;
		this.lemma = lemma;
	}

	/**
	 * コンストラクタ。
	 */
	public Tag(){
	}

	/**
	 * このオブジェクトの文字列表現を返す。
	 * @return 文字列表現
	 */
	@Override
	public String toString(){
		StringBuilder b = new StringBuilder();
		b.append(word)
			.append("/")
			.append(tag)
			.append("/")
			.append(lemma)
			;
		return b.toString();
	}

	/**
	 * 語。入力と同じ。
	 */
	public String word;

	/**
	 * TreeTaggerによって付けられたタグ。
	 */
	public String tag;

	/**
	 * 語の原型。存在しない場合"<unknown>"。
	 */
	public String lemma;
}
