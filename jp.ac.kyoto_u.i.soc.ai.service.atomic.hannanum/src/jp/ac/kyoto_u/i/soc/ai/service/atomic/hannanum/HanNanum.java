/*
 * $Id: ICTCLAS.java 28009 2010-08-09 10:25:33Z Takao Nakaguchi $
 *
 * This is a program to wrap language resources as Web services.
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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.hannanum;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ko;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;
import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * HanNanumでの形態素解析ラッパー
 * 
 * @author Ryo Morimoto
 */
public class HanNanum
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService {
	
	/** Logger */
	private static Log _log = LogFactory.getLog(HanNanum.class);
	
	/** データディレクトリ */
	private String baseDir = null;
	
	/** 形態素解析用設定ファイル */
	private String morphAnalyzerConf = null;
	
	/** タグ付け用設定ファイル */
	private String posTaggerConf = null;
	
	/** 品詞Map */
	private static final HashMap<String, PartOfSpeech> partsOfSpeechMap = new HashMap<String, PartOfSpeech>();
	static {
		partsOfSpeechMap.put("ncpa", PartOfSpeech.noun);
		partsOfSpeechMap.put("ncps", PartOfSpeech.noun); 
		partsOfSpeechMap.put("ncn", PartOfSpeech.noun);
		partsOfSpeechMap.put("ncr", PartOfSpeech.noun); 
		partsOfSpeechMap.put("nqpa", PartOfSpeech.noun);
		partsOfSpeechMap.put("nqpb", PartOfSpeech.noun);
		partsOfSpeechMap.put("nqpc", PartOfSpeech.noun);
		partsOfSpeechMap.put("nqq", PartOfSpeech.noun);
		partsOfSpeechMap.put("nbu", PartOfSpeech.noun);
		partsOfSpeechMap.put("nbn", PartOfSpeech.noun); 
		partsOfSpeechMap.put("nbs", PartOfSpeech.noun); 
		partsOfSpeechMap.put("npp", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("npd", PartOfSpeech.noun_pronoun);
		partsOfSpeechMap.put("nnc", PartOfSpeech.noun);
		partsOfSpeechMap.put("nno", PartOfSpeech.noun);
		partsOfSpeechMap.put("nq", PartOfSpeech.noun);
		partsOfSpeechMap.put("pvd", PartOfSpeech.verb);
		partsOfSpeechMap.put("pvg", PartOfSpeech.verb);
		partsOfSpeechMap.put("pad", PartOfSpeech.adjective);
		partsOfSpeechMap.put("paa", PartOfSpeech.adjective);
		partsOfSpeechMap.put("mmd", PartOfSpeech.adverb);
		partsOfSpeechMap.put("mma", PartOfSpeech.adverb);
		partsOfSpeechMap.put("mad", PartOfSpeech.adverb);
		partsOfSpeechMap.put("maj", PartOfSpeech.adverb);
		partsOfSpeechMap.put("mag", PartOfSpeech.adverb);
		partsOfSpeechMap.put("px", PartOfSpeech.other);
		partsOfSpeechMap.put("jcs", PartOfSpeech.other);
		partsOfSpeechMap.put("jco", PartOfSpeech.other);
		partsOfSpeechMap.put("jcc", PartOfSpeech.other);
		partsOfSpeechMap.put("jcm", PartOfSpeech.other);
		partsOfSpeechMap.put("jcv", PartOfSpeech.other);
		partsOfSpeechMap.put("jca", PartOfSpeech.other);
		partsOfSpeechMap.put("jcj", PartOfSpeech.other);
		partsOfSpeechMap.put("jct", PartOfSpeech.other);
		partsOfSpeechMap.put("jcr", PartOfSpeech.other);
		partsOfSpeechMap.put("jxc", PartOfSpeech.other);
		partsOfSpeechMap.put("jxf", PartOfSpeech.other);
		partsOfSpeechMap.put("jp", PartOfSpeech.other);
		partsOfSpeechMap.put("ep", PartOfSpeech.other);
		partsOfSpeechMap.put("ecc", PartOfSpeech.other);
		partsOfSpeechMap.put("ecs", PartOfSpeech.other);
		partsOfSpeechMap.put("ecx", PartOfSpeech.other);
		partsOfSpeechMap.put("etn", PartOfSpeech.other);
		partsOfSpeechMap.put("etm", PartOfSpeech.other);
		partsOfSpeechMap.put("ef", PartOfSpeech.other);
		partsOfSpeechMap.put("xp", PartOfSpeech.other);
		partsOfSpeechMap.put("xsnu", PartOfSpeech.other);
		partsOfSpeechMap.put("xsnca", PartOfSpeech.other);
		partsOfSpeechMap.put("xsncc", PartOfSpeech.other);
		partsOfSpeechMap.put("xsna", PartOfSpeech.other);
		partsOfSpeechMap.put("xsns", PartOfSpeech.other);
		partsOfSpeechMap.put("xsnp", PartOfSpeech.other);
		partsOfSpeechMap.put("xsnx", PartOfSpeech.other);
		partsOfSpeechMap.put("xsvv", PartOfSpeech.other);
		partsOfSpeechMap.put("xsva", PartOfSpeech.other);
		partsOfSpeechMap.put("xsvn", PartOfSpeech.other);
		partsOfSpeechMap.put("xsms", PartOfSpeech.other);
		partsOfSpeechMap.put("xsmn", PartOfSpeech.other);
		partsOfSpeechMap.put("xsam", PartOfSpeech.other);
		partsOfSpeechMap.put("xsas", PartOfSpeech.other);
		partsOfSpeechMap.put("iwg", PartOfSpeech.other);
		partsOfSpeechMap.put("unk", PartOfSpeech.other);
		partsOfSpeechMap.put("xsv", PartOfSpeech.other);
		partsOfSpeechMap.put("xsn", PartOfSpeech.other);
		partsOfSpeechMap.put("xsm", PartOfSpeech.other);
		partsOfSpeechMap.put("xsa", PartOfSpeech.other);
		partsOfSpeechMap.put("ii", PartOfSpeech.other);
		partsOfSpeechMap.put("sp", PartOfSpeech.other);
		partsOfSpeechMap.put("sf", PartOfSpeech.other);
		partsOfSpeechMap.put("sl", PartOfSpeech.other);
		partsOfSpeechMap.put("sr", PartOfSpeech.other);
		partsOfSpeechMap.put("sd", PartOfSpeech.other);
		partsOfSpeechMap.put("se", PartOfSpeech.other);
		partsOfSpeechMap.put("su", PartOfSpeech.other);
		partsOfSpeechMap.put("sy", PartOfSpeech.other);
		partsOfSpeechMap.put("f", PartOfSpeech.other);
	}
	
	/**
	 * コンストラクタ
	 */
	public HanNanum() {
		setSupportedLanguageCollection(Arrays.asList(ko));
	}

	/**
	 * HanNanumでの韓国語の形態素解析結果から形態素オブジェクトリストを生成して返す。
	 * jhannanum.jarと専用のデータファイルや設定ファイルの配置が必要。
	 * 
	 * @param language 言語
	 * @param text 解析する文章
	 */
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		Collection<Morpheme> morphemes = null;
		Workflow workflow = new Workflow(baseDir);
		workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), morphAnalyzerConf);
		workflow.setPosTagger(new HMMTagger(), posTaggerConf);
		try {
			workflow.activateWorkflow(true);
			workflow.analyze(text);
			morphemes = parse(workflow.getResultOfDocument());
		} catch (Exception e) {
			_log.error("hannanum error. " + e.getLocalizedMessage());
			e.printStackTrace();
		} finally {
			if (workflow != null) workflow.close();
		}
		return morphemes;
	}
	
	/** タブ */
	private static final String TAB = "\t";
	
	/** 単語の区切り */
	private static final String WORD_SEPARATOR = "\\+";
	
	/** 単語と品詞の区切り */
	private static final String MORPH_SEPARATOR = "/";
	
	/**
	 * 解析結果から形態素オブジェクトリストを生成して返す
	 * <br />
	 * 解析結果：<br />
	 * <dl>
	 * <dt>지금부터</dt>
	 * <dd>지금/ncn+부터/jxc</dd>
	 * </dl>
	 * <br />
	 * @param parseText 解析結果テキスト
	 * @return 形態素オブジェクトリスト
	 */
	private Collection<Morpheme> parse(String parseText) {
//		System.out.println(parseText);
		Collection<Morpheme> morphemes = new ArrayList<Morpheme>();
		String[] lines = parseText.split(System.getProperty("line.separator"));
		boolean flag = false;
		for (String str : lines) {
			if (str.startsWith(TAB)) {
				str = str.replaceFirst(TAB, "");
				String[] morphStrs = str.split(WORD_SEPARATOR);
				flag = false;
				for (String morph : morphStrs) {
					if (morph.indexOf("/") == -1) flag = true;
				}
				if (flag) {
					morphemes.add(createMorpheme(str));
					continue;
				}
				for (String morph : morphStrs) {
					morphemes.add(createMorpheme(morph));
				}
			}
		}
		return morphemes;
	}
	
	/**
	 * 解析結果から形態素オブジェクトを生成して返す
	 * @param morph 解析結果 (単語/品詞)
	 * @return 形態素オブジェクト
	 */
	private Morpheme createMorpheme(String morph) {
		String[] parts = morph.split(MORPH_SEPARATOR);
		PartOfSpeech pos = PartOfSpeech.unknown;
		// セパレータ(/)の場合の対処
		if (parts.length != 2) {
			return new Morpheme(
					morph.substring(0, morph.lastIndexOf("/")),
					morph.substring(0, morph.lastIndexOf("/")),
					partsOfSpeechMap.get(morph.substring(morph.lastIndexOf("/") + 1, morph.length())));
		}
		if (partsOfSpeechMap.containsKey(parts[1])) {
			pos = partsOfSpeechMap.get(parts[1]);
		}
		return new Morpheme(parts[0], parts[0], pos);
	}
	
	/**
	 * データディレクトリをセットする
	 * @param baseDir データディレクトリ
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	
	/**
	 * 形態素解析用設定ファイルをセットする
	 * @param morphAnalyzerConf 形態素解析用設定ファイル
	 */
	public void setMorphAnalyzerConf(String morphAnalyzerConf) {
		this.morphAnalyzerConf = morphAnalyzerConf;
	}
	
	/**
	 * タグ付け用設定ファイルをセットする
	 * @param posTaggerConf タグ付け用設定ファイル
	 */
	public void setPosTaggerConf(String posTaggerConf) {
		this.posTaggerConf = posTaggerConf;
	}

}
