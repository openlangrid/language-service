/*
 * $Id: JTreeTaggerProcessor.java 27012 2009-02-27 06:31:12Z kamiya $
 *
 * This is a program to wrap language resources as Web services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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
package jp.go.nict.langrid.wrapper.bindedservice;

import java.util.Arrays;

import jp.go.nict.langrid.client.axis.ProxySelectingAxisSocketFactory;
import jp.go.nict.langrid.commons.cs.binding.BindingNode;
import jp.go.nict.langrid.commons.net.proxy.pac.PacUtil;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.net.SocketFactory;
import org.junit.Test;

public class BindedTranslationTest {
	@Test
	public void test() throws Exception{
		BindedTranslation s = new BindedTranslation();
		s.setServiceId("TwoHopTranslationJa");
		s.setBindings(Arrays.asList(new BindingNode[]{
				new BindingNode("FirstTranslationPL", "KyotoUJServer")
				, new BindingNode("SecondTranslationPL", "KyotoUJServer")
				}));
		System.out.println(s.translate("en", "ko", "hello"));
		System.out.println(s.translate("en", "zh", "hello"));
	}

	static{
		try{
			System.setProperty("java.net.useSystemProxies", "true");
		}catch(SecurityException e){
		}
		AxisProperties.setProperty(SocketFactory.class.getName(),
				ProxySelectingAxisSocketFactory.class.getName());
		PacUtil.setupDefaultProxySelector();
	}
}
