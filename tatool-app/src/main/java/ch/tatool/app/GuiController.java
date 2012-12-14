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

import java.awt.event.*;
import java.util.*;

import javax.swing.SwingUtilities;

import org.slf4j.*;

import ch.tatool.app.gui.*;
import ch.tatool.app.service.UserAccountService;
import ch.tatool.app.service.exec.ExecutionService;
import ch.tatool.data.*;
import ch.tatool.exec.*;
import ch.tatool.module.*;

/**
 * This class acts as the gui and data manager for the Tatool.
 * 
 * It contains the glue code between the different windows to keep one from each
 * other separated and centrally managed.
 * 
 * The controller also organizes loading the single user account used to store all
 * user data and the module with the specified ID.
 * 
 * @author Michael Ruflin (original implementation)
 * @author Laura Chan (HitC modifications)
 */
public class GuiController {

	private Logger logger = LoggerFactory.getLogger(GuiController.class);

	// frame keys set in the state maps
	public static final String MODULE_OPEN_FRAME = "ModuleOpenFrame";
	public static final String MODULE_OVERVIEW_FRAME = "ModuleOverviewFrame";

	/** Current Account instance. */
	private UserAccount userAccount;

	/** Current module instance. */
	private Module module;

	// Services
	private UserAccountService userAccountService;
	private ModuleService moduleService;
	private ExecutionService executionService;

	// Classes for loading the user account and the desired module
	private LoginManager loginManager;
	private ModuleManager moduleManager;
	
	// Module overview GUI
	private ModuleOverviewFrame moduleOverviewFrame;

	private FrameVisibilityListener frameVisibilityListener;
	private ModuleExecutorWindowDisplayer moduleExecutionListener;

	/** state variables */
	private boolean enabled;

	private Map<String, Boolean> displayedWindows;
	private Map<String, Boolean> enabledWindows;
	
	// Additional info needed for loading the correct module and for HitC integration
	private int moduleID;
	private String code;

	public GuiController() {
		enabled = false;
		displayedWindows = new HashMap<String, Boolean>();
		enabledWindows = new HashMap<String, Boolean>();
		enabledWindows.put(MODULE_OVERVIEW_FRAME, true);
		enabledWindows.put(MODULE_OPEN_FRAME, true);
		displayedWindows.put(MODULE_OVERVIEW_FRAME, false);
		displayedWindows.put(MODULE_OPEN_FRAME, false);

		frameVisibilityListener = new FrameVisibilityListener();
		moduleExecutionListener = new ModuleExecutorWindowDisplayer();
	}

	/** Called by spring after all properties have been set. */
	public void init() {
		// add the frame visibility listener to all frames
		moduleOverviewFrame.addComponentListener(frameVisibilityListener);
		executionService.getPhaseListenerManager().addExecutionPhaseListener(moduleExecutionListener, ExecutionPhase.EXECUTION_START);
		executionService.getPhaseListenerManager().addExecutionPhaseListener(moduleExecutionListener, ExecutionPhase.EXECUTION_FINISH);
	}

