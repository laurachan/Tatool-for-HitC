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
package ch.tatool.app.export;

import java.awt.Component;

import ch.tatool.data.DataService;
import ch.tatool.data.Module;
import ch.tatool.export.DataExporter;
import ch.tatool.export.DataExporterError;

public class DataExporterImpl implements DataExporter {

	/** Exporter data service. */
	protected DataService dataService;

	/** Data Exporter properties. */
    private boolean autoExport;
    private boolean incrementalExport;
    
    private long id;
    
    public DataExporterImpl() {
    	super();
    }
    
	public String exportData(Component parentFrame, Module module,
			int fromSessionIndex, DataExporterError exporterError) {
		return null;
	}

	public boolean isAutoExport() {
		return autoExport;
	}

	public boolean getAutoExport() {
		return autoExport;
	}
	
	public boolean isIncrementalExport() {
		return incrementalExport;
	}

	public boolean getIncrementalExport() {
		return incrementalExport;
	}
	
	public void setAutoExport(boolean autoExport) {
		this.autoExport = autoExport;
	}

	public void setIncrementalExport(boolean incrementalExport) {
		this.incrementalExport = incrementalExport;
	}

	public DataService getDataService() {
		return dataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getExporterName() {
    	return "Export";
    }
}
