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
package jp.ac.kyoto_u.i.soc.ai.service.atomic.igo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.MorphologicalAnalysisService;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;
import net.reduls.igo.Tagger;

public class Igo
extends AbstractMorphologicalAnalysisService
implements MorphologicalAnalysisService{
	@Override
	protected Collection<Morpheme> doAnalyze(
			Language language, String text) throws InvalidParameterException,
			ProcessFailedException {
		init();
		List<Morpheme> ret = new ArrayList<Morpheme>();
		for(net.reduls.igo.Morpheme m : doAnalyze(text)){
			ret.add(createMorpheme(m.surface, m.feature));
		}
		return ret;
	}

	private static Morpheme createMorpheme(String surface, String feature){
		String[] v2 = feature.split(",");
		String posStr = v2[0];
		String posDetail = v2[1];
		String lemma;
		if(v2.length == 7){
			lemma = v2[4];
		} else if(v2.length == 9){
			lemma = v2[6];
		} else{
			 return new Morpheme(surface, surface, PartOfSpeech.unknown);
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
			lemma = surface;
		}
		return new Morpheme(surface, lemma, pos);
	}

	public void setDictPath(String dictPath) {
		this.dictPath = dictPath;
	}

	private synchronized List<net.reduls.igo.Morpheme> doAnalyze(String text)
	throws ProcessFailedException{
		init();
		return tagger.parse(text);
	}

	private void init() throws ProcessFailedException{
		if(tagger != null) return;
		try{
			tagger = new Tagger(dictPath);
		} catch(IOException e){
			throw new ProcessFailedException(e);
		}
	}

	private String dictPath = "ipadic";
	private Tagger tagger;
}
