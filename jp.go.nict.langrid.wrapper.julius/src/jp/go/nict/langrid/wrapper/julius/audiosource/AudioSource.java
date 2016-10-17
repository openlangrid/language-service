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
package jp.go.nict.langrid.wrapper.julius.audiosource;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.go.nict.langrid.service_1_2.speech.Speech;

public abstract class AudioSource {

	/**
	 * map of mime type with voiceSource.
	 */
	static final Map<String, String> voiceSourceSet;

	static {
		Map<String, String> m = new HashMap<String, String>();
		voiceSourceSet = Collections.unmodifiableMap(m);

		m.put("audio/vnd.wave", "jp.go.nict.langrid.wrapper.julius.audiosource.RawPCMSource");
		m.put("audio/wav", "jp.go.nict.langrid.wrapper.julius.audiosource.RawPCMSource");
		m.put("audio/wave", "jp.go.nict.langrid.wrapper.julius.audiosource.RawPCMSource");
		m.put("audio/x-wav", "jp.go.nict.langrid.wrapper.julius.audiosource.RawPCMSource");

		// m.put("audio/x-mp3", "jp.go.nict.langrid.wrapper.julius.audiosource.Mp3Source");
	}

	protected AudioSource() {}

	/**
	 * returns stream of raw audio.
	 * @return input stream of raw audio
	 */
	public abstract InputStream getStream();


	/**
	 * set speech data.
	 * @param b Byte array of Source audio.
	 */
	protected abstract void setInputSource(byte[] b);

	/**
	 * returns
	 * @param speech
	 * @return 
	 * @throws 
	 */
	public static AudioSource getSource(Speech speech) throws IllegalArgumentException {
		AudioSource source;
		try {
			if (! voiceSourceSet.containsKey(speech.getAudioType())) {
				throw new IllegalArgumentException("Speech audio type [" + speech.getAudioType() + "] is not supported.");
			}
			source = (AudioSource) Class.forName(voiceSourceSet.get(speech.getAudioType())).newInstance();
			source.setInputSource(speech.getAudio());

		} catch (InstantiationException e) { // IllegalStateException is thrown when fails instantiation.
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
		
		return source;
	}

	public static String[] getSupportedAudioTypes() {
		return voiceSourceSet.keySet().toArray(new String[voiceSourceSet.size()]);
	}
}
