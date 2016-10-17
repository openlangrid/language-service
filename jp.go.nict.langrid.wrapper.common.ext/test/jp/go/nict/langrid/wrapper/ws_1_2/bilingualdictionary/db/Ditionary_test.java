/*
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

package jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Ditionary_test {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws DBCommonReleaseFailure 
	 * @throws DBCommonConnectionFailure 
	 * @throws SQLException
	 * @throws DBCommonException
	 */
	public static void main(String[] args) throws Exception,SQLException {
		DictionaryService service = new DictionaryService();
		
		String[] dict = readFile();
		for(int i=0; i<dict.length; i++){
			String[] langs = new String[]{"en","ja","ko"};
			String[] terms = dict[i].split("\t");
			service.addTerm("radio", langs, terms);
			service.addTerm("radio", langs, terms);
		}
		//String[] langList = manager.getReadableDictionaryList("shigenobu");
		//String[] langList = new String[]{"ja","en"};
		
		//DictionaryData data = service.getDictionaryByLang("jearn", langList);
		//service.addTerm("test", "ko", "sss", "jearn");
		//service.setPriority("jearn", new int[]{1}, new int[]{1});
		//service.addNewTerm("test", "ko", "hyahhou");
		//service.deleteTermById("test", 3);
		//service.createDictionary("jearn");
		//service.addLanguage("jearn", "ja");
		//service.addLanguage("jearn", "en");
		//service.addTerm("jearn", "ja", "ホームページ");
		
		log("");
	}

	private static void log(String[] list) {
		for (int i = 0; i < list.length; i++) {
			System.out.println(list[i]);
		}
	}

	private static void log(String dict) {
		System.out.println(dict);
	}
	
	public static String[] readFile() {
		ArrayList<String> list = new ArrayList<String>();
		String[] dic = null;
		InputStreamReader isr;

		try {
			isr = new InputStreamReader(Ditionary_test.class
					.getResourceAsStream("/dict.dat"), "UTF-8");

			BufferedReader br = new BufferedReader(isr);

			while (br.ready()) {
				list.add(br.readLine());
			}
			br.close();

			dic = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				dic[i] = list.get(i);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dic;
	}
}
