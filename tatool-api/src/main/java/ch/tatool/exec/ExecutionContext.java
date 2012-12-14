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
package ch.tatool.exec;

import java.util.List;

import ch.tatool.data.DataService;
import ch.tatool.data.PropertyHolder;
import ch.tatool.display.ExecutionDisplay;
import ch.tatool.element.Element;
import ch.tatool.element.Executable;

/**
 * Execution context
 * 
 * This is a one stop shop for elements during execution. Everything they
 * possibly need during execution is contained in this object.
 * 
 * Please note that the context can change from one to the next execution,
 * therefore do not cache the object inside the elements.
 * 
 * @author Michael Ruflin
 */
public interface ExecutionContext extends PropertyHolder {

	// GENERAL

	/**
	 * Get the current phase of the execution.
	 * 
	 * @return the current phase
	 */
	public ExecutionPhase getPhase();

	// SERVICES

	/**
	 * Get the executor of this module. Provides access to execution specific
	 * functionality.
	 * 
	 * @return the Executor object
	 */
	public Executor getExecutor();

	/**
	 * Get the data service that allows access to the data layer of Tatool.
	 * 
	 * @return the DataService object
	 */
	public DataService getDataService();

	/**
	 * Get the display object that allows the elements to interact with the
	 * display.
	 * 
	 * @return the ExecutionDisplay object
	 */
	public ExecutionDisplay getExecutionDisplay();

	/**
	 * Get access to the execution data.
	 */
	public ExecutionData getExecutionData();

	// DATA

	/**
	 * Get the currently scheduled or executed ExecutableElement. This object is
	 * available between PRE_PROCESS and POST_PROCESS.
	 * 
	 * @return the ExecutableElement object or null
	 */
	public Executable getActiveExecutable();

	/**
	 * Get the active Element.
	 */
	public Element getActiveElement();

	/**
	 * Get the element stack, the stack of elements being executed. This object
	 * is always available, but empty before and including SESSION_START and
	 * after and including SESSION_END
	 * 
	 * @return the list of elements, beginning with the root element, containing
	 *         the executable element as last element
	 */
	public List<Element> getElementStack();
}
