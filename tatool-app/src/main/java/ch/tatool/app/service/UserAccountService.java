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
package ch.tatool.app.service;

import java.util.List;
import java.util.Map;

import ch.tatool.data.UserAccount;

/**
 * Manages local user accounts.
 * 
 * @author Michael Ruflin
 */
public interface UserAccountService {

    /** Get a list of users accounts available. */
    public List<UserAccount.Info> getAccounts();
    
    /**
     * Open a user account, possibly given the user password.
     * 
     * @param info the info object for this account
     * @param password the password if required - or null if not
     * 
     * @return A UserAccount object
     */
    public UserAccount loadAccount(UserAccount.Info info, String password);
    
    /**
     * Create a new user account using the provided information
     */
    public UserAccount createAccount(String accountName, Map<String, String> userProperties, String password);
    
    /**
     * Set the password for an account. This method requests an already loaded userAccount, the current
     * password has therefore already been validated.
     */
    public void changePassword(UserAccount userAccount, String newPassword);
    
    /**
     * Saves an account.
     */
    public void saveAccount(UserAccount account);
    
    /**
     * Closes an opened account.
     * This method should free all opened resources.
     * 
     * Note: This method does NOT save the account data!
     */
    public void closeAccount(UserAccount account);
    
    /**
     * Delete a user account.
     */
    public void deleteAccount(UserAccount account);

}
