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
package ch.tatool.core.element.handler.score;

import java.util.HashSet;
import java.util.Set;

import ch.tatool.core.data.Misc;
import ch.tatool.data.DataContainer;
import ch.tatool.data.Module;

/**
 * Utility methods for points and level handlers.
 * @author Michael Ruflin
 */
public class PointsAndLevelUtils {
	
	/** Property name used to store the registered PointsAndLevelHandlers in the module with "" as element name.
	 */
	public static final String PROPERTY_REGISTERED_POINTS_AND_LEVEL_HANDLERS = "registeredPointsAndLevelHandlers";
	
	private static final String REGISTERED_MARKER = "PointsAndLevelHandlerRegistered";
	
	/**
	 * Get all registered point and level handlers found for a given module.
	 * 
	 * @return a Set containing the unique ids of all registered PointsAndLevelHandlers
	 */
	public static Set<String> getRegisteredPointsAndLevelHandlers(Module module) {
		return fetchStringSetProperty(module, "", PROPERTY_REGISTERED_POINTS_AND_LEVEL_HANDLERS, ",");
	}
	
	public static String getPointsAndLevelHandlerDescription(Module module, String handlerId) {
		return Misc.getDescriptionProperty().getValue(module, handlerId);
	}
	
	/**
	 * Register a point and level handler onto the module.
	 * 
	 * This method can be called by PointsAndLevelHandlers as often as they wish.
	 * Upon first call for a handler, its id is registered in the module. If the handler is
	 * already registered, then this method won't have any effect.
	 */
	public static void registerPointAndLevelHandler(Module module, PointsAndLevelHandler handler) {
		// make sure we don't do this unnecessarily
		if (handler.getProperty(REGISTERED_MARKER) != null) {
			return;
		}
		
		// add the id to the id set
		String id = handler.getId();
		Set<String> handlerIds = getRegisteredPointsAndLevelHandlers(module);
		handlerIds.add(id);
		setStringSetProperty(module, "", PROPERTY_REGISTERED_POINTS_AND_LEVEL_HANDLERS, handlerIds, ",");
		
		// Also store the description of the handler
		Misc.getDescriptionProperty().setValue(module, handler);
		
		// mark the handler as registered (with the effect that this method is run for each handler
		// at most once per module instance
		handler.setProperty(REGISTERED_MARKER, "registered");
	}
    
    /*
     * Support for string list properties 
     */
    private static Set<String> fetchStringSetProperty(DataContainer dataContainer, String node, String propertyName, String delimiter) {
    	String propertyValue = dataContainer.getValue(node, propertyName);
    	if (propertyValue != null) {
    		String[] values = propertyValue.split(delimiter);
    		Set<String> valueSet = new HashSet<String>();
    		for (String value : values) {
    			valueSet.add(value);
    		}
    		return valueSet;
    	} else {
    		return new HashSet<String>();
    	}
    }
    
    private static void setStringSetProperty(DataContainer dataContainer, String node, String propertyName, Set<String> values, String delimiter) {
    	StringBuilder builder = new StringBuilder();
    	for (String s : values) {
    		builder.append(s).append(delimiter);
    	}
    	// cut off the last added comma
    	int len = builder.length();
    	if (len > 0) {
    		builder.deleteCharAt(len - 1);
    	}
    	String value = builder.toString();
    	dataContainer.putValue(node, propertyName, value);
    }
}
