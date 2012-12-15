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
package ch.tatool.core.module.creator;

import java.io.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;
import ch.tatool.module.ModuleCreator;
import ch.tatool.module.ModuleService;

public class LocalFileModuleCreator implements ModuleCreator {

    /** Last folder opened during module opening. */
    public String PROPERTY_ACCOUNT_LAST_OPENED_FOLDER = "LastOpenedFolder";
	
	private ModuleService moduleService;
	private JFrame parent;
	private Callback callback;
	private UserAccount userAccount;
	private ProgressDialog progressDialog;

	private Messages messages;
	
	// Additional module info: needed for HitC integration, ID also needed to load the appropriate module XML file.
	private int moduleID;
	private String code;
	
	public void setModuleID(int moduleID) {
		this.moduleID = moduleID;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void executeCreator(JFrame parent, UserAccount userAccount, ModuleService moduleService, Callback callback) throws IOException {
		this.parent = parent;
		this.callback = callback;
		this.userAccount = userAccount;
		this.moduleService = moduleService;
		
		// fetch the module file
		final File file = getModuleFile();
		
		if (file == null) {
			callback.closeDialog(null);
			return;
		}
		
		// do the rest in a separate thread
		progressDialog = new ProgressDialog(parent, true);
		progressDialog.setLabel(messages.getString("General.progress.label.openingModule"));
		
		// call setVisible() from another thread, as this thread would get blocked otherwise
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressDialog.setVisible(true);
			}
		});
		
		// Do the work in a separate thread
		new Thread() {
			public void run() {
				load(file);
			}
		}.start();
	}
	
	// local thread
	private void load(File file) {
		// create a worker to create the module
		final ModuleCreationWorker worker = new ModuleCreationWorker();
		worker.createModule(moduleService, userAccount, file, null, null, false, moduleID, code);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				finish(worker); 
			}
		});
	}
	
	// EDT thread
	private void finish(ModuleCreationWorker worker) {
		// hide the progress bar
		progressDialog.setVisible(false);
		
		// check whether we got a module
		Module t = worker.getModule();
		if (t == null) {
			JOptionPane.showMessageDialog(parent, worker.getErrorText(), worker.getErrorTitle(), JOptionPane.ERROR_MESSAGE);
		}

		// close the creator
		callback.closeDialog(t);
	}
	
	private File getModuleFile() throws IOException {
		// File chooser removed, now uses the module ID to determine the appropriate file
		// Assumes that the client JAR has a folder called modules that contains the modules
		InputStream in = getClass().getClassLoader().getResourceAsStream("modules/" + moduleID +".xml");
		File tempFile = File.createTempFile("module" + Integer.toString(moduleID), ".xml");
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(tempFile), "UTF8");
		
		int c;
		while ((c = in.read()) != -1) {
            out.write(c);
        }
		out.flush();
		
		in.close();
		out.close();
		
		return tempFile;
	}
	
	public String getCreatorId() {
		return "local-file-module-creator";
	}

	public String getCreatorName() {
		return messages.getString("General.creator.localFileCreator.title");
	}
	
	public void hideCreator() {
		// nothing to do
	}
	
	public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
		return messages;
	}
}
