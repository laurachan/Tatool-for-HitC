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
package ch.tatool.app.gui;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.Constants;
import ch.tatool.app.GuiController;
import ch.tatool.app.service.UserAccountService;
import ch.tatool.core.module.creator.LocalFileModuleCreator;
import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;
import ch.tatool.module.ModuleCreator;
import ch.tatool.module.ModuleService;

/**
 * Formerly ModuleManagerFrame.
 * 
 * This class creates the necessary objects for a particular module and opens the module once ready. 
 * The original functionality for maintaining a list of modules and allowing the user to add/select
 * modules has been removed since we only need to serve a single module (specified by the args given to
 * App.main() and passed to GuiController). 
 * 
 * @author Michael Ruflin (original ModuleManagerFrame implementation)
 * @author Laura Chan (HitC modifications)
 */
public class ModuleManager {
    
    Logger logger = LoggerFactory.getLogger(ModuleManager.class);
    
    public static final String DEFAULT_MODULE_ID = "default.module.id"; //$NON-NLS-1$
    
    /** IoC */
    private UserAccountService userAccountService;
    private ModuleService moduleService;
    private LocalFileModuleCreator moduleCreator; 
    private GuiController guiController;
    
    /** UserAccount for which to display modules. */
    private UserAccount account;

	private Messages messages;
  
    public ModuleManager() {
    	logger.info("Create new instance of ModuleManagerFrame"); //$NON-NLS-1$
    }
    
    /**
     * Initialize the manager with the user account and the ID and code of the module to be opened.
     */
    public void initialize(UserAccount account, int moduleID, String code) {
        this.account = account;
        moduleCreator.setModuleID(moduleID);
        moduleCreator.setCode(code);
        moduleCreator.setMessages(messages);
    }
    
    /** Opens a module. */
    private void openModule(Module module) {
        // update the account with information regarding the last opened module and whether the module should be loaded automatically
        account.getProperties().put(Constants.PROPERTY_LAST_LOADED_MODULE_ID, module.getId().toString());
        userAccountService.saveAccount(account);
        
        // now let the gui controller open the module
        guiController.setModule(module);
    }
    
    /**
     * Create and open the desired module. The module's ID and code should have already been set via a call to initialize().
     */
    public void createAndOpenModule() {
    	ModuleCreator.Callback callback = new ModuleCreator.Callback() {
    		public void closeDialog(Module newlyCreatedModule) {
    			moduleCreator.hideCreator();
    			if (newlyCreatedModule != null) {
    				openModule(newlyCreatedModule);
    			}
    		}
    	};
    	try {
    		moduleCreator.executeCreator(null, account, moduleService, callback);
    	} catch (IOException e) {
    		logger.error("Error creating module", e);
    	}
    }
    
    // Getters and setters
    public ModuleService getModuleService() {
        return moduleService;
    }

    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }
    
    public GuiController getGuiController() {
        return guiController;
    }

    public void setGuiController(GuiController guiController) {
        this.guiController = guiController;
    }

    public UserAccount getAccount() {
        return account;
    }

    public void setAccount(UserAccount account) {
        this.account = account;
    }

    public UserAccountService getUserAccountService() {
        return userAccountService;
    }

    public void setUserAccountService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }
    
    public LocalFileModuleCreator getModuleCreator() {
    	return moduleCreator;
    }
    
    public void setModuleCreator(LocalFileModuleCreator moduleCreator) {
    	this.moduleCreator = moduleCreator;
    }
	
	public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
		return messages;
	}
}
