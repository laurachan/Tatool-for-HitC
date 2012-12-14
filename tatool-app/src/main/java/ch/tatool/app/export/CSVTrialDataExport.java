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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;
import ch.tatool.app.Constants;
import ch.tatool.data.DataService;
import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;
import ch.tatool.data.DataContainer.Entry;

/**
 * Exports module data into a CSV file.
 * This is a helper class used by the various exporters
 * 
 * @author Michael Ruflin
 */
public class CSVTrialDataExport {

    Logger logger = LoggerFactory.getLogger(CSVTrialDataExport.class);
    
    private DataService dataService;
    
    private List<String> baseProperties;
    private int basePropertiesSize;
    private List<String> sessionProperties;
    private List<String> trialProperties;
    private int trialPropertiesSize;
    private boolean useTrialShortNames = false;
    private boolean useSessionShortNames = false;

	private char separator = ';';
    
    public CSVTrialDataExport(DataService dataService) {
        this.dataService = dataService;
    }
    
    /**
     * Exports the module data and stores the results in the provided Writer
     * 
     * @param module
     * @param file the file to write the data to
     * @return a file containing the module data or null in case an error occured
     */
    public File exportData(Module module, int fromSessionIndex) {
    	 // this won't change
        initBaseProperties(module);
        // initialize the trial properties
        initTrialProperties(module);

        // create a writer
        try {
            File tmpFile = File.createTempFile("trialData", "csv");
            FileOutputStream fos = new FileOutputStream(tmpFile, false);
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(fos,"ISO-8859-1"), separator);

            // write the csv file header
            List<String> headers = new ArrayList<String>(baseProperties);
            
            // try to use shorthand headers if unique
            HashSet<String> trialPropertiesShort = new HashSet<String>();
            for (String name : trialProperties) {
            	String newName = "trial.";
            	String[] nameParts = name.split("\\.");
            	if (nameParts.length > 1) {
            		newName += nameParts[nameParts.length - 2] + ".";
            	}
            	newName += nameParts[nameParts.length - 1];
            	trialPropertiesShort.add(newName);
            }

            if (trialPropertiesShort.size() == trialProperties.size()) {
            	useTrialShortNames = true;
            	trialProperties.clear();
            	trialProperties.addAll(trialPropertiesShort);
            	Collections.sort(trialProperties, String.CASE_INSENSITIVE_ORDER);
            	headers.addAll(trialProperties);
            } else {
            	Collections.sort(trialProperties, String.CASE_INSENSITIVE_ORDER);
            	headers.addAll(trialProperties);
            }

            writer.writeNext(headers.toArray(new String[headers.size()]));

            // fetch each session and trial, write one line for each trial
            List<ModuleSession> sessions = dataService.getSessions(module);
            for (ModuleSession session : sessions) {
                // skip if we only export some of the session
                if (fromSessionIndex >= 0 && session.getIndex() < fromSessionIndex) {
                    continue;
                }
                
                // export all trials for this session
                List<Trial> trials = dataService.getTrials(session);
                for (Trial trial : trials) {
                    List<String[]> trialData = getDataForTrial(session, trial);
                    writer.writeAll(trialData);
                }
            }
            
            // close the writer and return the file
            writer.close();
            return tmpFile;
        } catch (IOException ioe) {
            logger.error("Unable to write csv file", ioe);
            return null;
        }
    }
    
    /** Returns a list with all to be written values for a given trial object. */
    private List<String[]> getDataForTrial(ModuleSession session, Trial trial) {
        List<String[]> data = new ArrayList<String[]>(basePropertiesSize + trialPropertiesSize);

        // add trial properties
        addTrialProperties(session, trial, data);
        
        // done already
        return data;
    }
    
    
    // Base properties management
    
    /** Basic properties which are always written. */
    private void initBaseProperties(Module module) {
        baseProperties = new ArrayList<String>();
        
        // account related
        baseProperties.add("user.name");
        baseProperties.add("user." + Constants.PROPERTY_ACCOUNT_BIRTH);
        baseProperties.add("user." + Constants.PROPERTY_ACCOUNT_SEX);
        baseProperties.add("user." + Constants.PROPERTY_MACHINE_OS_NAME);
        
        // module related
        baseProperties.add("module.name");
        baseProperties.add("module.version");
        
        // tatool online related
        Map<String, String> moduleProperties = module.getModuleProperties();
        if (moduleProperties.containsKey("tatool.online.subject.code")) {
        	baseProperties.add("module.online.subject.code");
        }
        if (moduleProperties.containsKey("tatool.online.study.id")) {
        	baseProperties.add("module.online.study.id");
        }
        if (moduleProperties.containsKey("tatool.online.module.nr")) {
        	baseProperties.add("module.online.module.nr");
        }
        if (moduleProperties.containsKey("tatool.online.group.nr")) {
        	baseProperties.add("module.online.group.nr");
        }
        
        // session related
        baseProperties.add("session.id");
        baseProperties.add("session.index");
        baseProperties.add("session.startTime");
        baseProperties.add("session.endTime");
        addSessionBaseProperties(module);
        
        // trial related
        baseProperties.add("trial.id");
        baseProperties.add("trial.index");
        baseProperties.add("trial.name");

        basePropertiesSize = baseProperties.size();        
    }
    
    private void addSessionBaseProperties(Module module) {
    	sessionProperties = getAllSessionPropertyNames(module);
    	
    	// try to use shorthand headers if unique
        HashSet<String> sessionPropertiesShort = new HashSet<String>();
        for (String name : sessionProperties) {
        	String newName = "session.";
        	String[] nameParts = name.split("\\.");
        	if (nameParts.length > 1) {
        		newName += nameParts[nameParts.length - 2] + ".";
        	}
        	newName += nameParts[nameParts.length - 1];
        	sessionPropertiesShort.add(newName);
        }

        if (sessionPropertiesShort.size() == sessionProperties.size()) {
        	useSessionShortNames = true;
        	sessionProperties.clear();
        	sessionProperties.addAll(sessionPropertiesShort);
        	Collections.sort(sessionProperties, String.CASE_INSENSITIVE_ORDER);
        	baseProperties.addAll(sessionProperties);	
        } else {
        	Collections.sort(sessionProperties, String.CASE_INSENSITIVE_ORDER);
        	baseProperties.addAll(sessionProperties);	
        }
	}

	private String[] getBaseProperties(ModuleSession session, Trial trial) {

    	ArrayList<String> baseProps = new ArrayList<String>();
    	
    	 // account related
    	baseProps.add(session.getModule().getUserAccount().getName());
    	
    	Map<String, String> accountProperties = session.getModule().getUserAccount().getProperties();
    	baseProps.add(accountProperties.get(Constants.PROPERTY_ACCOUNT_BIRTH));
    	baseProps.add(accountProperties.get(Constants.PROPERTY_ACCOUNT_SEX));
    	baseProps.add(accountProperties.get(Constants.PROPERTY_MACHINE_OS_NAME));
        
    	Map<String, String> moduleProperties = session.getModule().getModuleProperties();
    	
    	// module related
    	baseProps.add(session.getModule().getName());
    	if (!moduleProperties.get(Module.PROPERTY_MODULE_VERSION).equals(null)) {
    		baseProps.add(moduleProperties.get(Module.PROPERTY_MODULE_VERSION));
    	} else {
    		baseProps.add("");
    	}
    	
    	
    	// tatool online related
        if (moduleProperties.containsKey("tatool.online.subject.code")) {
        	baseProps.add(moduleProperties.get("tatool.online.subject.code"));
        }
        if (moduleProperties.containsKey("tatool.online.study.id")) {
        	baseProps.add(moduleProperties.get("tatool.online.study.id"));
        }
        if (moduleProperties.containsKey("tatool.online.module.nr")) {
        	baseProps.add(moduleProperties.get("tatool.online.module.nr"));
        }
        if (moduleProperties.containsKey("tatool.online.group.nr")) {
        	baseProps.add(moduleProperties.get("tatool.online.group.nr"));
        }
        
        // session related
    	baseProps.add(session.getId().toString());
    	baseProps.add(session.getIndex() + "");
        
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        StringBuilder sessionStartTime = new StringBuilder("");
        StringBuilder sessionEndTime = new StringBuilder("");
        if (session.getStartTime() != null) {
        	sessionStartTime = new StringBuilder(dateformat.format(session.getStartTime()));
        }
        if (session.getEndTime() != null) {
        	sessionEndTime = new StringBuilder(dateformat.format(session.getEndTime()));
        }
        baseProps.add(sessionStartTime.toString());
        baseProps.add(sessionEndTime.toString());

    	// copy all session properties to a helper map, will greatly simplify chosing the correct value
        Map<String, String> propertyValues = new HashMap<String, String>();
        for (Entry p : session.getEntries()) {
        	if (useSessionShortNames) {
        		String newName = "session.";
            	String[] nameParts = p.getNodeId().split("\\.");
            	if (nameParts.length > 0) {
            		newName += nameParts[nameParts.length - 1] + ".";
            	}
            	newName += p.getName();
            	propertyValues.put(newName, p.getValue());
        	} else {
        		propertyValues.put(p.getNodeId() + "." + p.getName(), p.getValue());
        	}
        }
        
        // now add all session property values
        for (String name : sessionProperties) {
            // null is handled by addToData
        	baseProps.add(propertyValues.get(name));
        }
 
        // trial data
        baseProps.add(trial.getId().toString()); //id
        baseProps.add(trial.getIndex() + ""); //index
        baseProps.add(trial.getParentId()); //nodeid
    
        return baseProps.toArray(new String[basePropertiesSize]);
    }

	private void addToData(List<String[]> data, String[] values) {
        if (values != null) {
            data.add(values);
        }
    }

    // Trial properties management

    private void initTrialProperties(Module module) {
        // find the names of the additional trial properties to print out
        trialProperties = getAllTrialPropertyNames(module);
        trialPropertiesSize = trialProperties.size();   
    }
    
    /**
     * Returns a list of all existing module property names,
     * with the item name and property name separated by a point.
     */
    private List<String> getAllTrialPropertyNames(Module module) {
        List<Object[]> trialProperties = dataService.findDistinctTrialPropertyNames(module);
        List<String> props = new ArrayList<String>();
        for (Object[] o : trialProperties) {
            StringBuilder sb = new StringBuilder();
            for (int x=0, len=o.length; x < len; x++) {
                if (x > 0) {
                    sb.append('.');
                }
                sb.append(o[x]);
            }
            props.add(sb.toString());
        }
        return props;
    }
    
    /**
     * Returns a list of all existing session property names,
     * with the item name and property name separated by a point.
     */
    private List<String> getAllSessionPropertyNames(Module module) {
        List<Object[]> sessionProperties = dataService.findDistinctSessionPropertyNames(module);
        List<String> props = new ArrayList<String>();
        for (Object[] o : sessionProperties) {
            StringBuilder sb = new StringBuilder();
            for (int x=0, len=o.length; x < len; x++) {
                if (x > 0) {
                    sb.append('.');
                }
                sb.append(o[x]);
            }
            props.add(sb.toString());
        }
        return props;
    }

    /**
     * Adds the trial properties to the data object
     */
    private void addTrialProperties(ModuleSession session, Trial trial, List<String[]> data) {
    	List<String> trialProps = null;
    	trialProps = new ArrayList<String>();
    	trialProps.addAll(Arrays.asList(getBaseProperties(session, trial)));

        // copy all trial properties to a helper map, will greatly simplify chosing the correct value
        Map<String, String> propertyValues = new HashMap<String, String>();
        for (Entry p : trial.getEntries()) {
        	if (useTrialShortNames) {
        		String newName = "trial.";
            	String[] nameParts = p.getNodeId().split("\\.");
            	if (nameParts.length > 0) {
            		newName += nameParts[nameParts.length - 1] + ".";
            	}
            	newName += p.getName();
            	propertyValues.put(newName, p.getValue());
        	} else {
        		propertyValues.put(p.getNodeId() + "." + p.getName(), p.getValue());
        	}
        }
        
        // now write all properties, using "" where non-existent
        for (String name : trialProperties) {
            // null is handled by addToData
        	trialProps.add(propertyValues.get(name));
        }
        
        addToData(data, trialProps.toArray(new String[trialProps.size()]));
    }
    
    public void setCSVParameters(char separator) {
    	this.separator = separator;
    }
}
