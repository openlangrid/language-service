package jp.go.nict.langrid.wrapper.stanfordpostagger;

import java.util.Arrays;
import java.util.List;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import junit.framework.TestCase;

public class SLLPosTaggerTest extends TestCase {

	private StanfordPosTagger tagger;

	protected void setUp() throws Exception {
		super.setUp();
		tagger = new StanfordPosTagger();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		tagger = new StanfordPosTagger();
	}

	public void testCreateMorpheme() {
		String word = "hoge foo";
		String expect_word = "hoge foo";
		String posStr = "NNP";

		Morpheme morpheme = StanfordPosTagger.createMorpheme(word, posStr);
		assertEquals(expect_word, morpheme.getWord());
		assertEquals(expect_word, morpheme.getLemma());
		assertEquals(PartOfSpeech.noun_proper, morpheme.getPartOfSpeech());

		morpheme = StanfordPosTagger.createMorpheme(word, "NNP");
		assertEquals(PartOfSpeech.noun_proper, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "NNPS");
		assertEquals(PartOfSpeech.noun_proper, morpheme.getPartOfSpeech());

		morpheme = StanfordPosTagger.createMorpheme(word, "NN");
		assertEquals(PartOfSpeech.noun_common, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "NNS");
		assertEquals(PartOfSpeech.noun_common, morpheme.getPartOfSpeech());
		
		morpheme = StanfordPosTagger.createMorpheme(word, "PRP");
		assertEquals(PartOfSpeech.noun_pronoun, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "PRP$");
		assertEquals(PartOfSpeech.noun_pronoun, morpheme.getPartOfSpeech());
		
		morpheme = StanfordPosTagger.createMorpheme(word, "VB");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "VBD");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "VBG");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "VBN");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "VBP");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "VBZ");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());
		
		morpheme = StanfordPosTagger.createMorpheme(word, "JJ");
		assertEquals(PartOfSpeech.adjective, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "JJR");
		assertEquals(PartOfSpeech.adjective, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "JJS");
		assertEquals(PartOfSpeech.adjective, morpheme.getPartOfSpeech());
		
		morpheme = StanfordPosTagger.createMorpheme(word, "RB");
		assertEquals(PartOfSpeech.adverb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "RBR");
		assertEquals(PartOfSpeech.adverb, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "RBS");
		assertEquals(PartOfSpeech.adverb, morpheme.getPartOfSpeech());
		
		
		morpheme = StanfordPosTagger.createMorpheme(word, ".");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "<");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = StanfordPosTagger.createMorpheme(word, "X");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
	}
	
	public void testExecuteByServer() throws Exception {
		String result = tagger.executeByServer(SOURCE_TEXT);
		assertEquals(EXPECT_TEXT_BY_Server, result);
	}
	
//	public void testExecuteJavaAPI() throws Exception {
//		String result = tagger.executeByJavaAPI(SOURCE_TEXT);
//		assertEquals(EXPECT_TEXT_BY_JavaAPI, result);
//	}
	
	public void testDoAnalyze() throws Exception {
		
		jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme[] mor = tagger.analyze("en", SOURCE_TEXT);
		
		assertEquals(72, mor.length);
		
		List<jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme> list = Arrays.asList(mor);
		assertEquals("A", list.get(0).getWord());
		assertEquals("A", list.get(0).getLemma());
		assertEquals(PartOfSpeech.other.toString(), list.get(0).getPartOfSpeech());
		assertEquals("passenger", list.get(1).getWord());
		assertEquals("passenger", list.get(1).getLemma());
		assertEquals(PartOfSpeech.noun_common.toString(), list.get(1).getPartOfSpeech());
	}
	
	private static String SOURCE_TEXT = "A passenger plane has crashed shortly after take_off from Kyrgyzstan's capital, Bishkek, " +
			"killing a large number of those on board. The head of Kyrgyzstan's civil aviation authority said that out of about 90" +
			" passengers and crew, only about 20 people have survived. The Itek Air Boeing 737 took off bound for Mashhad," +
			" in north-eastern Iran, but turned round some 10 minutes later.";
	
	private static String EXPECT_TEXT_BY_JavaAPI = "A_DT passenger_NN plane_NN has_VBZ crashed_VBN shortly_RB after_IN" +
			" take_off_NN from_IN Kyrgyzstan's_NNP capital,_NNP Bishkek,_NNP killing_VBG a_DT large_JJ number_NN" +
			" of_IN those_DT on_IN board._NN The_DT head_NN of_IN Kyrgyzstan's_NNP civil_JJ aviation_NN authority_NN" +
			" said_VBD that_IN out_IN of_IN about_IN 90_CD passengers_NNS and_CC crew,_VBP only_RB about_RB 20_CD" +
			" people_NNS have_VBP survived._VBN The_DT Itek_NNP Air_NNP Boeing_NNP 737_CD took_VBD off_RB bound_VBN" +
			" for_IN Mashhad,_NNP in_IN north-eastern_JJ Iran,_NNP but_CC turned_VBD round_NN some_DT 10_CD" +
			" minutes_NNS later._VBP ";

	private static String EXPECT_TEXT_BY_Server = "A_DT passenger_NN plane_NN has_VBZ crashed_VBN shortly_RB after_IN" +
			" take_off_NN from_IN Kyrgyzstan_NNP 's_POS capital_NN ,_, Bishkek_NNP ,_, killing_VBG a_DT large_JJ number_NN" +
			" of_IN those_DT on_IN board_NN ._. The_DT head_NN of_IN Kyrgyzstan_NNP 's_POS civil_JJ aviation_NN authority_NN" +
			" said_VBD that_IN out_IN of_IN about_IN 90_CD passengers_NNS and_CC crew_NN ,_, only_RB about_IN 20_CD" +
			" people_NNS have_VBP survived_VBN ._. The_DT Itek_NNP Air_NNP Boeing_NNP 737_CD took_VBD off_RB bound_VBN" +
			" for_IN Mashhad_NNP ,_, in_IN north-eastern_JJ Iran_NNP ,_, but_CC turned_VBD round_NN some_DT 10_CD" +
			" minutes_NNS later_RB ._. ";
}
