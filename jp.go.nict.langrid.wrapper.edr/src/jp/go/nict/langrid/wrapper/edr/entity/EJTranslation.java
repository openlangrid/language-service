/*
 * $Id: EJTranslation.java 26411 2008-08-28 08:58:33Z nakaguchi $
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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author takao
 *
 */
@Entity
public class EJTranslation
implements Translation
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

	public List<EJTranslation.Translation> getTranslation() {
		return translation;
	}

	public void setTranslation(List<EJTranslation.Translation> translation) {
		this.translation = translation;
	}

	public String getManagementInfo() {
		return managementInfo;
	}

	public void setManagementInfo(String managementInfo) {
		this.managementInfo = managementInfo;
	}

	// <レコードID>
	@Id
	private String recordId;

	// <見出し情報>
	//   <単語見出し>
	private String headWord;

	// <文法情報>
	//   <品詞>
	private String partOfSpeech;

	// <意味情報> :(→5.3節)
	//   <概念識別子> :概念の同一性を示す番号(→5.3.1節)
	private String conceptId;
	//   <概念見出し> :概念を代表する単語見出し(内容語のみ→5.3.2節)
	//     <日本語概念見出し> :概念を代表する日本語単語見出し
	private String conceptHeadWordEnglish;
	//     <英語概念見出し> :概念を代表する英語単語見出し
	private String conceptHeadWordJapanese;
	//   <概念説明> :概念の文章による説明(内容語のみ→5.3.2節)
	//     <日本語概念説明>
	private String conceptGlossEnglish;
	//     <英語概念説明>
	private String conceptGlossJapanese;

	// <対訳情報> :(→5.4節)
	//   <訳語情報>。。。 :訳語の表記と文法的情報
	//     <訳語種別> :訳語の種類(→5.4.2節)
	//     <訳語表記> :訳語の表記(→5.4.3節)
	//     <訳語品詞> :訳語の品詞(→5.4.4節)
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="ownerRecordId", referencedColumnName="recordId")
	private List<EJTranslation.Translation> translation;

	// <管理情報> :(→5.5節)
	//   <管理履歴レコード> :更新日付等の管理情報}
	private String managementInfo;

	@Entity
	public static class Translation
	implements TranslationTranslation
	{
		public Translation() {
			
		}
		public Translation(String type, String headWord, String partOfSpeech) {
			this.type = type;
			this.headWord = headWord;
			this.partOfSpeech = partOfSpeech;
		}
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

		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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

		@Id
		@GeneratedValue
		@SuppressWarnings("unused")
		private int id;
		private String type;
		private String headWord;
		private String partOfSpeech;
	}

}
