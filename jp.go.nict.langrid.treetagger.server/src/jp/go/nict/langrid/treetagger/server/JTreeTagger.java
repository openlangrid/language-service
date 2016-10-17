/*
 * $Id: JTreeTagger.java 27012 2009-02-27 06:31:12Z kamiya $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;
import jp.go.nict.langrid.treetagger.engine.UnsupportedLanguageException;
import jp.go.nict.langrid.treetagger.engine.linux.jni.TreeTaggerLinuxJNIEngine;


/**
 * TreeTaggerのフロントエンドサーバ。
 * 受け付けるステータスコードは以下、それ以外は無視されます。
 * <ul>
 * <li>jp.go.nict.langrid.treetagger.client.typed.REQUEST</li>
 * <li>jp.go.nict.langrid.treetagger.client.typed.CLOSE</li>
 * </ul>
 * @author $Author: kamiya $
 * @version $Revision: 27012 $
 */
public class JTreeTagger{
	private static final Logger logger = Logger.getLogger(JTreeTagger.class.getCanonicalName());
	private static int port = 50001;
	private static BlockingQueue<Socket> queue;
	private static int queueSize = 40;
	private static int queueTimeOut = 10000;
	private static int connectionTimeOut = 10000;
	private static String resourcePlace = "./";
	private static TreeTaggerEngine tagger;
	private static int threadCount = 1;

	/**
	 * メイン
	 * @param args
	 */
	public static void main(String[] args){
		// parse arguments
		Language language = null;
		try{
			for(String arg : args){
				if(arg.equals("-h")){
					System.out.println("usage: JTreeTagger language(en,de,fr,it,es,bg,ru,es) port("
									+ port + ":default)");
					System.out.println("exsample.. JTreeTagger en 51010 or JTreeTagger en");
					return;
				}else if(language == null){
					language = Language.parse(arg);
				}else{
					String fileName = arg;
					BufferedReader br = new BufferedReader(new InputStreamReader(
							new FileInputStream(new File(fileName))));
					String line;
					while((line = br.readLine()) != null){
						if(line.equals("") || line.substring(0, 1).equals("#")){
							continue;
						}
						String[] pair = line.split("=");
						if(pair[0].equals("port")){
							port = Integer.valueOf(pair[1]);
						}else if(pair[0].equals("queueize")){
							queueSize = Integer.valueOf(pair[1]);
						}else if(pair[0].equals("timeout.process")){
							queueTimeOut = Integer.valueOf(pair[1]);
						}else if(pair[0].equals("timeout.connection")){
							connectionTimeOut = Integer.valueOf(pair[1]);
						}else if(pair[0].equals("thread.count")){
							threadCount = Integer.valueOf(pair[1]);
						}
					}
					br.close();
				}
			}
			FileHandler fh = new FileHandler("./log/JTreeTagger_" + language.getCode() + ".log.%g", 1048576, 10, true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			for(Handler hd : logger.getParent().getHandlers()){
				logger.getParent().removeHandler(hd);
			}
			logger.info("JTreeTagger Server initialized.\n" +
					"Language Code: " + language.getCode() +"\n"+
					"Read configuration\n" +
					"- Accepting socket port: " + port + "\n" +
					"- Request socket queue size: " + queueSize + "\n" +
					"- Request socket queue timeout: " + queueTimeOut + "ms\n" +
					"- Socket connection timeout: " + connectionTimeOut + "ms\n" +
					"- Server proccess thread count: " + threadCount);
		}catch(Exception e){
			System.out.println("Invalid arguments error.");
			System.out.println(
					"usage: JTreeTagger language(en,de,fr,it,es,bg,ru,es) config file(optional)");
			System.out.println(
					"example.. JTreeTagger en ./jtreetagger.conf or JTreeTagger en");
			logger.log(Level.SEVERE, "Invalid arguments error.", e);
			return;
		}
	
		ServerSocket ss = null;
		try{
			ss = invokeServerProcess(language);
		}catch(Exception e){
			logger.log(Level.SEVERE, "Server invoked error", e);
			return;
		}
		
		// server process
		while(true){
			Socket socket = null;
			try{
				socket = ss.accept();
				socket.setSoTimeout(connectionTimeOut);
				queue.offer(socket, queueTimeOut, TimeUnit.MILLISECONDS);
			}catch(SocketException e){
				logger.warning("Socket initialized process error:" + e.getMessage());
			}catch(IOException e){
				logger.warning("Socket initialized process error:" + e.getMessage());
			}catch(InterruptedException e){
				logger.warning("Request queue is full, not accept:"
						+ socket.getInetAddress());
				if(socket != null){
					try{
						socket.close();
					}catch(IOException e1){
						logger.warning("Socket can't close:" + socket.getInetAddress());
					}
				}
			}catch(Exception e){
				logger.log(Level.WARNING, "Socket initialized process error", e);
			}
		}
	}
	
	private static ServerSocket invokeServerProcess(Language language)
	throws IOException, SecurityException, UnsatisfiedLinkError, UnsupportedLanguageException
	{
		ServerSocket ss = new ServerSocket(port);
			tagger = new TreeTaggerLinuxJNIEngine(new File(resourcePlace), language);
			queue = new LinkedBlockingQueue<Socket>(queueSize);
			for(int i = 0; i < threadCount; i++){
				new JTreeTaggerProcessor(tagger, queue, language, logger).start();
			}
			logger.info("JTreeTagger is invoked, Address:" + ss.getInetAddress()
					+ " Port:" + ss.getLocalPort() + " Language:" + language.getCode());			
			System.out.println("JTreeTagger is invoked, Address:" + ss.getInetAddress()
					+ " Port:" + ss.getLocalPort() + " Language:" + language.getCode());
			
		return ss;
	}
}
