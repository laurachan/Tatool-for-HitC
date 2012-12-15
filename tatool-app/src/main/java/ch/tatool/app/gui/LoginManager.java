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
package ch.tatool.app.gui;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.*;
import ch.tatool.app.service.UserAccountException;
import ch.tatool.app.service.UserAccountService;
import ch.tatool.data.Messages;
import ch.tatool.data.UserAccount;

/** 
 * Formerly LoginFrame.
 * 
 * User account selection is not a feature that we use (or want) in HitC. Instead, we use the same user
 * account whenever the client is run. Therefore this class is used to either load the single existing 
 * user account or (invisibly) create and open it if it does not exist. This class is no longer a GUI frame.
 * 
 * @author Andre Locher (original LoginFrame implementation)
 * @author Laura Chan (HitC modifications)
 */
public class LoginManager {

	private static Logger logger = LoggerFactory.getLogger(LoginManager.class);

	/** UserAccountService - provides data for this frame. */
	private UserAccountService userAccountService;

	/** GuiController. */
	private GuiController guiController;

	private Messages messages;

	public LoginManager() {
		logger.info("Create new instance of LoginManager");
	}

	/**
	 * Creates and opens the user account if none exist, or loads the existing one otherwise.
	 */
	public void loginAccount() {
		if (userAccountService.getAccounts().isEmpty()) {
			createAccount();
		} else {
			UserAccount account = loadAccount(userAccountService, userAccountService.getAccounts().get(0));
			this.openAccount(account);
		}
	}

	/**
	 * Creates a new user account if and only if there are none in existence. 
	 * Opens the account once created.
	 */
	public void createAccount() {
		if (!userAccountService.getAccounts().isEmpty()) {
			return;
		}
		
        HashMap<String, String> accountProperties = new HashMap<String, String>();
        
        // Set account language 
        // TODO we probably need to set this dynamically (another command-line arg needed?)
        accountProperties.put(Constants.PROPERTY_ACCOUNT_LANG, "en");
        
        // add machine properties 
        // TODO do we really need this?
        accountProperties.put(Constants.PROPERTY_MACHINE_OS_NAME, System.getProperty("os.name")); //$NON-NLS-1$
        accountProperties.put(Constants.PROPERTY_MACHINE_OS_ARCH, System.getProperty("os.arch")); //$NON-NLS-1$
        accountProperties.put(Constants.PROPERTY_MACHINE_OS_VERSION, System.getProperty("os.version")); //$NON-NLS-1$
        accountProperties.put(Constants.PROPERTY_MACHINE_USER_HOME, System.getProperty("user.home")); //$NON-NLS-1$

        // Note: account should not be null--the method below returns null only when there's another account with the same name
        // But this method should have already returned if there was already an existing account in the first place!
        UserAccount account = userAccountService.createAccount("", accountProperties, null);
        this.openAccount(account);
	}

	public void openAccount(UserAccount account) {	
		// display the training loader frame
		guiController.setUserAccount(account);
	}

	/**
	 * Opens a UserAccount object by asking the user for a password if
	 * necessary.
	 * 
	 * @param info
	 *            the info object of the account
	 * @return a loaded UserAccount object or null in case an error occured/the
	 *         user aborded entering a password
	 */
	public UserAccount loadAccount(UserAccountService userAccountService, UserAccount.Info info) {
		// check whether it is password protected
		String password = null;
		//TODO: why is info.isPasswordProtected() not always set?
		//password = getPassword();
		// load the training
		try {
			UserAccount account = userAccountService
					.loadAccount(info, password);
			return account;
		} catch (RuntimeException e) {
			// TODO: following code is database specific and error prone, as
			// we directly check the error message thrown!
			boolean isAccessDeniedException = false;
			Throwable tmp = e;

			while (tmp != null) {
				// hsqldb error
				if (tmp.getMessage().equals("Login failed.")) { //$NON-NLS-1$
					isAccessDeniedException = true;
					break;
				}
				// derby error
				if (tmp.getMessage().trim().equals("Startup failed. An encrypted database cannot be accessed without the correct boot password.")) { //$NON-NLS-1$
					isAccessDeniedException = true;
					break;
				}
				tmp = tmp.getCause();
			}

			if (isAccessDeniedException) {
				JOptionPane
						.showMessageDialog(
								null,
								messages.getString("LoginManager.errorMessage.wrongPassword"), messages.getString("General.errorMessage.windowTitle.warning"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			} else {
				// unknown problem
				logger.error("unable to open account", e); //$NON-NLS-1$
				JOptionPane
				.showMessageDialog(
						null,
						messages.getString("LoginManager.errorMessage.unknownProblem"), messages.getString("General.errorMessage.windowTitle.warning"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
				writeToFile("tatool.log", getStackTrace(e)); //$NON-NLS-1$
				throw new UserAccountException(e.getMessage(), e);
			}
		}
	}
	
	public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }
	
	public void writeToFile(String filename, String output) {
	        
		BufferedWriter bufferedWriter = null;	        
	    try {
	    	//Construct the BufferedWriter object
	    	String userHome = System.getProperty("user.home"); //$NON-NLS-1$
	        String sep = System.getProperty("file.separator"); //$NON-NLS-1$
	        bufferedWriter = new BufferedWriter(new FileWriter(userHome + sep + filename));
	        bufferedWriter.write(output);
	            
	    } catch (FileNotFoundException ex) {
	    	ex.printStackTrace();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    } finally {
	    	//Close the BufferedWriter
	        try {
	        	if (bufferedWriter != null) {
	        		bufferedWriter.flush();
	                bufferedWriter.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	}

	// Getters and setters
	public UserAccountService getUserAccountService() {
		return userAccountService;
	}

	public void setUserAccountService(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	public GuiController getGuiController() {
		return guiController;
	}

	public void setGuiController(GuiController guiController) {
		this.guiController = guiController;
	}
	
	public void setMessages(Messages messages) {
		this.messages = messages;
	}
	
	public Messages getMessages() {
		return messages;
	}
}
