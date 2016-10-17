/*
 * $Id: EDRBilingualDictionary.java 27201 2009-09-28 04:20:48Z Takao Nakaguchi $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.bilingualdictionary.Translation;
import jp.go.nict.langrid.service_1_2.typed.MatchingMethod;
import jp.go.nict.langrid.wrapper.edr.entity.EJTranslation;
import jp.go.nict.langrid.wrapper.edr.entity.JETranslation;
import jp.go.nict.langrid.wrapper.edr.entity.TranslationTranslation;
import jp.go.nict.langrid.wrapper.edr.hibernate.EdrSessionFactory;
import jp.go.nict.langrid.wrapper.ws_1_2.bilingualdictionary.AbstractBilingualDictionaryWithLongestMatchSearchService;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;

/**
 * EDR英日・日英対訳辞書をラッピングするクラス。
 * <ul>
 * <li>初期化パラメータ
 * <ol><li>maxTranslationCount: 一度に返す対訳情報の最大個数</li></ol>
 * </li>
 * <li>searchの動作
 * <ol>
 * <li>入力の言語に応じ、英日対訳辞書又は日英対訳辞書のレコードが検索される。</li>
 * <li>ヒットした対訳を、見出し(Translation.headWord)でマージし、重複する対訳(Translation.targetWords)
 * を削除する。</li>
 * </ol>
 * </li>
 * </ul>
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 27201 $
 */
public class EDRBilingualDictionary
extends AbstractBilingualDictionaryWithLongestMatchSearchService{
	/**
	 * コンストラクタ。
	 */
	public EDRBilingualDictionary(){
		init();
	}

	@Override
	protected Collection<Translation> doSearch(Language headLang,
			Language targetLang, String headWord, MatchingMethod matchingMethod)
			throws InvalidParameterException, ProcessFailedException {
		if(exceptionInConstructor != null){
			throw new ProcessFailedException(exceptionInConstructor);
		}

		List<Translation> result = new ArrayList<Translation>();
		try{
			Session session = factory.getCurrentSession();
			session.beginTransaction();
			try{
				Class<? extends jp.go.nict.langrid.wrapper.edr.entity.Translation> clazz = EJTranslation.class;
				if(headLang.equals(ja)){
					clazz = JETranslation.class;
				}
				doSearch(result, session, clazz
						, headWord, matchingMethod);
				session.getTransaction().commit();
				return result;
			} catch(HibernateException e){
				logger.log(Level.WARNING, "failed to fetch results.", e);
				session.getTransaction().rollback();
				throw new ProcessFailedException(e);
			}
		} catch(Throwable e){
			logger.log(Level.WARNING, "seems to have problem on database.", e);
			throw new ProcessFailedException(e);
		}
	}

	private void init(){
		setSupportedLanguagePairs(Arrays.asList(
				new LanguagePair(en, ja)
				, new LanguagePair(ja, en)
				));
		try{
			factory = EdrSessionFactory.getSessionFactory();
		} catch(HibernateException e){
			exceptionInConstructor = e;
			logger.log(Level.SEVERE, "failed to initialize service.", e);
		}
	}

	private MatchMode matchingMode(MatchingMethod method){
		switch(method){
		case PREFIX:
			return MatchMode.START;
		case SUFFIX:
			return MatchMode.END;
		case PARTIAL:
			return MatchMode.ANYWHERE;
		case COMPLETE:
		default:
			return MatchMode.EXACT;
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends jp.go.nict.langrid.wrapper.edr.entity.Translation> void doSearch(
			List<Translation> result
			, Session session, Class<T> clazz
			, String headWord, MatchingMethod matchingMethod)
	throws HibernateException
	{
		List<jp.go.nict.langrid.wrapper.edr.entity.Translation> list = (List<jp.go.nict.langrid.wrapper.edr.entity.Translation>)
				session.createCriteria(
						clazz
				).add(
						Property.forName("headWord").like(
								headWord, matchingMode(matchingMethod)
						)
				).addOrder(
						Order.asc("headWord")
				).list();

		int maxTranslationCount = getMaxResults();
		Translation trans = new Translation();
		Set<String> transSet = new HashSet<String>();
		for(jp.go.nict.langrid.wrapper.edr.entity.Translation t : list){
			if(result.size() >= maxTranslationCount) break;
			if(t.getHeadWord() == null) continue;
			if(!t.getHeadWord().equals(trans.getHeadWord())){
				if(trans.getHeadWord() != null){
					trans.setTargetWords(transSet.toArray(new String[]{}));
					result.add(trans);
				}
				trans = new Translation();
				trans.setHeadWord(t.getHeadWord());
				transSet.clear();
			}
			for(TranslationTranslation tt : t.getTranslation()){
				transSet.add(tt.getHeadWord());
			}
		}
		if(trans.getHeadWord() != null && result.size() < maxTranslationCount){
			trans.setTargetWords(transSet.toArray(new String[]{}));
			result.add(trans);
		}
	}

	private HibernateException exceptionInConstructor;
	private SessionFactory factory;

	private static Logger logger = Logger.getLogger(
			EDRBilingualDictionary.class.getName());
}
