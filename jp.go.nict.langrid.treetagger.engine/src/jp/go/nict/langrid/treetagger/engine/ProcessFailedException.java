/*
 * $Id: Mecab.java 29018 2012-02-08 14:20:01Z Takao Nakaguchi $
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

public class ProcessFailedException extends Exception {
	/**
	 * コンストラクタ。
	 * @param message メッセージ
	 */
	public ProcessFailedException(String message){
		super(message);
	}

	/**
	 * コンストラクタ。
	 * @param cause 元となった例外
	 */
	public ProcessFailedException(Throwable cause){
		super(cause);
	}
}
