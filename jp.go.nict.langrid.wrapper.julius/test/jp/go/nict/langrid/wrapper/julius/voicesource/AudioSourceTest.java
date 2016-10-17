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
package jp.go.nict.langrid.wrapper.julius.voicesource;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import jp.go.nict.langrid.service_1_2.speech.Speech;
import jp.go.nict.langrid.wrapper.julius.audiosource.AudioSource;
import jp.go.nict.langrid.wrapper.julius.audiosource.RawPCMSource;
import junit.framework.TestCase;

public class AudioSourceTest extends TestCase {
	public void testGetSupportedAudioTypes() {
		Set<String> expected = new TreeSet<String>(Arrays.asList(new String[] {"audio/wav", "audio/x-mp3"}));
		Set<String> actual = new TreeSet<String>(Arrays.asList(AudioSource.getSupportedAudioTypes()));

		assertEquals(expected.toString(), actual.toString());
	}

	public void testGetSource() {

		// can create wave source
		AudioSource src = AudioSource.getSource(new Speech("female", "audio/wav", new byte[] {}));
		assertEquals(RawPCMSource.class.getName(), src.getClass().getName());

		try { // cannot create unknown type of audio format
			AudioSource.getSource(new Speech("female", "audio/x-ogg", new byte[] {}));
			fail("unsupported type passed.");
		} catch (IllegalArgumentException e) {
			// success
		}
	}
}