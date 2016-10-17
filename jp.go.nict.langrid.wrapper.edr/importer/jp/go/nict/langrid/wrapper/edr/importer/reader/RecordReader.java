/*
 * $Id: RecordReader.java 4109 2008-08-28 08:58:33Z nakaguchi $
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
package jp.go.nict.langrid.wrapper.edr.importer.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

public abstract class RecordReader<T>{
	public RecordReader(InputStreamReader streamReader)
	throws IOException
	{
		this.streamReader = streamReader;
		this.reader = new BufferedReader(streamReader);
		readCopyright();
	}

	public String getCopyright(){
		return copyright;
	}

	public int getCurrentLine(){
		return currentLine;
	}

	public T readRecord()
	throws IOException, ParseException
	{
		T record = null;
		while(record == null){
			String line = reader.readLine();
			if(line == null) return null;
			currentLine++;
			if(line.trim().length() == 0) return null;
			record = doBuildRecord(
					line.split("\t"), line.length()
					);
		}
		return record;
	}

	public void close()
	throws IOException
	{
		streamReader.close();
		reader.close();
	}

	protected abstract T doBuildRecord(
			String[] values, int lineLength)
	throws ParseException;

	private void readCopyright() throws IOException{
		copyright = reader.readLine();
		currentLine++;
	}

	private InputStreamReader streamReader;
	private BufferedReader reader;
	private String copyright;
	private int currentLine;
}
