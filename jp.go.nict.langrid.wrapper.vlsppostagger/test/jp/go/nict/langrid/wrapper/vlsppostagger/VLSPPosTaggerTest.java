package jp.go.nict.langrid.wrapper.vlsppostagger;

import java.util.Arrays;
import java.util.List;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import junit.framework.TestCase;

public class VLSPPosTaggerTest extends TestCase {
	private VLSPPosTagger vls;

	protected void setUp() throws Exception {
		super.setUp();
		vls = new VLSPPosTagger();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		vls = null;
	}
	
	public void testStripTags() throws Exception {
		assertEquals("google", vls.stripTags("<html><head></head><body onload='test'><h1>google</h1></body></html>"));
	}


	public void testTokenizer() throws Exception {
		List<VLSPPosTagger.TokenizeResult> list = new VLSPPosTagger.Tokenizer().invoke("Cuộc chiến lượt cuối ở Premier League. Ngoài ra, chiếc máy bay này cũng có khả năng mang theo thiết bị và vũ khí với trọng lượng lên tới hai tấn.");
		assertEquals(2, list.size());
		assertEquals("Cuộc chiến lượt cuối ở Premier League.", list.get(0).original);
		assertEquals("Cuộc_chiến lượt cuối ở Premier_League .", list.get(0).tokenized);
		assertEquals("Ngoài ra, chiếc máy bay này cũng có khả năng mang theo thiết bị và vũ khí với trọng lượng lên tới hai tấn.", list.get(1).original);
		assertEquals("Ngoài_ra , chiếc máy_bay này cũng có khả_năng mang theo thiết_bị và vũ_khí với trọng_lượng lên tới hai tấn .", list.get(1).tokenized);
	}

	public void testCreateMorpheme() {
		String word = "hoge_foo";
		String expect_word = "hoge foo";
		String posStr = "N";

		Morpheme morpheme = VLSPPosTagger.createMorpheme(word, posStr);
		assertEquals(expect_word, morpheme.getWord());
		assertEquals(expect_word, morpheme.getLemma());
		assertEquals(PartOfSpeech.noun, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "A");
		assertEquals(PartOfSpeech.adjective, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "R");
		assertEquals(PartOfSpeech.adverb, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "V");
		assertEquals(PartOfSpeech.verb, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "N");
		assertEquals(PartOfSpeech.noun, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "Nb");
		assertEquals(PartOfSpeech.noun, morpheme.getPartOfSpeech());

		// morpheme = VLSPPosTagger.createMorpheme(word, "");
		// assertEquals(PartOfSpeech.noun_common, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "P");
		assertEquals(PartOfSpeech.noun_other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "M");
		assertEquals(PartOfSpeech.noun_other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "Ny");
		assertEquals(PartOfSpeech.noun_other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "Nc");
		assertEquals(PartOfSpeech.noun_other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "Nu");
		assertEquals(PartOfSpeech.noun_other, morpheme.getPartOfSpeech());

		// morpheme = VLSPPosTagger.createMorpheme(word, posStr);
		// assertEquals(PartOfSpeech.noun_pronoun, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "Np");
		assertEquals(PartOfSpeech.noun_proper, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "E");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "C");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "L");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "T");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "a");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, ".");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());
		morpheme = VLSPPosTagger.createMorpheme(word, "<");
		assertEquals(PartOfSpeech.other, morpheme.getPartOfSpeech());

		morpheme = VLSPPosTagger.createMorpheme(word, "X");
		assertEquals(PartOfSpeech.unknown, morpheme.getPartOfSpeech());
	}
	
	public void testDoAnalyze() throws Exception {
		jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme[] mor = vls.analyze(
				"vi",
				"Cuộc chiến lượt cuối ở Premier League,"
						+ System.getProperty("line.separator")
						+ "Dù Wigan từng thắng Cheslea.");

		assertEquals(11, mor.length);
		
		List<jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme> list = Arrays.asList(mor);
		assertEquals("Cuộc chiến", list.get(0).getWord());
		assertEquals("Cuộc chiến", list.get(0).getLemma());
		assertEquals(PartOfSpeech.noun.toString(), list.get(0).getPartOfSpeech());

		assertEquals("lượt", list.get(1).getWord());
		assertEquals("lượt", list.get(1).getLemma());
		assertEquals(PartOfSpeech.noun.toString(), list.get(1).getPartOfSpeech());

		assertEquals("Premier League", list.get(4).getWord());
		assertEquals("Premier League", list.get(4).getLemma());
		assertEquals(PartOfSpeech.noun_proper.toString(), list.get(4).getPartOfSpeech());

		assertEquals(",", list.get(5).getWord());
		assertEquals(",", list.get(5).getLemma());
		assertEquals(PartOfSpeech.other.toString(), list.get(5).getPartOfSpeech());

		assertEquals("từng", list.get(7).getWord());
		assertEquals("từng", list.get(7).getLemma());
		assertEquals(PartOfSpeech.adverb.toString(), list.get(7).getPartOfSpeech());

		assertEquals("Cheslea", list.get(9).getWord());
		assertEquals("Cheslea", list.get(9).getLemma());
		assertEquals(PartOfSpeech.noun_proper.toString(), list.get(9).getPartOfSpeech());

		assertEquals(".", list.get(10).getWord());
		assertEquals(".", list.get(10).getLemma());
		assertEquals(PartOfSpeech.other.toString(), list.get(10).getPartOfSpeech());

	}
}
