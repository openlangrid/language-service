//Copyright 2009 Carnegie-Mellon University
//Licensed under the Apache License, Version 2.0 (the "License"); 
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, 
//software distributed under the License is distributed on an
//"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//KIND, either express or implied. See the License for the
//specific language governing permissions and limitations
//under the License.


package edu.cmu.lti.jawjaw;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.lti.jawjaw.db.SenseDAO;
import edu.cmu.lti.jawjaw.db.SynlinkDAO;
import edu.cmu.lti.jawjaw.db.SynsetDefDAO;
import edu.cmu.lti.jawjaw.db.WordDAO;
import edu.cmu.lti.jawjaw.pobj.Lang;
import edu.cmu.lti.jawjaw.pobj.Link;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Sense;
import edu.cmu.lti.jawjaw.pobj.Synlink;
import edu.cmu.lti.jawjaw.pobj.SynsetDef;
import edu.cmu.lti.jawjaw.pobj.Word;

/**
 * Java Wrapper for Japanese WordNet.
 * 
 * This is a facade class that provides simple APIs for end users.
 * For doing more complicated stuff, use DAO classes under the package edu.cmu.lti.jawjaw.dao
 * 
 * @author Hideki Shima
 *
 */
public class JAWJAW {
	
	/**
	 * Finds hypernyms of a word. According to <a href="http://en.wikipedia.org/wiki/WordNet">wikipedia</a>, 
	 * <ul>
	 * <li>(Noun) hypernyms: Y is a hypernym of X if every X is a (kind of) Y (canine is a hypernym of dog)</li>
	 * <li>(Verb) hypernym: the verb Y is a hypernym of the verb X if the activity X is a (kind of) Y (travel is an hypernym of movement)</li>
	 * </ul>
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return hypernyms
	 */
	public static Set<String> findHypernyms( String word, POS pos ) {
		return findLinks(word, pos, Link.hype);
	}
	/**
	 * Finds hyponyms of a word. According to <a href="http://en.wikipedia.org/wiki/WordNet">wikipedia</a>, 
	 * <ul>
	 * <li>(Noun) hyponyms: Y is a hyponym of X if every Y is a (kind of) X (dog is a hyponym of canine)
	 * </ul>
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return hyponyms
	 */
	public static Set<String> findHyponyms( String word, POS pos ) {
		return findLinks(word, pos, Link.hypo);
	}
	/**
	 * Finds meronyms of a word. According to <a href="http://en.wikipedia.org/wiki/WordNet">wikipedia</a>, 
	 * <ul>
	 * <li>(Noun) meronym: Y is a meronym of X if Y is a part of X (window is a meronym of building)
	 * </ul>
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return meronyms
	 */
	public static Set<String> findMeronyms( String word, POS pos ) {
		Set<String> results = new LinkedHashSet<String>();
		results.addAll( findLinks(word, pos, Link.mmem) );
		results.addAll( findLinks(word, pos, Link.msub) );
		results.addAll( findLinks(word, pos, Link.mprt) );
		return results;
	}
	/**
	 * Finds holonyms of a word. According to <a href="http://en.wikipedia.org/wiki/WordNet">wikipedia</a>, 
	 * <ul>
	 * <li>(Noun) holonym: Y is a holonym of X if X is a part of Y (building is a holonym of window)
	 * </ul>
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return holonyms
	 */
	public static Set<String> findHolonyms( String word, POS pos ) {
		Set<String> results = new LinkedHashSet<String>();
		results.addAll( findLinks(word, pos, Link.hmem) );
		results.addAll( findLinks(word, pos, Link.hsub) );
		results.addAll( findLinks(word, pos, Link.hprt) );
		return results;
	}
	/**
	 * Finds instances of a word. 
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return instances
	 */
	public static Set<String> findInstances( String word, POS pos ) {
		return findLinks(word, pos, Link.inst);
	}
	/**
	 * Finds has-instance relations of a word.
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return has-instance relations
	 */
	public static Set<String> findHasInstances( String word, POS pos ) {
		return findLinks(word, pos, Link.hasi);
	}
	/**
	 * Get attributes of a word. 
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return attributes
	 */
	public static Set<String> findAttributes( String word, POS pos ) {
		return findLinks(word, pos, Link.attr);
	}
	/**
	 * Finds similar-to relations of an adjective(?). 
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return similar-to relations
	 */
	public static Set<String> findSimilarTo( String word, POS pos ) {
		return findLinks(word, pos, Link.sim);
	}
	/**
	 * Finds entailed consequents of a word. According to <a href="http://en.wikipedia.org/wiki/WordNet">wikipedia</a>, 
	 * <ul>
	 * <li>(Verb) entailment: the verb Y is entailed by X if by doing X you must be doing Y (to sleep is entailed by to snore)
	 * </ul>
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return consequents
	 */
	public static Set<String> findEntailments( String word, POS pos ) {
		return findLinks(word, pos, Link.enta);
	}
	/**
	 * Finds causes of a word. 
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return causes
	 */
	public static Set<String> findCauses( String word, POS pos ) {
		return findLinks(word, pos, Link.caus);
	}

