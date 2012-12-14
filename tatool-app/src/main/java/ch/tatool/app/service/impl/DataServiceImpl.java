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
package ch.tatool.app.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import ch.tatool.app.data.ModuleImpl;
import ch.tatool.app.data.ModuleSessionImpl;
import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.DataService;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;
import ch.tatool.element.Node;

/**
 * DataService implementation.
 * 
 * @author Michael Ruflin
 */
public class DataServiceImpl implements DataService {
    
	// Module data
	
    private Messages messages;

	/**
     * Save the module instance.
     * This will save all changed module properties
     */
    public void saveModule(Module module) {
        final ModuleImpl moduleImpl = (ModuleImpl) module;
        // load the module data
        moduleImpl.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    ModuleDAO moduleDAO = moduleImpl.getModuleDAO();
                    moduleDAO.saveModule(moduleImpl);
                }
            }
        );
    }
    
    
    // Session data
    
    /**
     * Creates a new module session.
     * The module start date is set immediately
     */
    public ModuleSession createSession(Module module) {
        final ModuleImpl moduleImpl = (ModuleImpl) module;
        ModuleSession moduleSession = (ModuleSession) moduleImpl.getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                	ModuleSession moduleSession = new ModuleSessionImpl();
                    moduleSession.setStartTime(new Date());
                    moduleImpl.getSessionDAO().saveSession(moduleImpl, moduleSession);
                    return moduleSession;
                }
            }
        );
        return moduleSession;
    }
    
    /**
     * Save a session without finishing it.
     */
    public void saveSession(final ModuleSession moduleSession) {
    	final ModuleImpl moduleImpl = (ModuleImpl) moduleSession.getModule();
        moduleImpl.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    moduleImpl.getSessionDAO().saveSession(moduleImpl, moduleSession);
                }
            }
        );
    }
    
    /** Finishes a module session. */
    public void finishSession(final ModuleSession moduleSession) {
    	final ModuleImpl moduleImpl = (ModuleImpl) moduleSession.getModule();
        moduleImpl.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    moduleSession.setEndTime(new Date());
                    moduleImpl.getSessionDAO().saveSession(moduleImpl, moduleSession);
                }
            }
        );
    }

    /**
     * Load all session in a given module.
     */
    public List<ModuleSession> getSessions(Module module) {
        return ((ModuleImpl) module).getSessionDAO().getSessions((ModuleImpl) module);
    }
    
    
    // Trial data
    
    /** Inserts a trial object into the session. */
    public void insertTrial(final ModuleSession session, final Trial trial) {
        final ModuleImpl moduleImpl = (ModuleImpl) session.getModule();
        moduleImpl.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    moduleImpl.getTrialDAO().saveTrial(moduleImpl, session, trial);
                }
            }
        );
    }
    
    public List<Trial> getTrials(ModuleSession session) {
    	
        return ((ModuleImpl) session.getModule()).getTrialDAO().getTrials(session);
    }

    /** Load all trials of a given module and session. */
    public List<Trial> loadAllTrials(Module module) {
        return ((ModuleImpl) module).getTrialDAO().loadAllTrials((ModuleImpl) module);
    }
    
    /**
     * Returns a list of trial instances for a given element or session.
     * @return list of trials
     */
    public List<Trial> getTrials(Module module, ModuleSession session, Node node, int maxResults) {
        return ((ModuleImpl) module).getTrialDAO().getTrials(session, node, maxResults);
    }

    /**
     * Get the last session of a module.
     * 
     * @param module
     * @return the last created session 
     */
    public ModuleSession getLastSession(Module module) {
        return ((ModuleImpl) module).getSessionDAO().findLastSession((ModuleImpl) module); 
    }
    
    /**
     * Get the number of sessions in a module
     * 
     * @param module the module to check
     * @param includeUnfinished whether to include unfinished module (modules without end time)
     * @return
     */
    public long getSessionCount(Module module, final boolean includeUnfinished) {
        final ModuleImpl moduleImpl = (ModuleImpl) module;
        return (Long) moduleImpl.getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    return moduleImpl.getSessionDAO().getSessionCount(moduleImpl, includeUnfinished);
                }
            }
        );
    }
    
    /** Find the last x trials with given property of a given element
     * 
     */
    @SuppressWarnings("unchecked")
	public List<Trial> getTrials(final Module module, final ModuleSession session, final String elementNameLike, final String propertyNameLike, final int offset, final int maxResults) {
    	final ModuleImpl moduleImpl = (ModuleImpl) module;
        return (List<Trial>) moduleImpl.getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    return moduleImpl.getTrialDAO().getTrials(moduleImpl, session, elementNameLike, propertyNameLike, offset, maxResults);
                }
            }
        );
    }
    
    /**
     * Get all distinct trial property names contained in a module
     * 
     * @return a List of object arrays containing [0] the item name and [1] the property name
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findDistinctTrialPropertyNames(final Module module) {
        final ModuleImpl moduleImpl = (ModuleImpl) module;
        return (List<Object[]>) moduleImpl.getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    return moduleImpl.getTrialDAO().findDistinctTrialPropertyNames(moduleImpl);
                }
            }
        ); 
    }

    /**
     * Get all distinct session property names contained in a module
     * 
     * @return a List of object arrays containing [0] the item name and [1] the property name
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> findDistinctSessionPropertyNames(final Module module) {
        final ModuleImpl moduleImpl = (ModuleImpl) module;
        return (List<Object[]>) moduleImpl.getTransactionTemplate().execute(
            new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    return moduleImpl.getSessionDAO().findDistinctSessionPropertyNames(moduleImpl);
                }
            }
        ); 
    }  
    
    public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
    	return messages;
    }
}
