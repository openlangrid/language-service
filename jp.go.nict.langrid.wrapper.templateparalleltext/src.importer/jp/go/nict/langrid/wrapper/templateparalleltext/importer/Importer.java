package jp.go.nict.langrid.wrapper.templateparalleltext.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.lang.reflect.FieldUtil;
import jp.go.nict.langrid.wrapper.templateparalleltext.db.ConnectionManager;

public class Importer {
	public static void main(String[] args) throws Exception{
		String config = "config.properties";
		if(args.length > 0){
			config = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(config));
		
		ConnectionManager m = new ConnectionManager(
				"org.postgresql.Driver"
				, props.getProperty("db.url")
				, props.getProperty("db.username")
				, props.getProperty("db.password")
				, 100, 30, 10000, 10
				);
		String prefix = props.getProperty("db.tablePrefix");

		// import from Google Spreadsheet
		String mode = props.getProperty("source.mode");
		if(mode == null || mode.equals("LocalCSV")){
			String categories = getProp(props, "source.lc.category.filenames");
			String paralleltexts = getProp(props, "source.lc.paralleltext.filenames");
			importData(
					new FilesDataProvider(categories.split(", ?"))
					, new FilesDataProvider(paralleltexts.split(", ?"))
					, m, prefix);
		} else{
			String accessToken = getProp(props, "source.gs.accessToken");
			String categoryDoc = getProp(props, "source.gs.category.docname");
			String categorySheets = getProp(props, "source.gs.category.sheetnames");
			String paralleltextDoc = getProp(props, "source.gs.paralleltext.docname");
			String paralleltextSheets = getProp(props, "source.gs.paralleltext.sheetnames");
			importData(
					new GSheetDataProvider(accessToken, categoryDoc, categorySheets.split(", ?"))
					, new GSheetDataProvider(accessToken, paralleltextDoc, paralleltextSheets.split(", ?"))
					, m, prefix);
		}
	}

	private static String getProp(Properties props, String name)
	throws IllegalArgumentException{
		String v = props.getProperty(name);
		if(v == null) throw new IllegalArgumentException(name + " not found.");
		return v;
	}

	private static void importData(
			Iterable<String[]> categoryData, Iterable<String[]> templateData
			, ConnectionManager manager, String prefix)
	throws SQLException, IOException{
		Connection c = manager.getConnection();
		c.setAutoCommit(false);
		try{
			executeQuery(c, String.format(deleteCategoriesTemplatesQuery, prefix));
			executeQuery(c, String.format(deleteCategoriesQuery, prefix));
			executeQuery(c, String.format(deleteTemplatesQuery, prefix));
			Set<String> categories = importCategory(
					c, String.format(insertCategoryQuery, prefix)
					, categoryData
					);
			Set<String> unknownCats = new TreeSet<String>();
			int count = importTemplates(c, categories, unknownCats
					, String.format(selectTemplateSeqCurValQuery, prefix)
					, String.format(insertTemplateQuery, prefix)
					, String.format(insertCategoriesTemplatesQuery, prefix)
					, templateData);
			System.out.println("committing imports....");
			c.commit();

			System.out.println(String.format(
					"%d categories and %d templates added."
					, categories.size(), count));
			if(unknownCats.size() == 0){
				System.out.println("no unknown categories.");
			} else{
				System.out.println("unknown categories:");
				for(String cat : unknownCats){
					System.out.println("  " + cat);
				}
			}
			System.out.println("done.");
		} finally{
		}
	}

	private static void executeQuery(Connection con, String query)
	throws java.sql.SQLException{
		Statement s = con.createStatement();
		try{
			s.execute(query);
		} finally{
			s.close();
		}
	}

	private static Set<String> importCategory(
			Connection con, String insertQuery, Iterable<String[]> data)
	throws IOException, SQLException{
		Set<String> ret = new HashSet<String>();
		int c = 0;
		PreparedStatement s = con.prepareStatement(insertQuery);
		for(String[] values : data){
			if(values.length == 0) continue;
			if(values[0].length() == 0) continue;
			String category_id = values[0];
			String ja = getText(values, 2, 3, 4);
			String en = getText(values, 6, 7, 8);
			String vi = getText(values, 10, 11, 12);
			s.setString(1, category_id);
			s.setString(2, ja);
			s.setString(3, en);
			s.setString(4, vi);
			s.executeUpdate();
			ret.add(category_id);
			c++;
			if((c % 20) == 0){
				System.out.println(c + " categories imported.");
			}
		}
		if((c % 20) != 0){
			System.out.println(c + " categories imported.");
		}
		return ret;
	}

