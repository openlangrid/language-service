package jp.go.nict.langrid.wrapper.treetagger;

import java.io.FileInputStream;

import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.common.util.posmap.GlobPosMapper;
import junit.framework.TestCase;

public class GlobPosMapper_english_Test extends TestCase {
	@Override
	protected void setUp() throws Exception {
		mapper = new GlobPosMapper(new FileInputStream(
				"resource/posmap/english.posmap.glob"));
	}

	public void test() throws Exception{
		assertEquals(PartOfSpeech.noun_pronoun, mapper.get("PP"));
		assertEquals(PartOfSpeech.noun_pronoun, mapper.get("PP$"));
		assertEquals(PartOfSpeech.noun_other, mapper.get("SYN"));
		assertEquals(PartOfSpeech.other, mapper.get("SSK"));
		assertEquals(PartOfSpeech.other, mapper.get("POS"));
	}

	private GlobPosMapper mapper;
}
