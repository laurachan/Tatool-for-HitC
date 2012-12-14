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

/**
 * Phase Runnable Manager can be used to execute a Phase Runnable at a specific Execution Phase.
 * 
 * @author Michael Ruflin
 *
 */
public interface PhaseRunnableManager {
	
	public List<PhaseRunnable> getPhaseExecutables(ExecutionPhase phase);
	
	public boolean isRecurring(PhaseRunnable phaseRunnable, ExecutionPhase phase);
	
	/**
	 *  Add a PhaseRunnable to be run in one of the phases designated for such executions 
	 * 
	 * @param element the element to execute
	 * @param phase the phase before which the element should be executed
	 * @param recurring whether this is a one-off execution or should be executed several times
	 */
	public void addPhaseExecutable(PhaseRunnable phaseRunnable, ExecutionPhase phase, boolean recurring);
	
	/**
	 * Remove an element from the phase executable elements
	 */
	public void removePhaseExecutable(PhaseRunnable phaseRunnable, ExecutionPhase phase);
}
