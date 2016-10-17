/*
 * $Id: ExampleCategory.java 2009/02/23 koyama $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
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

package jp.go.nict.langrid.wrapper.exampletemplate.service;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 穴あき用例カテゴリのID、文言保持クラス<br>
 * 穴あき用例カテゴリのIDと文言を保持する．
 * @author koyama
 * @version $Revision$
 */
public class ExampleCategory implements Serializable {

	/**
	 * serialId
	 */
	private static final long serialVersionUID = -1630615155243720557L;
	
	/**
	 * デフォルトコンストラクタ．
	 */
	public ExampleCategory() {
		;
	}
	/**
	 * コンストラクタ．
	 * @param categoryId　穴あき用例カテゴリID
	 * @param exampleCategory 穴あき用例カテゴリ
	 */
	public ExampleCategory(String categoryId, String exampleCategory) {
		this.categoryId = categoryId;
		this.exampleCategory = exampleCategory;
	}
	/**
	 * 穴あき用例カテゴリID
	 */
	private String categoryId;
	/**
	 * 穴あき用例カテゴリ
	 */
	private String exampleCategory;
	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}

	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * categoryIdを返す。
	 * @return categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * categoryIdを設定する。
	 * @param categoryId categoryId
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * exampleCategoryを返す。
	 * @return exampleCategory
	 */
	public String getExampleCategory() {
		return exampleCategory;
	}

	/**
	 * exampleCategoryを設定する。
	 * @param exampleCategory exampleCategory
	 */
	public void setExampleCategory(String exampleCategory) {
		this.exampleCategory = exampleCategory;
	}

}
