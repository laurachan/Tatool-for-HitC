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

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.tatool.app.TestApp;
import ch.tatool.app.data.ModuleImpl;
import ch.tatool.data.Module;
import ch.tatool.data.UserAccount;

/**
 * Node test class for the UserAccountService implementation
 */
public class ModuleServiceTest extends TestApp {
    
    UserAccount account;
    
    @Before
    public void setUp() {
        super.startApplication();
        account = createAccount();
    }
    
    @After
    public void tearDown() {
        getUserAccountService().deleteAccount(account);
        super.deleteData();
    }
    
    
    @Test
    public void testCreateTraining() {
        System.out.println("START: Creating and deleting a training...");
        
        // create a training
        ModuleImpl training = (ModuleImpl) createModule(account);

        // load the training again
        Module.Info info = createTrainingInfo(training);
        training = (ModuleImpl) getModuleService().loadModule(info);
        
        // and delete right away
        deleteTraining(training);
        
        System.out.println("END:   Creating and deleting a training.");
    }
    
    private void deleteTraining(ModuleImpl training) {
        // delete the training
        Module.Info info = createTrainingInfo(training);
        getModuleService().deleteModule(info);        
    }
    
    private Module.Info createTrainingInfo(ModuleImpl training) {
        ModuleInfoImpl info = new ModuleInfoImpl();
        info.setAccount(training.getAccount());
        info.setId(training.getId());
        info.setName("Bla");
        
        return info;
    }
    
    
    @Test
    public void testGetTrainings() {
        // create an account
        Set<Module.Info> infos = getModuleService().getModules(account);
        assertEquals(0, infos.size());
        
        ModuleImpl training = (ModuleImpl) createModule(account);
        infos = getModuleService().getModules(account);
        assertEquals(1, infos.size());
        
        deleteTraining(training);
        infos = getModuleService().getModules(account);
        assertEquals(0, infos.size());
    }
}
