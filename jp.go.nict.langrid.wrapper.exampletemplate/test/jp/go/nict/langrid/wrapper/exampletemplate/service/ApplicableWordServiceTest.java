package jp.go.nict.langrid.wrapper.exampletemplate.service;

import org.apache.commons.lang.builder.ToStringBuilder;

import jp.go.nict.langrid.wrapper.exampletemplate.ApplicableWordService;
import jp.go.nict.langrid.wrapper.exampletemplate.db.ConnectionManager;
import junit.framework.TestCase;

public class ApplicableWordServiceTest extends TestCase {

	private ApplicableWordService service;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.service = new ApplicableWordService(){
			@Override
			protected boolean connectionInitializer() {
				connectionManager = new ConnectionManager(
						"org.postgresql.Driver"
						, "jdbc:postgresql://localhost:5433/example_template"
						, "langrid"
						, ""
						, 1, 1, 10000, -1);
				return true;
			}
		};
	}

	public void testGetApplicableWords_normal1() {
		try {
			ApplicableWord[] dist = service.getApplicableWords("ja", new String[]{"1"});
			for (ApplicableWord word : dist) {
				System.out.println(ToStringBuilder.reflectionToString(word));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testGetApplicableWords_normal2() {
		try {
			ApplicableWord[] dist = service.getApplicableWords("ja", new String[]{"2", "3"});
			for (ApplicableWord word : dist) {
				System.out.println(ToStringBuilder.reflectionToString(word));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testGetApplicableWords_abnormal1() {
		try {
			// categoryIdsがNull
			service.getApplicableWords("ja", null);
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void testGetApplicableWords_abnormal2() {
		try {
			// categoryIdsが空っぽ
			service.getApplicableWords("ja", new String[]{});
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
