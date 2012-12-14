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
package ch.tatool.test;

import org.junit.Ignore;

import ch.tatool.app.TestApp;
import ch.tatool.core.element.ExecutableElement;
import ch.tatool.core.element.ListElement;
import ch.tatool.core.element.RandomListSelector;
import ch.tatool.core.element.handler.score.BoundaryPointsAndLevelHandler;
import ch.tatool.core.element.handler.score.FactorPointAdaptor;
import ch.tatool.element.Element;

@Ignore
public class TestTrainingRunner extends TestApp {
	
    /**
     * This training consists of a task that returns success all the time.
     */
    protected Element createRootElement() {
    	ListElement root = new ListElement();
    	root.addHandler(new RandomListSelector(50));
        root.setLocalId("root");
        
        // score handler
        BoundaryPointsAndLevelHandler scoreHandler = new BoundaryPointsAndLevelHandler();
        scoreHandler.setLocalId("points");
        scoreHandler.setProperty(BoundaryPointsAndLevelHandler.PROPERTY_BOUNDARY_VALUE, 50);
        root.addHandler(scoreHandler);
        
        // create a test task that returns success all the time
        // add points with a factor 10
        ExecutableElement elem = new ExecutableElement();
        TestTask testTask = new TestTask();
        testTask.setLocalId("success-test-task");
        elem.setExecutable(testTask);

        FactorPointAdaptor pointAdaptor = new FactorPointAdaptor();
        pointAdaptor.setProperty(FactorPointAdaptor.PROPERTY_FACTOR, 10);
        elem.addHandler(pointAdaptor);

        root.addChild(elem);
        
        // create a test task that returns failure all the time
        // add points with a factor 1
        TestTask testTask2 = new TestTask();
        testTask2.setLocalId("failure-test-task");
        root.addChild(new ExecutableElement(testTask2));
        
        return root;
    }
    
    public static void main(String[] args) {
    	new TestTrainingRunner().runModule();
    }
}
