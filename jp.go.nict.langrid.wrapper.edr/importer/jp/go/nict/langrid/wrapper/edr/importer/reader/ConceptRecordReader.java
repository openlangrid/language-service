/*
 * $Id: ConceptRecordReader.java 4109 2008-08-28 08:58:33Z nakaguchi $
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
import jp.go.nict.langrid.wrapper.edr.entity.Concept;

public class ConceptRecordReader
extends RecordReader<Concept>{
	public ConceptRecordReader(InputStreamReader streamReader)
	throws IOException
	{
		super(streamReader);
	}

	public Concept doBuildRecord(String[] values, int lineLength)
	throws ParseException
	{
		if(values.length != 7){
			throw new ParseException(
					"too few elements.", lineLength
					);
		}
		// 重複レコードの除去
		// "CPH0419565";"201a1c";"";"生体が自己にとっての有害な状況から自己を守ろうとする性質";"";"抵抗性";"テイコウセイ";"DATE="98/3/16""
		// "CPH0419566";"201a1c";"";"生体が自己にとっての有害な状況から自己を守ろうとする性質";"";"抵抗性";"テイコウセイ";"DATE="98/3/16""
		// "CPH0416293";"200d54";"";"冷却媒体用の水";"";"冷却水";"レイキャクスイ";"DATE="98/2/19""
		// "CPH0419706";"201aa9";"";"冷却媒体用の水";"";"冷却水";"レイキャクスイ";"DATE="98/3/19""
		// "CPH0419707";"201aa9";"to deny other's claim giving evidence";"相手の主張に証拠をあげて否定すること";"disprove";"反証";"ハンショウ";"DATE="98/3/19""
		// 全く同じ情報が重複しているため、CPH0419566を除去する。
		// 概念IDが重複しており、異なる概念IDに同じ情報が存在するため、CPH0419706を除去する。
		if(values[0].equals("CPH0419566")) return null;
		if(values[0].equals("CPH0419706")) return null;

		Concept entity = new Concept();
		int current = 0;
		entity.setRecordId(values[0]);
		current += values[0].length() + 1;
		entity.setConceptId(values[1]);
		current += values[1].length() + 1;
		entity.setHeadWordEnglish(StringUtil.unquote(values[2]));
		current += values[2].length() + 1;
		String[] words = values[3].split("\\[|\\]");
		entity.setHeadWordJapanese(StringUtil.unquote(words[0]));
		if(words.length == 2){
			entity.setHeadWordJapaneseReading(words[1]);
		}
		current += values[3].length() + 1;
		entity.setGlossEnglish(StringUtil.unquote(values[4]));
		current += values[4].length() + 1;
		entity.setGlossJapanese(StringUtil.unquote(values[5]));
		current += values[5].length() + 1;
		entity.setManagementInfo(values[6]);
		current += values[6].length() + 1;
		return entity;
	}
}
