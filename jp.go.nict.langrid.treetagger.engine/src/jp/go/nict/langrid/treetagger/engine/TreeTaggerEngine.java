/*
 * $Id: TreeTaggerEngine.java 26557 2008-09-19 09:59:48Z nakaguchi $
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
 * TreeTaggerのインタフェース。
 * @author $Author: nakaguchi $
 * @version $Revision: 26557 $
 */
public interface TreeTaggerEngine {
	/**
	 * 解析を行う。
	 * @param sentence 解析する文章
	 * @return 解析結果
	 * @throws ProcessFailedException 処理に失敗した
	 */
	Tag[] tag(String sentence)
	throws ProcessFailedException;
}
