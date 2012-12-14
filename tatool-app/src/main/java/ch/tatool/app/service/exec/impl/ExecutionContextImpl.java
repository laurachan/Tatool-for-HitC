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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.tatool.data.DataService;
import ch.tatool.display.ExecutionDisplay;
import ch.tatool.element.Element;
import ch.tatool.element.Executable;
import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionData;
import ch.tatool.exec.ExecutionPhase;
import ch.tatool.exec.Executor;

/**
 * Execution context object
 * 
 * This is sort of a one stop shop for elements during execution.
 * 
 * Please note that the context can change from one to the next execution, therefore do not
 * cache the object inside the element.
 * 
 * @author Michael Ruflin
 */
public class ExecutionContextImpl implements ExecutionContext {
	
	/** Properties. */
    private Map<String, Object> properties;
    
	/** The current execution phase. */
	private ExecutionPhase phase;
	
	/** Executor - allows limited interaction with the executor. */
	private ExecutorImpl executor;
	
	/** DataService - provides access to the data layer. */
	private DataService dataService;
	
	/** Provided executionDisplay object. Used by tasks to interact with the user. */
	private ExecutionDisplay executionDisplay;
	
	/** ExecutionData object. */
	private ExecutionData executionData;
	
	public ExecutionContextImpl(ExecutorImpl executor, ExecutionData executionData,
			DataService dataService, ExecutionDisplay executionDisplay) {
		this.executor = executor;
		this.executionDisplay = executionDisplay;
		this.dataService = dataService;
		this.executionData = executionData;
		
		properties = new HashMap<String, Object>();
	}
	
	// PropertyHolder
	
	public String getId() {
		return "ExecutionContext";
	}
    
    public Object getProperty(String name) {
        return properties.get(name);
    }

    public void setProperty(String name, Object value) {
		properties.put(name, value);
    }
    
    public void removeProperty(String name) {
    	properties.remove(name);
    }
    
    /**
     * Get all properties contained in this holder
     */
    public Set<String> getKeys() {
    	return properties.keySet();
    }
    
    /**
     * Clear all properties
     */
    public void clearProperties() {
    	properties.clear();
    }
	
	// Getters and setters

	public ExecutionPhase getPhase() {
		return phase;
	}

	public void setPhase(ExecutionPhase executionPhase) {
		this.phase = executionPhase;
	}

	public Executor getExecutor() {
		return executor;
	}

	public DataService getDataService() {
		return dataService;
	}

	public ExecutionDisplay getExecutionDisplay() {
		return executionDisplay;
	}
	
	public ExecutionData getExecutionData() {
		return executionData;
	}
	
	// Getters that redirect to executionData

	/**
	 * Get the currently scheduled or executed ExecutableElement.
	 * This object is available between PRE_PROCESS and POST_PROCESS. 
	 * @return the ExecutableElement object or null
	 */
	public Executable getActiveExecutable() {
		return executor.getActiveExecutable();
	}

	/**
	 * Get the active Element.
	 */
	public Element getActiveElement() {
		return executor.getActiveElement();
	}
	
	/** Get the element stack, the stack of elements being executed.
	 * This object is always available, but empty before and including SESSION_START
	 * and after and including SESSION_END
	 * 
	 * @return the list of elements, beginning with the root element, containing the executable element as last element
	 */
	public List<Element> getElementStack() {
		return executor.getExecutionTree().getElementStack();
	}
}
