//Copyright 2009 Carnegie-Mellon University
//Licensed under the Apache License, Version 2.0 (the "License"); 
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, 
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied. See the License for the
//specific language governing permissions and limitations
//under the License.

package edu.cmu.lti.jawjaw.db;

/**
 * Stores sql commands.
 * Applying pure fabrication pattern for a maintainability.
 * 
 * @author Hideki Shima
 *
 */
public class SQLCommand {

	public static final String FIND_WORD_BY_LEMMA         = "SELECT * FROM word WHERE lemma=?";
	public static final String FIND_WORD_BY_LEMMA_AND_POS = "SELECT * FROM word WHERE lemma=? AND pos=?";
	public static final String FIND_WORD_BY_WORDID        = "SELECT * FROM word WHERE wordid=?";
	
	public static final String FIND_SENSES_BY_SYNSET      = "SELECT * FROM sense WHERE synset=? order by lang";
	public static final String FIND_SENSES_BY_WORDID      = "SELECT * FROM sense WHERE wordid=?";
	public static final String FIND_SENSES_BY_SYNSET_AND_LANG = "SELECT * FROM sense WHERE synset=? AND lang=?";
	
	public static final String FIND_SYNSETDEF_BY_SYNSET_AND_LANG = "SELECT * FROM synset_def WHERE synset=? AND lang=?";
	
	public static final String FIND_SYNLINK_BY_SYNSET     = "SELECT * FROM synlink WHERE synset1=?";
	public static final String FIND_SYNLINK_BY_SYNSET_AND_LINK = "SELECT * FROM synlink WHERE synset1=? AND link=?";
	
	public static final String FIND_SYNSET_BY_SYNSET      = "SELECT * FROM synset WHERE synset=?";
	public static final String FIND_SYNSETS_BY_NAME       = "SELECT * FROM synset WHERE name=?";
	public static final String FIND_SYNSETS_BY_NAME_AND_POS = "SELECT * FROM synset WHERE name=? AND pos=?";
	
}
