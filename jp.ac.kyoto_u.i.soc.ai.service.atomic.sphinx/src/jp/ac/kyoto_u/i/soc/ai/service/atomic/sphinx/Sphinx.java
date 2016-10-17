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

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.speech.Speech;
import jp.go.nict.langrid.wrapper.ws_1_2.speechrecognition.AbstractSpeechRecognitionService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * CMUSphinxによる音声認識ラッパー
 * 
 * @author Ryo Morimoto
 */
public class Sphinx extends AbstractSpeechRecognitionService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(Sphinx.class);
	
	private static final String TMP_FILE = "/var/tmp/sphinx.wav";
	
	/**
	 * コンストラクタ
	 */
	public Sphinx() {
		setSupportedLanguageCollection(Arrays.asList(en));
		setSupportedAudioTypes(new String[] {"audio/wav"});
	}

	@Override
	protected String doRecognize(Language language, Speech speech)
			throws InvalidParameterException, ProcessFailedException {

		StringBuffer resultStr = new StringBuffer();
		File tmp = null;
		FileOutputStream fo = null;
		
		try {
			tmp = new File(TMP_FILE);
			fo = new FileOutputStream(tmp);
			fo.write(speech.getAudio());
//			for (byte b : speech.getAudio()) {
//				fo.write(b);
//			}
			fo.flush();
			fo.close();
			ConfigurationManager cm = new ConfigurationManager(Sphinx.class.getResource("config.xml"));
			Recognizer recognizer = (Recognizer)cm.lookup("recognizer");
			recognizer.allocate();
			AudioFileDataSource dataSource = (AudioFileDataSource)cm.lookup("audioFileDataSource");
			dataSource.setAudioFile(tmp, null);
			Result result = null;
			System.out.println(dataSource.getData());
			while ((result = recognizer.recognize()) != null) {
				resultStr.append(result.getBestFinalResultNoFiller());
				System.out.println(resultStr.toString());
			}
		} catch (Exception e) {
			_log.error("sphinx error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("sphinx error : " + e.getLocalizedMessage());
		} finally {
			if (fo != null) {
				try {
					fo.close();
				} catch (IOException e) {}
			}
		}
		return resultStr.toString();
	}

}
