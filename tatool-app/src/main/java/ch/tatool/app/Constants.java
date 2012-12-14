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
package ch.tatool.app;

/**
 * Holds some constants used in different properties
 * 
 * @author Michael Ruflin
 */
public interface Constants {

    // Application runtime properties (used internally in Tatool)    
    public static final String APP_PROPERTY_DEFAULT_ACCOUNT_NAME = "DefaultAccountName";
    
    // Application configuration settings
    // TODO: remove! provide a possibility to disable creators by id instead
    public static final String APP_PROPERTY_ALLOW_CREATE_NEW_ACCOUNT = "CreateNewAccountAllowed";
    
    /** Disable module management. */
    public String DISABLE_MODULE_MANAGEMENT = "tatool.disable.module.management";
    
    
    // Module related
    
    /** Last loaded module. */
    public String PROPERTY_LAST_LOADED_MODULE_ID = "LastLoadedModule";

    /** Open last loaded module on startup. */
    public String PROPERTY_OPEN_LAST_LOADED_MODULE = "OpenLastLoadedModuleOnStartup";

    
    // Account related
    /** Age of account. */
    public String PROPERTY_ACCOUNT_BIRTH = "yearOfBirth";
    
    /** Gender of account. */
    public String PROPERTY_ACCOUNT_SEX = "sex";
    
    /** Language of account */
    public String PROPERTY_ACCOUNT_LANG = "userLanguage";
    
    
    // Machine related

    public String PROPERTY_MACHINE_OS_NAME = "osName";
    public String PROPERTY_MACHINE_OS_ARCH = "osArch";
    public String PROPERTY_MACHINE_OS_VERSION = "osVersion";
    public String PROPERTY_MACHINE_USER_HOME = "userHome";
    
}
