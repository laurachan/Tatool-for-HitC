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

import ch.tatool.app.service.exec.ExecutionService;
import ch.tatool.app.service.exec.PhaseListenerManager;
import ch.tatool.app.util.AppUtils;
import ch.tatool.data.DataService;
import ch.tatool.data.Module;
import ch.tatool.display.ExecutionDisplayProvider;
import ch.tatool.exec.Executor;
import ch.tatool.module.ExecutorInitializer;
import ch.tatool.module.ModuleScheduler;
import ch.tatool.module.ModuleSchedulerMessage;

/**
 * Execution service implementation.
 * 
 * Creates and manages module executions.
 * 
 * @author Michael Ruflin
 */
public class ExecutionServiceImpl implements ExecutionService {

    Logger logger = LoggerFactory.getLogger(ExecutionServiceImpl.class);
    
    /** DataService */
    private DataService dataService;
    
    /** Handles the phase event listeners as well as event distribution for this service. */
    private PhaseListenerManager phaseListenerManager;

    /** Constructor. */
    public ExecutionServiceImpl() {
    	phaseListenerManager = new PhaseListenerManagerImpl();
    }
    
    /**
     * Returns whether a module can be executed.
     */
    public boolean canExecute(Module module) {
        // check whether we are allowed to create a new session
        ModuleScheduler moduleScheduler = module.getModuleScheduler();
        if (moduleScheduler != null) {
        	ModuleSchedulerMessage message = moduleScheduler.isSessionStartAllowed(module);
        	return message.isSessionStartAllowed();
        } else {
        	return true;
        }
    }
    
    // Module Execution Service
    public Executor createExecutor(Module module) {
    	// create a new executor
    	ExecutorImpl executor = new ExecutorImpl();
    	executor.setExecutionServiceImpl(this);
    	executor.setDataService(dataService);
    	executor.setup(module);
    	
    	// create a display for the module
    	ExecutionDisplayProvider executionDisplayProvider = createExecutionDisplayProvider(executor, module);
    	executor.setExecutionDisplayProvider(executionDisplayProvider);
    	
    	// now setup the executor with the provided module
    	ExecutorInitializer initializer = module.getExecutorInitializer();
    	if (initializer != null) {
    		initializer.initialize(executor, module);
    	}
    	
    	return executor;
    }
    
    /**
     * Setup the display provider
     * @param module
     */
    private ExecutionDisplayProvider createExecutionDisplayProvider(Executor executor, Module module) {
    	ExecutionDisplayProvider tmpProvider = null;
		String viewClass = module
				.getModuleProperties()
				.get(Module.PROPERTY_MODULE_EXECUTION_DISPLAY_CLASS);
		if (viewClass != null) {
			// create an instance - fail if an error occurred
			tmpProvider = (ExecutionDisplayProvider) AppUtils.createInstance(viewClass, ExecutionDisplayProvider.class, true);
			tmpProvider.setup(executor, module);
		} else {
			// use swing execution display provider as default
			tmpProvider = (ExecutionDisplayProvider) AppUtils.createInstance("ch.tatool.core.display.swing.SwingExecutionDisplayProvider", ExecutionDisplayProvider.class, true);
			tmpProvider.setup(executor, module);
		}
		
		return tmpProvider;
    }
    
    /** Executes the executor. */
	public void startExecution(Executor executor, boolean blockCallerThread) {
		ExecutorImpl e = (ExecutorImpl) executor;
		e.execute(blockCallerThread);
	}
    
    public PhaseListenerManager getPhaseListenerManager() {
    	return phaseListenerManager;
    }
    
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}
