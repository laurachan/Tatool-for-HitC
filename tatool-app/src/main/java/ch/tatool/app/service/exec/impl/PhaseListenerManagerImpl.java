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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.service.exec.PhaseListenerManager;
import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionPhase;
import ch.tatool.exec.ExecutionPhaseListener;

/**
 * Encapsulates the functionality around sending out phase events.
 * All logic regarding whom to deliver the context for a given phase is handled
 * in here.
 */
public class PhaseListenerManagerImpl implements PhaseListenerManager {
	
	private Logger logger = LoggerFactory.getLogger(PhaseListenerManagerImpl.class);
	
    /** List of external listeners. */
    private Map<ExecutionPhaseListener, Set<ExecutionPhase>> moduleExecutionListeners;

    /** Map of phases with phase and the corresponding phase listeners. */
    private Map<ExecutionPhase, Set<ExecutionPhaseListener>> phasesToListeners;
    
    public PhaseListenerManagerImpl() {
    	moduleExecutionListeners = new HashMap<ExecutionPhaseListener, Set<ExecutionPhase>>();
    	phasesToListeners = new HashMap<ExecutionPhase, Set<ExecutionPhaseListener>>();
    }
    
    /** Get all phases for which a listener is registered. */
    public Set<ExecutionPhase> getPhaseListenerPhases(ExecutionPhaseListener listener) {
    	Set<ExecutionPhase> phases = moduleExecutionListeners.get(listener);
    	if (phases != null) {
    		return Collections.unmodifiableSet(phases);
    	} else {
    		return Collections.emptySet();
    	}
    }
    
    /** Register an ExecutionPhaseListener with the phases it should be informed of. */
    public void addExecutionPhaseListener(ExecutionPhaseListener listener, Set<ExecutionPhase> phases) {
    	if (listener != null && phases != null) {
	    	for (ExecutionPhase phase: phases) {
	    		addExecutionPhaseListenerImpl(listener, phase);
	    	}
    	}
    }
    
    /** Register an ExecutionPhaseListener with a single phase. */
    public void addExecutionPhaseListener(ExecutionPhaseListener listener, ExecutionPhase phase) {
    	addExecutionPhaseListenerImpl(listener, phase);
    }
    
    private void addExecutionPhaseListenerImpl(ExecutionPhaseListener listener, ExecutionPhase phase) {
    	if (listener == null || phase == null) return;
    	
    	// add the phase to the set of phases for the listener 
    	Set<ExecutionPhase> phases = moduleExecutionListeners.get(listener);
    	if (phases == null) {
    		phases = new HashSet<ExecutionPhase>();
    		moduleExecutionListeners.put(listener, phases);
    	}
    	phases.add(phase);
    	
    	// now add the listener to the opposite map
    	Set<ExecutionPhaseListener> listeners = phasesToListeners.get(phase); 
		if (listeners == null) {
			listeners = new HashSet<ExecutionPhaseListener>();
			phasesToListeners.put(phase, listeners);
		}
		listeners.add(listener);
    }
    
    public void removeExecutionPhaseListener(ExecutionPhaseListener listener) {
    	removeExecutionPhaseListener(listener, moduleExecutionListeners.get(listener));
    }
    
    public void removeExecutionPhaseListener(ExecutionPhaseListener listener, ExecutionPhase phase) {
    	removeExecutionPhaseListenerImpl(listener, phase);
    }
    
    public void removeExecutionPhaseListener(ExecutionPhaseListener listener, Set<ExecutionPhase> phases) {
    	if (listener != null && phases != null) {
	    	for (ExecutionPhase phase: phases) {
	    		removeExecutionPhaseListenerImpl(listener, phase);
	    	}
    	}
    }
    
    private void removeExecutionPhaseListenerImpl(ExecutionPhaseListener listener, ExecutionPhase phase) {
    	if (listener == null || phase == null) return;
    	
    	// add the phase to the set of phases for the listener 
    	Set<ExecutionPhase> phases = moduleExecutionListeners.get(listener);
    	if (phases == null) {
    		return;
    	}
    	
    	// remove the phase, return if the phase was not in the set in the first place
    	if (! phases.remove(phase)) {
    		return;
    	}
    	if (phases.isEmpty()) {
    		moduleExecutionListeners.remove(listener);
    	}
    	
    	// remove the listener from the phase set
    	Set<ExecutionPhaseListener> listeners = phasesToListeners.get(phase);
    	listeners.remove(listener);
    	if (listeners.isEmpty()) {
    		phasesToListeners.remove(phase);
    	}
    }
    
    /**
     * Delivers the context to interested parties.
     */
    public void informListeners(ExecutionContext context) {
    	if (logger.isDebugEnabled()) {
    		logger.debug(System.currentTimeMillis() + ": Distributing context for phase " + context.getPhase());
    	}
    	
    	// first inform external listeners
    	Set<ExecutionPhaseListener> listeners = phasesToListeners.get(context.getPhase());
    	if (listeners != null) {
    		fireToExternals(context, listeners);
    	}
    }
    
    /** Fire an event to external listeners. */
    private void fireToExternals(final ExecutionContext context, final Set<ExecutionPhaseListener> listeners) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	for (ExecutionPhaseListener listener : listeners) {
                		listener.processExecutionPhase(context);
                	}
                }
            });
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
        	logger.error(e.getMessage(), e);
        }
    }
}
