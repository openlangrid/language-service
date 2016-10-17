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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.jawjaw.pobj.Lang;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Word;

/**
 * Data Access Object for word table
 * @author hideki
 *
 */
public class WordDAO {

	/**
	 * Find words by lemma
	 * lemma can be either in English or Japanese
	 * @param lemma
	 * @return word records
	 */
	public static List<Word> findWordsByLemma( String lemma ) {
		lemma = lemma.toLowerCase();
		String sql = SQLCommand.FIND_WORD_BY_LEMMA;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Word> words = new ArrayList<Word>();
		try{
			try {
				// create a database connection
				ps.setString(1, lemma);
				ResultSet rs = ps.executeQuery();
				while ( rs.next() ) {
					words.add( rsToObject(rs) );
				}
			} finally{
				ps.close();
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return words;
	}

	/**
	 * Find words by lemma and POS. 
	 * lemma can be either in English or Japanese
	 * 
	 * @param lemma cannonical form of the word 
	 * @param pos POS of the lemma
	 * @return word records
	 */
	public static List<Word> findWordsByLemmaAndPos( String lemma, POS pos ) {
		lemma = lemma.toLowerCase();
		String sql = SQLCommand.FIND_WORD_BY_LEMMA_AND_POS;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Word> words = new ArrayList<Word>();
		try {
			// create a database connection
			ps.setString(1, lemma);
			ps.setString(2, pos.toString());
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				words.add( rsToObject(rs) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return words;
	}
	
	/**
	 * Find word by word id
	 * @param wordid
	 * @return word record
	 */
	public static Word findWordByWordid( int wordid ) {
		String sql = SQLCommand.FIND_WORD_BY_WORDID;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		Word word = null;
		try{
			try {
				// create a database connection
				ps.setInt(1, wordid);
				ResultSet rs = ps.executeQuery();
				if ( rs.next() ) {
					word = rsToObject(rs);
				}
			} finally{
				ps.close();
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return word;
	}
	
	private static Word rsToObject( ResultSet rs ) throws SQLException {
		Word word = new Word(
			rs.getInt(1),	
			Lang.valueOf( rs.getString(2) ),
			rs.getString(3),
			rs.getString(4),	
			POS.valueOf( rs.getString(5) )	
		);
		return word;
	}
	
	
}
