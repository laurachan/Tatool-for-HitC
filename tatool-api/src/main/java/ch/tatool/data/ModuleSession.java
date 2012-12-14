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
package ch.tatool.data;

import java.util.Date;

/**
 * Interface for a ModuleSession.
 * 
 * @author Michael Ruflin
 */
public interface ModuleSession extends DataContainer {
		
	/**
	 * Returns the start time of a ModuleSession.
	 * 
	 * @return the start time of a ModuleSession
	 */
	public Date getStartTime();
	
	/**
	 * Sets the start time of a ModuleSession.
	 * 
	 * @param startTime the start time of a ModuleSession
	 */
	public void setStartTime(Date startTime);
	
	/**
	 * Returns the end time of a ModuleSession.
	 * 
	 * @return the end time of a ModuleSession
	 */
	public Date getEndTime();
	
	/**
	 * Sets the end time of a ModuleSession.
	 * 
	 * @param endTime the end time of a ModuleSession
	 */
	public void setEndTime(Date endTime);

    /**
     * Gets the ID of the ModuleSession.
     * 
     * @return ID of the ModuleSession
     */
    public Long getId();
    
    /**
     * Set the ID of the ModuleSession.
     * 
     * @param id ID for the ModuleSession
     */
    public void setId(Long id);
    
    /**
     * Gets the Module this ModuleSession belongs to.
     * 
     * @return Module of this ModuleSession
     */
    public Module getModule();
    
    /**
     * Sets the Module of this ModuleSession.
     * 
     * @param module Module this ModuleSession belongs to
     */
    public void setModule(Module module);
    
    public boolean isActive();
    
    /**
     * Gets the Index of the ModuleSession.
     * 
     * @return Index of the ModuleSession
     */
    public int getIndex();
    
    /**
     * Sets the index of the ModuleSession used in the database.
     * 
     * @param index Index of the ModuleSession
     */
    public void setIndex(int index);
}
