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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.openmary;


import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.de;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.te;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.tr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.speech.Speech;
import jp.go.nict.langrid.wrapper.ws_1_2.texttospeech.AbstractTextToSpeechService;
import marytts.client.MaryClient;
import marytts.client.http.Address;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * OpenMaryでの音声合成ラッパー
 * 
 * @author Ryo Morimoto
 */
public class OpenMary extends AbstractTextToSpeechService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(OpenMary.class);
	
	/** 入力タイプ */
	private String inputType = null;
	
	/** 出力タイプ */
	private String outputType = null;
	
	/** ホスト名 */
	private static final String SERVER_HOST = "cling.dfki.uni-sb.de";
	
	/** ポート番号 */
	private static final int SERVER_PORT = 59125;
	
	/** オーディオタイプ：MP3 */
	private static final String TYPE_MP3 = "audio/mp3";
	
	/**
	 * コンストラクタ
	 */
	public OpenMary() {
		setSupportedAudioTypes("audio/wav", "audio/mp3");
		setSupportedLanguageCollection(Arrays.asList(en, de, te, tr));
		setSupportedVoiceTypes("woman");
	}

	/**
	 * OpenMaryでの音声合成を行い、実行結果のバイナリを返却する
	 * @param language 言語
	 * @param voiceType 合成された音声の声
	 * @param audioType 合成される音声ファイルの形式
	 * @return 音声オブジェクト
	 */
	@Override
	protected Speech doSpeak(Language language, String text, String voiceType,
			String audioType) throws InvalidParameterException,
			ProcessFailedException {
		
		Speech speech = null;
		try {
		
			String serverHost = System.getProperty("server.host", SERVER_HOST);
			int serverPort = Integer.getInteger("server.port", SERVER_PORT).intValue();
			MaryClient mary = MaryClient.getMaryClient(new Address(serverHost, serverPort));
			
			String defaultVoiceName = null;
			ByteArrayOutputStream baStream = new ByteArrayOutputStream();
			mary.process(text, inputType, outputType, getLocale(language), getAudioType(audioType), defaultVoiceName, baStream);
			
			speech = new Speech(voiceType, audioType, baStream.toByteArray());
		
		} catch (IOException e) {
			_log.error("openmary error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("openmary error : " + e.getLocalizedMessage());
		}
		return speech;
	}
	
	/**
	 * オーディオタイプを返す
	 * @param audioType 指定されたオーディオタイプ
	 * @return audio/mp3であれば MP3、それ以外は WAVE
	 */
	private String getAudioType(String audioType) {
		if (TYPE_MP3.equalsIgnoreCase(audioType)) {
			return "MP3";
		}
		return "WAVE";
	}
	
	/**
	 * ロケールを返す
	 * @param lang 言語
	 * @return 言語コード
	 */
	private String getLocale(Language lang) {
		if (lang.equals(en)) {
			return "en-US";
		}
		return lang.getCode();
	}
	
	/**
	 * 入力タイプをセットする
	 * @param inputType 入力タイプ
	 */
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	/**
	 * 出力タイプをセットする
	 * @param outputType 出力タイプ
	 */
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

}
