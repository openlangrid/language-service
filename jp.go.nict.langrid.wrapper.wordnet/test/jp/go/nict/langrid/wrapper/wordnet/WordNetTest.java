/*
 * $Id: WordNetTest.java 27054 2009-03-24 06:10:25Z nakaguchi $
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

import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.noun;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.verb;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.adjective;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.adverb;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HYPERNYMS;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HYPONYMS;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HOLONYMS;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.MERONYMS;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.util.Arrays;

import jp.go.nict.langrid.commons.test.AssertUtil;
import jp.go.nict.langrid.commons.transformer.ToStringTransformer;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
import jp.go.nict.langrid.commons.ws.LocalServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.conceptdictionary.Concept;
import jp.go.nict.langrid.service_1_2.conceptdictionary.ConceptDictionaryService;
import jp.go.nict.langrid.service_1_2.conceptdictionary.Gloss;
import jp.go.nict.langrid.service_1_2.conceptdictionary.Lemma;
import jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import junit.framework.TestCase;

public class WordNetTest extends TestCase{
	public void test_searchConcepts_bank_COMPLETE() throws Exception{
		Concept[] actual = service.searchConcepts("en", "bank", "COMPLETE");
		assertEquals(bank_concepts.length, actual.length);
		for(int i = 0; i < actual.length; i++){
			assertConceptEquals(bank_concepts[i], actual[i], i);
		}
	}

	public void test_searchConcepts_bank_PREFIX() throws Exception{
		Concept[] actual = service.searchConcepts("en", "bank", "PREFIX");
		assertEquals(86, actual.length);
	}

	public void test_searchConcepts_bankbank_COMPLETE() throws Exception{
		AssertUtil.assertArrayEquals(
				new Concept[]{}
				, service.searchConcepts("en", "bankbank", "COMPLETE")
				);
	}

	public void test_searchConcepts_great_COMPLETE() throws Exception{
		Concept[] actual = service.searchConcepts("en", "great", "COMPLETE");
		assertEquals(great_concepts.length, actual.length);
		for(int i = 0; i < actual.length; i++){
			dumpConcept(actual[i]);
			assertConceptEquals(great_concepts[i], actual[i], i);
		}
	}

	public void test_searchConcepts_closely_COMPLETE() throws Exception{
		Concept[] expected = closely_concepts;
		Concept[] actual = service.searchConcepts("en", "closely", "COMPLETE");
		assertEquals(expected.length, actual.length);
		for(int i = 0; i < actual.length; i++){
			dumpConcept(actual[i]);
			assertConceptEquals(expected[i], actual[i], i);
		}
	}

	public void test_getRelatedConcepts_bank_noun_8_hyponym() throws Exception{
		Concept[] actual = service.getRelatedConcepts(
				"en", "urn:langrid:wordnet:concept:noun:4139859", "HYPONYMS");
		for(int i = 0; i < actual.length; i++){
			assertConceptEquals(bank_noun_8_hyponyms[i], actual[i], i);
		}
	}

	public void test_getRelatedConcepts_backsword() throws Exception{
		Concept[] actual = service.getRelatedConcepts(
				"en", "urn:langrid:wordnet:concept:noun:2771750", "HYPERNYMS");
		for(int i = 0; i < actual.length; i++){
			assertConceptEquals(banksword_noun_2_hypernyms[i], actual[i], i);
		}
	}

	private void dumpConcept(Concept c){
		System.out.println("id: " + c.getConceptId());
		System.out.println("synsets: ");
		for(Lemma l : c.getSynset()){
			System.out.println(
					"  lang: " + l.getLanguage()
					+ "  form: " + l.getLemmaForm()
					);
		}
		System.out.println("gloss: ");
		for(Gloss g : c.getGlosses()){
			System.out.println(
					"  lang: " + g.getLanguage()
					+ "  text: " + g.getGlossText()
					);
		}
		System.out.println("crels: " + Arrays.toString(c.getRelations()));
		System.out.println();
	}

	private static Concept concept(PartOfSpeech pos, long offset
			, Lemma[] synsets, Gloss[] glosses, String[] relations){
		return new Concept(
				"urn:langrid:wordnet:concept:"
				+ pos.name() + ":" + offset
				, pos.name(), synsets, glosses
				, relations
				);
	}

	private static Lemma[] synsets(final Language language, String... synsets){
		return ArrayUtil.collect(synsets, new Transformer<String, Lemma>(){
			public Lemma transform(String value){
				return new Lemma(value, language.getCode());
			}
			});
	}

	private static Gloss[] glosses(final Language language, String... glosses){
		return ArrayUtil.collect(glosses, new Transformer<String, Gloss>(){
			public Gloss transform(String value){
				return new Gloss(value, language.getCode());
			}
			});
	}

	private static String[] relations(ConceptualRelation... relations){
		return ArrayUtil.collect(
				relations, new ToStringTransformer<ConceptualRelation>()
				);
	}

	private static void assertConceptEquals(
			Concept expected, Concept actual, int index)
	{
		assertEquals(expected.getConceptId(), actual.getConceptId());
		assertEquals(expected.getPartOfSpeech(), actual.getPartOfSpeech());
		AssertUtil.assertArrayEqualsIgnoreOrder(
				index + " th element"
				, expected.getSynset(), actual.getSynset()
				);
		AssertUtil.assertArrayEqualsIgnoreOrder(
				index + " th element"
				, expected.getGlosses(), actual.getGlosses()
		);
		AssertUtil.assertArrayEqualsIgnoreOrder(
				index + " th element"
				, expected.getRelations(), actual.getRelations()
		);
	}

	@SuppressWarnings("unused")
	private static void assertConceptEquals(
			Concept expected, Concept actual)
	{
		assertEquals(expected.getConceptId(), actual.getConceptId());
		assertEquals(expected.getPartOfSpeech(), actual.getPartOfSpeech());
		AssertUtil.assertArrayEqualsIgnoreOrder(
				expected.getSynset(), actual.getSynset()
				);
		AssertUtil.assertArrayEqualsIgnoreOrder(
				expected.getGlosses(), actual.getGlosses()
		);
		AssertUtil.assertArrayEqualsIgnoreOrder(
				expected.getRelations(), actual.getRelations()
		);
	}

	private Concept[] bank_concepts = {
			concept(noun, 9213565
					, synsets(en, "bank")
					, glosses(en, "sloping land (especially the slope beside a body of water)")
					, relations(HYPERNYMS, HYPONYMS)
					)
			, concept(noun, 8420278
					, synsets(en, "depository financial institution", "bank"
							, "banking concern", "banking company")
					, glosses(en, "a financial institution that accepts deposits and channels the money into lending activities")
					, relations(HYPERNYMS, HOLONYMS, HYPONYMS)
					)
			, concept(noun, 9213434
					, synsets(en, "bank")
					, glosses(en, "a long ridge or pile")
					, relations(HYPERNYMS, HYPONYMS)
					)
			, concept(noun, 8462066
					, synsets(en, "bank")
					, glosses(en, "an arrangement of similar objects in a row or in tiers")
					, relations(HYPERNYMS)
					)
			, concept(noun, 13368318
					, synsets(en, "bank")
					, glosses(en, "a supply or stock held in reserve for future use (especially in emergencies)")
					, relations(HYPERNYMS, HYPONYMS)
					)
			, concept(noun, 13356402
					, synsets(en, "bank")
					, glosses(en, "the funds held by a gambling house or the dealer in some gambling games")
					, relations(HYPERNYMS)
					)
			, concept(noun, 9213828
					, synsets(en, "bank", "cant", "camber")
					, glosses(en, "a slope in the turn of a road or track"
							, "the outside is higher than the inside in order to reduce the effects of centrifugal force")
					, relations(HYPERNYMS)
					)
			, concept(noun, 4139859
					, synsets(en, "savings bank", "coin bank", "money box", "bank")
					, glosses(en, "a container (usually with a slot in the top) for keeping money at home")
					, relations(HYPERNYMS, HYPONYMS)
					)
			, concept(noun, 2787772
					, synsets(en, "bank", "bank building")
					, glosses(en, "a building in which the business of banking transacted")
					, relations(HYPERNYMS, MERONYMS)
					)
			, concept(noun, 169305
					, synsets(en, "bank")
					, glosses(en, "a flight maneuver"
							, "aircraft tips laterally about its longitudinal axis (especially in turning)")
					, relations(HYPERNYMS, HYPONYMS)
					)
			, concept(verb, 2039413
					, synsets(en, "bank")
					, glosses(en, "tip laterally")
					, relations(HYPERNYMS)
					)
			, concept(verb, 1587705
					, synsets(en, "bank")
					, glosses(en, "enclose with a bank")
					, relations(HYPERNYMS)
					)
			, concept(verb, 2343374
					, synsets(en, "bank")
					, glosses(en, "do business with a bank or keep an account at a bank")
					, relations(HYPERNYMS)
					)
			, concept(verb, 2343252
					, synsets(en, "bank")
					, glosses(en, "act as the banker in a game or in gambling")
					, relations(HYPERNYMS)
					)
			, concept(verb, 2343056
					, synsets(en, "bank")
					, glosses(en, "be in the banking business")
					, relations(HYPERNYMS)
					)
			, concept(verb, 2310855
					, synsets(en, "deposit", "bank")
					, glosses(en, "put into a bank account")
					, relations(HYPERNYMS, HYPONYMS)
					)
			, concept(verb, 1234793
					, synsets(en, "bank")
					, glosses(en, "cover with ashes so to control the rate of burning")
					, relations(HYPERNYMS)
					)
			, concept(verb, 688377
					, synsets(en, "trust", "swear", "rely", "bank")
					, glosses(en, "have confidence or faith in")
					, relations(HYPERNYMS, HYPONYMS)
					)
			};
	private Concept[] bank_noun_8_hyponyms = {
			concept(noun, 3935335
					, synsets(en, "piggy bank", "penny bank")
					, glosses(en, "a child's coin bank (often shaped like a pig)")
					, relations(HYPERNYMS)
					)
			};
	private Concept[] banksword_noun_2_hypernyms = {
			concept(noun, 4373894
					, synsets(en, "sword", "blade", "brand", "steel")
					, glosses(en, "a cutting or thrusting weapon that has a long metal blade and a hilt with a hand guard")
					, relations(HYPONYMS, MERONYMS, HYPERNYMS)
					)
			};
	private Concept[] great_concepts = {
			concept(noun, 10145081
					, synsets(en, "great")
					, glosses(en, "a person who has achieved distinction and honor in some field")
					, relations(HYPERNYMS)
					)
			, concept(adjective, 1386883
					, synsets(en, "great")
					, glosses(en, "relatively large in size or number or extent", "larger than others of its kind")
					, relations()
					)
			, concept(adjective, 1278818
					, synsets(en, "great", "outstanding")
					, glosses(en, "of major significance or importance")
					, relations()
					)
			, concept(adjective, 1677433
					, synsets(en, "great")
					, glosses(en, "remarkable or out of the ordinary in degree or magnitude or effect")
					, relations()
					)
			, concept(adjective, 1123879
					, synsets(en, "bang-up", "bully", "corking"
							, "cracking", "dandy", "great", "groovy", "keen"
							, "neat", "nifty", "not bad", "peachy", "slap-up"
							, "swell", "smashing")
					, glosses(en, "very good")
					, relations()
					)
			, concept(adjective, 1467919
					, synsets(en, "capital", "great", "majuscule")
					, glosses(en, "uppercase")
					, relations()
					)
			, concept(adjective, 173391
					, synsets(en, "big", "enceinte", "expectant", "gravid"
							, "great", "large", "heavy", "with child")
					, glosses(en, "in an advanced stage of pregnancy")
					, relations()
					)
			, 
			};
	private Concept[] closely_concepts = {
			concept(adverb, 160440
					, synsets(en, "closely")
					, glosses(en, "in a close relation or position in time or space")
					, relations()
					)
			, concept(adverb, 505226
					, synsets(en, "close", "closely", "tight")
					, glosses(en, "in an attentive manner")
					, relations()
					)
			, concept(adverb, 160659
					, synsets(en, "closely", "intimately", "nearly")
					, glosses(en, "in a close manner")
					, relations()
					)
			, 
			};

	private ConceptDictionaryService service = new WordNet();
	static{
		System.setProperty(
				"wordnetPropertiesFile", "/test_file_properties.xml"
				);
	}
}
