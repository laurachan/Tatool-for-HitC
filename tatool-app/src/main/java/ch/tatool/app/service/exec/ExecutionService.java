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
package ch.tatool.app.service.exec;

import ch.tatool.data.Module;
import ch.tatool.exec.Executor;

/**
 * Service that allows executing a module instance.
 * 
 * @author Michael Ruflin
 */
public interface ExecutionService {
    
    /**
     * Returns whether a module can currently be executed.
     */
    public boolean canExecute(Module module);
	
	/** Initializes the executor. */
	public Executor createExecutor(Module module);
	
	/** Executes the executor. */
	public void startExecution(Executor executor, boolean blockCallerThread);
    
    /** Get the PhaseListener manager object that can be used to add new listeners to the service. */
    public PhaseListenerManager getPhaseListenerManager();
}
