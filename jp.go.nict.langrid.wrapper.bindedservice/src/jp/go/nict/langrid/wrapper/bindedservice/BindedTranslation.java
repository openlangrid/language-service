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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import jp.go.nict.langrid.client.ClientFactory;
import jp.go.nict.langrid.client.RequestAttributes;
import jp.go.nict.langrid.client.ws_1_2.impl.axis.LangridAxisClientFactory;
import jp.go.nict.langrid.commons.cs.binding.BindingNode;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguagePairNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguagePairException;
import jp.go.nict.langrid.service_1_2.translation.TranslationService;
import jp.go.nict.langrid.servicecontainer.service.AbstractService;

public class BindedTranslation
extends AbstractService
implements TranslationService{
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setBindings(Collection<BindingNode> bindings) {
		this.bindings = bindings;
	}

	@Override
	public String translate(String sourceLang, String targetLang, String source)
			throws AccessLimitExceededException, InvalidParameterException,
			LanguagePairNotUniquelyDecidedException,
			NoAccessPermissionException, ProcessFailedException,
			NoValidEndpointsException, ServerBusyException,
			ServiceNotActiveException, ServiceNotFoundException,
			UnsupportedLanguagePairException {
		try{
			TranslationService service = factory.create(
					TranslationService.class
					, new URL("http://langrid.org/service_manager/invoker/" + serviceId)
					);
			RequestAttributes reqAttrs = (RequestAttributes)service;
			reqAttrs.setUserId("userId");
			reqAttrs.setPassword("password");
			reqAttrs.getTreeBindings().addAll(bindings);
			return service.translate(sourceLang, targetLang, source);
		} catch(MalformedURLException e){
			throw new ProcessFailedException(e);
		}
	}

	private String serviceId;
	private Collection<BindingNode> bindings;
	
	private static ClientFactory factory = LangridAxisClientFactory.getInstance();
}
