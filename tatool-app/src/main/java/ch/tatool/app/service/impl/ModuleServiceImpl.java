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
package ch.tatool.app.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import ch.tatool.app.data.ModuleImpl;
import ch.tatool.app.data.UserAccountImpl;
import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.DataService;
import ch.tatool.data.UserAccount;
import ch.tatool.export.DataExporter;
import ch.tatool.module.ExecutorInitializer;
import ch.tatool.module.ModuleScheduler;
import ch.tatool.module.ModuleService;

/**
 * Manages Modules
 * 
 * @author Michael Ruflin
 */
public class ModuleServiceImpl implements ModuleService {
    
    /** Logger used by the service. */
    Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

    /** DataService */
    private DataService dataService;
    
    private Messages messages;
    
    // Module Info specific functionality
    
    /**
     * Get a list of all available modules.
     */
    @SuppressWarnings("unchecked")
    public Set<Module.Info> getModules(UserAccount account) {
        final UserAccountImpl accountImpl = (UserAccountImpl) account;
        return (Set<Module.Info>) accountImpl.getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    return accountImpl.getModuleDAO().getModules(accountImpl);
                }
            }
        );
    }
    
    // Module 
    
    /**
     * Creates a new module data object.
     * 
     * @param account the account to create a module for
     * @param properties the properties to use for the module. These will overwrite properties set in the
     *        configuration
     * @param configuration module configuration, such as the element tree and session scheduler
     */
    public Module createModule(UserAccount account, Map<String, String> moduleProperties, Map<String, byte[]> binaryModuleProperties, Map<String, DataExporter> moduleExporters) {
        final UserAccountImpl accountImpl = (UserAccountImpl) account;
        
        // add the properties and save the object
        final ModuleImpl module = new ModuleImpl();
        module.setAccount(accountImpl);
        module.setAccountId(accountImpl.getId());
        if (moduleProperties != null) {
        	module.setModuleProperties(moduleProperties);
        }
    	if (binaryModuleProperties != null) {
    		module.setBinaryModuleProperties(binaryModuleProperties);
    	}
    	if (moduleExporters != null) {
    		module.setModuleExporters(moduleExporters);
    	}
        
        // set the module name
    	String moduleName = moduleProperties.get(Module.PROPERTY_MODULE_NAME);
    	if (moduleName == null) {
    		logger.warn("No module name defined.");
        	moduleName = "Module " + System.currentTimeMillis();
        	module.getModuleProperties().put(Module.PROPERTY_MODULE_NAME, moduleName);    		
    	}
    	module.setName(moduleName);
        
        // save the module
        accountImpl.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    // save the module object
                    accountImpl.getModuleDAO().saveModule(module);
                }
            }
        );
        
        // initialize
        initializeModule(module);
 
        return module;
    }

	/**
     * Load a module given its info object
     */
    public Module loadModule(Module.Info info) {
        final ModuleInfoImpl moduleInfoImpl = (ModuleInfoImpl) info;
        
        // load the module from the database
        ModuleImpl module = (ModuleImpl) moduleInfoImpl.getAccount().getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    ModuleDAO moduleDAO = moduleInfoImpl.getAccount().getModuleDAO();
                    ModuleImpl module = moduleDAO.loadModule(moduleInfoImpl);
                    return module;
                }
            }
        );
        
        initializeModule(module);
        
        // initialize exporters for module
        // TODO: String "SpringXmlElementConfig" is defined in ch.tatool.core.module.initializer.SpringExecutorInitializer
        Map<String, DataExporter> exporters = getModuleExportersFromXML(module.getBinaryModuleProperty("SpringXmlElementConfig"));
        module.setModuleExporters(exporters);
        
        return module;
    }
    
    /** Initializes the module. */
    private void initializeModule(ModuleImpl module) {
    	// initialize the scheduler if available
        loadModuleScheduler(module);
        
        // load the configurator
        loadInitializer(module);
        
        loadMessages(module);
    }

	private Map<String, DataExporter> getModuleExportersFromXML(byte[] configurationXML) {
        // try loading the configuration
        XmlBeanFactory beanFactory = null;
        if (configurationXML != null) {
        	 try {
                 beanFactory = new XmlBeanFactory(new ByteArrayResource(configurationXML));
             } catch (BeansException be) {
                 // TODO: inform user that module configuration is broken
                 throw new RuntimeException("Unable to load Tatool file.", be);
             }
             
             // see whether we have exporters
             if (beanFactory.containsBean("moduleExporters")) {
             	Map<String, DataExporter> exporters = (Map<String, DataExporter>) beanFactory.getBean("moduleExporters");
                 return exporters;
             } else {
                 return Collections.emptyMap();
             } 
        } else {
        	return null;
        } 
    }
    
    /**
     * Initializes the session scheduler for a module.
     * 
     * The session scheduler class is taken from a module property, and an AlwaysAllowsSessionScheduler
     * used if such a property is missing.
     * 
     * @param module
     */
    private void loadModuleScheduler(ModuleImpl module) {
    	// load the scheduler instance
    	ModuleScheduler scheduler = (ModuleScheduler) instantiateObject(module,
    			Module.PROPERTY_MODULE_SCHEDULER_CLASS, ModuleScheduler.class);
        
    	// initialize if we got a scheduler. No scheduler = all access
    	if (scheduler != null) {
		    scheduler.setModule(module);
		    scheduler.setDataService(dataService);
		    scheduler.initialize();
		    module.setModuleScheduler(scheduler);
    	}
    }
   
    /**
     * Instantiates the module configurer. 
     */
    private void loadInitializer(ModuleImpl module) {
    	ExecutorInitializer initializer = (ExecutorInitializer) instantiateObject(module,
    			Module.PROPERTY_MODULE_EXECUTION_INITIALIZER_CLASS, ExecutorInitializer.class);
    	
    	// initialize if we got an initializer
    	if (initializer != null) {
    		module.setExecutorInitializer(initializer);
    	}
    }
    
    /**
     * Sets the messages object used for i18n
     */
    private void loadMessages(ModuleImpl module) {
		module.setMessages(messages);
	}
    
    /** Creates an object given the property containing the class name, as well as of
     * what type the object should be of.
     * @param module
     * @param propertyName
     * @param assignableTo
     */
    private Object instantiateObject(Module module, String propertyName, Class<?> assignableTo) {
    	// find the scheduler class
        Class<?> c = null;
        String className = module.getModuleProperties().get(propertyName);
        if (className != null && className.length() > 0) {
            try {
                c = Class.forName(className); 
            } catch (ClassNotFoundException e) {
                logger.warn("Class not found: " + e.getMessage(), e);
            }
        }
        if (c == null) { 
        	return null;
        }
        
        // Make sure the class is assignable as requested
        if (assignableTo != null && ! assignableTo.isAssignableFrom(c)) {
        	logger.warn("Class " + c.getName() + " is not assigable to class " + assignableTo.getName());
        	return null;
        }
        
        // try to instantiate an object
        Object o = null;
        try {
        	o = c.newInstance();
        } catch (InstantiationException e) {
            logger.error("Unable to create session scheduler", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to create session scheduler", e);
        }
        return o;
    }
    
    /** Close a module object. */
    public void closeModule(Module module) {
        // Nothing to do
    }
    
    public void saveModule(Module module) {
    	dataService.saveModule(module);
    }
    
    /**
     * Delete a module. 
     * 
     * This method completely removes all module related data from the system.
     */
    public void deleteModule(Module.Info info) {
        final ModuleInfoImpl moduleInfoImpl = (ModuleInfoImpl) info;
        
        moduleInfoImpl.getAccount().getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    ModuleDAO moduleDAO = moduleInfoImpl.getAccount().getModuleDAO();
                    moduleDAO.deleteModule(moduleInfoImpl);
                }
            }
        );
    }
    
    
    // Getters and setters
    
    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
    
    public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
		return messages;
	}
}
