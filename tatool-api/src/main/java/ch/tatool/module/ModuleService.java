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

import java.util.Map;
import java.util.Set;

import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;
import ch.tatool.export.DataExporter;

/**
 * Service for managing module objects in the system.
 * 
 * @author Michael Ruflin
 */
public interface ModuleService {

	/** Get a set of all available module instances. */
	public Set<Module.Info> getModules(UserAccount account);

	/**
	 * Create a new module given a description file.
	 * 
	 * @param 
	 * */
	public Module createModule(UserAccount account,
			Map<String, String> properties,
			Map<String, byte[]> binaryTrainingProperties,
			Map<String, DataExporter> moduleExporters);

	/** Load a complete module object. */
	public Module loadModule(Module.Info info);

	/**
	 * Save the current module instance. This method needs to be called after
	 * trial, session or module properties have been changed
	 */
	public void saveModule(Module module);

	/** Close a module */
	public void closeModule(Module module);

	/**
	 * Delete a module.
	 * 
	 * This method completely removes all module related data from the system.
	 */
	public void deleteModule(Module.Info info);

	/**
	 * Gets the Messages object used for i18n.
	 * 
	 * @return Messages object
	 */
	public Messages getMessages();

	/**
	 * Sets the Messages object used for i18n.
	 * 
	 * @param messages Messages object
	 */
	public void setMessages(Messages messages);

}
