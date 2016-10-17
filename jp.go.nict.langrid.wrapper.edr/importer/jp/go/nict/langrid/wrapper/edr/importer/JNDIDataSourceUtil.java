/*
 * $Id: JNDIDataSourceUtil.java 4109 2008-08-28 08:58:33Z nakaguchi $
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
package jp.go.nict.langrid.wrapper.edr.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jp.go.nict.langrid.commons.util.ArrayUtil;

import org.apache.commons.dbcp.BasicDataSourceFactory;

public class JNDIDataSourceUtil {
	public static void setUpDataSourceAndJDNIResource()
	throws Exception, IOException, NamingException
	{
		Properties props = new Properties();
		props.put("maxActive", 100);
		props.put("maxIdle", 50);
		props.put("maxWait", 10000);
		props.put("dataSourceName", "java:comp/env/jdbc/langrid");
		loadFromResource(props, "/importer.properties");
		DataSource ds = BasicDataSourceFactory.createDataSource(props);

		Properties ctxProps = new Properties();
		loadFromResource(ctxProps, "/jndi.properties");
		String dataSourceName = props.getProperty("dataSourceName");
		createSubContextAndBind(
				new InitialContext(ctxProps)
				, dataSourceName
				, ds);

		//hibernateからも読み込めるように
		for(Map.Entry<Object, Object> entry : ctxProps.entrySet()){
			System.setProperty(
					entry.getKey().toString()
					, entry.getValue().toString()
					);
		}
	}

	private static void loadFromResource(Properties properties, String resource)
	throws IOException
	{
		InputStream s = Import.class.getResourceAsStream(resource);
		try{
			properties.load(s);
		} finally{
			s.close();
		}
	}

	private static void createSubContextAndBind(
			Context context, String name, Object value)
	throws NamingException
	{
		Context current = context;
		String[] subContextNames = name.split("\\/");
		if(subContextNames.length == 1) return;
		subContextNames = ArrayUtil.subArray(
				subContextNames, 0, subContextNames.length - 1
				);
		for(String n : subContextNames){
			current = current.createSubcontext(n);
		}
		context.bind(name, value);
	}
}
