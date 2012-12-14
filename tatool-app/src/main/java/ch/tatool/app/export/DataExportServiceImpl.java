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
package ch.tatool.app.export;

import java.awt.Component;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.gui.MessagesImpl;
import ch.tatool.data.Module;
import ch.tatool.data.DataService;
import ch.tatool.data.ModuleSession;
import ch.tatool.export.DataExporter;

/**
 * Implementation of the ExporterService
 * 
 * @author Michael Ruflin
 *
 */
public class DataExportServiceImpl implements DataExportService {

    /** Logger used by the service. */
    Logger logger = LoggerFactory.getLogger(DataExportServiceImpl.class);
    
    private DataService dataService;
    
    private MessagesImpl messages;

    public DataExportServiceImpl() {
    }

    public boolean exportConfigured(Module module, String exporterKey) {
        return module.getModuleExporter(exporterKey) != null;
    }
    
    public boolean containsPendingExportData(Module module, String exporterKey) {
        // check whether we got an exporter class defined
        if (!exportConfigured(module, exporterKey)) {
            return false;
        }
        
        // nothing to export if there are no sessions
        ModuleSession session = dataService.getLastSession(module);
        if (session == null) {
            return false;
        }

        // check last exported session id with what's stored in the module
        String lastExportSessionId = module.getModuleProperties().get(exporterKey + "." + DataExportService.PROPERTY_LAST_EXPORTED_SESSION);

        if (lastExportSessionId != null && lastExportSessionId.equals(String.valueOf(session.getIndex()))) {
            return false;
        }
        
        return true;
    }
    
    public Date getLastExportDate(Module module, String exporterKey) {
    	Date lastExportDate = null;
    	
    	// check whether we got an exporter class defined
        if (!exportConfigured(module, exporterKey)) {
            return null;
        }
        
    	String lastExportDateString = module.getModuleProperties().get(exporterKey + "." + DataExportService.PROPERTY_LAST_EXPORT_TIMESTAMP);
    	if (lastExportDateString != null && !lastExportDateString.isEmpty()) {
    		lastExportDate = new Date(Long.parseLong(lastExportDateString));
    	}
 
		return lastExportDate;
    }

    public String exportData(Component parent, Module module, String exporterKey) {
    	
    	DataExporter exporter = module.getModuleExporter(exporterKey);
    	
    	// check whether we got an exporter class defined
        if (exporter == null) {
        	throw new RuntimeException("Exporter not found");
        }

        if (exporter.getDataService() == null) {
        	exporter.setDataService(dataService);
        }
        
        dataService.setMessages(messages);
        
        // session index to export from
        int fromSessionIndex = -1;
        
        // check whether we should export incrementally
        boolean incremental = exporter.isIncrementalExport();
        if (incremental) {
            // find out about the last exported session index
            String s = module.getModuleProperties().get(exporterKey + "." + DataExportService.PROPERTY_LAST_EXPORTED_SESSION);
            if (s != null) {
                try {
                    fromSessionIndex = Integer.parseInt(s);
                    fromSessionIndex++;
                } catch (NumberFormatException nfe) {
                    logger.warn("Unable to cast last session index to a number. Exporting all sessions", nfe);
                }
            }
        }
        
        // now perform the export
        DataExporterErrorImpl exporterError = new DataExporterErrorImpl();
        exporterError.setMessages(messages);
        try {
        	exporter.exportData(parent, module, fromSessionIndex, exporterError);
        	if (exporterError.getErrorType() != 0) {
        		dataService.saveModule(module);
        		return exporterError.getErrorMessage();
        	}
            // update the export information
            module.getModuleProperties().put(exporterKey + "." + DataExportService.PROPERTY_LAST_EXPORT_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
            ModuleSession session = dataService.getLastSession(module);
            if (session != null) {
                // should always be the case
                module.getModuleProperties().put(exporterKey + "." + DataExportService.PROPERTY_LAST_EXPORTED_SESSION, String.valueOf(session.getIndex()));
            }
            dataService.saveModule(module);
        } catch (RuntimeException ioe) {
        	logger.error(ioe.getMessage(), ioe);
        }
        return null;
    }

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
    
    public void setMessages(MessagesImpl messages) {
    	this.messages = messages;
    }
    
    public MessagesImpl getMessages() {
    	return messages;
    }
    
}
