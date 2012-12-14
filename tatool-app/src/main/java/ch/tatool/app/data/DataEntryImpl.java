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
package ch.tatool.app.data;

import ch.tatool.data.DataContainer.Entry;

/**
 * Encapsulates a single property value for an item. The values are stored as triple (item id, name, value)
 * and are stored either in the module or the trial.
 * 
 * @author Michael Ruflin
 */
public class DataEntryImpl implements Entry, Comparable<DataEntryImpl> {
	/** Id of the item property. This value is used by the database only. */
    private Long id;
    
    /** Complete id of the item. */
    private String item;
    
    /** Name of the property. */
    private String name;
    
    /** Value of the property. */
    private String value;

    /** Get the item id. */
    public String getNodeId() {
        return item;
    }
    
    /** Set the item id. */
    public void setNodeId(String item) {
        this.item = item;
    }
    
    /** Get the property name. */
    public String getName() {
        return name;
    }
    
    /** Set the property name. */
    public void setName(String name) {
        this.name = name;
    }
    
    /** Get the property value. */
    public String getValue() {
        return value;
    }
    
    /** Set the property value. */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Get the id of this object.
     * Note that this property is normally set the database layer (e.g. hibernate)
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Set the id of this object.
     * Note that this property is normally set the database layer (e.g. hibernate)
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Compare this property to another property.
     * First compares the item names, then the property names.
     */
    public int compareTo(DataEntryImpl other) {
        int result = other.item.compareTo(item);
        if (result != 0) {
            return result;
        } else {
            return other.name.compareTo(name);
        }
    }
}
