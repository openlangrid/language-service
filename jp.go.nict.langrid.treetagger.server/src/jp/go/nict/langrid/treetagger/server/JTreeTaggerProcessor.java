/*
 * $Id: JTreeTaggerProcessor.java 27012 2009-02-27 06:31:12Z kamiya $
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
package jp.go.nict.langrid.treetagger.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.client.JTreeTaggerProtocol;
import jp.go.nict.langrid.treetagger.client.typed.StatusCode;
import jp.go.nict.langrid.treetagger.engine.Tag;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;

/**
 * JTreeTaggerの処理をするクラス
 * @author $Author: kamiya $
 * @version $Revision: 27012 $
 */
public class JTreeTaggerProcessor extends Thread{
	/**
	 * コンストラクタ
	 * @param tagger TreeTagger本体のインスタンス
	 * @param queue 処理待ちキュー
	 * @param lang 処理言語
	 */
	public JTreeTaggerProcessor(TreeTaggerEngine tagger, BlockingQueue<Socket> queue,
			Language lang, Logger logger){
		this.tagger = tagger;
		this.queue = queue;
		this.language = lang;
		this.logger = logger;
	}

	/**
	 * リクエストされたオブジェクトを正規のプロトコルオブジェクトに変換する
	 * @param obj リクエストオブジェクト
	 * @return プロトコルオブジェクト
	 * @throws InvalidLanguageTagException 対応していない言語が指定された
	 */
	public JTreeTaggerProtocol convertValidProtocol(Object obj)
			throws InvalidLanguageTagException{
		if(!(obj instanceof JTreeTaggerProtocol)){
			JTreeTaggerProtocol request = new JTreeTaggerProtocol(language);
			request.setStatusCode(StatusCode.INVALIDREQUEST);
			return request;
		}
		JTreeTaggerProtocol request = (JTreeTaggerProtocol)obj;
		if(request.getLanguage() == null || !request.getLanguage().equals(language)
				|| request.getSentence() == null || request.getStatusCode() == null){
			request.setStatusCode(StatusCode.INVALIDPARAMETER);
			return request;
		}
		return request;
	}

	/**
	 * スレッドメイン
	 */
	@Override
	public void run(){
		while(true){
			Socket socket = null;
			String sentence = "";
			InetAddress address = null;
			try{
				socket = queue.take();
				address = socket.getInetAddress();
				logger.info("Do request process.["
						+ "Address:" + address
						+ ",ThreadID:"+ this.getId()
						+ ",lang:" + language
						+ "]"
					);
				ObjectInputStream ois = null;
				ObjectOutputStream oos = null;
				while(true){
					sentence = "";
					ois = new ObjectInputStream(socket.getInputStream());
					Object obj = ois.readObject();
					oos = new ObjectOutputStream(socket.getOutputStream());
					JTreeTaggerProtocol request = convertValidProtocol(obj);
					if(request.getStatusCode() == StatusCode.CLOSE){
						socket.close();
						break;
					}
					if(request.getStatusCode() != StatusCode.REQUEST){
						logger.warning("Recieve a invalid request.["
								+ ",Address:" + address
								+ ",RequestCode:" + request.getStatusCode()
								+ ",Language:" + request.getLanguage()
								+ ",ThreadID:" + this.getId()
								+ "]");
						oos.writeObject(request);
						break;
					}
					sentence = request.getSentence();
					Tag[] tags = tagger.tag(sentence);
					JTreeTaggerProtocol response = new JTreeTaggerProtocol(language);
					setTagResult(response, tags);
					response.setSentence(request.getSentence());
					response.setStatusCode(StatusCode.RESPONSE);
					response.setLanguage(language);
					oos.writeObject(response);
				}
			}catch(EOFException e){
			}catch(Exception e){
				logger.log(Level.WARNING, "Process was not completed.["
						+ "ThreadID:" + this.getId()
						+ ",Address:" + address
						+ ",Sentence:"+ sentence
						+ "]"
					, e);
				if(socket != null){
					try{
						socket.close();
					}catch(IOException ioE){
						logger.log(Level.WARNING, "ShutDown Error.["
								+ ",Address:" + address
								+ ",ThreadID:" + this.getId()
								+ "]"
							, ioE);
					}
				}
			}
			if(socket != null){
				try{
					socket.close();
				}catch(IOException ioE){
					logger.log(Level.WARNING, "ShutDown Error.["
							+ "Address:" + address
							+ ",ThreadID:" + this.getId()
							+ "]"
						, ioE);
				}
			}
		}
	}

	/**
	 * TreeTaggerからの結果をレスポンスオブジェクトに格納する
	 * @param protocol レスポンスオブジェクト
	 * @param tags 解析結果
	 */
	private void setTagResult(JTreeTaggerProtocol protocol, Tag[] tags){
		if(tags == null){
			return;
		}
		List<String> list = new ArrayList<String>();
		for(Tag tag : tags){
			StringBuilder sb = new StringBuilder();
			sb.append(tag.word);
			sb.append(JTreeTaggerProtocol.SEPARATOR);
			sb.append((tag.tag != null) ? tag.tag : "");
			sb.append(JTreeTaggerProtocol.SEPARATOR);
			sb.append((tag.lemma != null) ? tag.lemma : "");
			list.add(sb.toString());
		}
		protocol.setResultTags(list.toArray(new String[0]));
	}

	/**
	 * 対応言語
	 */
	private Language language;
	/**
	 * 処理待ちキュー
	 */
	private BlockingQueue<Socket> queue;
	/**
	 * TreeTagger本体
	 */
	private TreeTaggerEngine tagger;
	
	private Logger logger;
}
