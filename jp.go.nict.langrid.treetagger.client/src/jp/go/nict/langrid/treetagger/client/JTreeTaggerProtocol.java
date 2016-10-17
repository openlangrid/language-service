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

import java.io.Serializable;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.treetagger.client.typed.StatusCode;

/**
 * JTreeTagger用のプロトコルクラス
 * @author $Author$
 * @version $Revision$
 */
public class JTreeTaggerProtocol implements Serializable{
	public JTreeTaggerProtocol(Language language){
		statusCode = StatusCode.REQUEST;
		this.language = language;
		resultTags = new String[0];
		sentence = "";
	}

	public Language getLanguage(){
		return language;
	}

	public String[] getResultTags(){
		return resultTags;
	}

	public String getSentence(){
		return sentence;
	}

	public StatusCode getStatusCode(){
		return statusCode;
	}

	public void setLanguage(Language language){
		this.language = language;
	}

	public void setResultTags(String[] resultTags){
		this.resultTags = resultTags;
	}

	public void setSentence(String sentence){
		this.sentence = sentence;
	}

	public void setStatusCode(StatusCode statusCode){
		this.statusCode = statusCode;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("StatusCode:" + statusCode + "\r\n");
		sb.append("Language:" + language.getCode() + "\r\n");
		sb.append("Result:" + "\r\n");
		for(String tag : resultTags){
			sb.append(tag + "\r\n");
		}
		sb.append("\r\n");
		sb.append("Original:" + "\r\n");
		sb.append(sentence + "\r\n\r\n");
		return sb.toString();
	}

	protected Language language;
	protected StatusCode statusCode;
	private String[] resultTags;
	private String sentence;
	public static final String SEPARATOR = "\t";
	private static final long serialVersionUID = 7866302911870252483L;
}
