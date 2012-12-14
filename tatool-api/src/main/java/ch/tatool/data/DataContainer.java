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
package ch.tatool.data;

import java.util.Collection;

/**
 * A DataContainer provides persistent storage for data "entries", with each entry being a triplet
 * of nodeId, name and value.
 * 
 * Currently three different objects allow storing such data: Trial, ModuleSession and Module. 
 * 
 * @author Michael Ruflin
 */
public interface DataContainer {
	
	/**
	 * Get a value given a nodeId and propertyName
	 */
    public String getValue(String nodeId, String propertyName);
    
    /**
     * Get whether a given property value is set or not.
     * @return true if an entry for nodeId and propertyName exists. 
     */
    public boolean containsValue(String nodeId, String propertyName);
    
    /**
     * Add a value to the container. Any existing values will simply be replaced.
     * @return the previous value set for the given property or null if not previously set
     */
    public String putValue(String nodeId, String propertyName, String propertyValue);
    
    /**
     * Get all entries inside this data container.
     * 
     * @return a read-only collection of all entries stored in this data container
     */
    public Collection<Entry> getEntries();

    /**
     * Encapsulates a single data triplet (nodeId, name, value)
     */
    public interface Entry {
        /** Get the node id. */
        public String getNodeId();
        
        /** Get the property name. */
        public String getName();
        
        /** Get the property value. */
        public String getValue();
    }
}
