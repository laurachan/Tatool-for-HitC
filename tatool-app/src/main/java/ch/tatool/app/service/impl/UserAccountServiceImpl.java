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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import ch.tatool.app.Constants;
import ch.tatool.app.data.UserAccountImpl;
import ch.tatool.app.service.UserAccountService;
import ch.tatool.app.util.ContextUtils;
import ch.tatool.data.Messages;
import ch.tatool.data.UserAccount;
import ch.tatool.data.UserAccount.Info;

/**
 * Implementation of the user account service.
 * 
 * Each user account is backed by a database 
 * 
 * @author Michael Ruflin
 *
 */
public class UserAccountServiceImpl implements UserAccountService {
    
    /** Logger used by this class. */
    Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);
    
    /** Path where the spring configuration file for db objects resides. */
    private static final String configurationFilePath = "ch/tatool/app/service/impl/account-context.xml";
    
    /** Name of the project description file. */
    private String descriptionFileName = "account.properties";
    
    /** Directory where projects should be stored in. */
    private String dataDirName = ".tatooldata";
    
    /** Should the application data directory be relative to the users home directory?. */
    private boolean relativeToHome = true;

    /** i18n object used to set the locale */
	private Messages messages;    
    
    
    // UserAccount management
    
    /**
     * Create a new account folder.
     * 
     * @param userProperties the properties of the user account
     * @param password a password if the user account should be password protected
     */
    public UserAccount createAccount(String accountName, Map<String, String> userProperties, String password) {
    	
    	// check if user already exists
    	List<UserAccount.Info> accounts = getAccounts();
    	for (int i = 0; i < accounts.size(); i++) {
    		if (accounts.get(i).getName().equals(accountName)) {
    			return null;
    		}
    	}
    	
        File folder = createAccountFolder();
        
        // create a new account info object (with empty id)
        UserAccountImpl.InfoImpl info = new UserAccountImpl.InfoImpl();
        info.setFolder(folder);
        info.setName(accountName);
        info.setPasswordProtected(password != null && password.length() > 0);
        
        // open the database layer with an empty password by default
        UserAccountImpl account = (UserAccountImpl) loadAccount(info, null);
        
        // write the description file
        info.setId(account.getId());
        writeAccountDescFile(info);
        
        // set the password of the account if provided
        if (password != null && password.length() > 0) {
            changePassword(account, password);
        }
        
        // set additional properties of the account
        for(String s : userProperties.keySet()) {
        	account.getProperties().put(s, userProperties.get(s));
		}
        
        saveAccount(account);
        
        changeLocale(account);
        
        return account;
    }

	/**
     * Opens an account data object.
     * 
     * The account object is backed by the database. The password is conveniently used
     * as database password, which would fail in case of incorrect password.
     */
    public UserAccount loadAccount(Info info, String password) {
        UserAccountImpl.InfoImpl infoImpl = (UserAccountImpl.InfoImpl) info;
        
        // load the database support objects (spring does all the wiring work for us
        Properties properties = new Properties();
        String dataPath = new File(infoImpl.getFolder(), "data").getAbsolutePath();
        properties.setProperty("account.data.folder", dataPath);
        if (password != null && password.length() > 0) {
            properties.setProperty("account.password", password);
        } else {
            properties.setProperty("account.password", "");
        }

        BeanFactory beanFactory = ContextUtils.createBeanFactory(configurationFilePath, properties);
        
        // create an account object, and bind it to the database
        final UserAccountImpl userAccount = new UserAccountImpl();
        userAccount.setFolder(infoImpl.getFolder());
        userAccount.setBeanFactory(beanFactory);
        userAccount.setPassword(password);
        userAccount.setName(infoImpl.getName());
        userAccount.setId(infoImpl.getId());

        // initialize transaction management
        PlatformTransactionManager transactionManager = (PlatformTransactionManager) beanFactory.getBean("userAccountTxManager");  
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        userAccount.setTransactionTemplate(transactionTemplate);
        
        // load the account in a transaction
        try {
            transactionTemplate.execute(
                new TransactionCallbackWithoutResult() {
                    public void doInTransactionWithoutResult(TransactionStatus status) {
                        UserAccountDAO userAccountDAO = userAccount.getUserAccountDAO();
                        userAccountDAO.loadAccount(userAccount);
                    }
                }
            );

        } catch (org.hibernate.ObjectNotFoundException onfe) {
            logger.warn("Databasee entry for account does not exist anymore. Got the database deleted?");
            throw new RuntimeException("Unable to load user account due to missing data");
        }
        
        changeLocale(userAccount);
        
        return userAccount;
    }
  
    /**
     * Changes the currently used Locale which is used to determine the language of Tatool.
     */
    private void changeLocale(UserAccountImpl account) {
		String lang = account.getProperties().get(Constants.PROPERTY_ACCOUNT_LANG);
		if (lang == null) {
			lang = "en";
		}
		Locale local = new Locale(lang);
		messages.setLocale(local);
	}
    
    /**
     * Saves an account.
     */
    public void saveAccount(UserAccount account) {
        // write the database
        final UserAccountImpl userAccount = (UserAccountImpl) account;
        userAccount.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    UserAccountDAO userAccountDAO = userAccount.getUserAccountDAO();
                    userAccountDAO.saveAccount(userAccount);
                }
            }
        );
        updateUserAccountInfo(userAccount);
    }
    
    
    /**
     * Closes an opened account.
     * This method should free all opened resources.
     * 
     * Note: This method does NOT save the account data!
     */
    public void closeAccount(UserAccount account) {
        // force the database to shutdown
        final UserAccountImpl userAccount = (UserAccountImpl) account;
        // shutdown the database - necessary for hsql. we do it currently by adding a shutdown=true parameter to the connection string
        userAccount.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    UserAccountDAO userAccountDAO = userAccount.getUserAccountDAO();
                    userAccountDAO.shutdown();
                }
            }
        );
        
        // destroy all singletons - this will trigger the close method of the DataSource 
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) userAccount.getBeanFactory();
        beanFactory.destroySingletons();
    }
    
    /**
     * Change the password for an account.
     */
    public void changePassword(UserAccount account, final String newPassword) {
        final UserAccountImpl userAccount = (UserAccountImpl) account;
        userAccount.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    UserAccountDAO userAccountDAO = userAccount.getUserAccountDAO();
                    userAccountDAO.setAccountPassword(userAccount, newPassword);
                }
            }
        );
        updateUserAccountInfo(userAccount);
    }
    
    /** Delete a module.
     * 
     * This method should completely remove all module related data from the system.
     */
    public void deleteAccount(UserAccount account) {
        final UserAccountImpl userAccount = (UserAccountImpl) account;
        
        // delete the data object
        userAccount.getTransactionTemplate().execute(
            new TransactionCallbackWithoutResult() {
                public void doInTransactionWithoutResult(TransactionStatus status) {
                    UserAccountDAO userAccountDAO = userAccount.getUserAccountDAO();
                    userAccountDAO.deleteAccount(userAccount);
                }
            }
        );
        
        
        // first close the account
        closeAccount(account);
        
        // then delete the account folder
        File folder = userAccount.getFolder();
        if (folder.exists()) {
            // delete the folder and all files in it
            try {
                logger.info("Deleting directory {}", folder.getAbsolutePath());
                FileUtils.deleteDirectory(folder);
            } catch (IOException ioe) {
                logger.error("Unable to delete module directory!", ioe);
            }
        }
    }    
    
    
    //
    // Account folder management and info files
    //
    
    /**
     * Get all available module instances.
     */
    public List<UserAccount.Info> getAccounts(){
        // each module is stored in a separate folder inside the data folder
        File dataFolder = getTatoolDataFolder();
        
        // each module has a properties file that describes the module.
        // read each of them and construct the corresponding module objects.
        List<UserAccount.Info> accounts = new ArrayList<UserAccount.Info>();
        File[] children = dataFolder.listFiles();
        for (File f : children) {
            if (f.isDirectory()) {
                UserAccount.Info info = getAccountInfos(f);
                if (info != null) {
                    accounts.add(info);
                } else {
                    logger.info("Non-project folder: {}", f.getAbsolutePath());
                }
            }
        }
        
        return accounts;
    }

    
    /**
     * Get a account info file for an account folder
     */ 
    private Info getAccountInfos(File folder) {
        // only proceed if a description file exists
        File descriptionFile = getAccountDescFile(folder);
        if (descriptionFile == null) {
            logger.info("No project description file exists for folder {}", folder);
            return null;
        }
        
        // read the description file
        Properties p = readAccountDescFile(descriptionFile);
        if (p == null) {
            logger.info("Unable to read description file {}", descriptionFile.getAbsolutePath());
            return null;
        }
        
        // create and return a ModuleInfo object
        UserAccountImpl.InfoImpl info = new UserAccountImpl.InfoImpl();
        info.setFolder(folder);
        info.setName(p.getProperty("name", "Unknown account name"));
        info.setPasswordProtected(Boolean.parseBoolean(p.getProperty("passwordProtected", "false")));
        String idString = p.getProperty("id");
        if (idString != null) {
            try { 
                info.setId(Long.parseLong(idString));
            } catch (NumberFormatException nfe) {
                logger.warn("Unable to read account id", nfe);
            }
        }
        return info;
    }
    
    private void updateUserAccountInfo(UserAccountImpl userAccount) {
        // update the description file
        UserAccountImpl.InfoImpl info = new UserAccountImpl.InfoImpl();
        info.setName(userAccount.getName());
        info.setPasswordProtected(userAccount.isPasswordProtected());
        info.setFolder(userAccount.getFolder());
        info.setId(userAccount.getId());
        writeAccountDescFile(info);
    }
    
    /**
     * Reads the account description file
     * 
     * @param descriptionFileName
     * @return a properties object or null in case an error occured
     */
    private Properties readAccountDescFile(File descFile) {
        try {
            FileInputStream fis = new FileInputStream(descFile);
            Properties p = new Properties();
            p.load(fis);
            fis.close();
            return p;
        } catch(IOException ioe) {
            logger.error("Unable to read module description file", ioe);
            return null;
        }
    }
    
    /** Writes the description to a properties file. */
    private void writeAccountDescFile(UserAccountImpl.InfoImpl info) {
        // make sure the file exists - create if it doesn't
        File descriptionFile = createAccountDescFile(info);
        
        Properties p = new Properties();
        p.setProperty("name", info.getName());
        p.setProperty("passwordProtected", String.valueOf(info.isPasswordProtected()));
        p.setProperty("id", info.getId().toString());
        try {
            FileOutputStream fos = new FileOutputStream(descriptionFile);
            p.store(fos, "Tatool account info");
            fos.close();
        } catch (IOException ioe) {
            logger.error("Unable to write account info file", ioe);
        }
    }
    
    /** Creates a module description file. */
    private File createAccountDescFile(UserAccountImpl.InfoImpl info) {
        File folder = info.getFolder();
    
        // create the file
        File descriptionFile = new File(folder, descriptionFileName);
        if (descriptionFile.exists()) {
            return descriptionFile;
        }
        
        logger.info("Creating description file {}", descriptionFile.getAbsolutePath());
        try {
            descriptionFile.createNewFile();
            return descriptionFile;
        } catch (IOException ioe) {
            logger.error("Unable to create description file", ioe);
            return null;
        }
    }
    
    /**
     * Get the description file
     * 
     * @param moduleFolder the module folder
     * @return the description file or null if it does not exist
     */
    private File getAccountDescFile(File folder) {
        File descFile = new File(folder, descriptionFileName);
        if (descFile.exists()) {
            return descFile;
        } else {
            return null;
        }
    }

    /**
     * Create a new module folder given an id.
     * 
     * @param id the id to use for the new folder
     * @return the folder or null if a module with given id already exists.
     */
    private File createAccountFolder() {
        // let's take the date as folder name - is saver than anything else
        File tatoolDataFolder = getTatoolDataFolder();
        File moduleFolder = null;
        do {
            moduleFolder = new File(tatoolDataFolder, String.valueOf(System.currentTimeMillis()));
        } while (moduleFolder.exists());
        
        // create the folder and return it if we succeed
        boolean created = moduleFolder.mkdirs();
        return created ? moduleFolder : null;
    }
    
    /**
     * Get the Tatool data folder.
     * 
     * Note: The folder is created if it does not yet exist.
     * 
     * @return the folder
     */
    public File getTatoolDataFolder() {
        File userHome = null;
        if (relativeToHome) {
            userHome = new File(System.getProperty("user.home"));
        } else {
            userHome = new File(".");
        }
        
        // get or create the data folder
        File dataFolder = new File(userHome, dataDirName);
        if (! dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (! created) {
                logger.error("Cannot create data directory: {}", dataFolder.getAbsolutePath());
                throw new RuntimeException ("Unable to create data directory: " + dataFolder.getAbsolutePath());
            }
        }
        
        return dataFolder;
    }

    public String getDataDirName() {
        return dataDirName;
    }

    public void setDataDirName(String dataDirName) {
        this.dataDirName = dataDirName;
    }

    public boolean isRelativeToHome() {
        return relativeToHome;
    }

    public void setRelativeToHome(boolean relativeToHome) {
        this.relativeToHome = relativeToHome;
    }
    
    public void setMessages(Messages messages) {
    	this.messages = messages;
    }
    
    public Messages getMessages() {
		return messages;
	}
}
