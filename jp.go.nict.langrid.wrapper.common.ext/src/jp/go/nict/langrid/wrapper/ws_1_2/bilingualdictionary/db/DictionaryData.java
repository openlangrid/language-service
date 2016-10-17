/*
 * $Id:AbstractBilingualDictionaryService.java 1495 2006-10-12 18:42:55 +0900 (木, 12 10 2006) sagawa $
 *
 * Copyright (C) 2008 Language Grid Assocation.
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
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
package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.util.Hashtable;

public class DictionaryData {

	private String[] language;

	private int[] termId;
	
	private int[] priorityId;

	private Hashtable<String, Hashtable> hashDictionary;

	private Hashtable<Integer, Integer> hashPair;

	/*
	 * デフォルトコンストラクタ
	 */
	public DictionaryData() {
		hashDictionary = new Hashtable<String, Hashtable>();
		hashPair = new Hashtable<Integer, Integer>();
	}

	public Hashtable<String, Hashtable> getHashDictionary() {
		return hashDictionary;
	}

	public void setHashDictionary(Hashtable<String, Hashtable> hashDictionary) {
		this.hashDictionary = hashDictionary;
	}

	public Hashtable<Integer, Integer> getHashPair() {
		return hashPair;
	}

	public void setHashPair(Hashtable<Integer, Integer> hashPair) {
		this.hashPair = hashPair;
	}

	public String[] getLanguage() {
		return this.language;
	}

	public void setLanguage(String[] language) {
		this.language = language;
	}

	public int[] getTermId() {
		return termId;
	}

	public void setTermId(int[] termId) {
		this.termId = termId;
	}

	/*
	 * 辞書データの登録：用語
	 */
	public void setTermById(String language, int[] termId, String[] term) {
		Hashtable<Integer, String> hashTerm = new Hashtable<Integer, String>();
		for (int i = 0; i < termId.length; i++) {
			hashTerm.put(termId[i], term[i]);
		}
		hashDictionary.put(language, hashTerm);
	}

	/*
	 * 辞書データの登録：優先順位
	 */
	public void setPriorityById(int[] priority, int[] termId) {
		for (int i = 0; i < priority.length; i++) {
			hashPair.put(priority[i], termId[i]);
		}
	}

	public int[] getPriorityId() {
		return priorityId;
	}

	public void setPriorityId(int[] priorityId) {
		this.priorityId = priorityId;
	}

}
