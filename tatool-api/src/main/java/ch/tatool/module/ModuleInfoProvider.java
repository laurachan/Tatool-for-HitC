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
package ch.tatool.module;

import java.util.Date;

import javax.swing.JPanel;

import ch.tatool.data.Module;

/**
 * Module info provider interface.
 * 
 * @author Michael Ruflin
 */
public interface ModuleInfoProvider {
	
	public void setModule(Module module);

	/**
	 * Returns the module info panel provided by the provider.
	 */
	public JPanel getModuleInfoPanel();
	
	/**
	 * Called to force an update of the module info panel

	 * @param module
	 */
	public void updateModuleInfo(Module module, Date lastExportDate);
}
