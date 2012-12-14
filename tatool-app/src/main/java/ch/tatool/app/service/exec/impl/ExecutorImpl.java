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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.service.exec.impl.PhaseRunnableManagerImpl.PhaseRunnableData;
import ch.tatool.data.DataService;
import ch.tatool.data.Module;
import ch.tatool.display.ExecutionDisplay;
import ch.tatool.display.ExecutionDisplayProvider;
import ch.tatool.element.Element;
import ch.tatool.element.ElementTree;
import ch.tatool.element.Executable;
import ch.tatool.element.ExecutionStrategy;
import ch.tatool.exec.ExecutionPhase;
import ch.tatool.exec.Executor;
import ch.tatool.exec.PhaseRunnable;
import ch.tatool.exec.PhaseRunnableManager;

/**
 * Executor implementation 
 * 
 * @author Michael Ruflin
 */
public class ExecutorImpl implements Executor {

    private static Logger logger = LoggerFactory.getLogger(ExecutorImpl.class);
    
    // Injected properties
    
    /** DataService. */
    private DataService dataService;
    
    /** Service that created this object. */
    private ExecutionServiceImpl executionService;
    
    // internal
    
    /** ExecutionDisplayProvider. */
    private ExecutionDisplayProvider executionDisplayProvider;
    
    /** ExecutionContext object. */
    private ExecutionContextImpl executionContext = null;
    
    /** Variable used to flag whether execution should be continued. */
    private volatile boolean continueModule = true; 
    
    /** variable used to indicate that the current element should be finished asap. */
    private volatile boolean finishCurrentCycle = false;

    /** Thread executing the module. */
    private Thread executionThread;
    
    /**
     * This object holds the module, session and trial information for the current execution
     * as well as other execution relevant data 
     */
    private ExecutionDataImpl executionData;
    
    /** Manages the phase executables. */
    private PhaseRunnableManagerImpl phaseRunnableManager;
    
    /**Next element selector strategy. */
    private ExecutionStrategy selectorStrategy;
    
    /** holds the execution tree. */
    private ElementTreeImpl elementTree;
    
    /** Currently executed element */
    private volatile Executable currentExecutable;
    private volatile Element currentElement;
    
    /**
     * Currently executed phase executable
     */
    private volatile PhaseRunnable currentPhaseExecutable;
    
    
    /** Constructor. */
    public ExecutorImpl() {
    }
    
    /** Sets the ExecutionServiceImpl this executor works for. */
    void setExecutionServiceImpl(ExecutionServiceImpl executionService) {
    	this.executionService = executionService;
    }
    
    /** Sets the data service object. */
    void setDataService(DataService dataService) {
    	this.dataService = dataService;
    }
    
    void setExecutionDisplayProvider(ExecutionDisplayProvider executionDisplayProvider) {
    	this.executionDisplayProvider = executionDisplayProvider;
    }
    
    public void setup(Module module) {
    	phaseRunnableManager = new PhaseRunnableManagerImpl();
    	elementTree = new ElementTreeImpl();
    	executionData = new ExecutionDataImpl();
    	executionData.setDataService(dataService);
    	executionData.setModule(module);
    }
    
    /**
     * Starts a module.
     * 
     * Merely creates the ExecutionContext and the module execution thread.
     * See runModule for the actual module execution code
     */
    public void execute(boolean blockCallerThread) {        
        // start the execution in a new thread
        executionThread = new Thread(
            new Runnable() {
                public void run() {
                	runModule();
                }
            }
        );
        executionThread.setName("Module execution thread");
        executionThread.setDaemon(false);
        executionThread.start();
        
        // wait until that thread finished if blockCallerThread is true
        // this is used for testing, as element tests finish execution after the
        // initial element test thread returns
        if (blockCallerThread) {
        	while (executionThread.isAlive()) {
        		try {
        			executionThread.join();
        		} catch (InterruptedException ie) {
        			logger.error("Interrupted while waiting for execution thread to finish", ie);
        		}
        	}
        }
    }
    
    /**
     * Runs a module session.
     * 
     * This method is executed from a separate thread.
     */
    private void runModule() {
    	// initialize the execution context
    	ExecutionDisplay display = executionDisplayProvider != null ? executionDisplayProvider.getExecutionDisplay() : null; 
		executionContext = new ExecutionContextImpl(this, executionData, 
				dataService, display);
    	
        // run until we get signaled to not run any more tasks
        continueModule = true; 
        
    	// open the view
        if (executionDisplayProvider != null) {
        	executionDisplayProvider.open();
        }
        
        // Inform external listeners about execution start
    	setPhaseAndInform(ExecutionPhase.EXECUTION_START);
    	
    	// execute elements as long as the module should continue
        while (continueModule) {
        	runElement();
        }
        
        // make sure the session is closed at the end
        executionData.markSessionEnd();
        checkCloseSession();
        
        // inform listeners about execution end
        setPhaseAndInform(ExecutionPhase.EXECUTION_FINISH);
        
    	// close the view
        if (executionDisplayProvider != null) {
        	executionDisplayProvider.destroy();
            executionDisplayProvider = null;
        }
        
        // make sure a new thread can be created
        executionData.clear();
        executionThread = null;
    }

