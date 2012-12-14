/*******************************************************************************
 * Copyright (c) 2011 Michael Ruflin, André Locher, Claudia von Bastian.
 * 
 * This file is part of Tatool.
 * 
 * Tatool is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * Tatool is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Tatool. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ch.tatool.app.gui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ch.tatool.data.Messages;

public class MessagesImpl implements Messages {
	private static final String BUNDLE_NAME = "ch.tatool.messages"; //$NON-NLS-1$

	private ResourceBundle resourceBundle; 
	
	private Locale currentLocale;

	private MessagesImpl() {
		resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
	}
	
	public void setLocale(Locale local) {
		currentLocale = local;
		resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, local);
	}

	public String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public Locale getLocale() {
		return currentLocale;
	}

	public String getLanguage() {
		return currentLocale.getLanguage();
	}
	
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
