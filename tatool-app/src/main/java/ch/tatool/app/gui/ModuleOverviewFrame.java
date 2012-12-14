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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.GuiController;
import ch.tatool.app.export.DataExportService;
import ch.tatool.app.service.exec.ExecutionService;
import ch.tatool.app.util.AppUtils;
import ch.tatool.data.DataService;
import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionPhase;
import ch.tatool.exec.ExecutionPhaseListener;
import ch.tatool.exec.Executor;
import ch.tatool.export.DataExporter;
import ch.tatool.module.ModuleScheduler;
import ch.tatool.module.ModuleSchedulerMessage;
import ch.tatool.module.ModuleService;
import ch.tatool.module.ModuleInfoProvider;

/**
 * Displays module information to the user. This frame is the main window the
 * user interacts and therefore hosts the overall application menu.
 * 
 * @author Michael Ruflin
 * @author Laura Chan (minor modifications for HitC)
 */
public class ModuleOverviewFrame extends javax.swing.JFrame implements
		ExecutionPhaseListener {

	private static final long serialVersionUID = 8223517200199952504L;

	Logger logger = LoggerFactory.getLogger(ModuleOverviewFrame.class);

	/** Module service. */
	private ModuleService moduleService;

	/** Module data service. */
	private DataService dataService;

	/** Property Export service. */
	private DataExportService dataExportService;

	/** Module execution service. */
	private ExecutionService executionService;

	/** GuiController. */
	private GuiController guiController;

	/** Module statistics provider. */
	private ModuleInfoProvider moduleInfoProvider;

	/** Module this frame displays. */
	private Module module;

	/** Executor object during execution. */
	private Executor executor;

	private Map<String, DataExporter> exporters;
	
	private Date lastExportDate;

	private Messages messages;

	public ModuleOverviewFrame() {
		logger.info("Create new instance of ModuleOverviewFrame"); //$NON-NLS-1$
	}

	/** Called by Spring after all beans have been set. */
	public void init() {
		executionService.getPhaseListenerManager().addExecutionPhaseListener(this, ExecutionPhase.EXECUTION_FINISH);
	}

	/**
	 * Set the module displayed by the frame.
	 * 
	 * @param module
	 */
	public void initialize(Module module) {
		this.module = module;
		
		initComponents();
		getRootPane().setDefaultButton(startModuleButton);
		
		// initialize the statistics
		initializeModuleInfoProvider();

		// updates the UI with information provided by the module
		updateUI();

		pack();
		setLocationRelativeTo(null);
		java.net.URL iconUrl = this.getClass().getResource("/ch/tatool/app/gui/icon.png"); //$NON-NLS-1$
		Image icon = getToolkit().getImage(iconUrl); 
		setIconImage(icon);
	}

	private void initializeModuleInfoProvider() {
		// check whether a provider class is defined in the module
		String providerClass = module
				.getModuleProperties()
				.get(Module.PROPERTY_MODULE_INFO_PROVIDER_CLASS);
		if (providerClass != null) {
			// create an instance - fail if an error occured
			ModuleInfoProvider infoProvider = (ModuleInfoProvider) AppUtils
					.createInstance(providerClass,
							ModuleInfoProvider.class, true);
			moduleInfoProvider = infoProvider;
			moduleInfoProvider.setModule(module);
		} else {
			moduleInfoProvider = null;
		}

		// Add module info panel if we got a provider
		moduleInfoContainer.removeAll();
		if (moduleInfoProvider != null) {
			moduleInfoContainer.removeAll();
			moduleInfoContainer.add(moduleInfoProvider
					.getModuleInfoPanel(), BorderLayout.CENTER);
			moduleInfoContainer.validate();
		} else {
			moduleInfoContainer.removeAll();
			moduleInfoContainer.validate();
		}
	}

	/**
	 * Updates the displayed data with the latest information from the module.
	 */
	private void updateUI() {
		// update the ui
		Map<String, String> props = module.getModuleProperties();

		// module name
		String moduleName = messages.getString("ModuleOverviewFrame.label.unknownModule"); //$NON-NLS-1$
		if (props.containsKey(Module.PROPERTY_MODULE_NAME)) {
			moduleName = props.get(Module.PROPERTY_MODULE_NAME);
		}

		// window title
		setTitle(messages.getString("General.title") + " - " + moduleName); //$NON-NLS-1$

		// check the module exporters
		initModuleExporters();

		// update the module info
		if (moduleInfoProvider != null) {
			moduleInfoProvider.updateModuleInfo(module, lastExportDate);
		}
	}
	
	private void initModuleExporters() {
		exporters = module.getModuleExporters();

		exportButtonPanel.removeAll();

		if (exporters.size() > 0) {
			Iterator<String> it = exporters.keySet().iterator(); 
			while(it.hasNext()) { 
				final String exporterKey = it.next(); 
				DataExporter ex = exporters.get(exporterKey);
				// get last export date while we're scanning through
				if (dataExportService.getLastExportDate(module, exporterKey) == null) {
				} else if (lastExportDate == null) {
					lastExportDate = dataExportService.getLastExportDate(module, exporterKey);
				} else if (dataExportService.getLastExportDate(module, exporterKey).after(lastExportDate)) {
					lastExportDate = dataExportService.getLastExportDate(module, exporterKey);
				}
				JButton exportButton = new JButton();
				exportButton.setFont(new Font("Tahoma", 0, 13)); //$NON-NLS-1$
				exportButton.setText(ex.getExporterName());
				exportButton.setMaximumSize(new Dimension(109, 25));
				exportButton.setMinimumSize(new Dimension(109, 25));
				exportButton.setPreferredSize(new Dimension(109, 25));
		        exportButton.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent evt) {
		            	exportData(exporterKey);
		            }
		        });
				exportButtonPanel.add(exportButton);
			} 
		}
		exportButtonPanel.validate();
	}

	/** Enables or disables the module management links. */
	public void setModuleManagementEnabled(boolean enabled) {
		//manageTrainingsMenuItem.setVisible(enabled);
	}

	// TrainingExecutionListener

	public void processExecutionPhase(ExecutionContext event) {
		switch (event.getPhase()) {
		case EXECUTION_FINISH:
			moduleFinished();
			break;
		default: // nothing to do, this is just here to suppress a warning
			break;
		}
	}

	private void moduleFinished() {
		// update the ui
		if (this.module != null) {
			startModuleButton.setEnabled(true);
			updateUI();
			if (exporters != null) {
				Iterator<String> it = exporters.keySet().iterator(); 
				while(it.hasNext()) { 
					final String exporterKey = it.next(); 
					DataExporter ex = exporters.get(exporterKey);
					if (ex.isAutoExport()) {
						exportData(exporterKey);
					}
				}
			}
			
		}
	}

	private void startModule() {
		ModuleScheduler moduleScheduler = module.getModuleScheduler();
		ModuleSchedulerMessage message;
		
		if (moduleScheduler != null) {
			message = moduleScheduler.isSessionStartAllowed(module);
			
			if (message.isSessionStartAllowed()) {
				startModuleButton.setEnabled(false);
				executor = executionService.createExecutor(module);
				executionService.startExecution(executor, false);
			} else {
				JOptionPane.showMessageDialog(this,
					    message.getMessageText(),
					    message.getMessageTitle(), JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			// if we don't have a scheduler, allow the start of the module
			executor = executionService.createExecutor(module);
			executionService.startExecution(executor, false);
		}
	}

	private void exportData(final String exporterKey) {
		// check whether there is something to export
		if (!dataExportService.containsPendingExportData(module, exporterKey)) {
			// check whether the user really wants to upload
			int result = JOptionPane
					.showConfirmDialog(
							this,
							messages.getString("ModuleOverviewFrame.label.confirmExport"), //$NON-NLS-1$
							messages.getString("ModuleOverviewFrame.export.windowTitle"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$
			if (result != JOptionPane.YES_OPTION) {
				return;
			} else {
				//re-upload everything starting from -1
				module.getModuleProperties().put(exporterKey + "." + DataExportService.PROPERTY_LAST_EXPORTED_SESSION, "-1"); //$NON-NLS-1$
			}
		}
		
		// perform the export in a new thread
		final ProgressDialog progressDialog = new ProgressDialog(this, true);
		progressDialog.setTitle(messages.getString("General.progress.label.pleaseWait")); //$NON-NLS-1$
		progressDialog.setLabel(messages.getString("ModuleOverviewFrame.export.label.exportingData")); //$NON-NLS-1$
		// call setVisible() from another thread, as this thread would get blocked otherwise
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Note: if the module has auto-export enabled, this line may throw an UnsupportedOperationException 
				// This bug can also be reproduced in standard Tatool 1.2, so the HitC modifications aren't responsible...
				progressDialog.setVisible(true);
			}
		});
		
		// Do the work in a separate thread
		new Thread() {
			public void run() {
				doExport(progressDialog, exporterKey);
			}
		}.start();
	}
	
	// Non-EDT thread
	private void doExport(final ProgressDialog progressDialog, String exporterKey) {
		final String errorMessage = dataExportService.exportData(this, module, exporterKey);
		final Date exportDate = dataExportService.getLastExportDate(module, exporterKey);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				exportFinished(progressDialog, errorMessage, exportDate);
			}
		});
	}
	
	private void exportFinished(ProgressDialog progressDialog, String errorMessage, Date exportDate) {
		progressDialog.dispose();
		
		if (errorMessage != null) {
			JOptionPane.showMessageDialog(this, errorMessage, messages.getString("General.errorMessage.windowTitle.error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}
		
		// update the save date
		if (exportDate != null) {
			// update the module info
			if (moduleInfoProvider != null) {
				moduleInfoProvider.updateModuleInfo(module, exportDate);
			}
		}
		
		if (errorMessage == null) {
			JOptionPane.showMessageDialog(this, messages.getString("ModuleOverviewFrame.export.label.exportSuccessful"), messages.getString("ModuleOverviewFrame.export.windowTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	// Getters and Setters

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

	public DataService getDataService() {
		return dataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public GuiController getGuiController() {
		return guiController;
	}

	public void setGuiController(GuiController guiController) {
		this.guiController = guiController;
	}

	public DataExportService getDataExportService() {
		return dataExportService;
	}

	public void setDataExportService(DataExportService dataExportService) {
		this.dataExportService = dataExportService;
	}
	
	public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
		return messages;
	}

	// UI Action methods

	private void exitMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_exitMenuItemActionPerformed
		guiController.shutdown();
	}// GEN-LAST:event_exitMenuItemActionPerformed

	private void aboutMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_aboutMenuItemActionPerformed
		JDialog aboutDialog = new AboutDialog(this, false);
		aboutDialog.setLocationRelativeTo(null);
		aboutDialog.setVisible(true);
	}// GEN-LAST:event_aboutMenuItemActionPerformed

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// @SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    	getContentPane().removeAll();
        JPanel jPanel5 = new JPanel();
        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel();
        moduleInfoContainer = new JPanel();
        JPanel jPanel4 = new JPanel();
        JButton exitButton = new JButton();
        startModuleButton = new JButton();
        JLabel jLabel1 = new JLabel();
        exportButtonPanel = new JPanel();
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenu1 = new JMenu();
        JMenuItem exitMenuItem = new JMenuItem();
        JMenu jMenu3 = new JMenu();
        JMenuItem helpMenuItem = new JMenuItem();
        JMenuItem aboutMenuItem = new JMenuItem();
        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setTitle(messages.getString("General.title")); //$NON-NLS-1$
        setMinimumSize(new Dimension(600, 350));

        jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        jPanel2.setPreferredSize(new Dimension(400, 400));

        moduleInfoContainer.setBorder(BorderFactory.createTitledBorder("")); //$NON-NLS-1$
        moduleInfoContainer.setMinimumSize(new Dimension(633, 402));
        moduleInfoContainer.setPreferredSize(new Dimension(400, 400));
        moduleInfoContainer.setLayout(new BorderLayout());
        moduleInfoContainer.removeAll();

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(moduleInfoContainer, GroupLayout.PREFERRED_SIZE, 418, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(moduleInfoContainer, GroupLayout.PREFERRED_SIZE, 314, Short.MAX_VALUE)
        );

        exitButton.setFont(new Font("Tahoma", 0, 13)); //$NON-NLS-1$
        exitButton.setText(messages.getString("ModuleOverviewFrame.label.exit")); //$NON-NLS-1$
        exitButton.setMaximumSize(new Dimension(109, 25));
        exitButton.setMinimumSize(new Dimension(109, 25));
        exitButton.setPreferredSize(new Dimension(109, 25));
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        startModuleButton.setFont(new Font("Tahoma", 0, 13)); //$NON-NLS-1$
        startModuleButton.setText(messages.getString("ModuleOverviewFrame.label.start")); //$NON-NLS-1$

        startModuleButton.setMaximumSize(new Dimension(109, 25));
        startModuleButton.setMinimumSize(new Dimension(109, 25));
        startModuleButton.setPreferredSize(new Dimension(109, 25));
        startModuleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                startTrainingButtonActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new ImageIcon(getClass().getResource("/ch/tatool/app/gui/tatool.png"))); // NOI18N //$NON-NLS-1$
        exportButtonPanel.setLayout(new GridLayout(0, 1, 0, 5));

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                        .addGap(9, 9, 9))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(exportButtonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(startModuleButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(30, 30, 30)
                .addComponent(startModuleButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(exportButtonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(exitButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(178, Short.MAX_VALUE))
            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, BorderLayout.CENTER);

        jMenu1.setText(messages.getString("ModuleOverviewFrame.labelMenu.file")); //$NON-NLS-1$

        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        exitMenuItem.setText(messages.getString("ModuleOverviewFrame.labelMenu.exit")); //$NON-NLS-1$
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exitMenuItem);

        jMenuBar1.add(jMenu1);

        jMenu3.setText(messages.getString("ModuleOverviewFrame.labelMenu.help")); //$NON-NLS-1$

        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenuItem.setText(messages.getString("ModuleOverviewFrame.labelMenu.helpTopics")); //$NON-NLS-1$
        helpMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(helpMenuItem);

        aboutMenuItem.setText(messages.getString("ModuleOverviewFrame.labelMenu.aboutTatool")); //$NON-NLS-1$
        aboutMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        jMenu3.add(aboutMenuItem);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void startTrainingButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_startTrainingButtonActionPerformed
		startModule();
	}// GEN-LAST:event_startTrainingButtonActionPerformed

	private void exitButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_exitButtonActionPerformed
		guiController.shutdown();
	}// GEN-LAST:event_exitButtonActionPerformed

	private void helpMenuItemActionPerformed(ActionEvent evt) {// GEN-FIRST:event_aboutMenuItem1ActionPerformed
		
        if( !java.awt.Desktop.isDesktopSupported() ) {
            System.err.println( messages.getString("ModuleOverviewFrame.errorMessage.desktopError") ); //$NON-NLS-1$
            System.exit( 1 );
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
            JOptionPane.showMessageDialog(this, messages.getString("ModuleOverviewFrame.errorMessage.desktopError2")); //$NON-NLS-1$
        } else {
            try {
            java.net.URI uri = new java.net.URI( "http://www.tatool.ch/faq.htm" ); //$NON-NLS-1$
            desktop.browse( uri );
        } catch ( Exception e ) {
            System.err.println( e.getMessage() );
        }
        }

	}// GEN-LAST:event_aboutMenuItem1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JPanel exportButtonPanel;
    JPanel moduleInfoContainer;
    JButton startModuleButton;
    // End of variables declaration//GEN-END:variables

}
