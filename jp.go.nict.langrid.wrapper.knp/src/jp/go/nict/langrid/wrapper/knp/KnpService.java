/*
 * $Id$
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
package jp.go.nict.langrid.wrapper.knp;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.wrapper.ws_1_2.dependencyparser.AbstractDependencyParserService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JUMAN/KNPを用いた日本語係り受け解析器「KNP」のラッパー実装クラスです。
 * 以下のパラメータをサポートします。
 * <ul>
 * <li>knp.timeoutMillis - KNPプロセスを待機するミリ秒数。
 * 経過してもKNPが終了しない場合ProcessFailedExceptionが発生。規定値: 5000(5秒)</li>
 * </ul>
 * @author Koyama
 * @author Takao Nakaguchi
 * @author $Author$
 * @version $Revision$
 */
public final class KnpService extends AbstractDependencyParserService {
	/**
	 * コンストラクタ。
	 */
	public KnpService() {
		setSupportedLanguageCollection(Arrays.asList(ja));
		loadProperties();
		timeoutMillis = getInitParameterInt("knp.timeoutMillis", 5000);
	}

	@Override
	protected Collection<Chunk> doParseDependency(Language language,
			String sentence) throws InvalidParameterException,
			ProcessFailedException {
		_log.debug("call doParseDependency()");
		return ProcessExec.doService(sentence, props, timeoutMillis);
	}

	/**
	 * プロパティファイルをロードする
	 */
	private void loadProperties() {
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();

	    if (loader == null) {
	    	loader = this.getClass().getClassLoader();
	    }
	    URL url = loader.getResource("knp.properties");

		try {
			props.load(new FileInputStream(new File(url.getFile())));
		} catch (FileNotFoundException e) {
			_log.error("knp.properties not found." + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IOException e) {
			_log.error("knp.properties io error. " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private int timeoutMillis;
	private Properties props = new Properties();
	private static Log _log = LogFactory.getLog(KnpService.class);
}
