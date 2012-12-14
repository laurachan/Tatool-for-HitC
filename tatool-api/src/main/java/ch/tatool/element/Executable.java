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
 * Executable element interface.
 * 
 * ExecutableElements need to implement it in order to take part in the
 * execution flow.
 * 
 * @author Michael Ruflin
 */
public interface Executable extends Node {

	/**
	 * Execute the element. The Executor will hand over complete control over
	 * the execution to this element.
	 * 
	 * initializeElement is normally called before calling execute. The only
	 * exception is the case where this element has previously returned an
	 * outcome of SUSPENDED, in which case obviously NO re-initialization is
	 * required/desired.
	 * 
	 * This method gets called in a separate thread by the system. The element
	 * has to take care to block the thread until the element has finished
	 * (either through user input or by calling cancelExecution). Once the
	 * method is called, the element has full control over the ExecutionDisplay.
	 */
	public void execute();

	/**
	 * Cancels the execution.
	 * 
	 * This method allows the system to gracefully cancel an execution. The
	 * Executable should try to finish any ongoing work and return execute as
	 * soon as possible.
	 */
	public void cancel();

	/**
	 * Set the execution context.
	 * 
	 * This method is called by the Executor before and after calling execute to
	 * set / remove the context from the element.
	 * 
	 * @param context
	 */
	public void setExecutionContext(ExecutionContext context);
}
