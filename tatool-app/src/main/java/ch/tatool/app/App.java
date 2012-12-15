/*******************************************************************************
 * Copyright (c) 2011 Michael Ruflin, André Locher, Claudia von Bastian.
 * Copyright (c) 2012 Laura Chan.
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

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ch.tatool.app.service.UserAccountService;
import ch.tatool.data.UserAccount;
import ch.tatool.module.ModuleService;

/**
 * Application entry point.
 * 
 * Loads the ApplicationContext which in return instantiates this class, which finally kicks off the display of the UI
 * once everything has been initialized.
 */
public class App implements ApplicationListener { 
    
    private Logger logger = LoggerFactory.getLogger(App.class);
    
    /** IoC beans. */
    private GuiController guiController;
    private UserAccountService userAccountService;
    private ModuleService moduleService;
    
    /** Default constructor. */
    public App() {
        logger.info("App bean loaded - waiting until context completely initialized...");
    }
    
    /**
     * Starts the application once everything has been initialized.
     */
    private void startApplication() {
    	UserAccount account = null;
        guiController.setEnabled(true);
        guiController.setUserAccount(account);
    }
    
    
    /** Interface ApplicationListener. */
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextRefreshedEvent) {
            logger.info("Context initialized - starting the application...");
            startApplication();
        }
    }
 

    // Getters and Setters
    
    public GuiController getGuiController() {
        return guiController;
    }

    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }

    public UserAccountService getUserAccountService() {
        return userAccountService;
    }

    public void setUserAccountService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    public ModuleService getModuleService() {
        return moduleService;
    }

    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }


    /**
     * Main method. Loads the application through an application context.
     * Requires two arguments: module ID (integer) and code (String). These are used to
     * set which module will be loaded. 
     * 
     * The main class App itself then performs the final initialization by loading the 
     * module and displaying the GUI.
     */
    public static void main( String[] args )
    {
    	// TODO localize error messages
    	if (args.length < 2) {
    		JOptionPane.showMessageDialog(null, "Please provide module ID number and code.", "Error: missing module data", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	// load the application
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/tatool/application-context.xml");
    	
        // Set the module that should be loaded
    	GuiController controller = (GuiController) ctx.getBean("GuiController");
    	try {
    		controller.setModuleID(Integer.parseInt(args[0]));
    		controller.setCode(args[1]);
    	} catch (NumberFormatException e) {
    		JOptionPane.showMessageDialog(null, args[0] + " is not a valid module ID number.", "Error: invalid module ID", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    }
}
