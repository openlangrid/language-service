/*
 * $Id: JTreeTaggerTest.java 26919 2009-02-03 03:37:05Z Takao Nakaguchi $
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
 */package jp.go.nict.langrid.server.treetagger;

import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.client.JTreeTaggerProtocol;
import jp.go.nict.langrid.treetagger.client.typed.StatusCode;
import junit.framework.TestCase;

/**
 * JTreeTaggerの動作テスト
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26919 $
 */
public class JTreeTaggerTest extends TestCase{
	/**
	 * サーバから結果が返るかのテスト
	 */
	public void testGetResult() throws Exception{
		Socket socket = new Socket(InetAddress.getLocalHost(), 50001);
		JTreeTaggerProtocol packet = new JTreeTaggerProtocol(language);
		packet.setSentence(testMessage);
		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(socket
				.getOutputStream()));
		oos.writeObject(packet);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Object obj = ois.readObject();
		if(obj == null){
			fail("result is null.");
		}
		if(!(obj instanceof JTreeTaggerProtocol)){
			fail("Error:Invalid protocol");
		}
		JTreeTaggerProtocol result = (JTreeTaggerProtocol)obj;
		if(result.getStatusCode() != StatusCode.RESPONSE
				&& result.getStatusCode() != StatusCode.CLOSE){
			fail("invalid result.");
		}
	}

	protected void setUp() throws Exception{
		language = Language.parse("en");
	}

	protected void tearDown() throws Exception{
	}

	private Language language;
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
