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

import java.util.List;

/**
 * Holds all the registered creators.
 * Having this as a separate object allows easy reconfiguration through spring overwrites.
 * 
 * @author Michael Ruflin
 */
public interface ModuleCreatorRegistry {

	/** Get the available training creators. */
	public List<ModuleCreator> getCreators();
	
	/** Get a creator given its id.
	 * @param id the id of the creator
	 * @return the TrainingCreator if available or null if not found 
	 */
	public ModuleCreator getCreatorById(String id);
	
	/** Add a list of creators.
	 * @param creators the creators to add
	 */
	public void addCreators(List<ModuleCreator> creators);
	
	/** Add a creator to the registry.
	 * @param creator the creator to add 
	 */
	public void addCreator(ModuleCreator creator);
	
	/** Remove all registered creators from the registry. */
	public void removeAll();
}
