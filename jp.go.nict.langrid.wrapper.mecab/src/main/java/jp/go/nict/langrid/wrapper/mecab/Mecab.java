/*
 * $Id: Mecab.java 29018 2012-02-08 14:20:01Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.mecab;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

/**
 * 京都大学情報学研究科－日本電信電話株式会社コミュニケーション科学基礎研究所共同研究ユニットプロジェクトを
 * 通じて開発されたオープンソース形態素解析エンジン「MeCab」のラッパーです。
 * <p>以下の初期化パラメータをとります。</p>
 * <ul>
 * <li>mecabBin - mecabの実行バイナリへのフルパス(デフォルト: /usr/bin/mecab)</li>
 * <li>mecabCharset - mecabとデータをやりとりする際のキャラクタセット(デフォルト: UTF-8)</li>
 * <li>mecabPattern - mecabの結果を解析する際に使用する正規表現(デフォルト: ([^\\t]+)\\t([^,]+),([^,]+),[^,]+,[^,]+,[^,]+,[^,]+(,([^,]+))? </li>
 * </ul>
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 29018 $
 */
public class Mecab extends AbstractMorphologicalAnalysisService {
	/**
	 * Mecabのコンストラクタ。
	 * @param context サービスコンテキスト
	 */
	public Mecab() {
		mecabPath = getInitParameterString("mecabBin", "/usr/bin/mecab");
		mecabEncoding = getInitParameterString("mecabCharset", "UTF-8");
		setSupportedLanguageCollection(Arrays.asList(ja));
	}

	public void setMecabPath(String mecabPath) {
		this.mecabPath = mecabPath;
	}

	public void setMecabEncoding(String mecabEncoding) {
		this.mecabEncoding = mecabEncoding;
	}

	public void setDicDir(String dicDir) {
		this.dicDir = dicDir;
	}
	
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		if(bootupException != null){
			throw bootupException;
		}
		try{
			// execute mecab
			String[] args = null;
			if(dicDir != null){
				args = new String[]{mecabPath, "-d", dicDir};
			} else{
				args = new String[]{mecabPath};
			}
			ProcessBuilder pb = new ProcessBuilder(args);
			Process p = pb.start();

			// write text to analyze
			OutputStreamWriter w = new OutputStreamWriter(
					p.getOutputStream(), mecabEncoding);
			try{
				w.write(text);
				w.write("\r\n");
				w.close();
				// read result
				return analyzeResult(p.getInputStream(), mecabEncoding);
			} finally{
				StreamUtil.transfer(p.getErrorStream(), System.out);
				p.destroy();
			}
		} catch(IOException e){
			logger.log(Level.SEVERE, "failed to execute service.", e);
			throw new ProcessFailedException(e);
		}
	}

	static List<Morpheme> analyzeResult(InputStream stream, String charset)
	throws IOException{
		List<Morpheme> result = new ArrayList<Morpheme>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				stream, charset));
		String line = null;
		while((line = br.readLine()) != null){
			line = line.trim();
			if(line.length() == 0) continue;
			String[] v = line.split("\t");
			if(v.length != 2) continue;
			String word = v[0];
			String[] v2 = v[1].split(",");
			String posStr = v2[0];
			String posDetail = v2[1];
			String lemma;
			if(v2.length == 7){
				lemma = v2[4];
			} else if(v2.length == 9){
				lemma = v2[6];
			} else{
				continue;
			}
			PartOfSpeech pos = PartOfSpeech.unknown;
			if(posStr.equals("名詞")){
				if(posDetail.equals("固有名詞")){
					pos = PartOfSpeech.noun_proper;
				} else if(posDetail.equals("人名")){
					pos = PartOfSpeech.noun_proper;
				} else if(posDetail.equals("組織名")){
					pos = PartOfSpeech.noun_proper;
				} else if(posDetail.equals("地名")){
					pos = PartOfSpeech.noun_proper;
				} else if(posDetail.equals("一般")){
					pos = PartOfSpeech.noun_common;
				} else if(posDetail.equals("普通名詞")){
					pos = PartOfSpeech.noun_common;
				} else if(posDetail.equals("*")){
					pos = PartOfSpeech.noun;
				} else{
					pos = PartOfSpeech.noun_other;
				}
			} else if(posStr.equals("動詞")){
				pos = PartOfSpeech.verb;
			} else if(posStr.equals("形容詞")){
				pos = PartOfSpeech.adjective;
			} else if(posStr.equals("副詞")){
				pos = PartOfSpeech.adverb;
			} else if(posStr.equals("*")){
				//unknown
				pos = PartOfSpeech.unknown;
			} else{
				//other
				pos = PartOfSpeech.other;
			}
			if(lemma.equals("*")){
				lemma = word;
			}
			result.add(new Morpheme(word, lemma, pos));
		}
		return result;
	}

	private ProcessFailedException bootupException;
	private String mecabPath;
	private String mecabEncoding;
	private String dicDir;

	private static Logger logger = Logger.getLogger(Mecab.class.getName());
}
