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

import java.util.Map;

import ch.tatool.export.DataExporter;
import ch.tatool.module.ExecutorInitializer;
import ch.tatool.module.ModuleScheduler;


/**
 * Interface for a Module.
 * 
 * @author Michael Ruflin
 */
public interface Module extends DataContainer {
    
	/** Property key of the module name property. */
    public String PROPERTY_MODULE_NAME = "module.name";
    /** Property key of the module description property. */
	public String PROPERTY_MODULE_DESCRIPTION = "module.description";
	/** Property key of the module author property. */
	public String PROPERTY_MODULE_AUTHOR = "module.author";
	/** Property key of the module version property. */
	public String PROPERTY_MODULE_VERSION = "module.version";

	/** Property keys for HitC integration. */
	public String PROPERTY_MODULE_CODE = "tatool.online.subject.code";
	public String PROPERTY_MODULE_ID = "tatool.online.module.id";

	/** Default pause time between elements. */
	public String PROPERTY_EXECUTION_DEFAULT_PAUSE_DURATION = "execution.pausetime";
	/** Property used to define the scheduler class for a module. */
	public String PROPERTY_MODULE_SCHEDULER_CLASS = "module.scheduler.classname";
	/** Property used to define the execution configurer class. */
	public String PROPERTY_MODULE_EXECUTION_INITIALIZER_CLASS = "module.execution.initializer.classname";
	/** Property used to define the execution view class. */
	public String PROPERTY_MODULE_EXECUTION_DISPLAY_CLASS = "module.execution.display.classname";
	/** Property used to define the module info provider. */
	public String PROPERTY_MODULE_INFO_PROVIDER_CLASS = "module.info.classname";
	/** Property used to define the module info base path. */
	public String PROPERTY_MODULE_INFO_BASE = "module.info.base";
	/** Property used to define the module info start page. */
	public String PROPERTY_MODULE_INFO_PAGE = "module.info.page";
	
	/**
	 * Gets the ID of the module.
	 * 
	 * @return the ID of the module
	 */
	public Long getId();

    /**
     * Gets the name of the module.
     * 
     * @return the name of the module
     */
    public String getName();
    
    /**
     * Get the account this module is part of
     */
    public UserAccount getUserAccount();

    /** Get the scheduler for this module. */
    public ModuleScheduler getModuleScheduler();
    public void setModuleScheduler(ModuleScheduler scheduler);
    
    /** Get the initializer for this module. */
    public ExecutorInitializer getExecutorInitializer();
    public void setExecutorInitializer(ExecutorInitializer configurer);
    
    /** Get the module properties. */
    public Map<String, String> getModuleProperties();
    
    /**
     * Get a large value from the module.
     * This is implemented as BLOBs behind the scene, but the implementation
     * might hold all values in memory. Use therefore with consideration... 
     */
    public byte[] getBinaryModuleProperty(String propertyName);
    
    /** Set a large property value. */
    public void putBinaryModuleProperty(String propertyName, byte[] propertyValue);

    public Map<String, DataExporter> getModuleExporters();
    
    public DataExporter getModuleExporter(String exporterKey);
    
    public void setMessages(Messages messages);
    
    public Messages getMessages();
    
    /**
     * Module information object interface.
     * 
     * Objects of this type are used when querying for available modules.
     */
    public interface Info {
    	
    	/** Get the id of the described module. */
        public Long getId();
        
        /** Get the name of the described module. */
        public String getName();
    }
}
