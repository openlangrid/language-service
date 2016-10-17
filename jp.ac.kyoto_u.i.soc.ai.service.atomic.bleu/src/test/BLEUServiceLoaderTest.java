package test;

import static org.junit.Assert.*;
import jp.go.nict.langrid.service_1_2.similaritycalculation.SimilarityCalculationService;
import jp.go.nict.langrid.servicecontainer.handler.ServiceLoader;
import jp.go.nict.langrid.webapps.blank.EclipseServiceContext;

import org.junit.Test;


public class BLEUServiceLoaderTest {

	@Test
	public void testBLEU() throws Exception{
		SimilarityCalculationService service = new ServiceLoader(
				new EclipseServiceContext()).load("BLEU", SimilarityCalculationService.class);

		assertEquals(1.0, service.calculate("en", "hello", "hello"),0.01);
	}

}
