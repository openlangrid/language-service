/*
 * $Id: StringScrapingJa.java 266 2010-10-03 10:33:59Z t-nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2010 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.common.db.paralleltext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opensymphony.oscache.util.StringUtil;

/**
 * 
 * 
 * @author NiCT
 * @author $Author: t-nakaguchi $
 * @version $Revision: 266 $
 */
public class StringScrapingJa {

	/**
	 * 
	 */
	public static String scrape(String text) {
		if (!validateTextFormat(text)) {
			return text;
		}
		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile(SCRAPE_REGX, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(text);
		if (!m.find()) {
			return text;
		}
		m.reset();
		while (m.find()) {
			sb.append(m.group(1));
			sb.append(m.group(2));
			sb.append(m.group(3));
		}
		return sb.toString();
	}

	/**
	 * 
	 */
	private static boolean validateTextFormat(String text) {
		if (StringUtil.isEmpty(text)) {
			return false;
		}
		String t = text.replaceAll("[^:\\{\\}]", "");

		return Pattern.matches("^(\\{:\\})*$", t);

//		Pattern p = Pattern.compile(VALIDATE_REGX, Pattern.MULTILINE);
//		Matcher m = p.matcher(text);
//		if (m.matches()) {
//			return true;
//		}
//		return false;
	}

	/** 必要な部分を切り出す用 */
	private static final String SCRAPE_REGX = "([^:\\{\\}]*)\\{([^:\\{\\}]*):[^:\\{\\}]*\\}([^:\\{\\}]*)";
	/** 書式チェック用 */
	private static final String VALIDATE_REGX = "^([^:\\{\\}]*?\\{[^:\\}]*?:[^:\\{]*?\\}[^:\\{\\}]*?[^:\\{\\}]*?)*$";
}
