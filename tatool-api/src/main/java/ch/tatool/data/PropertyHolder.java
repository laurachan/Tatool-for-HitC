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

import java.util.Set;

/**
 * Classes implementing this interface provide support for storing properties.
 */
public interface PropertyHolder {

    /**
     * Get the id of this property holder, usable to store the property in a DataContainer. 
     * The id can be arbitrary. For tree structures it is encouraged to concatenate
     * the node ids to form unique ids among the complete tree structure.
     */
    public String getId();
	
    /**
     * Get a specific element property.
     * 
     * @return the property value or null if the property is not present.
     */
    public Object getProperty(String key);

    /**
     * Set a specific element property.
     */
    public void setProperty(String key, Object value);
    
    /**
     * Remove the property for the given key.
     */
    public void removeProperty(String key);
    
    /**
     * Get all properties contained in this holder
     */
    public Set<String> getKeys();
    
    /**
     * Clear all properties
     */
    public void clearProperties();
}
