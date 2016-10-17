/*
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
package jp.go.nict.langrid.wrapper.echo;

import static jp.go.nict.langrid.language.LangridLanguageTags.any;

import java.util.Arrays;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.ws_1_2.translation.AbstractTranslationService;

public class EchoTranslation extends AbstractTranslationService{
	public EchoTranslation(){
		setSupportedLanguagePairs(Arrays.asList(new LanguagePair(
				any, any)));
	}
	@Override
	protected String doTranslation(Language sourceLang, Language targetLang,
			String source) throws InvalidParameterException,
			ProcessFailedException {
		return source;
	}
}
