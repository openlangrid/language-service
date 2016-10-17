import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguageNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.wrapper.knp.KnpService;
import junit.framework.TestCase;


public class KnpServiceTest extends TestCase {
	private KnpService test = new KnpService();

	protected void setUp() throws Exception {
	}

	public void testDoParseDependency() {
		try {
			Chunk[] results = this.test.parseDependency("ja", "突然雨が降りました。");
			assertEquals("adverb", results[0].getMorphemes()[0].getPartOfSpeech());
			assertEquals("突然", results[0].getMorphemes()[0].getWord());
			assertEquals("verb", results[1].getMorphemes()[0].getPartOfSpeech());
			assertEquals("降り", results[1].getMorphemes()[0].getWord());
			assertEquals("降る", results[1].getMorphemes()[0].getLemma());
//			Chunk[] results2 = this.test.parseDependency("en", "突然雨が降りました。");
			
		} catch (LanguageNotUniquelyDecidedException e) {
			e.printStackTrace();
		} catch (UnsupportedLanguageException e) {
			;
		} catch (AccessLimitExceededException e) {
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		} catch (NoAccessPermissionException e) {
			e.printStackTrace();
		} catch (ProcessFailedException e) {
			e.printStackTrace();
		} catch (NoValidEndpointsException e) {
			e.printStackTrace();
		} catch (ServerBusyException e) {
			e.printStackTrace();
		} catch (ServiceNotActiveException e) {
			e.printStackTrace();
		} catch (ServiceNotFoundException e) {
			e.printStackTrace();
		}
	}

}
