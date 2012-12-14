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
package ch.tatool.display;

import ch.tatool.data.Module;
import ch.tatool.exec.Executor;


/**
 * Interface to be implemented by the module execution views.
 * 
 * @author Michael Ruflin
 */
public interface ExecutionDisplayProvider {
	
	/**
	 * Initialize the module execution view
	 * 
	 * This method should create any necessary resources required for the view
	 */
	public void setup(Executor executor, Module module);
	
	/**
	 * Opens the view
	 */
	public void open();
	
	/**
	 * Closes the view and destroys any resources used by this view.
	 */
	public void destroy();
	
	
    /**
     * Get the ExecutionDisplay this view provides.
     * 
     * The ExecutionDisplay is available to the elements through the ExecutionContext.  
     */
    public ExecutionDisplay getExecutionDisplay();
}
