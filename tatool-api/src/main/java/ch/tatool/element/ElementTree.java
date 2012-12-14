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

import java.util.List;

/** Contains elements to be executed. */
public interface ElementTree {

	/**
	 * Get the root execution element.
	 */
	public Element getRootElement();

	/**
	 * Set the root execution element. This method will automatically clear the
	 * execution stack and push the root onto the stack.
	 */
	public void setRootElement(Element element);

	/**
	 * Get the current execution stack, with the first element being the root
	 * element, the last element the one that contains the currently executed
	 * element.
	 */
	public List<Element> getElementStack();

	/** Push a new element onto the stack. */
	public void pushElement(Element elementToPush);

	/** Pops the most recently added execution element from the stack. */
	public void popElement();

	/** Get the top element of the stack. */
	public Element getTop();
}
