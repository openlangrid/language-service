/*
 * $Id: EJTranslationRecordReader.java 4109 2008-08-28 08:58:33Z nakaguchi $
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
import java.util.ArrayList;
import java.util.List;

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.wrapper.edr.entity.EJTranslation;

public class EJTranslationRecordReader
extends RecordReader<EJTranslation>{
	public EJTranslationRecordReader(InputStreamReader streamReader)
	throws IOException
	{
		super(streamReader);
	}

	public EJTranslation doBuildRecord(String[] values, int lineLength)
	throws ParseException
	{
		if(values.length != 10){
			throw new ParseException(
					"too few elements.", lineLength
					);
		}

		EJTranslation entity = new EJTranslation();
		int current = 0;
		entity.setRecordId(values[0]);
		current += values[0].length() + 1;
		entity.setHeadWord(StringUtil.unquote(values[1]));
		current += values[1].length() + 1;
		entity.setPartOfSpeech(values[2]);
		current += values[2].length() + 1;
		entity.setConceptId(values[3]);
		current += values[3].length() + 1;
		entity.setConceptHeadWordEnglish(StringUtil.unquote(values[4]));
		current += values[4].length() + 1;
		entity.setConceptHeadWordJapanese(StringUtil.unquote(values[5]));
		current += values[5].length() + 1;
		entity.setConceptGlossEnglish(StringUtil.unquote(values[6]));
		current += values[6].length() + 1;
		entity.setConceptGlossJapanese(StringUtil.unquote(values[7]));
		current += values[7].length() + 1;
		List<EJTranslation.Translation> translation = new ArrayList<EJTranslation.Translation>();
		for(String s : values[8].split("\\/\\/")){
			String[] trans = s.split("\\|");
			if(trans.length <= 1 || trans.length > 3){
				throw new ParseException(
						"invalid translation information format.", current
						);
			}
			translation.add(new EJTranslation.Translation(
					trans[0], StringUtil.unquote(trans[1])
					, ArrayUtil.getWithinBound(trans, 2)
					));
		}
		entity.setTranslation(translation);
		current += values[8].length();

		entity.setManagementInfo(values[9]);
		return entity;
	}
}