	public void setModuleID(int moduleID) {
		this.moduleID = moduleID;
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Enable the controller. If enabled, the controller will react to module
	 * and account changes by adapting the UI accordingly.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Starts the controller
	 */
	public void startGUI() {
		displayMostSuitableWindow();
	}

	private void displayMostSuitableWindow() {
		if (userAccount != null && module != null) {
			displayModuleOverviewFrame();
		} else if (userAccount != null) {
			// We are ready to load the module
			moduleManager.initialize(userAccount, moduleID, code);
			moduleManager.createAndOpenModule();
		} else {
			loginManager.loginAccount();
		}
	}

	// frame hiding

	private void hideModuleOverviewFrame() {
		if (displayedWindows.get(MODULE_OVERVIEW_FRAME)) {
			displayedWindows.put(MODULE_OVERVIEW_FRAME, false);
			moduleOverviewFrame.setVisible(false);
		}
	}

	// frame displaying

	public void displayModuleOverviewFrame() {
		if (userAccount == null || module == null) {
			throw new RuntimeException(
					"Cannot display module overview without account and module");
		}

		moduleOverviewFrame.initialize(module);
		moduleOverviewFrame.setVisible(true);
		moduleOverviewFrame.toFront();
		displayedWindows.put(MODULE_OVERVIEW_FRAME, true);
	}

	// data objects closing

	private void closeCurrentModule() {
		if (this.module != null) {
			// close the module window if it is displayed currently
			hideModuleOverviewFrame();

			// close the module
			moduleService.closeModule(this.module);
			this.module = null;
		}
	}

	private void closeCurrentAccount() {
		if (this.userAccount != null) {
			// close the previous user account
			userAccountService.closeAccount(this.userAccount);
			this.userAccount = null;
		}
	}

	// data object setting/setup

	public void setModule(final Module module) {
		// close the module if one is present
		// as this will involve closing the window
		closeCurrentModule();

		// assign the new module
		this.module = module;

		// if enabled, display the window. Postpone this until all AWT events
		// have been processed,
		// which includes the window closing event possibly triggered by
		// closeCurrentModule
		if (enabled) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					displayMostSuitableWindow();
				}
			});
		}
	}

	public void setUserAccount(final UserAccount userAccount) {
		// close the current module and account
		closeCurrentModule();
		closeCurrentAccount();
		this.userAccount = userAccount;

		// display the appropriate window, but postpone this until all AWT
		// events have been processed
		if (enabled) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (userAccount != null) {
						checkAccountPreferences();
					}
					displayMostSuitableWindow();
				}
			});
		}
	}

	/**
	 * Accounts can have a default module set, or defined that no module
	 * selection/management is allowed
	 */
	private void checkAccountPreferences() {
		checkLoadLastOpenedAccount();
		checkDisableModuleManager();
	}

	/** Check whether the last opened account should be loaded. */
	private void checkLoadLastOpenedAccount() {
		// check whether we should automatically load the last opened module
		if (!(userAccount.getProperties().containsKey(
				Constants.PROPERTY_OPEN_LAST_LOADED_MODULE) && (Boolean
				.parseBoolean(userAccount.getProperties().get(
						Constants.PROPERTY_OPEN_LAST_LOADED_MODULE))))) {
			return;
		}

		// try to get the id of the module to load
		String defaultModuleId = userAccount.getProperties().get(
				Constants.PROPERTY_LAST_LOADED_MODULE_ID);
		if (defaultModuleId == null) {
			return;
		}

		// find a module info object with given id
		Module.Info tInfo = null;
		for (Module.Info i : moduleService.getModules(userAccount)) {
			if (i.getId().toString().equals(defaultModuleId)) {
				tInfo = i;
				break;
			}
		}
		// load the module if we have a module info object
		if (tInfo != null) {
			Module module = moduleService.loadModule(tInfo);
			// bypass the setter here
			this.module = module;
		}
	}

	/** Check whether the module manager should be disabled. */
	private void checkDisableModuleManager() {
		String value = userAccount.getProperties().get(
				Constants.DISABLE_MODULE_MANAGEMENT);
		boolean moduleManagerDisabled = (value != null && Boolean
				.parseBoolean(value));
		if (!moduleManagerDisabled) {
			moduleOverviewFrame.setModuleManagementEnabled(true);
			return;
		}

		// disable module management
		moduleOverviewFrame.setModuleManagementEnabled(false);

		// check whether we already got a module (hardly possible,
		// but use that module in such a case)
		if (this.module != null) {
			return;
		}

		// load the first module available in the account
		Set<Module.Info> infos = moduleService.getModules(userAccount);
		if (infos.size() < 1) {
			// this should never happen - it means we have an account without
			// module but that does not allow a module creation
			throw new RuntimeException(
					"Invalid user account - module management disabled but no modules available!");
		}

		Module.Info tInfo = infos.iterator().next();
		Module module = moduleService.loadModule(tInfo);
		// bypass the setter here
		this.module = module;
	}
	
	void openDetailClosed() {
		if (displayedWindows.get(MODULE_OPEN_FRAME)) {
			displayedWindows.put(MODULE_OPEN_FRAME, false);
			checkProgramExit();
		}
	}

	void moduleOverviewClosed() {
		if (displayedWindows.get(MODULE_OVERVIEW_FRAME)) {
			displayedWindows.put(MODULE_OVERVIEW_FRAME, false);
			checkProgramExit();
		}
	}

	void checkProgramExit() {
		boolean open = false;
		for (Boolean b : displayedWindows.values()) {
			open = open || b;
		}
		if (!open) {
			// cleanup
			shutdown();
		}
	}

	public void shutdown() {
		logger.info("Shutting down...");
		closeCurrentModule();
		closeCurrentAccount();
		System.exit(0);
	}

	// event listeners

	class FrameVisibilityListener extends ComponentAdapter {

		@Override
		public void componentHidden(ComponentEvent e) {
			if (e.getSource() == moduleOverviewFrame) {
				moduleOverviewClosed();
			} 
		}

	}

	// Module Listener

	class ModuleExecutorWindowDisplayer implements ExecutionPhaseListener {

		public void processExecutionPhase(ExecutionContext context) {
			switch (context.getPhase()) {
			case EXECUTION_FINISH:
				moduleFinished();
				 break;
			default:
				break;
			}
		}

		private void moduleFinished() {
			checkProgramExit();
		}
	}

	// Setters and getters

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public Module getModule() {
		return module;
	}

	public LoginManager getLoginManager() {
		return loginManager;
	}

	public void setLoginManager(LoginManager loginManager) {
		this.loginManager = loginManager;
	}

	public ModuleOverviewFrame getModuleOverviewFrame() {
		return moduleOverviewFrame;
	}

	public void setModuleOverviewFrame(
			ModuleOverviewFrame moduleOverviewFrame) {
		this.moduleOverviewFrame = moduleOverviewFrame;
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

	public ExecutionService getExecutionService() {
		return executionService;
	}

	public void setExecutionService(
			ExecutionService executionService) {
		this.executionService = executionService;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public void setModuleManager(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}
}
