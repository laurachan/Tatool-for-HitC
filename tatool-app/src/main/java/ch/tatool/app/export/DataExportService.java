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
import java.util.Date;

import ch.tatool.data.Module;

/**
 * Provides data export capability for a module.
 * By default, an exporter class needs to be defined in order to be able to
 * export data.
 * 
 * @author Michael Ruflin
 *
 */
public interface DataExportService {
    
    /** What exporter class to use for a given module. */
    public static final String PROPERTY_EXPORTER_CLASS = "module.exporter.classname";
    
    /**
     * Whether exports should happen incrementally, or always the complete data be exported.
     * By default we always export the full data.
     */
    public static final String PROPERTY_EXPORT_INCREMENTAL = "incremental";
    
    /** Last export date. */
    public static final String PROPERTY_LAST_EXPORT_TIMESTAMP = "last.timestamp";
    
    /** Last exported session. */
    public static final String PROPERTY_LAST_EXPORTED_SESSION = "last.session";
    
    /**
     * Can the module be exported? Currently an exporter class needs to be specified
     * in order to allow exporting the data.
     */
    public boolean exportConfigured(Module module, String exporterKey);
    
    /**
     * Returns whether some or all of the data in the module is still due to be exported
     */
    public boolean containsPendingExportData(Module module, String exporterKey);
    
    /**
     * Returns the date when the last data was exported.
     */
    public Date getLastExportDate(Module module, String exporterKey);
    
    /**
     * Exports the data.
     */
    public String exportData(Component parent, Module module, String exporterKey);

}
