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
package ch.tatool.display;

/**
 * An execution display provides a view for elements during execution.
 * 
 * The default implementation provides a Swing frame alongside different
 * helper classes to simplify element development.
 * 
 * Some modules might prefer implementing elements using a different view technology,
 * this aspect of modules has therefore been split out to cater for such wishes.  
 * 
 * @author Michael Ruflin
 */
public interface ExecutionDisplay {
	// empty currently
}
