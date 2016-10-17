/*
 * $Id: ParallelTextWithId.java 266 2010-10-03 10:33:59Z t-nakaguchi $
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
public class ParallelTextWithId extends CategorizedEntity {
	public ParallelTextWithId(
			long id, String[] texts, Category[] categories
			, Calendar createdDateTime, Calendar updatedDateTime)
	{
		super(id, texts, categories, createdDateTime, updatedDateTime);
	}

	public ParallelTextWithId(
			Category[] categories, String[] texts)
	{
		super(texts, categories);
	}

	public String[] getTexts(){
		return getValues();
	}
}
