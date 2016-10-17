/*
 * $Id: WordNet.java 29280 2012-03-29 04:25:23Z kamiya $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.wordnet;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static net.didion.jwnl.data.PointerType.HYPERNYM;
import static net.didion.jwnl.data.PointerType.HYPONYM;
import static net.didion.jwnl.data.PointerType.INSTANCES_HYPONYM;
import static net.didion.jwnl.data.PointerType.INSTANCE_HYPERNYM;
import static net.didion.jwnl.data.PointerType.MEMBER_HOLONYM;
import static net.didion.jwnl.data.PointerType.MEMBER_MERONYM;
import static net.didion.jwnl.data.PointerType.PART_HOLONYM;
import static net.didion.jwnl.data.PointerType.PART_MERONYM;
import static net.didion.jwnl.data.PointerType.SUBSTANCE_HOLONYM;
import static net.didion.jwnl.data.PointerType.SUBSTANCE_MERONYM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.conceptdictionary.Concept;
import jp.go.nict.langrid.service_1_2.conceptdictionary.Gloss;
import jp.go.nict.langrid.service_1_2.conceptdictionary.Lemma;
import jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.conceptdictionary.AbstractConceptDictionaryService;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

/**
 * WordNetのラッパー。
 * @author $Author: kamiya $
 * @version $Revision: 29280 $
 */
public class WordNet extends AbstractConceptDictionaryService{
	/**
	 * コンストラクタ。
	 */
	public WordNet(){
		super(Arrays.asList(supportedLanguage));
		maxConcepts = getInitParameterInt("wordnet.maxConcepts", 100);
		if(propertiesFile == null) {
			propertiesFile = getInitParameterString("wordnet.propertiesFile"
				, "/file_properties.xml");
		}
		synchronized(WordNet.class){
			if(!initialized){
				initialized = true;
				initialize(propertiesFile);
			}
			dictionary = Dictionary.getInstance();
		}
	}

	@Override
	protected Collection<Concept> doSearchConcepts(Language language,
			String word, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		if(exceptionAtStaticInitialize != null){
			throw new ProcessFailedException(exceptionAtStaticInitialize);
		}
		List<Concept> concepts = new ArrayList<Concept>();
		try{
			for(Object pos : POS.getAllPOS()){
				if(concepts.size() == maxConcepts) break;
				if(matchingMethod.equals(MatchingMethod.COMPLETE)){
					IndexWord iw = dictionary.getIndexWord((POS)pos, word);
					if(iw != null){
						addConcepts(iw, concepts, maxConcepts);
					}
				} else{
					Iterator<?> it = dictionary.getIndexWordIterator((POS)pos, word);
					while(it.hasNext()){
						if(concepts.size() == maxConcepts) break;
						IndexWord iw = (IndexWord)it.next();
						boolean matched = false;
						switch(matchingMethod){
							case PREFIX:
								matched = iw.getLemma().startsWith(word);
								break;
							case SUFFIX:
								matched = iw.getLemma().endsWith(word);
								break;
							case PARTIAL:
								matched = true;
								break;
						}
						if(matched){
							addConcepts(iw, concepts, maxConcepts);
						}
					}
				}
			}
			return concepts;
		} catch(JWNLException e){
			logger.log(Level.WARNING, "failed to search concepts", e);
			throw new ProcessFailedException(e);
		}
	}
	

	@Override
	protected Collection<Concept> doGetRelatedConcepts(Language language,
			String conceptId, ConceptualRelation relation)
			throws InvalidParameterException, ProcessFailedException
	{
		if(exceptionAtStaticInitialize != null){
			throw new ProcessFailedException(exceptionAtStaticInitialize);
		}

		List<Concept> concepts = new ArrayList<Concept>();

		// parameter checking and extracting
		String[] ids = conceptId.split(":");
		boolean valid = false;
		long offset = -1;
		POS pos = null;
		do{
			if(ids.length != 6) break;
			if(!ids[0].equals("urn")) break;
			if(!ids[1].equals("langrid")) break;
			if(!ids[2].equals("wordnet")) break;
			if(!ids[3].equals("concept")) break;
			if(ids[4].length() == 0) break;
			if(ids[5].length() == 0) break;
			pos = POS.getPOSForLabel(ids[4]);
			if(pos == null) break;
			try{
				offset = Long.parseLong(ids[5]);
			} catch(NumberFormatException e){
				break;
			}
			if(offset != -1){
				valid = true;
			}
		} while(false);
		if(!valid){
			throw new InvalidParameterException(
					"conceptId"
					, "\"" + conceptId + "\" must follow urn:langrid.wordnet:concept:${pos}:${offset}"
					);
		}

		try{
			Synset s = dictionary.getSynsetAt(pos, offset);
			if(s == null){
				throw new InvalidParameterException(
						"conceptId", offset + " is not valid offset."
						);
			}
			for(PointerType pt : relToPts.get(relation)){
				if(concepts.size() == maxConcepts) break;
				Pointer[] pointers = s.getPointers(pt);
				for(Pointer p : pointers){
					if(concepts.size() == maxConcepts) break;
					addConcept(p, concepts);
				}
			}
		} catch(JWNLException e){
			logger.log(Level.WARNING, "failed to search concepts", e);
			throw new ProcessFailedException(e);
		}
		return concepts;
	}

