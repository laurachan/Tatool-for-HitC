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
package ch.tatool.test.swing;

import java.util.Map;

import ch.tatool.core.display.swing.SwingExecutionDisplayProvider;
import ch.tatool.data.Module;

/**
 * Abstract Module tester. Provides support for running a module.
 * 
 * This tester provides two methods to overwrite: getExecutable or getRootElement.
 * If getExecutable is overwritten the executable is run exactly once. getRootElement
 * on the other hand allows you arbitarily defining the Element hierarchy you want to run.
 * 
 * The default training properties
 */
public class SwingModuleRunner extends ModuleRunner {
    
    public SwingModuleRunner() {
    }
    
    /**
     * Overwritten to set the Swing display and other default properties.
     */
    protected Map<String, String> createModuleProperties() {
    	Map<String, String> props = super.createModuleProperties();
    	
    	// use SwingExecutionDisplayProvider
    	props.put(Module.PROPERTY_MODULE_EXECUTION_DISPLAY_CLASS, SwingExecutionDisplayProvider.class.getName());
    	
    	// don't execute in full screen mode
    	props.put(SwingExecutionDisplayProvider.PROPERTY_DISPLAY_MODE, SwingExecutionDisplayProvider.DISPLAY_MODE_WINDOW);
    	
    	// create the regions container already
        props.put(SwingExecutionDisplayProvider.PROPERTY_INITIALIZE_REGIONS_CONTAINER, "true");
        
        return props;
    }
}
