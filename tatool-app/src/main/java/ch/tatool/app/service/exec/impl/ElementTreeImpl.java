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
package ch.tatool.app.service.exec.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import ch.tatool.element.Element;
import ch.tatool.element.ElementTree;
import ch.tatool.exec.ExecutionContext;
import ch.tatool.exec.ExecutionPhaseListener;

/**
 * Managers the actual Element Tree execution.
 * 
 * @author mruflin
 */
public class ElementTreeImpl implements ElementTree {

	/** Root element of the execution tree. */
	private Element rootElement;
	
	/** Current execution stack. */
	private LinkedList<Element> stack;
	private List<Element> immutableStack;
	
	public ElementTreeImpl() {
		stack = new LinkedList<Element>();
		immutableStack = Collections.unmodifiableList(stack);
	}
	
	public List<Element> getElementStack() {
		return immutableStack;
	}

	public Element getRootElement() {
		return rootElement;
	}

	public void setRootElement(Element element) {
		this.rootElement = element;
		stack.clear();
	}
	
	public void popElement() { 
		if (! stack.isEmpty()) {
			stack.removeLast();
		}
	}

	public void pushElement(Element elementToPush) {
		if (elementToPush != null) {
			stack.add(elementToPush);
		}
	}

	public void clear() {
		stack.clear();
		rootElement = null;
	}
		
	public Element getTop() {
		if (! stack.isEmpty()) {
			return stack.getLast();
		} else {
			return null;
		}
	}
	
	// Event management
	
	/**
	 * Phase events are managed a bit specially for elements.
	 * SESSION_START / SESSION_END will be delivered to the current stack automatically,
	 */
	public void deliverPhaseEvent(ExecutionContext context) {
    	// now handle internal events
    	switch (context.getPhase()) {
    	case SESSION_START:
    	case SESSION_FINISH:
    		fireToTree(context, rootElement);
    		break;
    	case PRE_PROCESS:
    	case POST_PROCESS:
    		fireToStack(context, stack);
    		break;
		default:
			break;
    	}
	}
    
    /**
     * Fires the context to the complete execution tree.
     * Events are delivered in the execution thread scope, NOT in the event dispatch thread 
     */
    private void fireToTree(ExecutionContext context, Element element) {
    	// first the element and its handlers
    	fireToElement(context, element);
        
        // then continue with the children
        for (Element child : element.getChildren()) {
        	fireToTree(context, child);
        }
    }
    
    /**
     * Fires a context to the passed stack.
     * The events are delivered in the event dispatch thread 
     */
    private void fireToStack(final ExecutionContext context, final List<Element> stack) {
    	// start with root upwards
    	try {
    		SwingUtilities.invokeAndWait(new Runnable() {
    			public void run() {
    				for (Element element : stack) {
			            fireToElement(context, element);
			        }
    			}
    		});
    	} catch (InvocationTargetException ite) {
	        throw new RuntimeException(ite);
	    } catch (InterruptedException ie) {
	        throw new RuntimeException(ie);
	    }
    }
    
    /**
     * Fires the context to a single Element, thus to the executable if available and all handlers.
     */
    private void fireToElement(ExecutionContext context, Element element) {
    	// first inform the executable
        if (element.getExecutable() != null && element.getExecutable() instanceof ExecutionPhaseListener) {
        	((ExecutionPhaseListener) element.getExecutable()).processExecutionPhase(context);
        }
        // then inform the handlers
        for (Object handler : element.getHandlers()) {
	        if (handler instanceof ExecutionPhaseListener) {
	            ((ExecutionPhaseListener) handler).processExecutionPhase(context);
	        }
        }
    }
}
