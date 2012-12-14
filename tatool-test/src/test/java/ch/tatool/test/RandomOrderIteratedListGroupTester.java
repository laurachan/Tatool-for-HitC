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
import ch.tatool.core.element.ListElement;
import ch.tatool.core.element.RandomOrderIteratedListSelector;
import ch.tatool.element.Element;
import ch.tatool.test.swing.ModuleRunner;

@Ignore
public class RandomOrderIteratedListGroupTester extends ModuleRunner {

    /** This training consists of a task that returns success all the time. */
    protected Element createRootElement() {
    	ListElement root = new ListElement();
    	root.addHandler(new RandomOrderIteratedListSelector());
        //root.setId("root");
        
        TestTask one = new TestTask();
        one.setLocalId("one");
        ExecutableElement oneElem = new ExecutableElement(one);
        TestTask two = new TestTask();
        one.setLocalId("two");
        ExecutableElement twoElem = new ExecutableElement(two);
        root.addChild(oneElem);
        root.addChild(twoElem);
        return root;
    }
    
    public static void main(String[] args) {
        new RandomOrderIteratedListGroupTester().runModule();
    }
}
