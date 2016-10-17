/*
 * $Id: EConceptWord.java 26411 2008-08-28 08:58:33Z nakaguchi $
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
package jp.go.nict.langrid.wrapper.edr.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 英語単語辞書の情報から、概念辞書の実装に必要な情報のみを格納する。
 * @author $Author: nakaguchi $
 * @version $Revision: 26411 $
 */
@Entity
public class EConceptWord
implements ConceptWord
{
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

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getHeadWord() {
		return headWord;
	}

	public void setHeadWord(String headWord) {
		this.headWord = headWord;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public String getConceptId() {
		return conceptId;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public String getConceptHeadWordEnglish() {
		return conceptHeadWordEnglish;
	}

	public void setConceptHeadWordEnglish(String conceptHeadWordEnglish) {
		this.conceptHeadWordEnglish = conceptHeadWordEnglish;
	}

	public String getConceptHeadWordJapanese() {
		return conceptHeadWordJapanese;
	}

	public void setConceptHeadWordJapanese(String conceptHeadWordJapanese) {
		this.conceptHeadWordJapanese = conceptHeadWordJapanese;
	}

	public String getConceptHeadWordReadingJapanese() {
		return conceptHeadWordReadingJapanese;
	}

	public void setConceptHeadWordReadingJapanese(String conceptHeadWordReadingJapanese) {
		this.conceptHeadWordReadingJapanese = conceptHeadWordReadingJapanese;
	}

	public String getGloss(){
		return getConceptGlossEnglish();
	}

	public String getConceptGlossEnglish() {
		return conceptGlossEnglish;
	}

	public void setConceptGlossEnglish(String conceptGlossEnglish) {
		this.conceptGlossEnglish = conceptGlossEnglish;
	}

	public String getConceptGlossJapanese() {
		return conceptGlossJapanese;
	}

	public void setConceptGlossJapanese(String conceptGlossJapanese) {
		this.conceptGlossJapanese = conceptGlossJapanese;
	}

	public String getManagementInfo() {
		return managementInfo;
	}

	public void setManagementInfo(String managementInfo) {
		this.managementInfo = managementInfo;
	}

	@Id
	private String recordId;
	private String headWord;
	private String partOfSpeech;
	private String conceptId;
	private String conceptHeadWordEnglish;
	private String conceptHeadWordJapanese;
	private String conceptHeadWordReadingJapanese;
	private String conceptGlossEnglish;
	private String conceptGlossJapanese;
	private String managementInfo;
}
