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
package ch.tatool.app.service.exec.impl;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.data.TrialImpl;
import ch.tatool.data.DataService;
import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;
import ch.tatool.exec.ExecutionData;

/**
 * Main execution data holder object.
 * 
 * Manages the root elements, the current element stack (including switching from one to the next),
 * the pause durations, etc.
 * 
 * @author Michael Ruflin
 */
public class ExecutionDataImpl implements ExecutionData {
	
	Logger logger = LoggerFactory.getLogger(ExecutionDataImpl.class);

	/** Data service used to store new data. */
	private DataService dataService;
	
    /** Flag that holds whether the session is requested to be closed. */
    private boolean closeCurrentSession;
    
    // actual execution data
    
	/** Module object - always available. */
	private Module module;
	
	/** Session object. */
	private ModuleSession moduleSession;
	
	/** Trial object. */
	private LinkedList<Trial> trials;
	
	/** Create a new ExecutionDataImpl object. */
	public ExecutionDataImpl() {
		trials = new LinkedList<Trial>();
	}
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	/** Opens a session if no session is opened currently
	 * @return true if a new session was opened
	 */
	public boolean openSession() {
        // return if we already got a session
		if (moduleSession != null) return false;
		
    	// create a module session
    	moduleSession = dataService.createSession(module);
    	closeCurrentSession = false;
    	return true;
	}
	
	/** Returns whether a session is closable.
	 * @return true if a session is available and its closing has been requested 
	 */
	public boolean isCloseSessionRequested() {
		// must have a session and closing requested
		return closeCurrentSession && moduleSession != null;
	}
	
	/** Closes and clears a session. */
	public void closeSession() {
		if (moduleSession != null) {
	        // save/finish the session
	        dataService.finishSession(moduleSession);
	        dataService.saveModule(module);
	        
	        moduleSession = null;
	        closeCurrentSession = false;
		} else {
			logger.warn("Closing nonexisting session - should never happen!");
		}
	}
	
	/** Persists the current trials and removes them from the data object. */
	public void persistTrials() {
        for (Trial t : trials) {
        	dataService.insertTrial(moduleSession, t);
        }
        
        // save the session and the module, as both or either might have been updated
        dataService.saveSession(moduleSession);
        dataService.saveModule(module);
        
        // do some cleanup
        trials.clear();
	}
	
	public void setModule(Module module) {
		this.module = module;
	}
	
	// clearing
	
	/** Clears the execution data object. */
	public void clear() {
		closeCurrentSession = false;
		module = null;
		moduleSession = null;
		trials.clear();
	}

	public boolean isCloseCurrentSession() {
		return closeCurrentSession;
	}

	public void setCloseCurrentSession(boolean closeCurrentSession) {
		this.closeCurrentSession = closeCurrentSession;
	}
	
	// ExecutionData

	public Module getModule() {
		return module;
	}

	public ModuleSession getModuleSession() {
		return moduleSession;
	}

	public void markSessionEnd() {
		setCloseCurrentSession(true);
	}
	
	public Trial getCreateFirstTrial() {
		if (trials.isEmpty()) {
			return addTrial();
		} else {
			return trials.getFirst();
		}
	}
	
	public Trial getCreateLastTrial() {
		if (trials.isEmpty()) {
			return addTrial();
		} else {
			return trials.getLast();
		}
	}
	
	public LinkedList<Trial> getTrials() {
		return trials;
	}
	
	public Trial addTrial() {
		TrialImpl trial = new TrialImpl();
		addTrial(trial);
		return trial;
	}
	
	private void addTrial(Trial trial) {
		if (trials == null) {
			trials = new LinkedList<Trial>();
		}
		trials.add(trial);
	}
}
