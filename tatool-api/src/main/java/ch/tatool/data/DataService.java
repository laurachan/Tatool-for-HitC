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

import java.util.List;

import ch.tatool.element.Node;

/**
 * Interface for working with different module data management methods.
 * 
 * @author Michael Ruflin
 */
public interface DataService {

    /** Save the module instance.
     * This will save all changed module properties
     */
    public void saveModule(Module module);
    
    /** Create a new session.
     * This will set the session start date
     */
    public ModuleSession createSession(Module module);
    
    /**
     * Save a session without finishing it.
     */
    public void saveSession(ModuleSession session);
    
    /** Finish a module session
     * This will set the end time of the session 
     */
    public void finishSession(ModuleSession session);
    
    /** Get all sessions for a given module */
    public List<ModuleSession> getSessions(Module module);
    
    /** Get the last session of a module.
     * 
     * @param module the module to search for
     * @return the last created session 
     */
    public ModuleSession getLastSession(Module module);
        
    /**
     * Get the number of sessions in a module
     * 
     * @param module the module to check
     * @param includeUnfinished whether to include unfinished sessions (sessions without end time)
     * @return
     */
    public long getSessionCount(Module module, boolean includeUnfinished);
    

    /** Inserts a new trial object into the session. */
    public void insertTrial(ModuleSession session, Trial trial);
    
    /** Get all trials for a session. */
    public List<Trial> getTrials(ModuleSession session);
    
    /**
     * Get a list of trials for a given element, ordered by the most recent trial first.
     * 
     * @param the training to find trials for
     * @param session the session to find trials in. Use null to search for all sessions
     * @param node the element to find trials for. Use null to search for all element.
     * @param maxResults the maximum amount of results.
     * 
     * @return a list of trials
     */
    public List<Trial> getTrials(Module module, ModuleSession session, Node node, int maxResults);

    /** Get a list of x trials with a given property of a given element, ordered by the most recent trial first.
     * 
     * @param training The training to search for
     * @param session The sessions the trials should be belong to, null if all sessions should be taken into account
     * @param elementNameLike the name of the element to search for (either this or propertyName needs to be non-null)
     * @param propertyNameLike the name of the property to search for (either this or elementName needs to be non-null
     * @param offSet the offset to start from, starting with 0
     * @param maxResults the maximum number of Trials to return, -1 for all results
     * @return a list of Trials
     */
    public List<Trial> getTrials(Module module, ModuleSession session, String elementNameLike, String propertyNameLike, int offSet, int maxResults);
    
    /**
     * Get all distinct trial property names for a module.
     * 
     * @return An array with [0] containing the item name, [1] the property name
     */
    public List<Object[]> findDistinctTrialPropertyNames(Module module);
    
    /**
     * Get all distinct session property names for a module.
     * 
     * @return An array with [0] containing the item name, [1] the property name
     */
    public List<Object[]> findDistinctSessionPropertyNames(Module module);
 
    /** Sets the messages used by Tatool */
    public void setMessages(Messages messages);
    
    /** Gets the messages used by Tatool */
    public Messages getMessages();
}
