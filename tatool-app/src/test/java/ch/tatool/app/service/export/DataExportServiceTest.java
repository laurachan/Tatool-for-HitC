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
package ch.tatool.app.service.export;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.TestApp;
import ch.tatool.app.data.TrialImpl;
import ch.tatool.app.export.DataExportService;
import ch.tatool.app.export.ServerDataExporter;
import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;
import ch.tatool.data.UserAccount;

/**
 * Node test class for the UserAccountService implementation
 */
public class DataExportServiceTest extends TestApp {

    Logger logger = LoggerFactory.getLogger(DataExportServiceTest.class);
    
    private UserAccount account;
    private Module training;
    
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
    
    @Ignore
    @Test
    public void testFileExportTraining() {
        training = createModule(account);
        insertTestData();
        training.getModuleProperties().put(DataExportService.PROPERTY_EXPORTER_CLASS, "ch.tatool.app.export.FileDataExporter");
        getDataService().saveModule(training);
        getDataExportService().exportData(null, training, "");
    }
    
    @Ignore
    @Test
    public void testServerExportTraining() {
        training = createModule(account);
        insertTestData();
        training.getModuleProperties().put(DataExportService.PROPERTY_EXPORTER_CLASS, "ch.tatool.app.export.ServerDataExporter");
        training.getModuleProperties().put(ServerDataExporter.PROPERTY_EXPORT_USERNAME, "username");
        training.getModuleProperties().put(ServerDataExporter.PROPERTY_EXPORT_PASSWORD, "password");
        getDataService().saveModule(training);
        getDataExportService().exportData(null, training, "");
    }
    
    private void insertTestData() {
        for (int x=0; x < 5; x++) {
            ModuleSession session = getDataService().createSession(training);
            
            // add a few trials
            for (int y=0; y < 10; y++) {
                Trial trial = new TrialImpl();
                
                for (int z=0; z < 5; z++) {
                    trial.putValue("test", "abc" + z, String.valueOf(x + y + z));
                }
                
                getDataService().insertTrial(session, trial);
            }
            
            getDataService().finishSession(session);
        }
        
        System.out.println("END:   Adding sessions and trials to training.");
    }
}
