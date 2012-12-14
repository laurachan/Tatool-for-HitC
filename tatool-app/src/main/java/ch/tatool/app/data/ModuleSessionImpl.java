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
package ch.tatool.app.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;

/**
 * Represents a module session (set of trials). 
 * 
 * @author Michael Ruflin
 */
public class ModuleSessionImpl extends DataContainerImpl implements ModuleSession {
	
    /** The module this session belongs to. */
    private Module module;
    
    /** Id of the session. */
    private Long id;
    
    /** Index of the session within the module. */
    private int index;
    
	/** Start time of the module. */
	private Date startTime;
	
	/** End time of the module. */
	private Date endTime;
	
	/** Trials in this session. */
	private List<Trial> trials;
	
	public ModuleSessionImpl() {
	    trials = new ArrayList<Trial>();
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public List<Trial> getTrials() {
		return trials;
	}

	public void setTrials(List<Trial> trials) {
		this.trials = trials;
	}
	
	public int getTrialCount() {
	    return trials.size();
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }
    
    public boolean isActive() {
        return endTime == null;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
	
}
