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
import edu.cmu.lti.jawjaw.pobj.Sense;

/**
 * Data Access Object for sense table 
 * @author Hideki Shima
 *
 */
public class SenseDAO {

	/**
	 * Find sense records by synset (one-to-many relationship)
	 * @param synset
	 * @return sense records
	 */
	public static List<Sense> findSensesBySynset( String synset ) {
		String sql = SQLCommand.FIND_SENSES_BY_SYNSET;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Sense> senses = new ArrayList<Sense>(); 
		try{
			try {
				// create a database connection
				ps.setString(1, synset);
				ResultSet rs = ps.executeQuery();
				while ( rs.next() ) {
					senses.add( rsToObject(rs) );
				}
			} finally{
				ps.close();
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return senses;
	}
	
	/**
	 * Find sense records by word id (one-to-many relationship)
	 * @param wordid word id
	 * @return sense records
	 */
	public static List<Sense> findSensesByWordid( int wordid ) {
		String sql = SQLCommand.FIND_SENSES_BY_WORDID;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Sense> senses = new ArrayList<Sense>();
		try{
			try {
				// create a database connection
				ps.setInt(1, wordid);
				ResultSet rs = ps.executeQuery();
				while ( rs.next() ) {
					senses.add( rsToObject(rs) );
				}
			} finally{
				ps.close();
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return senses;
	}
	
	/**
	 * Find sense records by synset and language (one-to-many relationship)
	 * @param synset
	 * @param lang
	 * @return sense records
	 */
	public static List<Sense> findSensesBySynsetAndLang( String synset, Lang lang ) {
		String sql = SQLCommand.FIND_SENSES_BY_SYNSET_AND_LANG;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Sense> senses = new ArrayList<Sense>();
		try {
			// create a database connection
			ps.setString(1, synset);
			ps.setString(2, lang.toString());
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				senses.add( rsToObject(rs) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return senses;
	}
	
	private static Sense rsToObject( ResultSet rs ) throws SQLException {
		Sense sense = new Sense(
			rs.getString(1),	
			rs.getInt(2),	
			Lang.valueOf( rs.getString(3) ),
			rs.getInt(4),
			rs.getInt(5),
			rs.getInt(6),
			rs.getString(7)	
		);
		return sense;
	}
	
	
}
