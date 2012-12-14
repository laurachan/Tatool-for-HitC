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

/**
 * The different execution phases during execution of an Executable Element.
 * 
 * @author Michael Ruflin
 *
 */
public enum ExecutionPhase {
	/** Fired when execution starts. */
	EXECUTION_START,
	
	/** Fired after a new session has been created. */
	SESSION_START,
	
	/** Fired before the next element gets executed.
	 * At this moment an element has already been selected for execution. 
	 */
	PRE_EXECUTABLE_EXECUTION,
	
	/** Called on the element tree to let elements and handlers do stuff prior to element execution. */
	PRE_PROCESS,
	
	/** Phase in which the actual element is executed. */
	EXECUTE_EXECUTABLE,
	
	/** Allows elements in the element tree to do post processing of the inserted data. */
	POST_PROCESS,
	
	/** Called after the scheduled element has finished executing. All data has been saved already. */
	POST_EXECUTABLE_EXECUTION,
	
	/** Event fired before a session gets closed. */
	SESSION_FINISH,
	
	/** Fired when execution has finished. */
	EXECUTION_FINISH,
}
