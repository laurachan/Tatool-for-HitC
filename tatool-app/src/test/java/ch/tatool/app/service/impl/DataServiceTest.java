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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.TestApp;
import ch.tatool.app.data.ModuleImpl;
import ch.tatool.app.data.TrialImpl;
import ch.tatool.data.Module;
import ch.tatool.data.ModuleSession;
import ch.tatool.data.Trial;
import ch.tatool.data.UserAccount;
import ch.tatool.exec.ExecutionOutcome;

/**
 * Node test class for the UserAccountService implementation
 */
public class DataServiceTest extends TestApp {

    Logger logger = LoggerFactory.getLogger(DataServiceTest.class);
    
    UserAccount account;
    Module training;
    Module training2;
    
    @Before
    public void setUp() {
        super.startApplication();
        
        account = createAccount();
        // create a training
        training = createModule(account);
        training2 = createModule(account);
    }
        
    @After
    public void tearDown() {
        if (training != null) {
            deleteTraining(training);
        }
        if (training2 != null) {
            deleteTraining(training2);
        }
        getUserAccountService().deleteAccount(account);
        super.deleteData();
    }
    
    private void deleteTraining(Module training) {
        getModuleService().deleteModule((ModuleInfoImpl) createTrainingInfo((ModuleImpl) training));
    }
    
    private Module.Info createTrainingInfo(ModuleImpl training) {
        ModuleInfoImpl info = new ModuleInfoImpl();
        info.setAccount(training.getAccount());
        info.setId(training.getId());
        info.setName("Bla");
        
        return info;
    }
    
    @Test
    public void testAddSessionData() {
        System.out.println("START: Adding sessions and trials to training...");
        
        for (int x=0; x < 3; x++) {
        	ModuleSession session = getDataService().createSession(training);
            
            // add a few trials
            for (int y=0; y < 10; y++) {
                Trial trial = new TrialImpl();
                getDataService().insertTrial(session, trial);
            }
            
            getDataService().finishSession(session);
        }
        
        System.out.println("END:   Adding sessions and trials to training.");
    }

    //@Test
    public void testTrainingItemProperties() {
        training.putValue("abc", "def", "ghi");
        getDataService().saveModule(training);
    }
    
    @Test
    public void testLargeTrainingProperties() {
    	String testString = "abcäöü";
    	byte[] bytes = testString.getBytes();
    	training.putBinaryModuleProperty("test", bytes);
    	getDataService().saveModule(training);
    }
    
    @Test
    public void testDataQueries() {
        // add some data to the training
    	ModuleSession session = getDataService().createSession(training2);
        Trial trial = new TrialImpl();
        trial.putValue("test", "propertyA", "10");
        trial.putValue("test", "propertyB", "5");
        getDataService().insertTrial(session, trial);
        
        trial = new TrialImpl();
        trial.putValue("test", "propertyA", "15");
        trial.putValue("test", "propertyB", "10");
        getDataService().insertTrial(session, trial);
        
        getDataService().finishSession(session);
        
        session = getDataService().createSession(training2);
        
        trial = new TrialImpl();
        trial.putValue("test", "propertyA", "25");
        trial.putValue("test", "propertyB", "20");
        getDataService().insertTrial(session, trial);
        
        trial = new TrialImpl();
        trial.putValue("test2", "propertyA", "30");
        trial.putValue("test2", "propertyB", "25");
        getDataService().insertTrial(session, trial);
        
        trial = new TrialImpl();
        //trial.setProperty("test", "property", "50");
        trial.putValue("test", "propertyB", "60");
        trial.putValue("test", "propertyC", "70");
        getDataService().insertTrial(session, trial);
        
        getDataService().finishSession(session);
        
        // now do some search tests
        List<Trial> trials = null;
        
        // get all trials
        trials = getDataService().getTrials(training2, null, "test", null, 0, -1);
        printResults3(trials);
        assertEquals(4, trials.size());
        
        // only get trials for the second session
        trials = getDataService().getTrials(training2, session, "test", null, 0, -1);
        printResults3(trials);
        assertEquals(2, trials.size());
        
        // only get trials for the second session with propertyA
        trials = getDataService().getTrials(training2, session, null, "propertyA", 0, -1);
        printResults3(trials);
        assertEquals(2, trials.size());
        
        // only 1 trial contains "propertyC"
        trials = getDataService().getTrials(training2, null, "test", "propertyC", 0, -1);
        printResults3(trials);
        assertEquals(1, trials.size());
        
        // limit results
        trials = getDataService().getTrials(training2, null, "test",  "propertyA", 0, 3);
        printResults3(trials);
        assertEquals(3, trials.size());
        
        // test likeness
        trials = getDataService().getTrials(training2, null, "test",  "property%", 0, -1);
        printResults3(trials);
        assertEquals(4, trials.size());
        
        trials = getDataService().getTrials(training2, null, null,  "property%", 0, -1);
        printResults3(trials);
        assertEquals(5, trials.size());
        
        trials = getDataService().getTrials(training2, null, null,  "property%", 1, 3);
        printResults3(trials);
        assertEquals(3, trials.size());
        
        trials = getDataService().getTrials(training2, null, null,  "property%", 0, 0);
        printResults3(trials);
        assertEquals(0, trials.size());
        
        // find all distinct trial properties for a training
        List<Object[]> props = getDataService().findDistinctTrialPropertyNames(training2);
        printProperties(props);
        
        // get all sessions and all trials in it
        List<ModuleSession> sessions2 = getDataService().getSessions(training2);
        //List<Trial> trials2 = getTrainingDataService().loadAllTrials(training2);
        for (ModuleSession session2 : sessions2) {
            List<Trial> trials4 = getDataService().getTrials(session2);
        }
    }
    
    private void printProperties(List<Object[]> props) {
        logger.info("-------------------------------");
        logger.info("Distinct properties:");
        for (Object[] o : props) {
            logger.info(o[0].toString() + o[1].toString());
        }
        logger.info("-------------------------------");
    }
    
    
    private void printResults2(List<Object[]> objects) {
        logger.info("-------------------------------");
        logger.info("Number of results: {}", objects.size());
        int x=1;
        for (Object[] o: objects) {
            int id = (Integer) o[0];
            Trial t = (Trial) o[1];
            logger.info(x + ": id=" + id + ", t=" + t.getId());
            
            /*
            for (Entry prop: t.getUnitProperties()) {
                logger.info("   " + prop.getItem() + ", " + prop.getName() + ", " + prop.getValue());
            }
            */
        }
        logger.info("-------------------------------");
    }
    
    private void printResults(List<Trial> trials) {
        logger.info("-------------------------------");
        logger.info("Number of results: {}", trials.size());
        int x=1;
        for (Trial t: trials) {
            logger.info(x + ": " + t.getId());
            /*
            for (Entry prop: t.getUnitProperties()) {
                logger.info("   " + prop.getItem() + ", " + prop.getName() + ", " + prop.getValue());
            }
            */
        }
        logger.info("-------------------------------");
    }
    
    private void printResults3(List<Trial> trials) {
        System.err.println("-------------------------------");
        System.err.println("Number of results: " + trials.size());
        int x=1;
        for (Trial t: trials) {
        	System.err.println(x + ": " + t.getId());
            /*
            for (Entry prop: t.getUnitProperties()) {
                logger.info("   " + prop.getItem() + ", " + prop.getName() + ", " + prop.getValue());
            }
            */
        }
        System.err.println("-------------------------------");
    }
}
