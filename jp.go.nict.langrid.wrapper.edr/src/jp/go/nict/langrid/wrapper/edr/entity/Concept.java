/*
 * $Id: Concept.java 26411 2008-08-28 08:58:33Z nakaguchi $
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

@Entity
public class Concept {
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
	public String getConceptId() {
		return conceptId;
	}
	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}
	public String getHeadWordEnglish() {
		return headWordEnglish;
	}
	public void setHeadWordEnglish(String headWordEnglish) {
		this.headWordEnglish = headWordEnglish;
	}
	public String getHeadWordJapanese() {
		return headWordJapanese;
	}
	public void setHeadWordJapanese(String headWordJapanese) {
		this.headWordJapanese = headWordJapanese;
	}
	public String getHeadWordJapaneseReading() {
		return headWordJapaneseReading;
	}
	public void setHeadWordJapaneseReading(String headWordJapaneseReading) {
		this.headWordJapaneseReading = headWordJapaneseReading;
	}
	public String getGlossEnglish() {
		return glossEnglish;
	}
	public void setGlossEnglish(String glossEnglish) {
		this.glossEnglish = glossEnglish;
	}
	public String getGlossJapanese() {
		return glossJapanese;
	}
	public void setGlossJapanese(String glossJapanese) {
		this.glossJapanese = glossJapanese;
	}
	public String getManagementInfo() {
		return managementInfo;
	}
	public void setManagementInfo(String udpate) {
		this.managementInfo = udpate;
	}

	@Id
	private String recordId;
	private String conceptId;
	private String headWordEnglish;
	private String headWordJapanese;
	private String headWordJapaneseReading;
	private String glossEnglish;
	private String glossJapanese;
	private String managementInfo;
}
