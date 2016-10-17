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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.CollectionUtil;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.speech.Speech;
import jp.go.nict.langrid.wrapper.julius.audiosource.AudioSource;
import jp.go.nict.langrid.wrapper.julius.modelconfig.ModelConfig;
import jp.go.nict.langrid.wrapper.julius.modelconfig.PropertyConfig;
import jp.go.nict.langrid.wrapper.ws_1_2.speechrecognition.AbstractSpeechRecognitionService;

public class JuliusService extends AbstractSpeechRecognitionService {

	public JuliusService() {
		setModelConfig(PropertyConfig.getModelConfig());
	}

	/**
	 * Path for julius binary.
	 */
	private String juliusPath = "julius";

	/**
	 * Model configurations.
	 */
	private List<ModelConfig> modelConfig = null;

	@Override
	protected String doRecognize(Language language, Speech speech)
			throws InvalidParameterException, ProcessFailedException {
		ModelConfig conf;
		try {
			conf = findConfig(speech.getVoiceType());
		} catch (IllegalArgumentException e) {
			throw new InvalidParameterException("speech.voiceType", e.getMessage());
		}

		if (! language.getCode().equals(conf.getLanguage())) {
			throw new InvalidParameterException("language",
					"VoiceType [" + conf.getName() + "] does not support language [" + language + "]");
		}

		try {
			JuliusProcess proc = new JuliusProcess(conf);
			return proc.recognize(AudioSource.getSource(speech));
		} catch (Exception e) {
			throw new ProcessFailedException(e);
		}
	}
	
	@Override
	public String[] getSupportedAudioTypes() throws NoValidEndpointsException,
			ProcessFailedException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException {
		return AudioSource.getSupportedAudioTypes();
	}
	
	public String getJuliusPath() {
		return juliusPath;
	}

	public void setJuliusPath(String juliusPath) {
		this.juliusPath = juliusPath;
	}

	/**
	 * @return the modelConfig
	 */
	public List<ModelConfig> getModelConfig() {
		return modelConfig;
	}

	/**
	 * @param modelConfig the modelConfig to set
	 */
	public void setModelConfig(List<ModelConfig> modelConfig) {
		this.modelConfig = modelConfig;

		Set<String> langs = new TreeSet<String>();
		Set<String> voiceTypes = new TreeSet<String>();
		for (ModelConfig conf : modelConfig) {
			langs.add(conf.getLanguage());
			voiceTypes.add(conf.getName());
		}

		setSupportedLanguageCollection(CollectionUtil.collect(langs, new Transformer<String, Language>() {
			public Language transform(String value)
					throws TransformationException {
				try {
					return Language.parse(value);
				} catch (InvalidLanguageTagException e) {
					throw new TransformationException(e);
				}
			}
		}));
		setSupportedVoiceTypes(CollectionUtil.toArray(voiceTypes, String.class));
		setSupportedAudioTypes(AudioSource.getSupportedAudioTypes());
	}

	/**
	 * Returns a model config its specified.
	 * @param name name of config.
	 * @return a model config.
	 */
	protected ModelConfig findConfig(String name) {
		for (ModelConfig conf : getModelConfig()) {
			if (conf.getName().equals(name)) {
				return conf;
			}
		}
		throw new IllegalArgumentException("Model NAME [" + name + "] is not found in properties.");
	}

	/**
	 * Process manager for julius binary.
	 * @author haruo-31
	 */
	public class JuliusProcess {
		public static final String INPUT_STDIN = "-nolog -input stdin -output stdout";
		public static final String RESOURCE_OPTION_FLAG = "-C";
		public static final String EXTRA_PREFIX = "pass1_best:";
		
		public final ModelConfig config;
		private Process proc = null;

		public JuliusProcess(ModelConfig conf) {
			this.config = conf;
		}

		public InputStream getInputStream() throws IOException {
			return getProcess().getInputStream();
		}

		public InputStream getErrorStream() throws IOException {
			return getProcess().getErrorStream();
		}

		public String recognize(AudioSource source) throws IOException {
			try {
				byte[] data = new byte[4096];
				OutputStream out = this.getOutputStream();
				InputStream is = source.getStream();
				for (int len = is.read(data); len > 0; len = is.read(data)) {
					out.write(data, 0, len);
				}
				is.close();
				out.close();
				
				return this.readResultFromStream();
			} catch (IOException e) {
				BufferedReader stderr = new BufferedReader(new InputStreamReader(getProcess().getErrorStream(), "ISO-8859-1"));
				StringBuilder buf = new StringBuilder();
				String line;
				while ((line = stderr.readLine()) != null) {
					buf.append(line);
				}
				throw new IOException(buf.toString(), e);
			}
		}

		protected OutputStream getOutputStream() throws IOException {
			return getProcess().getOutputStream();
		}

		protected Process exec(String command) throws IOException {
			return Runtime.getRuntime().exec(command);
		}

		protected String readResultFromStream() throws IOException {
			BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream(), Charset.forName(config.getRecognizedCharset())));

			StringBuilder buf = new StringBuilder();

			int ic;
			while ((ic = in.read()) != -1) {
				char c = (char)(ic & 0xffff);
				if (c == '\r') {  // julius's tty output erases previous output in console.
					buf.setLength(0);
				} else if (c != ' ') {
					buf.append(c);
				}
			}

			String pass = buf.toString();

			return pass.substring(pass.indexOf(EXTRA_PREFIX) + EXTRA_PREFIX.length()).trim();
		}
		
		protected String getCommandString() {
			return new StringBuilder()
				.append(getJuliusPath()).append(" ")
				.append(RESOURCE_OPTION_FLAG).append(" ")
				.append(this.config.getPath()).append(" ")
				.append(INPUT_STDIN).toString();
		}

		private Process getProcess() throws IOException {
			if (this.proc == null) {
				this.proc = exec(getCommandString());
			}

			return this.proc;
		}
	}
}
