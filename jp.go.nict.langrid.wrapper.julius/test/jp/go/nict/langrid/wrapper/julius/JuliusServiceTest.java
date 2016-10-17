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
package jp.go.nict.langrid.wrapper.julius;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.speech.Speech;
import jp.go.nict.langrid.wrapper.julius.audiosource.AudioSource;
import jp.go.nict.langrid.wrapper.julius.modelconfig.ModelConfig;
import jp.go.nict.langrid.wrapper.julius.modelconfig.PropertyConfig;
import junit.framework.TestCase;

public class JuliusServiceTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		PropertyConfig.reloadConfig("lmotsukaresama");
	}

	public void testDoRecognize() throws Exception {
		JuliusService obj = new JuliusService();

		InputStream st = ClassLoader.getSystemResourceAsStream("otsukaresama_test.wav");
		StringBuilder buf = new StringBuilder();
		byte[] bi = new byte[2];
		while (st.read(bi) != -1) {
			buf.append(new String(bi, "ISO-8859-1"));
		}
		
		// ちなみに、「おつかれさまです。テストです。」と言ってます。
		assertEquals("細川さんはベーステストです。", obj.doRecognize(new Language("ja"), new Speech("light", "audio/x-wav", buf.toString().getBytes("ISO-8859-1"))));
	}

	public void testJuliusProcessRecognize() throws Exception {
		JuliusService srv = new JuliusService();
		JuliusService.JuliusProcess obj = srv.new JuliusProcess(srv.findConfig("standard"));

		InputStream st = ClassLoader.getSystemResourceAsStream("otsukaresama_test.wav");
		StringBuilder buf = new StringBuilder();
		byte[] bi = new byte[2];
		while (st.read(bi) != -1) {
			buf.append(new String(bi, "ISO-8859-1"));
		}
		
		assertEquals("追いつかれさんはベーステストです。", obj.recognize(AudioSource.getSource(new Speech("light", "audio/x-wav", buf.toString().getBytes("ISO-8859-1")))));
	}

	public void testJuliusProcessGetCommandString() throws Exception {
		JuliusService srv = new JuliusService();
		JuliusService.JuliusProcess obj = srv.new JuliusProcess(srv.findConfig("light"));
		
		assertEquals(srv.getJuliusPath() + " -C /home/haruo-31/Downloads/japanese-models/jconf/light.jconf -nolog -input stdin -output stdout", obj.getCommandString());
	}

	public void testGetConfigById() throws Exception {
		JuliusService srv = new JuliusService();
		String id = srv.getSupportedVoiceTypes()[0];
		ModelConfig conf = srv.findConfig(id);

		ResourceBundle bundle = ResourceBundle.getBundle("lm");
		String path = bundle.getString(id.concat(".path"));
		String language = bundle.getString(id.concat(".language"));
		String charset = bundle.getString(id.concat(".charset"));
		
		assertEquals(id, conf.getName());
		assertEquals(path, conf.getPath());
		assertEquals(language, conf.getLanguage());
		assertEquals(charset, conf.getRecognizedCharset());
	}

	public void testGetSupportedModelIds() throws Exception {
		JuliusService srv = new JuliusService();
		assertTrue(srv.getSupportedVoiceTypes().length > 0);
	}

	public void testGetSupportedLanguages() throws Exception {
		JuliusService srv = new JuliusService();
		assertTrue(srv.getSupportedLanguages().length > 0);
	}
	
	public void testReloadConfigParamString() throws Exception {
		PropertyConfig.reloadConfig("lmtest");
		JuliusService srv = new JuliusService();

		assertEquals(Arrays.asList(new String [] {"en", "ja"}).toString(),
				Arrays.asList(srv.getSupportedLanguages()).toString());

		assertEquals(Arrays.asList(new String[] {"englishresource", "testresource"}).toString(),
				Arrays.asList(srv.getSupportedVoiceTypes()).toString());
	}
	
	public void testReloadConfigParamProperties() throws Exception {
		Properties properties = new Properties();
		JuliusService srv = new JuliusService();
		
		properties.load(ClassLoader.getSystemResourceAsStream("lmtest.properties"));

		PropertyConfig.reloadConfig(properties);
		srv = new JuliusService();

		assertEquals(Arrays.asList(new String [] {"en", "ja"}).toString(),
				Arrays.asList(srv.getSupportedLanguages()).toString());

		assertEquals(Arrays.asList(new String[] {"englishresource", "testresource"}).toString(),
				Arrays.asList(srv.getSupportedVoiceTypes()).toString());
	}
}
