/*
 * $Id: TreeTaggerClientEngine.java 26579 2008-09-22 06:41:06Z nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.treetagger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.client.JTreeTaggerClient;
import jp.go.nict.langrid.treetagger.client.JTreeTaggerException;
import jp.go.nict.langrid.treetagger.client.JTreeTaggerProtocol;
import jp.go.nict.langrid.treetagger.engine.ProcessFailedException;
import jp.go.nict.langrid.treetagger.engine.Tag;
import jp.go.nict.langrid.treetagger.engine.TreeTaggerEngine;

/**
 * TreeTaggerサーバを利用するエンジン実装。
 * ローカルホストに接続する。
 * @author $Author: nakaguchi $
 * @version $Revision: 26579 $
 */
public class TreeTaggerClientEngine
implements TreeTaggerEngine{
	/**
	 * コンストラクタ。
	 * @param language 言語
	 * @param port ポート
	 */
	public TreeTaggerClientEngine(Language language, int port){
		this.language = language;
		this.port = port;
	}

	public Tag[] tag(String sentence)
	throws ProcessFailedException
	{
		List<Tag> ret = new ArrayList<Tag>();
		JTreeTaggerClient jtc = null;
		try{
			jtc = createJTreeTaggerClient();
			jtc.makeConnection();
			String[] results = jtc.doTag(sentence);
			for(String result : results){
				StringTokenizer tokens = new StringTokenizer(
						result, JTreeTaggerProtocol.SEPARATOR);
				if(tokens.countTokens() != 3){
					continue;
				}
				String word = tokens.nextToken();
				String tag = tokens.nextToken();
				String lemma = tokens.nextToken();
				if(lemma.equals("<unknown>")){
					lemma = word;
				}
				ret.add(new Tag(word, tag, lemma));
			}
			return ret.toArray(new Tag[]{});
		}catch(UnknownHostException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		}catch(IOException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		}catch(InvalidLanguageTagException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		}catch(ClassNotFoundException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		}catch(JTreeTaggerException e){
			logger.warning(e.getMessage());
			throw new ProcessFailedException(e);
		}finally{
			try{
				if(jtc != null){
					jtc.closeConnection();
				}
			}catch(IOException e){
				logger.warning(e.getMessage());
				throw new ProcessFailedException(e);
			}catch(InvalidLanguageTagException e){
				logger.warning(e.getMessage());
				throw new ProcessFailedException(e);
			}
		}
	}

	private JTreeTaggerClient createJTreeTaggerClient()
	throws UnknownHostException, IOException, InvalidLanguageTagException
	{
		return new JTreeTaggerClient(
				InetAddress.getLocalHost()
				, port, language
				);
	}

	private Language language;
	private int port;

	private static Logger logger = Logger.getLogger(TreeTaggerClientEngine.class.getName());
}