	private void addConcepts(
			IndexWord indexWord, List<Concept> concepts, int maxCount)
	throws JWNLException
	{
		int n = Math.min(
				indexWord.getSenseCount()
				, maxCount - concepts.size()
				);
		for(int i = 0; i < n; i++){
			addConcept(
					indexWord.getPOS(), indexWord.getSynsetOffsets()[i]
					, indexWord.getSense(i + 1), concepts
					);
		}
	}

	private void addConcept(Pointer pointer, List<Concept> concepts)
	throws JWNLException
	{
		addConcept(
				pointer.getTargetPOS(), pointer.getTargetOffset()
				, pointer.getTargetSynset(), concepts
				);
	}

	private void addConcept(POS pos, long offset, Synset synset, List<Concept> concepts)
	throws JWNLException
	{
		Concept c = new Concept();
		c.setConceptId(makeConceptId(offset, pos));
		setConcept(synset, c);
		concepts.add(c);
	}

	private String makeConceptId(long offset, POS pos){
		return "urn:langrid:wordnet:concept:"
				+ pos.getLabel()
				+ ":"
				+ offset 
				;
	}

	private void setConcept(Synset synset, Concept concept)
	throws JWNLException
	{
		concept.setPartOfSpeech(posToPos.get(synset.getPOS()).name());

		List<Lemma> lemmas = new ArrayList<Lemma>();
		for(Word w: synset.getWords()){
			lemmas.add(new Lemma(
					w.getLemma().replace("_", " ")
					, supportedLanguage.getCode()
					));
		}
		concept.setSynset(lemmas.toArray(new Lemma[]{}));

		List<Gloss> glosses = new ArrayList<Gloss>();
		for(String gloss : synset.getGloss().split(";")){
			gloss = gloss.trim();
			if(gloss.startsWith("\"")) continue;
			glosses.add(new Gloss(gloss, supportedLanguage.getCode()));
		}
		concept.setGlosses(glosses.toArray(new Gloss[]{}));

		Set<String> relations = new HashSet<String>();
		for(Pointer p : synset.getPointers()){
			ConceptualRelation rel = ptToRel.get(p.getType());
			if(rel != null){
				relations.add(rel.name());
			}
		}
		concept.setRelations(relations.toArray(new String[]{}));
	}

	private static synchronized void initialize(String propertiesFile){
		try{
			JWNL.initialize(WordNet.class.getResourceAsStream(propertiesFile));
		} catch(JWNLException e){
			logger.log(Level.SEVERE, "failed to initialize JWNL.", e);
			exceptionAtStaticInitialize = e;
		}
		posToPos.put(POS.ADJECTIVE, PartOfSpeech.adjective);
		posToPos.put(POS.ADVERB, PartOfSpeech.adverb);
		posToPos.put(POS.NOUN, PartOfSpeech.noun);
		posToPos.put(POS.VERB, PartOfSpeech.verb);
		relToPts.put(ConceptualRelation.HYPERNYMS, Arrays.asList(
				HYPERNYM, INSTANCE_HYPERNYM
				));
		relToPts.put(ConceptualRelation.HYPONYMS, Arrays.asList(
				HYPONYM, INSTANCES_HYPONYM
				));
		relToPts.put(ConceptualRelation.MERONYMS, Arrays.asList(
				MEMBER_MERONYM, PART_MERONYM, SUBSTANCE_MERONYM
				));
		relToPts.put(ConceptualRelation.HOLONYMS, Arrays.asList(
				MEMBER_HOLONYM, PART_HOLONYM, SUBSTANCE_HOLONYM
				));
		ptToRel.put(HYPERNYM, ConceptualRelation.HYPERNYMS);
		ptToRel.put(INSTANCE_HYPERNYM, ConceptualRelation.HYPERNYMS);
		ptToRel.put(HYPONYM, ConceptualRelation.HYPONYMS);
		ptToRel.put(INSTANCES_HYPONYM, ConceptualRelation.HYPONYMS);
		ptToRel.put(MEMBER_MERONYM, ConceptualRelation.MERONYMS);
		ptToRel.put(PART_MERONYM, ConceptualRelation.MERONYMS);
		ptToRel.put(SUBSTANCE_MERONYM, ConceptualRelation.MERONYMS);
		ptToRel.put(MEMBER_HOLONYM, ConceptualRelation.HOLONYMS);
		ptToRel.put(PART_HOLONYM, ConceptualRelation.HOLONYMS);
		ptToRel.put(SUBSTANCE_HOLONYM, ConceptualRelation.HOLONYMS);
	}

	private String propertiesFile;
	private int maxConcepts;
	private Dictionary dictionary;
	private static Exception exceptionAtStaticInitialize;
	private static Language supportedLanguage = en;
	private static Map<POS, PartOfSpeech> posToPos = new HashMap<POS, PartOfSpeech>();
	private static Map<ConceptualRelation, List<PointerType>> relToPts
			= new HashMap<ConceptualRelation, List<PointerType>>();
	private static Map<PointerType, ConceptualRelation> ptToRel
			= new HashMap<PointerType, ConceptualRelation>();
	private static boolean initialized = false;
	private static Logger logger = Logger.getLogger(WordNet.class.getName());
}
