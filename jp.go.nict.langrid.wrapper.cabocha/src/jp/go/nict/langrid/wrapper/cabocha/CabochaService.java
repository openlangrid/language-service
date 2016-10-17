/*
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
package jp.go.nict.langrid.wrapper.cabocha;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.wrapper.ws_1_2.dependencyparser.AbstractDependencyParserService;

/**
 * Support Vector Machines に基づく日本語係り受け解析器「Cabocha」のラッパー実装クラスです．
 */
public class CabochaService
extends AbstractDependencyParserService {
	/**
	 * コンストラクタ
	 */
	public CabochaService() {
		path = getInitParameterString("cabochaPath", "/usr/local/bin/cabocha");
		encoding = getInitParameterString("cabochaEncoding", "UTF-8");
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
	protected Collection<Chunk> doParseDependency(
		Language language, String sentence)
	throws InvalidParameterException, ProcessFailedException
	{
		try {
			return new Parser().parse(sentence, path, encoding);
		} catch(IOException e) {
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		}
	}

	private String path;
	private String encoding;
	
	private static Logger logger = Logger.getLogger(CabochaService.class.getName());
}
