package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import evalMethod.similarityCalculator.BLEU.BLEU;

public class BLEUTest {
	@Test
	public void testCaluculate() throws Exception{
		String lang ="en";
		String candidate = "this is test one.";
		String reference = "this is test two.";
		double result = new BLEU().calculate(lang, candidate, reference);
		System.out.println(result);
		assertEquals(0.0,result,0.01);
	}

	@Test
	public void testCalculateSimilarity() {
		ArrayList<String> refs = new ArrayList<String>();
		refs.add("this is a test.");
		double result= new BLEU().calculateSimilarity("this is a test.", refs , jp.go.nict.langrid.language.ISO639_1LanguageTags.en);
		System.out.println(result);
		assertEquals(1.0, result, 0.01);

	}


}
