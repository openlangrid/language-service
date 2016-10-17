package jp.go.nict.langrid.wrapper.wordnetja;

import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HYPERNYMS;

import jp.go.nict.langrid.service_1_2.conceptdictionary.Concept;
import jp.go.nict.langrid.service_1_2.conceptdictionary.ConceptDictionaryService;
import jp.go.nict.langrid.wrapper.wordnetja.WordNetJA;
import junit.framework.TestCase;

public class WordNetJATest extends TestCase{
	public void setUp(){
		System.setProperty("wordnetja.dbPath", "./data/wnjpn-0.9.db");
		service = new WordNetJA();
	}

	public void test_searchConcepts_company_COMPLETE() throws Exception{
		System.out.println("-- concepts of 会社 --");
		for(Concept c : service.searchConcepts("ja", "会社", "COMPLETE")){
			System.out.println(c);
		}
	}

	public void test_getRelatedConcepts_company_COMPLETE() throws Exception{
		System.out.println("-- hypernyms of 会社 --");
		Concept concept = service.searchConcepts("ja", "会社", "COMPLETE")[0];
		for(Concept c : service.getRelatedConcepts(
				"ja", concept.getConceptId(), HYPERNYMS.name())){
			System.out.println(c);
		}
	}

	private ConceptDictionaryService service;
}
