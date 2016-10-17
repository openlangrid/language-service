package jp.go.nict.langrid.wrapper.wordnetja;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HOLONYMS;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HYPERNYMS;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HYPONYMS;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.MERONYMS;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.go.nict.langrid.commons.util.Pair;
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
import edu.cmu.lti.jawjaw.db.SQL;
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

public class WordNetJA
extends AbstractConceptDictionaryService{
	public WordNetJA(){
		dbPath = getInitParameterString("wordnetja.dbPath", "/srv/langrid/resources/wordnetja/1.0/wnjpn.db");
		SQL.setDBPath(dbPath);
		setSupportedLanguageCollection(Arrays.asList(ja, en));
		setSupportedMatchingMethods(new HashSet<MatchingMethod>(){
				{ add(MatchingMethod.COMPLETE);}
				private static final long serialVersionUID = -946727431882831635L;
				});
	}
	
	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
		SQL.setDBPath(dbPath);
	}

	@Override
	protected Collection<Concept> doSearchConcepts(Language language,
			String word, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		List<Concept> ret = new ArrayList<Concept>();
		for(Word w : WordDAO.findWordsByLemma(word)){
			if(!l2l.get(w.getLang()).equals(language)) continue;
			Set<String> synsets = new HashSet<String>();
			for(Sense s : SenseDAO.findSensesByWordid(w.getWordid())){
				synsets.add(s.getSynset());
			}

			PartOfSpeech pos = p2p.get(w.getPos());
			for(String synset : synsets){
				ret.add(createConcept(synset, pos));
				if(ret.size() == getMaxResults()) break;
			}
			if(ret.size() == getMaxResults()) break;
		}
		return ret;
	}

	@Override
	protected Collection<Concept> doGetRelatedConcepts(Language language,
			String conceptId, ConceptualRelation relation)
			throws InvalidParameterException, ProcessFailedException {
		Pair<String, PartOfSpeech> idAndPos = null;
		try{
			idAndPos = decodeConceptId(conceptId);
		} catch(ParseException e){
			throw new InvalidParameterException("conceptId", e.getMessage());
		}
		List<Concept> ret = new ArrayList<Concept>();
		for(Synlink l : SynlinkDAO.findSynlinksBySynset(idAndPos.getFirst())){
			if(relation.equals(r2r.get(l.getLink()))){
				ret.add(createConcept(l.getSynset2(), idAndPos.getSecond()));
				if(ret.size() == getMaxResults()) break;
			}
		}
		return ret;
	}

	private Concept createConcept(String id, PartOfSpeech pos){
		Concept c = new Concept();
		c.setConceptId(encodeConceptId(id));
		Set<String> sids = new HashSet<String>();
		Set<Integer> wordIds = new LinkedHashSet<Integer>();
		List<Gloss> glosses = new ArrayList<Gloss>();
		for(Sense s : SenseDAO.findSensesBySynset(id)){
			wordIds.add(s.getWordid());
			SynsetDef def = SynsetDefDAO.findSynsetDefBySynsetAndLang(
					s.getSynset(), s.getLang()
					);
			if(def == null) continue;
			String key = def.getLang().name() + ":" + def.getDef();
			if(sids.contains(key)) continue;
			sids.add(key);
			glosses.add(new Gloss(def.getDef(), l2l.get(def.getLang()).getCode()));
		}
		List<Lemma> lemmas = new ArrayList<Lemma>();
		for(int wid : wordIds){
			Word sw = WordDAO.findWordByWordid(wid);
			lemmas.add(new Lemma(sw.getLemma()
					, l2l.get(sw.getLang()).getCode()
					));
			
		}
		c.setSynset(lemmas.toArray(new Lemma[]{}));
		c.setGlosses(glosses.toArray(new Gloss[]{}));
		c.setPartOfSpeech(pos.getExpression());

		Set<String> relations = new HashSet<String>();
		for(Synlink l : SynlinkDAO.findSynlinksBySynset(id)){
			ConceptualRelation rel = r2r.get(l.getLink());
			if(rel != null) relations.add(rel.name());
		}
		c.setRelations(relations.toArray(new String[]{}));
		return c;
	}

	private String encodeConceptId(String synset){
		return "urn:langrid:wnja:concept:" + synset;
	}

	private Pair<String, PartOfSpeech> decodeConceptId(String conceptId)
	throws ParseException
	{
		String prefix = "urn:langrid:wnja:concept:";
		if(!conceptId.startsWith(prefix)){
			throw new ParseException("invalid concept id format", 0);
		}
		String synset = conceptId.substring(prefix.length());
		PartOfSpeech pos = p2p.get(POS.valueOf(
				synset.substring(synset.length() - 1)
				));
		return Pair.create(synset, pos);
	}

	private String dbPath;
	private static Map<Lang, Language> l2l = new HashMap<Lang, Language>();
	private static Map<Language, Lang> l2lr = new HashMap<Language, Lang>();
	private static Map<POS, PartOfSpeech> p2p = new HashMap<POS, PartOfSpeech>();
	private static Map<Link, ConceptualRelation> r2r = new HashMap<Link, ConceptualRelation>();
	static{
		l2l.put(Lang.eng, en);
		l2l.put(Lang.jpn, ja);
		l2lr.put(en, Lang.eng);
		l2lr.put(ja, Lang.jpn);
		p2p.put(POS.n, PartOfSpeech.noun);
		p2p.put(POS.r, PartOfSpeech.noun_proper);
		p2p.put(POS.v, PartOfSpeech.verb);
//		p2p.put(POS.g, PartOfSpeech.adjective);
		p2p.put(POS.a, PartOfSpeech.adverb);
//		p2p.put(POS.p, PartOfSpeech.other);
//		p2p.put(POS.c, PartOfSpeech.other);
//		p2p.put(POS.d, PartOfSpeech.other);
//		p2p.put(POS.o, PartOfSpeech.other);
		r2r.put(Link.hype, HYPERNYMS);
		r2r.put(Link.hypo, HYPONYMS);
		r2r.put(Link.mero, MERONYMS);
		r2r.put(Link.mmem, MERONYMS);
		r2r.put(Link.mprt, MERONYMS);
		r2r.put(Link.msub, MERONYMS);
		r2r.put(Link.holo, HOLONYMS);
		r2r.put(Link.hmem, HOLONYMS);
		r2r.put(Link.hprt, HOLONYMS);
		r2r.put(Link.hsub, HOLONYMS);
	}
}
