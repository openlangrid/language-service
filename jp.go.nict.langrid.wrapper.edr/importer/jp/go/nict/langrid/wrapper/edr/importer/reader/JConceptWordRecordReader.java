/*
 * $Id: JConceptWordRecordReader.java 4109 2008-08-28 08:58:33Z nakaguchi $
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
import jp.go.nict.langrid.wrapper.edr.entity.JConceptWord;

public class JConceptWordRecordReader
extends RecordReader<JConceptWord>{
	public JConceptWordRecordReader(InputStreamReader streamReader)
	throws IOException
	{
		super(streamReader);
	}

	public JConceptWord doBuildRecord(String[] values, int lineLength)
	throws ParseException
	{
		if(values.length != 19){
			throw new ParseException(
					"invalid element count: " + values.length
					+ "  must be 19.", lineLength
					);
		}

		JConceptWord entity = new JConceptWord();
		entity.setRecordId(values[0]);
		Pair<String, String> pair = extractReading(values[1]);
		entity.setHeadWord(StringUtil.unquote(pair.getFirst()));
		entity.setHeadWordReading(pair.getSecond());
		entity.setPartOfSpeech(values[5]);
		entity.setConceptId(values[11]);
		entity.setConceptHeadWordEnglish(StringUtil.unquote(values[12]));
		Pair<String, String> conceptHeadWords = extractReading(values[13]);
		entity.setConceptHeadWordJapanese(conceptHeadWords.getFirst());
		entity.setConceptHeadWordJapaneseReading(conceptHeadWords.getSecond());
		entity.setConceptGlossEnglish(StringUtil.unquote(values[14]));
		entity.setConceptGlossJapanese(StringUtil.unquote(values[15]));
		entity.setManagementInfo(values[18]);
		return entity;
	}

	private Pair<String, String> extractReading(String value){
		String[] words = value.split("\\[|\\]");
		return Pair.create(words[0], ArrayUtil.getWithinBound(words, 1));
	}
}
