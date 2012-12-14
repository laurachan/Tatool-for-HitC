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
package ch.tatool.app.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.module.ModuleCreator;
import ch.tatool.module.ModuleCreatorRegistry;

/**
 * Default registry implementation.
 * Objects can either be defined by adding them to the registry directly or by using
 * a registry registration bean.
 * 
 * Please note that creators need to be unique in their id, otherwise they overwrite each other!
 * 
 * @author Michael Ruflin
 */
public class DefaultModuleCreatorRegistry implements ModuleCreatorRegistry {

	Logger logger = LoggerFactory.getLogger(DefaultModuleCreatorRegistry.class);
	
	private Map<String, ModuleCreator> creatorsMap;
	
	public DefaultModuleCreatorRegistry() {
		creatorsMap = new TreeMap<String, ModuleCreator>();
	}

	/** Get the list of creators. */
	public List<ModuleCreator> getCreators() {
		return new ArrayList<ModuleCreator>(creatorsMap.values());
	}

	/** Get a creator given its id.
	 * @param id the id of the creator
	 * @return the ModuleCreator if available or null if not found 
	 */
	public ModuleCreator getCreatorById(String id) {
		return creatorsMap.get(id);
	}
	
	/** Add a list of creators.
	 * @param creators the creators to add
	 */
	public void addCreators(List<ModuleCreator> creators) {
		for (ModuleCreator c : creators) {
			addCreator(c);
		}
	}
	
	/**
	 * Add a creator to the registry.
	 * @param creator the creator to add 
	 */
	public void addCreator(ModuleCreator creator) {
		creatorsMap.put(creator.getCreatorId(), creator);
		if (logger.isInfoEnabled()) {
			logger.info("Registered creator \"" + creator.getCreatorId() + "\".");
		}
	}
	
	/** Remove all registered creators from the registry. */
	public void removeAll() {
		creatorsMap.clear();
	}
}
