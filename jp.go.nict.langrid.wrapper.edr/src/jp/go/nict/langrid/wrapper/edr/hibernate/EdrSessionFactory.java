/*
 * $Id: EdrSessionFactory.java 26411 2008-08-28 08:58:33Z nakaguchi $
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
package jp.go.nict.langrid.wrapper.edr.hibernate;

import java.io.IOException;
import java.util.Properties;

import jp.go.nict.langrid.wrapper.edr.entity.Concept;
import jp.go.nict.langrid.wrapper.edr.entity.ConceptRelation;
import jp.go.nict.langrid.wrapper.edr.entity.EConceptWord;
import jp.go.nict.langrid.wrapper.edr.entity.JConceptWord;
import jp.go.nict.langrid.wrapper.edr.entity.EJTranslation;
import jp.go.nict.langrid.wrapper.edr.entity.JETranslation;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * langridで使用するhibernateセッションのファクトリ。
 * @author $Author:nakaguchi $
 * @version $Revision:4384 $
 */
public class EdrSessionFactory {
	/**
	 * セッションファクトリを取得する。
	 * @return セッションファクトリ
	 */
	public static SessionFactory getSessionFactory(){
		return factory;
	}

	private static SessionFactory factory;
	static{
		try{
			String base = "/" + EdrSessionFactory.class.getPackage().getName().replace('.', '/');
			Properties props = new Properties();
			props.load(EdrSessionFactory.class.getResourceAsStream(
					base + "/hibernate.properties"));
			factory = new AnnotationConfiguration()
				.addAnnotatedClass(JETranslation.Translation.class)
				.addAnnotatedClass(JETranslation.class)
				.addAnnotatedClass(EJTranslation.Translation.class)
				.addAnnotatedClass(EJTranslation.class)
				.addAnnotatedClass(Concept.class)
				.addAnnotatedClass(ConceptRelation.class)
				.addAnnotatedClass(JConceptWord.class)
				.addAnnotatedClass(EConceptWord.class)
				.addProperties(props)
				.setNamingStrategy(new PrefixNamingStrategy("EDR_"))
				.configure(base + "/hibernate.cfg.xml")
				.buildSessionFactory();
		} catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch(RuntimeException e){
			e.printStackTrace();
			throw e;
		} catch(Error e){
			e.printStackTrace();
			throw e;
		}
	}
}
