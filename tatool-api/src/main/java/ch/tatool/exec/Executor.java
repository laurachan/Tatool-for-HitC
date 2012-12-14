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

import ch.tatool.element.ExecutionStrategy;
import ch.tatool.element.ElementTree;

/**
 * New executor implementation. Expects a single Element to be executed.
 * 
 * @author Michael Ruflin
 */
public interface Executor {
	
	/**
	 * Get the manager that holds the PhaseExecutables. Contrary to ExecutableElements, a PhaseRunnable
	 * is run by the executor directly without firing any events before or after execution.
	 * It can be used to pause the executor or do other quick work inbetween elements.
	 */
	public PhaseRunnableManager getPhaseRunnableManager();
	
	/**
	 * Get the execution tree this executor works on.
	 */
	public ElementTree getExecutionTree();
	
	/** Set the selector strategy. */
	public void setExecutionStrategy(ExecutionStrategy selectorStrategy);
	
	/** Get the selector strategy. */
	public ExecutionStrategy getExecutionStrategy();
	
	/**
	 * Stops the execution of the current element.
	 */
	public void stopCurrentElementExecution();
	
	/**
	 * Stops the current execution when called.
	 */
	public void stopExecution();
}
