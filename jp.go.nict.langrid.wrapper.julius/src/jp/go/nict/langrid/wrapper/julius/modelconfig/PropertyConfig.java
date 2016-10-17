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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

public class PropertyConfig implements ModelConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1489935674323285176L;


	private static final PropertyConfig singleton = new PropertyConfig();
	private final List<ModelConfig> models;

	private final String id;
	private final String path;
	private final String language;
	private final String recognizedCharset;

	private PropertyConfig() {
		this.models = new ArrayList<ModelConfig>();
		this.id = null;
		this.path = null;
		this.language = null;
		this.recognizedCharset = null;
		this.loadConfig("lm");
	}

	private PropertyConfig(String id, String path, String language, String recognizedCharset) {
		this.models = null;
		this.id = id;
		this.path = path;
		this.language = language;
		this.recognizedCharset = recognizedCharset;
	}

	public String getName() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public String getLanguage() {
		return language;
	}

	public String getRecognizedCharset() {
		return recognizedCharset;
	}

	public static List<ModelConfig> getModelConfig() {
		return Collections.unmodifiableList(singleton.models);
	}

	/**
	 * プロパティから設定をリロードします。
	 * リロード前に保持されていた設定は破棄されます。
	 * @param properties プロパティオブジェクト
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reloadConfig(Properties properties) {
		singleton.models.clear();
		singleton.loadConfig((Map)properties); 
	}

	/**
	 * 指定された名称のリソースバンドルから設定をリロードします。
	 * リロード前に保持されていた設定は破棄されます。
	 * @param bundleName バンドル名称
	 */
	public static void reloadConfig(String bundleName) {
		singleton.models.clear();
		singleton.loadConfig(bundleName);
	}

	private void loadConfig(String bundleName) {
		final ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
		final Map<String, String> entries = new HashMap<String,String>();

		for (Enumeration<String> e = bundle.getKeys(); e.hasMoreElements();) {
			final String key = e.nextElement();
			entries.put(key, bundle.getString(key));
		}

		loadConfig(entries);
	}

	private void loadConfig(Map<String, String> map) {
		final Set<String> idSet = new HashSet<String>();
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
			final String key = it.next();
			idSet.add(key.substring(0, key.indexOf('.')));
		}

		for (Iterator<String> it = idSet.iterator(); it.hasNext();) {
			final String id = it.next();
			final String path = map.get(id.concat(".path"));
			final String language = map.get(id.concat(".language"));
			final String charset = map.get(id.concat(".charset"));
			models.add(new PropertyConfig(id, path, language, charset));
		}
	}
}
