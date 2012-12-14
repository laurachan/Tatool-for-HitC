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

import ch.tatool.app.TestApp;
import ch.tatool.core.element.ExecutableElement;
import ch.tatool.core.module.initializer.MemoryExecutorInitializer;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;
import ch.tatool.element.Element;
import ch.tatool.element.Executable;
import ch.tatool.test.TestTask;

/**
 * Provides support for running a module without a display
 * 
 * This tester provides two methods to overwrite: getExecutable or getRootElement.
 * If getExecutable is overwritten the executable is run exactly once. getRootElement
 * on the other hand allows you arbitarily defining the Element hierarchy you want to run.
 */
public class ModuleRunner extends TestApp {
    
    public ModuleRunner() {
    }

    /**
     * Overwritten to set a memory initializer that sets the Element returned by createRootElement()
     */
    protected Module createModule(UserAccount account) {
    	// first create the training
    	Module module = super.createModule(account);
    	
    	// then assign a memory initializer that sets our root element
    	Element rootElement = createRootElement();
        module.setExecutorInitializer(new MemoryExecutorInitializer(rootElement));
        
        return module;
    }
    
    /**
     * Returns a simple test executable that return SUCCESS
     */
    protected Element createRootElement() {
    	Executable executable = createExecutable();
    	return new ExecutableElement(executable);
    }
    
    /**
     * Create an executable to run.
     * Overwrite this method if you simply want to run an executable
     */
    protected Executable createExecutable() {
    	return new TestTask();
    }
    
    public static void main(String[] args) {
    	new ModuleRunner().runModule();
    }
}