	private static int importTemplates(
			Connection con, Set<String> categories, Set<String> unknownCats
			, String selectSeqCurValQuery, String insertTemplateQuery, String insertCategoriesTemplatesQuery
			, Iterable<String[]> data)
	throws IOException, SQLException{
		int c = 0;
		Set<String> duplicatedIds = new TreeSet<String>();
		PreparedStatement ts = con.prepareStatement(insertTemplateQuery);
		PreparedStatement cts = con.prepareStatement(insertCategoriesTemplatesQuery);
		PreparedStatement curval = con.prepareStatement(selectSeqCurValQuery);
		for(String[] values : data){
			if(values.length < 7) continue;
			String tid = values[1].trim();
			if(tid.length() == 0){
				System.out.println(String.format(
						"skipped because empty id: ja[%s], en[%s], vi[%s]"
						, values[4], values[5], values[6]
						));
				continue;
			}
			if(duplicatedIds.contains(tid)) continue;

			String[] dupIds = values[2].split("\n|\r| |,");
			String ja = values[3].trim();
			String en = Normalizer.normalize(values[4].trim(), Form.NFKC);
			String vi = Normalizer.normalize(values[5].trim(), Form.NFKC);
			String[] cats = values[7].split(",");

			for(String di : dupIds){
				di = di.trim();
				if(di.length() > 0)
					duplicatedIds.add(di);
			}
			if(ja.length() == 0 || en.length() == 0 || vi.length() == 0){
				System.out.println(String.format(
						"tid: %s, lang: %s"
						, tid
						, (ja.isEmpty() ? "ja," : "") + (en.isEmpty() ? "en," : "")
						  + (vi.isEmpty() ? "vi" : "")
						));
//				System.out.println(String.format(
//						"skipped because one of text is empty: tid[%s], ja[%s], en[%s], vi[%s]"
//						, tid, values[3], values[4], values[5]
//						));
				continue;
			}

			ts.setString(1, tid);
			ts.setString(2, ja);
			ts.setString(3, en);
			ts.setString(4, vi);
			ts.executeUpdate();
			ResultSet gen = curval.executeQuery();
			if(!gen.next()){
				throw new RuntimeException("");
			}
			int id = gen.getInt(1);
			for(String cat : cats){
				cat = cat.trim();
				if(cat.length() == 0) continue;
				if(!categories.contains(cat)){
					System.out.println("category reference error: " + cat);
					unknownCats.add(cat);
					continue;
				}
				cts.setString(1, cat);
				cts.setInt(2, id);
				cts.executeUpdate();
			}
			c++;
			if((c % 20) == 0){
				System.out.println(c + " templates imported.");
			}
		}
		if((c % 20) != 0){
			System.out.println(c + " templates imported.");
		}
		System.out.println("duplicated ids: " + duplicatedIds);
		return c;
	}

	private static String load(String resource) throws IOException{
		Class<Importer> clazz = Importer.class;
		InputStream is = clazz.getResourceAsStream(resource);
		try{
			return StreamUtil.readAsString(is, "UTF-8");
		} finally{
			is.close();
		}
	}

	private static String getText(String[] texts, int i1, int i2, int i3){
		StringBuilder b = new StringBuilder();
		if(i1 < texts.length){
			b.append(texts[i1]);
		}
		b.append(":");
		if(i2 < texts.length){
			b.append(texts[i2]);
		}
		b.append(":");
		if(i3 < texts.length){
			b.append(texts[i3]);
		}
		String s = b.toString();
		if(s.equals("::")) return "";
		return s;
	}

	private static String deleteCategoriesQuery;
	private static String deleteCategoriesTemplatesQuery;
	private static String deleteTemplatesQuery;
	private static String insertCategoriesTemplatesQuery;
	private static String insertCategoryQuery;
	private static String insertTemplateQuery;
	private static String selectTemplateSeqCurValQuery;
	static{
		try {
			for(Field f : FieldUtil.listDeclaredFields(Importer.class, ".*Query$")){
				if((f.getModifiers() & Modifier.STATIC) != 0){
					f.set(null, load(f.getName() + ".sql"));
				}
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
