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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.client.typed.StatusCode;

/**
 * JTreeTaggerサーバのクライアントクラス
 * @author $Author$
 * @version $Revision$
 */
public class JTreeTaggerClient{
	/**
	 * コンストラクタ
	 * @param address サーバの待ち受けアドレス
	 * @param port サーバの待ち受けポート
	 * @throws UnknownHostException アドレスにサーバが存在しなかった
	 * @throws IOException 接続に失敗した
	 */
	public JTreeTaggerClient(InetAddress address, int port, Language language) throws UnknownHostException,
			IOException{
		this.address = address;
		this.port = port;
		this.language = language;
	}

	/**
	 * JTreeTaggerの処理を開始する
	 * @param sentence 解析する文字列
	 * @param language 解析する文字列の言語表現
	 * @return 解析結果の配列
	 * @throws IOException 接続に失敗した
	 * @throws InvalidLanguageTagException 解析する言語に対応していない
	 * @throws ClassNotFoundException クラスが存在しない
	 * @throws JTreeTaggerException サーバとの応答に失敗した
	 */
	public String[] doTag(String sentence) throws IOException,
			 ClassNotFoundException, JTreeTaggerException
	{
		JTreeTaggerProtocol packet = new JTreeTaggerProtocol(language);
		packet.setSentence(sentence);
		oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(packet);
		ois = new ObjectInputStream(socket.getInputStream());
		Object obj = ois.readObject();
		if(obj == null){
			return new String[0];
		}
		if(!(obj instanceof JTreeTaggerProtocol)){
			JTreeTaggerException e = new JTreeTaggerException("Error:Invalid protocol");
			throw e;
		}
		JTreeTaggerProtocol result = (JTreeTaggerProtocol)obj;
		if(result.getStatusCode() != StatusCode.RESPONSE
				&& result.getStatusCode() != StatusCode.CLOSE){
			JTreeTaggerException e = new JTreeTaggerException("Error:"
					+ result.getStatusCode().name());
			throw e;
		}
		return result.getResultTags();
	}
	
	public void makeConnection() throws IOException {
		socket = new Socket(address, port);
	}

	public void closeConnection() throws IOException, InvalidLanguageTagException{
		if(socket != null){
			JTreeTaggerProtocol request = new JTreeTaggerProtocol(language);
			request.setStatusCode(StatusCode.CLOSE);
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(request);
			socket.close();
		}
	}

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Socket socket;
	private InetAddress address;
	private int port;
	private Language language;
}
