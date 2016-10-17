/*
 * $Id: TreeTagger.java 3604 2008-07-15 04:46:14Z murakami $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.treetagger.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import junit.framework.TestCase;

/**
 * JTreeTaggerClientのテスト
 * @author $Author$
 * @version $Revision$
 */
public class JTreeTaggerClientTest extends TestCase{
	public void testDoTag(){
		JTreeTaggerClient client = null;
		try{
			client = new JTreeTaggerClient(InetAddress.getLocalHost(), 50001, Language.parse("en"));
			String[] result = client.doTag(testMessage);
			assertFalse(result == null);
			assertTrue(result.length > 0);
			for(String message : testMessages){
				result = client.doTag(message);
				assertFalse(result == null);
				assertTrue(result.length > 0);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}finally{
			try{
				client.closeConnection();
			}catch(InvalidLanguageTagException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	private String testMessage = "There are books, but I can't write. because My note is not found.";
	private List<String> testMessages = new ArrayList<String>(){
		{
			add("There are books, but I can't write. because My note is not found.");
			add("My name is hoge, and I came from kyoto.");
			add("Please sit down, I talk to you on my life.");
			add("And you, and me, and he, and she, there to other.");
		}
		private static final long serialVersionUID = 1L;
	};
}
