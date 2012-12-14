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

import java.util.Set;

import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionPhase;
import ch.tatool.exec.ExecutionPhaseListener;

/**
 * Managers the listeners attached to the ExecutionService.
 */
public interface PhaseListenerManager {
	
	/** Get the phases that are registered for a given listener. */
	public Set<ExecutionPhase> getPhaseListenerPhases(ExecutionPhaseListener listener);
	
    /** Add a ExecutionPhaseListener 
     * 
     * @param listener the listener to register
     * @param phases the phases the listener is interested in. 
     */
    public void addExecutionPhaseListener(ExecutionPhaseListener listener, Set<ExecutionPhase> phases);
    
    /**
     * Registers a listener with a single phase.
     * 
     * @param listener the listener to register
     * @param phase
     */
    public void addExecutionPhaseListener(ExecutionPhaseListener listener, ExecutionPhase phase);
    
    /** Unregisters a ExecutionPhaseListener
     * 
     * The listener is unregistered from all registered phases. 
     */
    public void removeExecutionPhaseListener(ExecutionPhaseListener listener);
    
    /**
     * Unregister the listener from the provided phases
     * @param listener
     * @param phases
     */
    public void removeExecutionPhaseListener(ExecutionPhaseListener listener, Set<ExecutionPhase> phases);
    
    /**
     * Unregisters a ExecutionPhaseListener from the provided phas
     */
    public void removeExecutionPhaseListener(ExecutionPhaseListener listener, ExecutionPhase phase);
    
    /**
     * Delivers a phase event to all applicable listeners.
     * @param context
     */
    public void informListeners(ExecutionContext context);
}
