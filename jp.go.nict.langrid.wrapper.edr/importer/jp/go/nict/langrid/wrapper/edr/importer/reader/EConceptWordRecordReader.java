/*
 * $Id: EConceptWordRecordReader.java 4109 2008-08-28 08:58:33Z nakaguchi $
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

import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.util.Pair;
import jp.go.nict.langrid.wrapper.edr.entity.EConceptWord;

public class EConceptWordRecordReader
extends RecordReader<EConceptWord>{
	public EConceptWordRecordReader(InputStreamReader streamReader)
	throws IOException
	{
		super(streamReader);
	}

	public EConceptWord doBuildRecord(String[] values, int lineLength)
	throws ParseException
	{
		if(values.length != 20){
			throw new ParseException(
					"invalid element count: " + values.length
					+ "  must be 20.", lineLength
					);
		}

		EConceptWord entity = new EConceptWord();
		entity.setRecordId(values[0]);
		entity.setHeadWord(StringUtil.unquote(values[1]));
		entity.setPartOfSpeech(values[5]);
		entity.setConceptId(values[12]);
		entity.setConceptHeadWordEnglish(StringUtil.unquote(values[13]));
		Pair<String, String> conceptHeadWords = extractReading(values[14]);
		entity.setConceptHeadWordJapanese(conceptHeadWords.getFirst());
		entity.setConceptHeadWordReadingJapanese(conceptHeadWords.getSecond());
		entity.setConceptGlossEnglish(StringUtil.unquote(values[15]));
		entity.setConceptGlossJapanese(StringUtil.unquote(values[16]));
		entity.setManagementInfo(values[19]);
		return entity;
	}

	private Pair<String, String> extractReading(String value){
		String[] words = value.split("\\[|\\]");
		return Pair.create(words[0], ArrayUtil.getWithinBound(words, 1));
	}
}
