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

/**
 * A Property is a read-write object of values from/to elements and from/to DataContainers.
 * 
 * @author Michael Ruflin
 */
public interface Property<T> {
	
	public String getPropertyName();
	public Class<?> getPropertyType();
	
	// Node specific methods
	public T getValue(PropertyHolder holder);
	public T getValue(PropertyHolder holder, T defaultValue);
	public void setValue(PropertyHolder holder, T value);
	public void clearValue(PropertyHolder holder);
	
	// DataContainer specific
	public String getStringValue(DataContainer dataContainer, PropertyHolder holder);
	public String getStringValue(DataContainer dataContainer, String id);
	public String getStringValue(DataContainer dataContainer, PropertyHolder holder, String defaultValue);
	public String getStringValue(DataContainer dataContainer, String id, String defaultValue);
	
	public void setStringValue(DataContainer dataContainer, PropertyHolder holder, String value);
	public void setStringValue(DataContainer dataContainer, String id, String value);
	
	/**
	 * Get a value stored in a DataContainer.
	 * Please note that only certain types can be restored, @see PropertyConvertor class-
	 * 
	 * @throws UnsupportedOperationException if the property does not support conversion from a string.
	 */
	public T getValue(DataContainer dataContainer, PropertyHolder holder);
	public T getValue(DataContainer dataContainer, String id);
	public T getValue(DataContainer dataContainer, PropertyHolder holder, T defaultValue);
	public T getValue(DataContainer dataContainer, String id, T defaultValue);
	
	public void setValue(DataContainer dataContainer, PropertyHolder holder);
	public void setValue(DataContainer dataContainer, PropertyHolder holder, T value);
	public void setValue(DataContainer dataContainer, String id, T value);
	
	public void clearValue(DataContainer dataContainer, PropertyHolder holder);
	public void clearValue(DataContainer dataContainer, String id);
	
	/**
	 * Restores a value from a DataContainer back into the element.
	 * 
	 * Note: this method is a mere shortcut of getValue(DataContainer) followed by a setValue(Node).
	 * 
	 * @throws UnsupportedOperationException if the value type of this property can't be converted from a String. 
	 * @throws RuntimeException if any conversion errors occured.
	 * @see getValue 
	 */
	public void restoreValue(DataContainer dataContainer, PropertyHolder holder);
	public void restoreValue(DataContainer dataContainer, PropertyHolder holder, T defaultValue);
}
