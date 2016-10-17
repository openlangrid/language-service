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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.openjtalk;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.speech.Speech;
import jp.go.nict.langrid.wrapper.ws_1_2.texttospeech.AbstractTextToSpeechService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * OpenJTalkでの音声合成ラッパー
 * 
 * @author Ryo Morimoto
 */
public class OpenJTalk extends AbstractTextToSpeechService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(OpenJTalk.class);
	
	/** 作業ディレクトリ */
	private String path = null;
	
	/** 文字コード */
	private static final String CHAR_SET = "UTF-8";
	
	/** 一時ファイル */
	private static final String TMP_FILE = "/var/tmp/output.wav";
	
	/**
	 * コンストラクタ
	 */
	public OpenJTalk() {
		setSupportedLanguageCollection(Arrays.asList(ja));
		setSupportedVoiceTypes("man");
		setSupportedAudioTypes("audio/wav");
	}

	/**
	 * OpenJtalkによる音声合成を実行し、作成された音声ファイルのデータを返す。
	 * <br />
	 * @param language	言語
	 * @param text	原文
	 * @param voiceType 合成された音声の声
	 * @param audioType 合成される音声ファイルの形式
	 * @return 音声オブジェクト
	 */
	@Override
	protected Speech doSpeak(Language language, String text, String voiceType,
			String audioType) throws InvalidParameterException,
			ProcessFailedException {
		
		Process proc = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		File tmp = null;
		FileInputStream fis = null;
		byte[] bytes = null;
		
		try {
			
			String[] args = {
					"open_jtalk",
					"-td",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/tree-dur.inf",
					"-tf",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/tree-lf0.inf",
					"-tm",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/tree-mgc.inf",
					"-md",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/dur.pdf",
					"-mf",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/lf0.pdf",
					"-mm",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/mgc.pdf",
					"-df",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/lf0.win1",
					"-df",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/lf0.win2",
					"-df",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/lf0.win3",
					"-dm",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/mgc.win1",
					"-dm",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/mgc.win2",
					"-dm",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/mgc.win3",
					"-ef",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/tree-gv-lf0.inf",
					"-em",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/tree-gv-mgc.inf",
					"-cf",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/gv-lf0.pdf",
					"-cm",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/gv-mgc.pdf",
					"-k",
					"/usr/local/share/hts_voice/hts_voice_nitech_jp_atr503_m001-1.04/gv-switch.inf",
					"-x",
					"/usr/local/share/open_jtalk/open_jtalk_dic_utf_8-1.05/",
					"-ow",
					TMP_FILE
			};
			
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(path));
			proc = pb.start();
			
			// 出力ストリームを取得する
			bw = new BufferedWriter(new OutputStreamWriter(
					proc.getOutputStream(), CHAR_SET));
			bw.write(text);
			bw.write(System.getProperty("line.separator"));
			bw.flush();
			bw.close();
			proc.waitFor();
			
			tmp = new File(TMP_FILE);
			fis = new FileInputStream(tmp);
			FileChannel channel = fis.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
			channel.read(buffer);
			buffer.clear();
			bytes = new byte[buffer.capacity()];
			for (int i=0; i<buffer.capacity(); i++) {
//				System.out.println(i + " : " + buffer.get(i));
				bytes[i] = buffer.get(i);
			}
			channel.close();
			
		} catch (Exception e) {
			_log.error("jtalk error. " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ProcessFailedException("jtalk error : " + e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {}
			}
			if (proc != null) {
				proc.destroy();
				try {
					proc.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				proc = null;
			}
			if (tmp != null && !tmp.delete()) {
				tmp.deleteOnExit();
			}
		}
		
		return new Speech(voiceType, audioType, bytes);

	}
	
	/**
	 * 作業ディレクトリをセットする
	 * @param path 作業ディレクトリ
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
