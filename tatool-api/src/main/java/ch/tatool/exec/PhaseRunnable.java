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
package ch.tatool.exec;

/**
 * To be implemented by classes that want to be executed before or after an element execution.
 * 
 * @author Michael Ruflin
 */
public interface PhaseRunnable {
	
	/**
	 * Execute the element and return an outcome.
	 * @param outcome the outcome of the outer element execution cycle. "" before execution,
	 * whatever returned by the main ExecutableElement after execution.
	 * 
	 * @return a new outcome. In 99% of the cases this should simply be what has been provided,
	 * but would differ in cases where the execution is about being stopped / canceled
	 */
	public void run(ExecutionContext context);
	
	/**
	 * Request the execution to be stopped as soon as possible.
	 */
	public void stop();
}
