/*
 * $Id: EDRConceptDictionary.java 26988 2009-02-20 06:24:08Z Takao Nakaguchi $
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
package jp.go.nict.langrid.wrapper.edr;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
import static jp.go.nict.langrid.service_1_2.conceptdictionary.typed.ConceptualRelation.HYPERNYMS;
import static jp.go.nict.langrid.service_1_2.typed.PartOfSpeech.unknown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.commons.transformer.EnquoteTransformer;
import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.ArrayUtil;
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
import jp.go.nict.langrid.wrapper.edr.entity.ConceptWord;
import jp.go.nict.langrid.wrapper.edr.hibernate.EdrSessionFactory;
import jp.go.nict.langrid.wrapper.ws_1_2.conceptdictionary.AbstractConceptDictionaryService;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * EDR概念辞書をラッピングするクラス。
 * 概念見出し辞書、概念体系辞書、英語単語辞書、日本語単語辞書の4つを用いて概念辞書としてラッピングする。
 * <ul>
 * <li>初期化パラメータ
 * <ol><li>maxConceptCount: 一度に返す概念情報の最大個数(デフォルト: 100)</li></ol>
 * </li>
 * <li>searchの動作
 * <ol>
 * <li>概念見出し辞書の見出し(英語or日本語)、英語単語辞書の見出しor日本語単語辞書の見出しを検索し、
 * 見つかった概念、単語の概念IDのセットを作成する。</li>
 * <li>この際、下位概念、上位概念の有無も検索される。</li>
 * <li>同じ概念IDを持つ概念見出し辞書のレコード、単語辞書のレコードから、品詞別にsynsetとglossを作成し、
 * 概念情報を作成する。</li>
 * <li>単語辞書のレコードが存在する場合、概念見出し辞書の情報はそれぞれの品詞別の情報にマージされる。</li>
 * <li>単語辞書のレコードが存在しない場合、概念見出し辞書の情報は不明の品詞の情報として概念情報が作成される。</li>
 * </ol>
 * <li>getRelatedConceptsの動作
 * <ol>
 * <li>概念体系辞書を検索し概念IDのセットを作成する。</li>
 * <li>以下、searchの動作と同じ。</li>
 * </ol>
 * </li>
 * </ul>
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 26988 $
 */
