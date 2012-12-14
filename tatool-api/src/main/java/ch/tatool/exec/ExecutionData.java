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

import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;

/**
 * The Execution Data object gives you access to the core data objects of Tatool, including the module, the session and trials.
 * 
 * @author Michael Ruflin
 *
 */
public interface ExecutionData {
	
	// DATA fetching
	/**
	 * Get the module object. The module object is always available.
	 * @return the module object
	 */
	public Module getModule();

	/**
	 * Get the module session object. The session object is available
	 * between SESSION_START and SESSION_FINISH. 
	 * @return the session object
	 */
	public ModuleSession getModuleSession();
	
	/**
	 * Get the trial objects for the actual execution.
	 * This object is only available during element execution (PRE_PROCESS - POST_PROCESS)
	 * 
	 * @return the trial object
	 */
	public List<Trial> getTrials();
	
	/** Get the first trial in the list, create a trial if none exist. */
	public Trial getCreateFirstTrial();
	
	/** Get the last trial in the list, create a trial if none exist. */
	public Trial getCreateLastTrial();
	
	/**
	 * Add a new trial to the list of trials for the current execution.
	 * 
	 * The trial will be stored with the same index as the first trial provided by the system.
	 * All trials are stored at once at the end of each element execution.
	 */
	public Trial addTrial();

	/** Request the current session to be closed after the execution of the current element.
	 * If further elements are executed during the current run, a new session will be created. 
	 */
	public void markSessionEnd();
}
