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

/**
 * Trial datacontainer interface. Each executable can store one or more trials.
 * 
 * Trials need to be created manually, if an Executable doesn't create a trial no
 * trace of its execution is recorded.
 * 
 * @author Michael Ruflin
 */
public interface Trial extends DataContainer {
    
    /**
     * Gets the ID of the Trial.
     * 
     * @return ID of the Trial
     */
    public Long getId();

    /**
     * Gets the Node ID of the Trial.
     * 
     * @return ID of the Node
     */
    public String getParentId();
    
    /**
     * Sets the Node ID of the Trial.
     * 
     * @param parentId Node ID of the Trial
     */
    public void setParentId(String parentId);

    /**
     * Gets the TrainingSession this Trial belongs to.
     * 
     * @return TrainingSession of this Trial
     */
    public ModuleSession getSession();
    
    /**
     * Sets the TrainingSession of this Trial.
     * 
     * @param session TrainingSession this Trial belongs to
     */
    public void setSession(ModuleSession session);

    /**
     * Gets the Index of the Trial.
     * 
     * @return Index of the Trial
     */
    public int getIndex();
    
    /**
     * Sets the index of the Trial used in the database.
     * 
     * @param index Index of the Trial
     */
    public void setIndex(int index);
}
