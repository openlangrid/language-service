/*
 * $Id: ICTCLAS.java 28009 2010-08-09 10:25:33Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.julius.modelconfig;

import java.io.Serializable;


public interface ModelConfig extends Serializable {
	/**
	 * 音声モデルのIDを返します。
	 * @return
	 */
	String getName();

	/**
	 * 音声モデルの物理ファイルへのパスを返します
	 * @return 
	 */
	String getPath();
	
	/**
	 * 音声モデルが対応している言語を返します
	 * @return
	 */
	String getLanguage();

	/**
	 * 音声モデルを記述している文字コードセットを返します
	 * @return
	 */
	String getRecognizedCharset();
}