public class EDRConceptDictionary
extends AbstractConceptDictionaryService
{
	/**
	 * コンストラクタ。
	 */
	public EDRConceptDictionary(){
		maxConceptCount = getInitParameterInt("maxConceptCount", 100);
		init();
	}

	private static class ConceptRecord{
		public ConceptRecord(String headWord, String gloss, 
				long upperConceptCount, long lowerConceptCount) {
			super();
			this.headWord = headWord;
			this.gloss = gloss;
			this.upperConceptCount = upperConceptCount;
			this.lowerConceptCount = lowerConceptCount;
		}

		public String getHeadWord() {
			return headWord;
		}
		public void setHeadWord(String headWord) {
			this.headWord = headWord;
		}
		public String getGloss() {
			return gloss;
		}
		public void setGloss(String gloss) {
			this.gloss = gloss;
		}
		public long getUpperConceptCount() {
			return upperConceptCount;
		}
		public void setUpperConceptCount(long upperConceptCount) {
			this.upperConceptCount = upperConceptCount;
		}
		public long getLowerConceptCount() {
			return lowerConceptCount;
		}
		public void setLowerConceptCount(long lowerConceptCount) {
			this.lowerConceptCount = lowerConceptCount;
		}

		private String headWord;
		private String gloss;
		private long upperConceptCount;
		private long lowerConceptCount;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<Concept> doSearchConcepts(Language language,
			String word, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		if(exceptionInConstructor != null){
			throw new ProcessFailedException(exceptionInConstructor);
		}

		Session session = null;
		try{
			session = factory.getCurrentSession();
			session.beginTransaction();
		} catch(HibernateException e){
			logger.log(Level.WARNING, "failed to start transaction.", e);
			throw new ProcessFailedException(e);
		} catch(Throwable e){
			logger.log(Level.SEVERE, "unknown error occurred.", e);
			throw new ProcessFailedException(e);
		}

		try{
			Pair<String, String> opAndValue = getOpAndValue(
					matchingMethod, word);

			Map<String, ConceptRecord> conceptIdToConceptRecord
				= new HashMap<String, ConceptRecord>();
			String hql = language.equals(ja) ? jQueryGatherConceptsByHeadWord : eQueryGatherConceptsByHeadWord;
			hql = String.format(hql, opAndValue.getFirst()); 
			Query query = session.createQuery(hql);
			query.setString("matchingValue", opAndValue.getSecond());
			query.setMaxResults(maxConceptCount);
			for(Object o : query.list()){
				Object[] values = (Object[])o;
				conceptIdToConceptRecord.put(
						(String)values[0], new ConceptRecord(
								(String)values[1], (String)values[2]
								, (Long)values[3], (Long)values[4]
								)
						);
			}

			hql = language.equals(ja) ? jQueryGatherConceptWordIdsByHeadWord : eQueryGatherConceptWordIdsByHeadWord;
			hql = String.format(hql, opAndValue.getFirst()); 
			query = session.createQuery(hql);
			query.setString("matchingValue", opAndValue.getSecond());
			query.setMaxResults(maxConceptCount);

			Set<String> conceptIds = new HashSet<String>((Collection<String>)query.list());
			conceptIds.addAll(conceptIdToConceptRecord.keySet());
			if(conceptIds.size() == 0) return Arrays.asList();
			Collection<Concept> result = makeConcepts(
					session, language
					, conceptIds.toArray(new String[]{})
					, conceptIdToConceptRecord
					, null
					);
			session.getTransaction().commit();
			return result;
		} catch(HibernateException e){
			logger.log(Level.WARNING, "failed to fetch results.", e);
			session.getTransaction().rollback();
			throw new ProcessFailedException(e);
		} catch(Throwable e){
			logger.log(Level.SEVERE, "unknown error occurred.", e);
			session.getTransaction().rollback();
			throw new ProcessFailedException(e);
		}
	}

	@Override
	protected Collection<Concept> doGetRelatedConcepts(Language language,
			String conceptId, ConceptualRelation relation)
			throws InvalidParameterException, ProcessFailedException {
		if(exceptionInConstructor != null){
			throw new ProcessFailedException(exceptionInConstructor);
		}

		Pair<String, PartOfSpeech> idAndPos = getConceptIdAndPos(conceptId);
		if(idAndPos == null){
			throw new InvalidParameterException(
					"conceptId", "invalid concept id"
					);
		}
		String cid = idAndPos.getFirst();
		PartOfSpeech pos = idAndPos.getSecond();

		Session session = null;
		try{
			session = factory.getCurrentSession();
			session.beginTransaction();
		} catch(HibernateException e){
			logger.log(Level.WARNING, "failed to start transaction.", e);
			throw new ProcessFailedException(e);
		} catch(Throwable e){
			logger.log(Level.SEVERE, "unknown error occurred.", e);
			throw new ProcessFailedException(e);
		}

		try{
			Map<String, ConceptRecord> conceptIdToConceptRecord
					= new HashMap<String, ConceptRecord>();
			String hql = language.equals(ja)
					? (
							relation.equals(HYPERNYMS)
							? jQueryGetConceptsHypernyms
							: jQueryGetConceptsHyponyms
					) : (
							relation.equals(HYPERNYMS)
							? eQueryGetConceptsHypernyms
							: eQueryGetConceptsHyponyms
					);
			Query q = session.createQuery(hql);
			q.setString("conceptId", cid);
			q.setMaxResults(maxConceptCount);
			for(Object o : q.list()){
				Object[] values = (Object[])o;
				conceptIdToConceptRecord.put(
						(String)values[0], new ConceptRecord(
								(String)values[1], (String)values[2]
								, (Long)values[3], (Long)values[4]
								)
						);
			}

			Set<String> conceptIds = new HashSet<String>(
					conceptIdToConceptRecord.keySet()
					);
			if(conceptIds.size() == 0) return Arrays.asList();
			if(conceptIds.size() >= maxConceptCount){
				int overCount = conceptIds.size() - maxConceptCount;
				Iterator<?> it = conceptIds.iterator();
				for(int i = 0; i < overCount; i++){
					if(!it.hasNext()) break;
					it.next();
					it.remove();
				}
			}

			List<Concept> result = makeConcepts(
				session, language
				, conceptIds.toArray(new String[]{})
				, conceptIdToConceptRecord
				, pos
				);
			if(result.size() > maxConceptCount)
			session.getTransaction().commit();
			return result;
		} catch(HibernateException e){
			logger.log(Level.WARNING, "failed to fetch results.", e);
			session.getTransaction().rollback();
			throw new ProcessFailedException(e);
		} catch(Throwable e){
			logger.log(Level.SEVERE, "unknown error occurred.", e);
			session.getTransaction().rollback();
			throw new ProcessFailedException(e);
		}
	}

	private List<Concept> makeConcepts(
			Session session, Language language
			, String[] conceptIds
			, Map<String, ConceptRecord> conceptIdToConceptRecord
			, PartOfSpeech partOfSpeech)
	{
		List<Concept> result = new ArrayList<Concept>();

		String currentConceptId = null;
		long currentUpperRelationCount = 0;
		long currentLowerRelationCount = 0;
		Map<PartOfSpeech, Set<String>> currentSynsetsParPos
			= new HashMap<PartOfSpeech, Set<String>>();
		Map<PartOfSpeech, Set<String>> currentGlossesParPos
			= new HashMap<PartOfSpeech, Set<String>>();

		String hql = language.equals(ja) ? jQueryGetConceptWords : eQueryGetConceptWords;
		hql = String.format(hql, StringUtils.join(
				ArrayUtil.collect(conceptIds, new EnquoteTransformer())
				, ",")); 
		Query query = session.createQuery(hql);
		query.setMaxResults(maxConceptCount);
		for(Object o : query.list()){
			Object[] results = (Object[])o;
			ConceptWord cw = (ConceptWord)results[0];
			long upperRelationCount = (Long)results[1];
			long lowerRelationCount = (Long)results[2];

			String conceptId = cw.getConceptId();
			if(currentConceptId != null
					&& !currentConceptId.equals(conceptId)){
				ConceptRecord cr = conceptIdToConceptRecord.get(currentConceptId);
				mergeSynsetAndGloss(
						currentSynsetsParPos, currentGlossesParPos
						, cr
						);
				if(cr != null){
					currentUpperRelationCount += cr.getUpperConceptCount();
					currentLowerRelationCount += cr.getLowerConceptCount();
				}
				addConcepts(result, currentConceptId
						, currentSynsetsParPos
						, currentGlossesParPos
						, currentUpperRelationCount
						, currentLowerRelationCount
						, language
						);
				conceptIdToConceptRecord.remove(currentConceptId);
				currentSynsetsParPos.clear();
				currentGlossesParPos.clear();
			}
			currentConceptId = conceptId;
			for(PartOfSpeech pos : getPoses(cw.getPartOfSpeech(), language)){
				if(partOfSpeech != null && !partOfSpeech.equals(pos)) continue;
				Set<String> synsets = currentSynsetsParPos.get(pos);
				if(synsets == null){
					synsets = new HashSet<String>();
					currentSynsetsParPos.put(pos, synsets);
				}
				synsets.add(cw.getHeadWord());
				Set<String> glosses = currentGlossesParPos.get(pos);
				if(glosses == null){
					glosses = new HashSet<String>();
					currentGlossesParPos.put(pos, glosses);
				}
				glosses.add(cw.getGloss());
			}
			currentUpperRelationCount = upperRelationCount;
			currentLowerRelationCount = lowerRelationCount;
		}
		if(currentConceptId != null){
			ConceptRecord cr = conceptIdToConceptRecord.get(currentConceptId);
			mergeSynsetAndGloss(
					currentSynsetsParPos, currentGlossesParPos, cr
					);
			if(cr != null){
				currentUpperRelationCount += cr.getUpperConceptCount();
				currentLowerRelationCount += cr.getLowerConceptCount();
			}
			addConcepts(result, currentConceptId
					, currentSynsetsParPos
					, currentGlossesParPos
					, currentUpperRelationCount
					, currentLowerRelationCount
					, language
					);
			conceptIdToConceptRecord.remove(currentConceptId);
		}
		for(Map.Entry<String, ConceptRecord> entry : conceptIdToConceptRecord.entrySet()){
			currentConceptId = entry.getKey();
			ConceptRecord cr = entry.getValue();
			currentSynsetsParPos.clear();
			currentGlossesParPos.clear();
			mergeSynsetAndGloss(
					currentSynsetsParPos, currentGlossesParPos, cr
					);
			currentUpperRelationCount = cr.getUpperConceptCount();
			currentLowerRelationCount = cr.getLowerConceptCount();
			addConcepts(result, currentConceptId
					, currentSynsetsParPos
					, currentGlossesParPos
					, currentUpperRelationCount
					, currentLowerRelationCount
					, language
					);
		}
		if(result.size() > maxConceptCount){
			result = result.subList(0, maxConceptCount);
		}
		return result;
	}

	private static void addConcepts(
			List<Concept> result, String conceptId
			, Map<PartOfSpeech, Set<String>> synsetsParPos
			, Map<PartOfSpeech, Set<String>> glossesParPos
			, long upperRelationCount, long lowerRelationCount
			, final Language language)
	{
		Set<String> relations = new HashSet<String>();
		if(upperRelationCount > 0){
			relations.add(ConceptualRelation.HYPERNYMS.name());
		}
		if(lowerRelationCount > 0){
			relations.add(ConceptualRelation.HYPONYMS.name());
		}
		String[] rel = relations.toArray(new String[]{});
		for(Map.Entry<PartOfSpeech, Set<String>> entry : synsetsParPos.entrySet()){
			result.add(createConcept(
					conceptId, entry.getKey(), entry.getValue()
					, glossesParPos.get(entry.getKey()), rel, language
					));
		}
	}

	private static void mergeSynsetAndGloss(
			Map<PartOfSpeech, Set<String>> synsetsParPos
			, Map<PartOfSpeech, Set<String>> glossesParPos
			, ConceptRecord conceptRecord
			)
	{
		if(conceptRecord == null) return;
		String synset = conceptRecord.getHeadWord();
		if(synset == null) synset = "";
		String gloss = conceptRecord.getGloss();
		if(gloss == null) gloss = "";
		if(synsetsParPos.size() != 0){
			for(PartOfSpeech pos : synsetsParPos.keySet()){
				if(synset.length() > 0){
					synsetsParPos.get(pos).add(synset);
				}
				if(gloss.length() > 0){
					glossesParPos.get(pos).add(gloss);
				}
			}
		} else{
			synsetsParPos.put(unknown, new HashSet<String>(Arrays.asList(synset)));
			glossesParPos.put(unknown, new HashSet<String>(Arrays.asList(gloss)));
		}
	}

	private Pair<String, PartOfSpeech> getConceptIdAndPos(String conceptId){
		String[] values = conceptId.split(":");
		if(values.length != 6) return null;
		PartOfSpeech pos = PartOfSpeech.valueOfExpression(values[4]);
		if(pos == null) return null;
		return Pair.create(values[5], pos);
	}

	private static Concept createConcept(
			String conceptId, PartOfSpeech pos
			, Collection<String> synsets
			, Collection<String> glosses
			, String[] relations, final Language language)
	{
		return new Concept(
				"urn:langrid:edr:concept:" + pos.getExpression() + ":" + conceptId
				, pos.getExpression()
				, ArrayUtil.collect(
						synsets.toArray(new String[]{})
						, Lemma.class
						, new Transformer<String, Lemma>(){
								public Lemma transform(String value)
										throws TransformationException {
									return new Lemma(value, language.getCode());
								}
				})
				, ArrayUtil.collect(
						glosses.toArray(new String[]{})
						, Gloss.class
						, new Transformer<String, Gloss>(){
								public Gloss transform(String value)
										throws TransformationException {
									return new Gloss(value, language.getCode());
								}
				})
				, relations
				);
	}

	private void init(){
		setSupportedLanguageCollection(Arrays.asList(en, ja));
		try{
			factory = EdrSessionFactory.getSessionFactory();
		} catch(HibernateException e){
			exceptionInConstructor = e;
			logger.log(Level.SEVERE, "failed to initialize service.", e);
		}
	}

	private Pair<String, String> getOpAndValue(
			MatchingMethod matchingMethod, String value)
	{
		String escapedValue = StringEscapeUtils.escapeSql(value);
		switch(matchingMethod){
			case PREFIX:
				return Pair.create("like", escapedValue + '%');
			case SUFFIX:
				return Pair.create("like", '%' + escapedValue);
			case PARTIAL:
				return Pair.create("like", '%' + escapedValue + '%');
			case COMPLETE:
			default:
				return Pair.create("=", escapedValue);
		}
	}

	private static PartOfSpeech[] getPoses(String edrPos, Language language){
		Map<String, PartOfSpeech> posMap = new HashMap<String, PartOfSpeech>();
		if(language.equals(en)){
			posMap = ePosMap;
		} else if(language.equals(ja)){
			posMap = jPosMap;
		}
		List<PartOfSpeech> poses = new ArrayList<PartOfSpeech>();
		for(String p : edrPos.split(";")){
			PartOfSpeech pos = posMap.get(p);
			if(pos != null){
				poses.add(pos);
			} else{
				poses.add(unknown);
			}
		}
		return poses.toArray(new PartOfSpeech[]{});
	}

	private String baseQueryGatherConceptsByHeadWord =
		"select" +
		"  c.conceptId, c.headWord%1$s, c.gloss%1$s" +
		"  , (select count(lowerConceptId) from ConceptRelation where lowerConceptId=c.conceptId)" +
		"  , (select count(upperConceptId) from ConceptRelation where upperConceptId=c.conceptId)" +
		" from Concept c" +
		" where c.headWord%1$s %2$s :matchingValue";
	private String jQueryGatherConceptsByHeadWord = String.format(
			baseQueryGatherConceptsByHeadWord, "Japanese", "%s"
			);
	private String eQueryGatherConceptsByHeadWord = String.format(
			baseQueryGatherConceptsByHeadWord, "English", "%s"
			);

	private String baseQueryFormatGatherConceptWordIdsByHeadWord =
		"select" +
		"  distinct conceptId" +
		"  from %1$s" +
		"  where headWord %2$s :matchingValue";
	private String jQueryGatherConceptWordIdsByHeadWord = String.format(
			baseQueryFormatGatherConceptWordIdsByHeadWord, "JConceptWord", "%s"
			);
	private String eQueryGatherConceptWordIdsByHeadWord = String.format(
			baseQueryFormatGatherConceptWordIdsByHeadWord, "EConceptWord", "%s"
			);

	private String baseQueryFormatGetConceptWords =
		"select" +
		"  w" +
		"  , (select count(lowerConceptId) from ConceptRelation where lowerConceptId=w.conceptId)" +
		"  , (select count(upperConceptId) from ConceptRelation where upperConceptId=w.conceptId)" +
		" from" +
		"  %1$s as w" +
		" where" +
		"  w.conceptId in (%2$s)" +
		" order by" +
		"  w.conceptId asc";
	private String jQueryGetConceptWords = String.format(
			baseQueryFormatGetConceptWords, "JConceptWord", "%s"
			);
	private String eQueryGetConceptWords = String.format(
			baseQueryFormatGetConceptWords, "EConceptWord", "%s"
			);

	private String baseQueryGatherConceptsByRelation =
		"select" +
		"  c.conceptId, c.headWord%1$s, c.gloss%1$s" +
		"  , (select count(lowerConceptId) from ConceptRelation where lowerConceptId=c.conceptId)" +
		"  , (select count(upperConceptId) from ConceptRelation where upperConceptId=c.conceptId)" +
		" from Concept c" +
		" where c.conceptId in (" +
		"    select cr.%2$s" +
		"      from ConceptRelation cr" +
		"      where cr.%3$s=:conceptId" +
		"    )";
	private String jQueryGetConceptsHypernyms = String.format(
			baseQueryGatherConceptsByRelation
			, "Japanese", "upperConceptId", "lowerConceptId"
			);
	private String jQueryGetConceptsHyponyms = String.format(
			baseQueryGatherConceptsByRelation
			, "Japanese", "lowerConceptId", "upperConceptId"
			);
	private String eQueryGetConceptsHypernyms = String.format(
			baseQueryGatherConceptsByRelation
			, "English", "upperConceptId", "lowerConceptId"
			);
	private String eQueryGetConceptsHyponyms = String.format(
			baseQueryGatherConceptsByRelation
			, "English", "lowerConceptId", "upperConceptId"
			);

	private HibernateException exceptionInConstructor;
	private SessionFactory factory;
	private int maxConceptCount;

	private static Map<String, PartOfSpeech> jPosMap = new HashMap<String, PartOfSpeech>();
	private static Map<String, PartOfSpeech> ePosMap = new HashMap<String, PartOfSpeech>();
	private static Logger logger = Logger.getLogger(
			EDRConceptDictionary.class.getName());
	static{
		Properties prop = new Properties();
		try{
			prop.load(EDRConceptDictionary.class.getResourceAsStream(
					"posmap_japanese.properties"));
			for(Map.Entry<Object, Object> entry : prop.entrySet()){
				jPosMap.put(
						entry.getKey().toString()
						, PartOfSpeech.valueOfExpression(entry.getValue().toString())
						);
			}
			prop.clear();
			prop.load(EDRConceptDictionary.class.getResourceAsStream(
					"posmap_english.properties"));
			for(Map.Entry<Object, Object> entry : prop.entrySet()){
				ePosMap.put(
						entry.getKey().toString()
						, PartOfSpeech.valueOfExpression(entry.getValue().toString())
						);
			}
		} catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
