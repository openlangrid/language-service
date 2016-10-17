package net.didion.jwnl;

import java.util.Iterator;

import junit.framework.TestCase;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;

public class JWNLTest extends TestCase{
	public JWNLTest() throws Exception{
		JWNL.initialize(getClass().getResourceAsStream("/test_file_properties.xml"));
	}

	public void test1() throws Exception{
		Dictionary dic = Dictionary.getInstance();
		IndexWordSet words = dic.lookupAllIndexWords("bank");
		for(IndexWord word : words.getIndexWordArray()){
			System.out.println("word: " + word.getLemma() + "  " + word.getPOS());
			for(Synset synset : word.getSenses()) {
				System.out.println("[gloss] " + synset.getGloss());
				for(Word w: synset.getWords()) {
					System.out.println("  [word] " + w.getLemma());
				}
			}
		}
	}

	public void test2() throws Exception{
		Dictionary dic = Dictionary.getInstance();
		int c = 0;
		for(Object pos : POS.getAllPOS()){
			Iterator<?> it = dic.getIndexWordIterator((POS)pos, "bank");
			while(it.hasNext()){
				IndexWord word = (IndexWord)it.next();
				System.out.println("word: " + word.getLemma() + "  " + word.getPOS());
				c++;
/*
				for(Synset synset : word.getSenses()) {
					System.out.println("[gloss] " + synset.getGloss());
					for(Word w: synset.getWords()) {
						System.out.println("  [word] " + w.getLemma());
					}
				}
*/
			}
		}
		System.out.println(c + " concepts found.");
	}
}
