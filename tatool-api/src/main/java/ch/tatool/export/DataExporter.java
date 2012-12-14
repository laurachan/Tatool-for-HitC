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
package ch.tatool.export;

import java.awt.Component;

import ch.tatool.data.Messages;
import ch.tatool.data.Module;
import ch.tatool.data.DataService;

/**
 * Exporter for module data.
 * 
 * @author Michael Ruflin
 */
public interface DataExporter {
    
    public void setDataService(DataService dataService);
    
    public DataService getDataService();
    
    /** Exports data out of the application.
     * 
     * @param fromSessionIndex the first session index to export from. -1 for all sessions
     * @return null if everything was ok, otherwise an error message
     */
    public String exportData(Component parentFrame, Module module, int fromSessionIndex, DataExporterError exporterError);
    
    /** Sets the incremental export property */
    public void setIncrementalExport(boolean incrementalExport);
    
    /** Sets the auto export property which gets triggered when a Module execution finishes */
    public void setAutoExport(boolean autoExport);
    
    /** Gets the incremental export property */
    public boolean isIncrementalExport();
    
    /** Gets the auto export property */
    public boolean isAutoExport();
    
    /** Gets the exporter name */
    public String getExporterName();
    
}
