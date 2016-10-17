/*
 * $Id: ConceptRelation.java 26411 2008-08-28 08:58:33Z nakaguchi $
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
public class ConceptRelation {
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
	public String getUpperConceptId() {
		return upperConceptId;
	}
	public void setUpperConceptId(String upperConceptId) {
		this.upperConceptId = upperConceptId;
	}
	public String getLowerConceptId() {
		return lowerConceptId;
	}
	public void setLowerConceptId(String lowerConceptId) {
		this.lowerConceptId = lowerConceptId;
	}
	public String getManagementInfo() {
		return managementInfo;
	}
	public void setManagementInfo(String managementInfo) {
		this.managementInfo = managementInfo;
	}

	@Id
	private String recordId;
	private String upperConceptId;
	private String lowerConceptId;
	private String managementInfo;
}
