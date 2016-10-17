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

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Synset;

/**
 * Data Access Object for synset table
 * @author Hideki Shima
 *
 */
public class SynsetDAO {

	/**
	 * Find synset record by synset id key
	 * @param synset id
	 * @return synset record
	 */
	public static Synset findSynsetBySynset( String synset ) {
		String sql = SQLCommand.FIND_SYNSET_BY_SYNSET;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		Synset s = null;
		try {
			// create a database connection
			ps.setString(1, synset);
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				s = rsToObject(rs);
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Find synset records by name. This method is deprecated 
	 * since synset only has a name in English. Start from word
	 * record instead.
	 * 
	 * @param name label on a synset
	 * @return synset records
	 */
	@Deprecated
	public static List<Synset> findSynsetsByName( String name ) {
		String sql = SQLCommand.FIND_SYNSETS_BY_NAME;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Synset> synsets = new ArrayList<Synset>();
		try {
			// create a database connection
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				synsets.add( rsToObject(rs) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return synsets;
	}
	
	/**
	 * Find synset records by name and POS. This method is deprecated 
	 * since synset only has a name in English. Start from word
	 * record instead.
	 * 
	 * @param name
	 * @param pos
	 * @return synset records
	 */
	public static List<Synset> findSynsetsByNameAndPos( String name, POS pos ) {
		String sql = SQLCommand.FIND_SYNSETS_BY_NAME_AND_POS;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Synset> synsets = new ArrayList<Synset>();
		try {
			// create a database connection
			ps.setString(1, name);
			ps.setString(2, pos.toString());
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				synsets.add( rsToObject(rs) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return synsets;
	}
	
	private static Synset rsToObject( ResultSet rs ) throws SQLException {
		Synset synset = new Synset(
			rs.getString(1),	
			POS.valueOf( rs.getString(2) ),
			rs.getString(3),
			rs.getString(4)	
		);
		return synset;
	}
	
	
}
