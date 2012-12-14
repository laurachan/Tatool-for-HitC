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
package ch.tatool.module;

import java.io.IOException;

import javax.swing.JFrame;

import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;

/**
 * Interface describing a module creator.
 * 
 * @author Michael Ruflin
 */
public interface ModuleCreator {

	/** Get an id of this creator. */
	public String getCreatorId();
	
	/** Return the name of this creator. */
	public String getCreatorName();

	/** Display a creator.
	 * @throws IOException 
	 * @parent parent the parent window calling this creator
	 */
	public void executeCreator(JFrame parent, UserAccount account, ModuleService moduleService, Callback callback) throws IOException;
	
	/** Hide possibly open windows. */
	// PENDING: improve/remove
	public void hideCreator();
	
	/**
	 * Get the current messages object used for i18n.
	 * 
	 * @return messages
	 */
	public Messages getMessages();
	
	/**
	 * Set the current message object used for i18n.
	 * 
	 * @param messages The Messages object
	 */
	public void setMessages(Messages messages);

	/**
	 * Allows the creators to register created modules
	 */
	public interface Callback {
		// PENDING: this callback is bad...
		public void closeDialog(Module newlyCreatedModule); 
	}
}
