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
package ch.tatool.app.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.support.TransactionTemplate;

import ch.tatool.app.service.impl.ModuleDAO;
import ch.tatool.app.service.impl.ModuleSessionDAO;
import ch.tatool.app.service.impl.TrialDAO;
import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.UserAccount;
import ch.tatool.export.DataExporter;
import ch.tatool.module.ExecutorInitializer;
import ch.tatool.module.ModuleScheduler;

/**
 * Represents a module.
 * 
 * @author Michael Ruflin
 */
public class ModuleImpl extends DataContainerImpl implements Module {
    
    /** Id of this module. */
    private Long id;
    
    /** Id of the account this module is part of. */
    private Long accountId;
    
    /** Name of the module. */
    private String name;
    
    /** Account this module is part of. */
    private UserAccountImpl account;
    
    private ModuleScheduler moduleScheduler;
    private ExecutorInitializer executorConfiguration;
    
    /** Sessions contained in this module. */
    private List<ModuleSession> sessions;
    
    /** Module properties. */
    private Map<String, String> moduleProperties;
    
    /** Export properties. */
    private Map<String, DataExporter> moduleExporters;

	/** Large module properties. */
    private Map<String, byte[]> binaryModuleProperties;
    
    private Messages messages;
        
    /** Create a new module instance. */
    public ModuleImpl() {
    	super();
        moduleProperties = new HashMap<String, String>();
        binaryModuleProperties = new HashMap<String, byte[]>();
        moduleExporters = new HashMap<String, DataExporter>();
        sessions = new ArrayList<ModuleSession>();
    }
    
    // Helper getters
    
    public ModuleDAO getModuleDAO() {
        return (ModuleDAO) account.getBeanFactory().getBean("moduleDAO");
    }
    
    public ModuleSessionDAO getSessionDAO() {
        return (ModuleSessionDAO) account.getBeanFactory().getBean("sessionDAO");
    }

    public TrialDAO getTrialDAO() {
        return (TrialDAO) account.getBeanFactory().getBean("trialDAO");
    }
    
    public TransactionTemplate getTransactionTemplate() {
        return account.getTransactionTemplate();
    }
    
    public String toString() {
        return String.valueOf(getId() + " - " + getName());
    }
    
    // BLOBS
    
    /** Get a large value from the module.
     * This is implemented as BLOBs behind the scene, but the implementation
     * might hold all values in memory. Use therefore with consideration... 
     */
    public byte[] getBinaryModuleProperty(String propertyName) {
    	return binaryModuleProperties.get(propertyName);
    }
    
    public Map<String, byte[]> getBinaryModuleProperties() {
    	return binaryModuleProperties;
    }
    
    /** Set a large property value. */
    public void putBinaryModuleProperty(String id, byte[] value) {
    	binaryModuleProperties.put(id, value);
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public void setBinaryModuleProperties(
			Map<String, byte[]> largeModuleProperties) {
			this.binaryModuleProperties = largeModuleProperties;
	}
	
    public Map<String, String> getModuleProperties() {
        return moduleProperties;
    }
    
    public void setModuleProperties(Map<String, String> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }

    public List<ModuleSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<ModuleSession> sessions) {
        this.sessions = sessions;
    }
    
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public UserAccount getUserAccount() {
        return account;
    }
    
    public UserAccountImpl getAccount() {
        return account;
    }

    public void setAccount(UserAccountImpl account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public ModuleScheduler getModuleScheduler() {
		return moduleScheduler;
	}

	public void setModuleScheduler(ModuleScheduler executionScheduler) {
		this.moduleScheduler = executionScheduler;
	}

	public ExecutorInitializer getExecutorInitializer() {
		return executorConfiguration;
	}

	public void setExecutorInitializer(ExecutorInitializer executorConfiguration) {
		this.executorConfiguration = executorConfiguration;
	}

    public Map<String, DataExporter> getModuleExporters() {
		return moduleExporters;
	}
    
    public DataExporter getModuleExporter(String exporterKey) {
    	if (moduleExporters.containsKey(exporterKey)) {
    		return moduleExporters.get(exporterKey);
    	} else {
    		return null;
    	}
    }

	public void setModuleExporters(Map<String, DataExporter> moduleExporters) {
		this.moduleExporters = moduleExporters;
	}
	
	public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
		return messages;
	}

}
