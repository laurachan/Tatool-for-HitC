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
package ch.tatool.element;

/**
 * Objects that implement this interface get initialized upon first or
 * subsequent uses.
 * 
 * @author Michael Ruflin
 */
public interface Initializable {

	/**
	 * This method is called whenever an Element is pushed onto the
	 * ExecutionStack. Sole exception is the primary child Element of a
	 * CompoundElement, which is only executed once, together with the
	 * CompoundElement initialization.
	 */
	public void initialize(Element element);
}
