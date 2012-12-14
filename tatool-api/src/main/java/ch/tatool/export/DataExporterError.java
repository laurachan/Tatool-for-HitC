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

/**
 * Data Exporter Error Class defines different errors that can happen during export.
 * 
 * @author Andre Locher
 *
 */
public interface DataExporterError {

	/** General */
	public static final int NO_ERROR = 0;
	public static final int GENERAL_NO_DATA = 1;
	public static final int GENERAL_UNKNOWN = 2;
	public static final int GENERAL_CANCEL = 3;
	
	/** ServerDataExporter */
	public static final int SERVER_ONLINE_SETUP_MISSING = 11;
	public static final int SERVER_ONLINE_URL_MISSING = 12;
	public static final int SERVER_ONLINE_HTTP = 13;

	/** FileDataExporter */
	public static final int FILE_GENERAL = 21;
	
	public int getErrorType();
	
	public void setErrorType(int errorType);
	
	public void setErrorReason(String errorReason);
	
	public String getErrorMessage();
}
