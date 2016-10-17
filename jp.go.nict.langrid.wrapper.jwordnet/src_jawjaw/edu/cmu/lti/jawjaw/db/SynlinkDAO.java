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

import edu.cmu.lti.jawjaw.pobj.Link;
import edu.cmu.lti.jawjaw.pobj.Synlink;

/**
 * Data Access Object for synlink table
 * @author Hideki Shima
 *
 */
public class SynlinkDAO {

	/**
	 * Find synlink records by synset (one-to-many relationship)
	 * @param synset
	 * @return synlink records
	 */
	public static List<Synlink> findSynlinksBySynset( String synset ) {
		String sql = SQLCommand.FIND_SYNLINK_BY_SYNSET;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Synlink> synlinks = new ArrayList<Synlink>();
		try{
			try {
				// create a database connection
				ps.setString(1, synset);
				ResultSet rs = ps.executeQuery();
				while ( rs.next() ) {
					synlinks.add( rsToObject(rs) );
				}
			} finally{
				ps.close();
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return synlinks;
	}
	
	/**
	 * Find synlink records by synset and link (one-to-many relationship)
	 * @param synset first argument of a relationship
	 * @param link lexical relationship
	 * @return synlink records
	 */
	public static List<Synlink> findSynlinksBySynsetAndLink( String synset, Link link ) {
		String sql = SQLCommand.FIND_SYNLINK_BY_SYNSET_AND_LINK;
		PreparedStatement ps = SQL.getInstance().getPreparedStatement(sql);
		List<Synlink> synlinks = new ArrayList<Synlink>();
		try {
			// create a database connection
			ps.setString(1, synset);
			ps.setString(2, link.toString());
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				synlinks.add( rsToObject(rs) );
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		return synlinks;
	}
	
	private static Synlink rsToObject( ResultSet rs ) throws SQLException {
		Synlink synlink = new Synlink(
			rs.getString(1),	
			rs.getString(2),
			Link.valueOf( rs.getString(3) ),
			rs.getString(4)	
		);
		return synlink;
	}
	
	
}
