/*
 * $Id: DaoException.java 79 2010-04-22 05:47:32Z t-nakaguchi $
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
package jp.go.nict.langrid.wrapper.common.db.dao;

public class DaoException extends Exception{
	public DaoException(String message){
		super(message);
	}

	public DaoException(Throwable cause){
		super(cause);
	}

	public DaoException(String message, Throwable cause){
		super(message, cause);
	}

	private static final long serialVersionUID = 2794773028676226800L;
}
