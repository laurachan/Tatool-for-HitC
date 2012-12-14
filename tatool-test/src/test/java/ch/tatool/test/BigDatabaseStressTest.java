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

import ch.tatool.core.element.ExecutableElement;
import ch.tatool.core.element.IteratedListSelector;
import ch.tatool.core.element.ListElement;
import ch.tatool.element.Element;
import ch.tatool.test.swing.ModuleRunner;

/**
 * Test to see how the database reacts to a lot of trials added to it
 * 
 * @author Michael Ruflin
 */
@Ignore
public class BigDatabaseStressTest extends ModuleRunner {
    
    protected Element createRootElement() {
    	// create list element
    	IteratedListSelector selector = new IteratedListSelector();
    	selector.setNumIterations(1000);
    	ListElement listElem = new ListElement();
    	listElem.addHandler(selector);
    	
    	// add test task
        TestTask task = new TestTask();
        ExecutableElement elem = new ExecutableElement(task);
        listElem.addChild(elem);
    	
        return listElem;
    }
    
    public static void main(String[] args) {
    	new BigDatabaseStressTest().runModule();
    }
}
