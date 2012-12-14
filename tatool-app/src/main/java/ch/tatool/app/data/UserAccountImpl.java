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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.support.TransactionTemplate;

import ch.tatool.app.service.impl.ModuleDAO;
import ch.tatool.app.service.impl.ModuleSessionDAO;
import ch.tatool.app.service.impl.TrialDAO;
import ch.tatool.app.service.impl.UserAccountDAO;
import ch.tatool.data.UserAccount;

/**
 * Represents a user account.
 * 
 * @author Michael Ruflin
 */
public class UserAccountImpl implements UserAccount {

    /**
     * Id of the account - each account resides in an individual folder,
     * we therefore only have one account entry per database, with an id
     * generated randomly.  
     */
    private Long id;
    
    private String name;
    private boolean passwordProtected;
    private String password;
    private File folder;
    private BeanFactory beanFactory;
    private TransactionTemplate transactionTemplate;
    private Map<String, String> properties;
    
    
    public UserAccountImpl() {
        properties = new HashMap<String, String>();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public boolean isPasswordProtected() {
        return passwordProtected;
    }

    public void setPasswordProtected(boolean passwordProtected) {
        this.passwordProtected = passwordProtected;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public UserAccountDAO getUserAccountDAO() {
        return (UserAccountDAO) beanFactory.getBean("userAccountDAO");
    }

    public ModuleDAO getModuleDAO() {
        return (ModuleDAO) beanFactory.getBean("moduleDAO");
    }
    
    public ModuleSessionDAO getModuleSessionDAO() {
    	return (ModuleSessionDAO) beanFactory.getBean("moduleSessionDAO");
    }
    
    public TrialDAO getTrialDAO() {
    	return (TrialDAO) beanFactory.getBean("trialDAO");
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }



    /**
     * User account info object.
     */
    public static class InfoImpl implements UserAccount.Info {
        
        private Long id;
        private String name;
        private boolean passwordProtected;
        private File folder;
    
        public String getName() {
            return name;
        }
    
        public boolean isPasswordProtected() {
            return passwordProtected;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    
        public void setPasswordProtected(boolean passwordProtected) {
            this.passwordProtected = passwordProtected;
        }
    
        public File getFolder() {
            return folder;
        }
    
        public void setFolder(File folder) {
            this.folder = folder;
        }
        
        public String toString() {
            return getName();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        
    }
}