	/**
	 * Finds words in see also relationship.
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return see also words
	 */
	public static Set<String> findSeeAlso( String word, POS pos ) {
		return findLinks(word, pos, Link.also);
	}

	/**
	 * Find synonyms of a word.
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return synonyms
	 */
	public static Set<String> findSynonyms( String word, POS pos ) {
		return findLinks(word, pos, Link.syns);
	}
	
	/**
	 * Find antonyms of a word.
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return antonyms
	 */
	public static Set<String> findSeeAntonyms( String word, POS pos ) {
		return findLinks(word, pos, Link.ants);
	}
	
	/**
	 * Get domains of a word. 
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return domains
	 */
	public static Set<String> findDomains( String word, POS pos ) {
		Set<String> results = new LinkedHashSet<String>();
		results.addAll( findLinks(word, pos, Link.dmnc) );
		results.addAll( findLinks(word, pos, Link.dmnr) );
		results.addAll( findLinks(word, pos, Link.dmnu) );
		return results;
	}
	/**
	 * Get in-domain relations of a word. 
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return in-domain relations
	 */
	public static Set<String> findInDomains( String word, POS pos ) {
		Set<String> results = new LinkedHashSet<String>();
		results.addAll( findLinks(word, pos, Link.dmtc) );
		results.addAll( findLinks(word, pos, Link.dmtr) );
		results.addAll( findLinks(word, pos, Link.dmtu) );
		return results;
	}
	
	/**
	 * Finds translations of a word.
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return translations
	 */
	public static Set<String> findTranslations( String word, POS pos ) {
		Set<String> results = new LinkedHashSet<String>();
		Set<String> synsets = wordToSynsets( word, pos );
		Lang anotherLang = findLang( word ).equals(Lang.jpn)?Lang.eng:Lang.jpn;
		for ( String synset : synsets ) {
			List<Sense> moreSenses = SenseDAO.findSensesBySynsetAndLang(synset, anotherLang);
			for ( Sense moreSense : moreSenses ) {
				Word translation = WordDAO.findWordByWordid( moreSense.getWordid() );
				results.add( translation.getLemma() );
			}
		}
		return results;
	}

	/**
	 * Finds definitions of a word. As of Japanese WordNet version 0.9, only English definitions are available.
	 * 
	 * @param word word in English or Japanese
	 * @param pos part of speech
	 * @return definitions in English
	 */
	public static Set<String> findDefinitions( String word, POS pos ) {
		Set<String> results = new LinkedHashSet<String>();
		Set<String> synsets = wordToSynsets( word, pos );
		// Currently, only English is available.
		Lang lang = Lang.eng; 
		
		for ( String synset : synsets ) {
			SynsetDef def = SynsetDefDAO.findSynsetDefBySynsetAndLang( synset, lang );
			results.add( def.getDef() );
		}
		return results;
	}
	
	private static Set<String> findLinks( String word, POS pos, Link link ) {
		Set<String> results = new LinkedHashSet<String>();
		Set<String> synsets = wordToSynsets( word, pos );
		Lang lang = findLang(word);
		for ( String synset : synsets ) {
			List<Synlink> synlinks = SynlinkDAO.findSynlinksBySynsetAndLink(synset, link);
			for ( Synlink synlink : synlinks ) {
				List<Sense> senses = SenseDAO.findSensesBySynsetAndLang(synlink.getSynset2(), lang);
				for ( Sense sense : senses ) {
					Word wordObj = WordDAO.findWordByWordid( sense.getWordid() );
					results.add( wordObj.getLemma() );
				}
			}
		}
		return results;
	}
	
	private static Lang findLang( String word ) {
		List<Word> words = WordDAO.findWordsByLemma(word);
		if ( words.size() > 0 ) {
			return words.get(0).getLang();
		} else {
			// default = jpn
			return Lang.jpn;
		}
	}
	
	private static Set<String> wordToSynsets( String word, POS pos ) {
		List<Word> words = WordDAO.findWordsByLemmaAndPos(word, pos);
		Set<String> results = new LinkedHashSet<String>();
		for ( Word wordObj : words ) {
			int wordid = wordObj.getWordid();
			List<Sense> senses = SenseDAO.findSensesByWordid( wordid );
			for ( Sense sense : senses ) {
				results.add( sense.getSynset() );
			}
		}
		return results;
	}
	
	
}
