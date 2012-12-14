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

import ch.tatool.exec.ExecutionContext;

/**
 * Implemented by handlers that take care of adapting the execution tree.
 *  
 * @author Michael Ruflin
 */
public interface ElementSelector {
	/**
	 * Called on the top element of the stack.
	 * 
	 * @return true if the stack was adapted
	 */
	public boolean selectNextElement(ExecutionContext executionContext);
}
