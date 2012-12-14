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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.tatool.app.TestApp;
import ch.tatool.app.data.UserAccountImpl;
import ch.tatool.data.UserAccount;

/**
 * Node test class for the UserAccountService implementation
 */
public class UserAccountServiceTest extends TestApp {    
    
    @Before
    public void setUp() {
    	startApplication();
    }
    
    @After
    public void tearDown() {
        super.deleteData();
    }
    
    @Test
    public void testCreateAndLoadOpenAccount() {
        System.out.println("Creating an open account...");
        createLoadAndStoreAccount(null, true);
    }
    
    private UserAccountImpl.InfoImpl createAccountInfo(UserAccount account) {
        UserAccountImpl impl = (UserAccountImpl) account;
        UserAccountImpl.InfoImpl info = new UserAccountImpl.InfoImpl();
        
        info.setPasswordProtected(false);
        info.setFolder(impl.getFolder());
        info.setName(impl.getName());
        
        return info;
    }

    private UserAccount createLoadAndStoreAccount(String password, boolean delete) {
        Map<String, String> userProperties = new HashMap<String, String>();
        UserAccount account = getUserAccountService().createAccount("Test User", userProperties, password);
        assertNotNull(account);
    
        // get the info object
        UserAccountImpl.InfoImpl info = createAccountInfo(account);
        
        // close the account
        getUserAccountService().closeAccount(account);
        
        // try to load it 
        account = getUserAccountService().loadAccount(info, password);
        
        // now delete the account
        if (delete) {
            getUserAccountService().deleteAccount(account);
            account = null;
        }
        
        return account;   
    }
    
    @Test
    public void testGetAccounts() {
        
        List<UserAccount.Info> infos = getUserAccountService().getAccounts();
        int currentSize = infos.size();
        
        // create an account
        UserAccount account = createLoadAndStoreAccount(null, false);
        infos = getUserAccountService().getAccounts();
        assertEquals(currentSize+1, infos.size());
        
        // now delete the account
        getUserAccountService().deleteAccount(account);
        infos = getUserAccountService().getAccounts();
        assertEquals(currentSize, infos.size());
    }
}
