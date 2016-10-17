/*
 * This is a program to wrap language resources as Web services.
 * 
 * Copyright (C) 2012 Department of Social Informatics, Kyoto University.
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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.sphinx;

import java.io.File;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class Test {
	
	public static void main(String[] args) {
		File tmp = new File("/var/tmp/sphinx.wav");
		ConfigurationManager cm = new ConfigurationManager(Sphinx.class.getResource("config.xml"));
		Recognizer recognizer = (Recognizer)cm.lookup("recognizer");
		recognizer.allocate();
		AudioFileDataSource dataSource = (AudioFileDataSource)cm.lookup("audioFileDataSource");
		dataSource.setAudioFile(tmp, null);
		Result result = null;
		StringBuffer resultStr = new StringBuffer();
		System.out.println(dataSource.getData());
		while ((result = recognizer.recognize()) != null) {
			resultStr.append(result.getBestFinalResultNoFiller());
			System.out.println(resultStr.toString());
		}
	}

}
