/*
 * $Id: AdjacencyPair.java 266 2010-10-03 10:33:59Z t-nakaguchi $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2009 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.common.db.dao.entity;

import java.util.Calendar;

/**
 * 
 * 
 * @author Takao Nakaguchi
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class AdjacencyPair extends CategorizedEntity{
	public static class SecondTurn extends ChildEntity{
		public SecondTurn(long sid, long parentId, String[] secondTurns
				, Calendar createdDateTime, Calendar updatedDateTime){
			super(sid, parentId, secondTurns, createdDateTime, updatedDateTime);
		}

		public SecondTurn(long parentId, String[] secondTurns){
			super(parentId, secondTurns);
		}

		public String[] getSecondTurns() {
			return getValues();
		}

		public void setSecondTurns(String[] secondTurns) {
			setValues(secondTurns);
		}
	}

	/**
	 * 
	 * 
	 */
	public AdjacencyPair(
			long id, String[] firstTurns, SecondTurn[] secondTurns
			, Category[] categories
			, Calendar createdDateTime, Calendar updatedDateTime)
	{
		super(id, firstTurns, categories, createdDateTime, updatedDateTime);
		this.secondTurns = secondTurns;
	}

	/**
	 * 
	 * 
	 */
	public AdjacencyPair(
			String[] firstTurns, SecondTurn[] secondTurns
			, Category[] categories){
		super(firstTurns, categories);
		this.secondTurns = secondTurns;
		this.setCategories(categories);
	}

	public String[] getFirstTurns() {
		return getValues();
	}

	public void setFirstTurns(String[] firstTurns) {
		setValues(firstTurns);
	}

	public SecondTurn[] getSecondTurns() {
		return secondTurns;
	}

	public void setSecondTurns(SecondTurn[] secondTurns) {
		this.secondTurns = secondTurns;
	}

	private SecondTurn[] secondTurns;
}
