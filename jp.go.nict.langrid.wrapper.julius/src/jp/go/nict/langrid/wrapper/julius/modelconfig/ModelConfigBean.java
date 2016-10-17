/*
 * $Id: ICTCLAS.java 28009 2010-08-09 10:25:33Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.julius.modelconfig;

public class ModelConfigBean implements ModelConfig {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2333550883198181013L;
	/**
	 * Name of the model configuration
	 */
	private String name;
	/**
	 * File path string for the model configuration.
	 */
	private String path;
	/**
	 * Target language of model configuration.
	 */
	private String language;
	/**
	 * Character set of result of the recognition of the model.
	 */
	private String recognizedCharset;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the recognizedCharset
	 */
	public String getRecognizedCharset() {
		return recognizedCharset;
	}
	/**
	 * @param recognizedCharset the recognizedCharset to set
	 */
	public void setRecognizedCharset(String recognizedCharset) {
		this.recognizedCharset = recognizedCharset;
	}
	
}
