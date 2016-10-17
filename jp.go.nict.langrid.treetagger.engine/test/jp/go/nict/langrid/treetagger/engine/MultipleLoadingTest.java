/*
 * $Id: MultipleLoadingTest.java 26403 2008-08-28 08:12:22Z nakaguchi $
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
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

public class MultipleLoadingTest extends TestCase{
	public void test1() throws Exception{
		System.loadLibrary("TreeTaggerJNIEngine");
		System.loadLibrary("TreeTaggerJNIEngine2");
	}

	/**
	 * soのロードはJVMで1つなので、失敗する。
	 * @throws Exception
	 */
	public void test2() throws Exception{
		System.setProperty("java.library.path", "./lib");
		URL[] path = {
				new File("./lib").toURL()
				, new File("./build").toURL()
				, new File("./build.test").toURL()
				};
		go(new URLClassLoader(path), "load");
		go(new URLClassLoader(path), "load2");
	}

	private static void go(ClassLoader cl, String method) throws Exception{
		cl.loadClass(
				"jp.go.nict.langrid.engine.treetagger.MultipleLoadingTestLoader"
				)
				.getMethod(method)
				.invoke(null);
	}
}
