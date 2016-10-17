/*
 * $Id: UnsupportedLanguageException.java 26567 2008-09-19 10:36:04Z nakaguchi $
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

import jp.go.nict.langrid.language.Language;

/**
 * サポートしていない言語が指定された場合に発生する例外。
 * @author $Author: nakaguchi $
 * @version $Revision: 26567 $
 */
public class UnsupportedLanguageException extends Exception {
	/**
	 * コンストラクタ。
	 * @param language 指定された言語
	 */
	public UnsupportedLanguageException(Language language){
		super("Unsupported Language: " + language);
		this.language = language;
	}

	public Language getLanguage(){
		return language;
	}

	private Language language;
	private static final long serialVersionUID = -7030703721898712653L;
}
