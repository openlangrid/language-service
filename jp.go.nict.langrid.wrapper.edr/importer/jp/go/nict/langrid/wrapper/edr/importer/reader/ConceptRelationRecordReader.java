/*
 * $Id: ConceptRelationRecordReader.java 4109 2008-08-28 08:58:33Z nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.edr.importer.reader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import jp.go.nict.langrid.wrapper.edr.entity.ConceptRelation;

public class ConceptRelationRecordReader
extends RecordReader<ConceptRelation>{
	public ConceptRelationRecordReader(InputStreamReader streamReader)
	throws IOException
	{
		super(streamReader);
	}

	public ConceptRelation doBuildRecord(
			String[] values, int lineLength)
	throws ParseException
	{
		if(values.length != 4){
			throw new ParseException(
					"too few elements.", lineLength
					);
		}

		// 存在しない概念見出しを参照している概念関係の除去
		// "CPC0376538";"36c39d";"DATE="95/6/7"";"3f9856"
		// 下位概念、36c39dが概念見出しに存在しない(日本語単語辞書には存在する)ため除去する。
		if(values[0].equals("CPC0376538")) return null;

		ConceptRelation entity = new ConceptRelation();
		int current = 0;
		entity.setRecordId(values[0]);
		current += values[0].length() + 1;
		entity.setUpperConceptId(values[1]);
		current += values[1].length() + 1;
		entity.setLowerConceptId(values[2]);
		current += values[2].length() + 1;
		entity.setManagementInfo(values[3]);
		current += values[3].length() + 1;
		return entity;
	}
}
