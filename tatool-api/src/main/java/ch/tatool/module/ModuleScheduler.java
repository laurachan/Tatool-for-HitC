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
package ch.tatool.module;

import ch.tatool.data.Module;
import ch.tatool.data.DataService;

/**
 * Manages session scheduling for a given module.
 * 
 * Not each module is alike. Some might allow unlimited sessions to be created,
 * others only allow a single session with 100 trials per day. Again others might
 * limit the amount of time the module can last.
 * 
 * During a running session, the scheduler is only checked before/after each trial,
 * so cutting off a running trial won't be possible. 
 * 
 * @author Michael Ruflin
 */
public interface ModuleScheduler {
    
    /** Set the module this scheduler should manage. */
    public void setModule(Module module);
    
    /** Set the DataService it might need. */
    public void setDataService(DataService dataService);
    
    /** Give the scheduler a chance to initialize. */
    public void initialize();
    
    /** Get the name of the scheduler.
     * The name serves as a scheduler id. 
     */
    public String getName();

    /** Get the numer of sessions done so far */
    public String getSchedulerNumSessions(Module module);
    
    /** Get the date off the last session */
    public String getSchedulerLastSessionDate(Module module);
    
    /**
     * Returns whether a new session can be created.
     * 
     * @param module
     * @return ModuleSchedulerMessage
     */
    public ModuleSchedulerMessage isSessionStartAllowed(Module module);

}
