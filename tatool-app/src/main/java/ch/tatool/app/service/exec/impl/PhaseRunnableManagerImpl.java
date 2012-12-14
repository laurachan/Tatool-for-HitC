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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.tatool.exec.ExecutionPhase;
import ch.tatool.exec.PhaseRunnable;
import ch.tatool.exec.PhaseRunnableManager;

/**
 * Manages the registered phase executables
 * 
 * @author Michael Ruflin
 *
 */
class PhaseRunnableManagerImpl implements PhaseRunnableManager {
	
	/** Contains the phase executables registered. */
	private Map<ExecutionPhase, List<PhaseRunnableData>> phaseRunnables;
	
	public PhaseRunnableManagerImpl() {
		phaseRunnables = new HashMap<ExecutionPhase, List<PhaseRunnableData>>();
	}
	
	/** Clears the root elements - this will also reset the execution data. */
	public void clear() {
		phaseRunnables.clear();
	}
		
	public void addPhaseExecutable(PhaseRunnable phaseRunnable, ExecutionPhase phase, boolean recurring) {
		List<PhaseRunnableData> elements = phaseRunnables.get(phase);
		if (elements == null) {
			elements = new ArrayList<PhaseRunnableData>();
			phaseRunnables.put(phase, elements);
		}
		PhaseRunnableData data = new PhaseRunnableData(phaseRunnable, recurring);
		elements.add(data);
	}
	
	/**
	 * Remove an element from the phase executable elements
	 */
	public void removePhaseExecutable(PhaseRunnable phaseRunnable, ExecutionPhase phase) {
		List<PhaseRunnableData> elements = phaseRunnables.get(phase);
		for (PhaseRunnableData data : elements) {
			if (data.getPhaseExecutable() == phaseRunnable) 
			{
				elements.remove(data);
				if (elements.isEmpty()) {
					phaseRunnables.remove(elements);
				}
				break;
			}
		}
	}
	
	/**
	 * Returns whether a registered PhaseRunnable is registered as recurring.
	 * @return true if recurring, false otherwise. Note that false is also returned if the specified
	 *         PhaseRunnable could not be found.
	 */
	public boolean isRecurring(PhaseRunnable phaseRunnable, ExecutionPhase phase) {
		List<PhaseRunnableData> elements = phaseRunnables.get(phase);
		for (PhaseRunnableData data : elements) {
			if (data.getPhaseExecutable() == phaseRunnable) 
			{
				return data.recurring;
			}
		}
		return false;
	}
	
	public List<PhaseRunnable> getPhaseExecutables(ExecutionPhase phase) {
		List<PhaseRunnableData> elements = phaseRunnables.get(phase);
		if (elements != null) {
			List<PhaseRunnable> list = new ArrayList<PhaseRunnable>();
			for (PhaseRunnableData data : elements) {
				list.add(data.phaseRunnable);
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}
	
	public List<PhaseRunnableData> getPhaseExecutablesData(ExecutionPhase phase) {
		List<PhaseRunnableData> elements = phaseRunnables.get(phase);
		if (elements != null) {
			return Collections.unmodifiableList(elements);
		} else {
			return Collections.emptyList();
		}
	}
	
	public static class PhaseRunnableData {
		PhaseRunnable phaseRunnable;
		boolean recurring;
		public PhaseRunnableData(PhaseRunnable phaseRunnable, boolean recurring) {
			this.phaseRunnable = phaseRunnable;
			this.recurring = recurring;
		}
		public PhaseRunnable getPhaseExecutable() {
			return phaseRunnable;
		}
		public void setPhaseExecutable(PhaseRunnable phaseRunnable) {
			this.phaseRunnable = phaseRunnable;
		}
		public boolean isRecurring() {
			return recurring;
		}
		public void setRecurring(boolean recurring) {
			this.recurring = recurring;
		}
	}
}
