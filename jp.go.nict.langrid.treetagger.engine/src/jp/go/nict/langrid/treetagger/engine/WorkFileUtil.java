/*
 * $Id: WorkFileUtil.java 26403 2008-08-28 08:12:22Z nakaguchi $
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
package jp.go.nict.langrid.treetagger.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.go.nict.langrid.commons.io.StreamUtil;

/**
 * 
 * @author $Author: nakaguchi $
 * @version $Revision: 26403 $
 */
public class WorkFileUtil {
	/**
	 * リソースを指定されたディレクトリにコピーする。
	 * @param clazz リソースをロードするクラス
	 * @param resources リソース名の配列
	 * @param directory コピーするディレクトリ
	 * @throws IOException コピーに失敗した
	 */
	public static void storeResources(Class<?> clazz, Iterable<String> resources, File directory)
			throws IOException
	{
		for(String s : resources){
			InputStream is = clazz.getResourceAsStream(s);
	
			String fname = s;
			int i = fname.lastIndexOf("/");
			if(i != -1){
				fname = fname.substring(i);
			}
			fname = File.separator + fname;
			File file = new File(directory.getAbsolutePath() + fname);
			File parent = file.getParentFile();
			if(!parent.exists() && !parent.mkdirs()){
				throw new IOException("can't make directory: " + parent);
			}
			OutputStream os = new FileOutputStream(file);

			try{
				StreamUtil.transfer(is, os);
			} finally{
				is.close();
				os.close();
			}
		}
	}
}
