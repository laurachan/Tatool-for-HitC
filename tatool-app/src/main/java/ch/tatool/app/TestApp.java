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
package ch.tatool.app;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.tatool.app.export.DataExportService;
import ch.tatool.app.service.UserAccountService;
import ch.tatool.app.service.exec.ExecutionService;
import ch.tatool.app.service.impl.UserAccountServiceImpl;
import ch.tatool.data.DataService;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;
import ch.tatool.exec.Executor;
import ch.tatool.module.ModuleService;
import ch.tatool.export.DataExporter;

/**
 * Test Tatool Application class.
 * 
 * Use this class to instantiate a complete tatool test instance without
 * actually triggering the display of the UI. 
 */
public class TestApp {

	/** Holds the directory name that will contain created data. */
	protected String dataDirName = "testDataDir";
	
	/** Loaded application context. */
	private ApplicationContext ctx;
    
    /**
     * Creates all services and a default test account
     */
    public void startApplication() {
      	// load the application
        ctx = new ClassPathXmlApplicationContext("/tatool/test-application-context.xml");
    	
       	// make sure the data directory is relative
       	UserAccountServiceImpl userAccountService = (UserAccountServiceImpl) getUserAccountService();
        userAccountService.setRelativeToHome(false);
        userAccountService.setDataDirName(dataDirName);
    }

    /**
     * Clears the data created by the test application, that is,
     * all user accounts created by it
     */
    public void deleteData() {
        System.out.println("Deleting data folder...");
        File dataDir = ((UserAccountServiceImpl) getUserAccountService()).getTatoolDataFolder();
        try {
            FileUtils.deleteDirectory(dataDir);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    /**
     * Call this method to run the module
     */
    public void runModule() {
    	// load tatool
        System.out.println("Initializing application...");
        startApplication();
        
        // create the account and the module
        UserAccount account = createAccount();
        Module module = createModule(account);
        
        // execute the module
        System.out.println("START: Executing module...");
        Executor executor = getExecutionService().createExecutor(module);
        getExecutionService().startExecution(executor, true);
        System.out.println("END:   Executing module.");
        
        // cleanup
        getUserAccountService().deleteAccount(account);
        deleteData();
    }
    
    /**
     * Creates and initializes a user account.
     * @return a newly created user account
     */
    protected UserAccount createAccount() {
        // create a test user account
        return getUserAccountService().createAccount(String.valueOf(new Date().getTime()), createAccountProperties(), null);   
    }
    
    /** Get the Account properties to use for account creation. */
    protected Map<String, String> createAccountProperties() {
    	Map<String, String> accountProperties = new HashMap<String, String>();
    	return accountProperties;
    }
    
    /**
     * Creates an empty persisted module instance.
     */
    protected Module createModule(UserAccount account) {
        // create the module using that configuration
        Module module = getModuleService().createModule(account, createModuleProperties(), createBinaryModuleProperties(), createModuleExporters());
        return module;
    }
    
    /** Create module properties used for module creation.
     * The default implementation only sets the module name.
     */
    protected Map<String, String> createModuleProperties() {
    	// put together the configuration
        Map<String, String> moduleProps = new HashMap<String,String>();
        moduleProps.put(Module.PROPERTY_MODULE_NAME, "Test module");
        return moduleProps;
    }
    
    /** Create binary module properties.
     * The default implementation returns an empty map.
     */
    protected Map<String, byte[]> createBinaryModuleProperties() {
    	Map<String, byte[]> props = new HashMap<String,byte[]>();
    	return props;
    }
    
    /** Create module exporters.
     * The default implementation returns an empty list.
     */
    protected Map<String, DataExporter> createModuleExporters() {
    	Map<String, DataExporter> exporters = new HashMap<String, DataExporter>();
    	return exporters;
    }
    
    public ApplicationContext getCtx() {
        return ctx;
    }

    public ModuleService getModuleService() {
    	return (ModuleService) ctx.getBean("ModuleService");
    }

    public UserAccountService getUserAccountService() {
    	return (UserAccountService) ctx.getBean("UserAccountService");
    }
    
    public ExecutionService getExecutionService() {
    	return (ExecutionService) ctx.getBean("ExecutionService");
    }
    
    public DataService getDataService() {
        return (DataService) ctx.getBean("DataService");
    }
    
    public DataExportService getDataExportService() {
        return (DataExportService) ctx.getBean("DataExportService");
    }
}
