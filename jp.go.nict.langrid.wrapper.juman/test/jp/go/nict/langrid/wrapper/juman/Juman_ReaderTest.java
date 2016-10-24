package jp.go.nict.langrid.wrapper.juman;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import jp.go.nict.langrid.commons.lang.ClassResource;
import jp.go.nict.langrid.commons.lang.ClassResourceLoader;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;

public class Juman_ReaderTest {
	@Test
	public void test_juman() throws Throwable{
		ClassResourceLoader.load(this);
		System.out.println(JSON.encode(
				new Juman().convertResult(new ByteArrayInputStream(juman), "UTF-8"),
				true
				));
	}

	@Test
	public void test_jumanpp() throws Throwable{
		ClassResourceLoader.load(this);
		System.out.println(JSON.encode(
				new Juman().convertResult(new ByteArrayInputStream(jumanpp), "UTF-8"),
				true
				));
	}

	@ClassResource(path="Juman_ReaderTest_juman.txt")
	private byte[] juman;

	@ClassResource(path="Juman_ReaderTest_jumanpp.txt")
	private byte[] jumanpp;
}
