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
package ch.tatool.data;

import java.util.Map;

/**
 * User account information.
 * 
 * @author Michael Ruflin
 */
public interface UserAccount {
    
    /** Get the name of the account. */
    public String getName();
    
    /** Set the name of the account. */
    public void setName(String name);
    
    /** Is this account password protected ? */
    public boolean isPasswordProtected();
    
    /**
     * Get the properties associated with this account
     */
    public Map<String, String> getProperties();
    
    
    /**
     * Info object for user accounts.
     * Available data before an account is actually loaded.
     */
    public interface Info {
        /** Get the name of this account. */
        public String getName();
        
        /** Is the account password protected? .*/
        public boolean isPasswordProtected();
    }
}
