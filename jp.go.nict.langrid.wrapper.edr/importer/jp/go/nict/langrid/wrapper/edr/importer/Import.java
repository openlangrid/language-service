/*
 * $Id: Import.java 4109 2008-08-28 08:58:33Z nakaguchi $
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
package jp.go.nict.langrid.wrapper.edr.importer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.go.nict.langrid.wrapper.edr.hibernate.EdrSessionFactory;
import jp.go.nict.langrid.wrapper.edr.importer.reader.ConceptRecordReader;
import jp.go.nict.langrid.wrapper.edr.importer.reader.ConceptRelationRecordReader;
import jp.go.nict.langrid.wrapper.edr.importer.reader.EConceptWordRecordReader;
import jp.go.nict.langrid.wrapper.edr.importer.reader.EJTranslationRecordReader;
import jp.go.nict.langrid.wrapper.edr.importer.reader.JETranslationRecordReader;
import jp.go.nict.langrid.wrapper.edr.importer.reader.JConceptWordRecordReader;
import jp.go.nict.langrid.wrapper.edr.importer.reader.RecordReader;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Import {
	public static void main(String[] args) throws Exception{
		JNDIDataSourceUtil.setUpDataSourceAndJDNIResource();
		
		SessionFactory f = EdrSessionFactory.getSessionFactory();
		importRecords(f, new JConceptWordRecordReader(
				newEUCFileReader("EDR/WordDictionary/JWord/JWD.DIC")
				));
		importRecords(f, new JConceptWordRecordReader(
				newEUCFileReader("EDR/WordDictionary/JWord/JWC.DIC")
				));
		importRecords(f, new EConceptWordRecordReader(
				newEUCFileReader("EDR/WordDictionary/EWord/EWD.DIC")
				));
		importRecords(f, new EJTranslationRecordReader(
				newEUCFileReader("EDR/BilingualDictionary/EJ/EJB.DIC")
				));
		importRecords(f, new JETranslationRecordReader(
				newEUCFileReader("EDR/BilingualDictionary/JE/JEB.DIC")
				));
		importRecords(f, new ConceptRecordReader(
				newEUCFileReader("EDR/ConceptDictionary/CPH.DIC")
				));
		importRecords(f, new ConceptRelationRecordReader(
				newEUCFileReader("EDR/ConceptDictionary/CPC.DIC")
				));
	}

	private static <T> void importRecords(SessionFactory factory
			, RecordReader<T> reader)
	throws Exception
	{
		Session s = factory.getCurrentSession();
		s.beginTransaction();

		int i = 0;
		T record = null;
		while((record = reader.readRecord()) != null){
			i++;
			if(i % 100 == 0){
				System.out.println(String.format(
						"%06d result: %s", i, record
						));
				s.getTransaction().commit();
				s = factory.getCurrentSession();
				s.beginTransaction();
			}
			s.save(record);
//			if(i == 1000) break;
		}
		reader.close();
		s.getTransaction().commit();
	}

	private static InputStreamReader newEUCFileReader(
			String fileName)
	throws FileNotFoundException, IOException
	{
		return new InputStreamReader(
				new FileInputStream(fileName)
				, "EUC_JP"
				);
	}

}