    /**
     * Executes a full element execution cycle consisting of phase executables, and a single executable execution.
     */
    private void runElement() {
    	// reset the finish current element execution flag
    	finishCurrentCycle = true;
   
        // find the next element to execute or exit if none could be found
    	continueModule = selectorStrategy.updateElementStack(executionContext);
    	if (! continueModule) return;
    	
    	// fetch the new execution element
        Element newElement = elementTree.getTop();
        Executable newExecutable = null;
        if (newElement == null) {
        	continueModule = false;
        	return;
        }
        
        // fetch the executable executable
        newExecutable = newElement.getExecutable();
        if (newExecutable == null) {
        	continueModule = false;
        	return;
        }
        
        // make sure we have an open session
        if (finishCurrentCycle) {
        	checkOpenSession();
        }
        
        if (finishCurrentCycle) {
        	executePhaseExecutables(ExecutionPhase.PRE_EXECUTABLE_EXECUTION);
        }
        
        // run the executable
        if (finishCurrentCycle) {
        	runExecutable(newElement, newExecutable);
        }
        
        if (finishCurrentCycle) {
        	executePhaseExecutables(ExecutionPhase.POST_EXECUTABLE_EXECUTION);
        }
        
        // check whether the session should be closed
        if (finishCurrentCycle) {
        	checkCloseSession();
        }
    }
    
    /**
     * Executes an ExecutableElement
     */
    private void runExecutable(Element element, Executable executable) {
    	currentElement = element;
    	currentExecutable = executable;
    	
        // first inform the task execution listeners
		setPhaseAndInform(ExecutionPhase.PRE_PROCESS);

        // execute the executable
        if (finishCurrentCycle) {
	        executionContext.setPhase(ExecutionPhase.EXECUTE_EXECUTABLE);
	        executable.setExecutionContext(executionContext);
        	executable.execute();
        	executable.setExecutionContext(null);
        }

        // give listeners a chance to change trial data or do other work
        if (finishCurrentCycle) {
        	setPhaseAndInform(ExecutionPhase.POST_PROCESS);
        }
        
        // store current execution data
    	executionData.persistTrials();

    	// do some cleanup
    	currentElement.setProperty(Element.EXECUTED, Boolean.TRUE);
 
        // clear what we just executed
        currentElement = null;
        currentExecutable = null;
    }
        
    // Session management
    
    /** Checks whether a session is open, creates a new session if not. */
    private void checkOpenSession() {
    	if (executionData.openSession()) {
        	setPhaseAndInform(ExecutionPhase.SESSION_START);
        }
    }
    
    /** Checks whether the session closure is requested. Closes the session in that case. */
    private void checkCloseSession() {
    	if (executionData.isCloseSessionRequested()) {
    		// execute pre phase elements, inform listeners and execute post phase elements
            setPhaseAndInform(ExecutionPhase.SESSION_FINISH);
            
            // close session
            executionData.closeSession();
    	}
    }
    
    // Selector strategy
    
	/** Set the selector strategy. */
	public void setExecutionStrategy(ExecutionStrategy selectorStrategy) {
		this.selectorStrategy = selectorStrategy;
	}
	
	/** Get the selector strategy. */
	public ExecutionStrategy getExecutionStrategy() {
		return selectorStrategy;
	}
    
    // Phase Executables
    
    /** Phase executable element support. */
    private void executePhaseExecutables(ExecutionPhase phase) {
    	executionContext.setPhase(phase);
    	for (PhaseRunnableData phaseRunnableData : phaseRunnableManager.getPhaseExecutablesData(phase)) {
    		if (! continueModule || ! finishCurrentCycle) {
    			return;
    		}
    		
    		// execute the executable
    		currentPhaseExecutable = phaseRunnableData.getPhaseExecutable();
    		currentPhaseExecutable.run(executionContext);
    		currentPhaseExecutable = null;
    		if (! phaseRunnableData.recurring) {
    			phaseRunnableManager.removePhaseExecutable(currentPhaseExecutable, phase);
    		}
    	}
    }

    /**
     * Stops the module.
     */
    private void stopModuleImpl() {
    	finishCurrentCycle = false;
        continueModule = false;
        stopCurrentCycle();
    }
    
    private void stopCurrentCycle() {
        // stop a currently running task
        Executable executable = currentExecutable;
        if (executable != null) {
            logger.info("cancelTask: Cancelling executable " + executable.getId());
            executable.cancel();
        }
        
        // stop currently running phase executable
        PhaseRunnable phaseRunnable = currentPhaseExecutable;
        if (phaseRunnable != null) {
        	logger.info("stopping current phase executable " + phaseRunnable.toString());
        	phaseRunnable.stop();
        }
    }
    
    // Helper methods
    
    /** Sets a given phase and informs all listeners. */
    private void setPhaseAndInform(ExecutionPhase phase) {
    	executionContext.setPhase(phase);
    	executionService.getPhaseListenerManager().informListeners(executionContext);
    	elementTree.deliverPhaseEvent(executionContext);
    }    
    
    // Executor interface
    
    /**
     * Stops the current element execution.
     * 
     * This will stop the execution of the current element and issue the next element to be
     * scheduled using the outcome provided
     */
    private void stopCurrentElementExecutionImpl() {
    	finishCurrentCycle = false;
    	stopCurrentCycle();
    }
    
    /**
	 * Finishes the execution of the current executable
	 */
	public void stopCurrentElementExecution() {
		stopCurrentElementExecutionImpl();
	}
	
	/**
	 * Stops the current execution when called.
	 */
	public void stopExecution() {
		stopModuleImpl();
	}

	/**
	 * Get the manager that holds the PhaseExecutables. Contrary to ExecutableElements, a PhaseRunnable
	 * is run by the executor directly without firing any events before or after execution.
	 * It can be used to pause the executor or do other quick work in between elements.
	 */
	public PhaseRunnableManager getPhaseRunnableManager() {
		return phaseRunnableManager;
	}
	
	/**
	 * Get the execution tree this executor works on.
	 */
	public ElementTree getExecutionTree() {
		return elementTree;
	}
	
	public Element getActiveElement() {
		return currentElement;
	}
	
	public Executable getActiveExecutable() {
		return currentExecutable;
	}


}
