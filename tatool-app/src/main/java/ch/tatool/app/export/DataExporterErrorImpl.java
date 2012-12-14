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

import ch.tatool.app.gui.MessagesImpl;
import ch.tatool.export.DataExporterError;

public class DataExporterErrorImpl implements DataExporterError {

	private int errorType = NO_ERROR;
	private String errorReason = "";
	
	private MessagesImpl messages;
	
	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}
	
	public int getErrorType() {
		return errorType;
	}
	
	public String getErrorMessage() {
		String errorMessage;
		switch (errorType) {
		case GENERAL_NO_DATA:
			errorMessage = messages.getString("DataExportError.general.noData");
			break;
		case GENERAL_UNKNOWN:
			errorMessage = messages.getString("DataExportError.general.unknown");
			break;
		case SERVER_ONLINE_SETUP_MISSING:
			errorMessage = messages.getString("DataExportError.online.missingSetup");
			break;
		case SERVER_ONLINE_URL_MISSING:
			errorMessage = messages.getString("DataExportError.online.missingUrl");
			break;
		case SERVER_ONLINE_HTTP:
			errorMessage = messages.getString("DataExportError.online.http");
			break;
		default: 
			errorMessage = messages.getString("DataExportError.general.unknown");
			break;
		}
		
		if (!errorMessage.equals("")) {
			errorMessage += "\n" + errorReason;
		}
		
		return errorMessage;
	}
	
	public void setMessages(MessagesImpl messages) {
		this.messages = messages;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}
	
	public String getErrorReason() {
		return errorReason;
	}

}
