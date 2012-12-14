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
package ch.tatool.element;

import ch.tatool.data.PropertyHolder;

/**
 * The node interface allows for a class to be used as part of the hierarchy tree in Tatool.
 * 
 * @author Michael Ruflin
 */
public interface Node extends PropertyHolder
{
    /**
     * Get the parent node
     * 
     * @return the parent node or null if not set
     */
    public Node getParent();
    
    /**
     * Set the parent node.
     * 
     * Note: the parent node is normally set when a node is added to a parent.
     */
    public void setParent(Node parentNode);
}
