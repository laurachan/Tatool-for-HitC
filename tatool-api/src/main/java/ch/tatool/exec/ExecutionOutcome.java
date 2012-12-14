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
 * The Execution Outcome defines the outcome of an ExecutableElement.
 * 
 * @author Michael Ruflin
 *
 */
public interface ExecutionOutcome {
	
	public static final String FINISHED = "FINISHED";
	public static final String SKIP = "SKIP";
	public static final String SUSPENDED = "SUSPENDED";
	public static final String STOPPED = "STOPPED";
	
}
