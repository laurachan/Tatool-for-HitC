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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ch.tatool.data.DataContainer;

/**
 * Class implementing element data support. This is only a helper class used by
 * the various data classes
 * 
 * @author Michael Ruflin
 */
public class DataContainerImpl implements DataContainer {
    
    /**
     * Contains the entries stored by this data container.
     * The implementation is used by hibernate, other code uses
     * Entry objects instead.
     */
    private Set<DataEntryImpl> entriesImpl;
    
    public DataContainerImpl() {
    	entriesImpl = new HashSet<DataEntryImpl>();
    }
    
	public Set<Entry> getEntries() { 
		return Collections.unmodifiableSet(new HashSet<Entry>(entriesImpl));
    }

	public Set<DataEntryImpl> getEntriesImpl() {
		return entriesImpl;
	}

	public void setEntriesImpl(Set<DataEntryImpl> entriesImpl) {
		this.entriesImpl = entriesImpl;
	}

	public String getValue(String item, String name) {
    	Entry entry = findEntry(entriesImpl, item, name);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }
    
    public String putValue(String item, String name, String value) {
        return setEntry(entriesImpl, item, name, value);
    }

    public boolean containsValue(String item, String name) {
    	Entry prop = findEntry(entriesImpl, item, name);
        return (prop != null);
    }
    
    /**
     * Implementation of finding an Entry in a properties set, given the item name and the item value
     * @param properties
     * @param item
     * @param name
     * @return
     */
    private DataEntryImpl findEntry(Set<DataEntryImpl> properties, String item, String name) {
        for (DataEntryImpl prop : properties) {
            if (prop.getNodeId().equals(item) && prop.getName().equals(name)) {
                return prop;
            }
        }
        return null;
    }
    
    /**
     * Set an Entry in the properties set,
     * given the item name, the property name and the property value.
     * 
     * If the Entry is already preset, its value is updated, otherwise a new Entry is added.
     * 
     * @return the previously set value for the given property
     */
    private String setEntry(Set<DataEntryImpl> properties, String item, String name, String value) {
        // check whether we already have that property, update in that case otherwise create a new one
    	DataEntryImpl property = findEntry(properties, item, name);
        if (property != null) {
        	String previousValue = property.getValue();
            property.setValue(value);
            return previousValue;
        } else {
            property = new DataEntryImpl();
            property.setNodeId(item);
            property.setName(name);
            property.setValue(value);
            properties.add(property);
            return null;
        }
    }   
}
