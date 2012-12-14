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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.app.gui.MessagesImpl;
import ch.tatool.data.Module;
import ch.tatool.export.DataExporterError;

/**
 * Uploads module data to a server.
 * This class works together with the HitC (which represents the server side
 * part of the connection).
 * 
 * @author Michael Ruflin
 * @author Laura Chan (HitC modifications)
 */
public class ServerDataExporter extends DataExporterImpl {
    
    Logger logger = LoggerFactory.getLogger(ServerDataExporter.class);
    
    /** Username used to export the data (used for server authentication). */
    public static final String PROPERTY_EXPORT_USERNAME = "username";
    
    /** Password used to export the data (used for server authentication). */
    public static final String PROPERTY_EXPORT_PASSWORD = "password";
    
    public String url;
    
    public MessagesImpl messages;
    
    public DataExporterError exporterError;

    public ServerDataExporter() {
    	super();
    }
    
    public String exportData(Component parentFrame, Module module, int fromSessionIndex, DataExporterError exporterError) {
    	
    	this.exporterError = exporterError;

        CSVTrialDataExport csvTrialExport = new CSVTrialDataExport(dataService);
        csvTrialExport.setCSVParameters(';');
        File tmpTrialFile = csvTrialExport.exportData(module, fromSessionIndex);
        
        // don't do anything if no files got exported
        if (tmpTrialFile == null) {
        	this.exporterError.setErrorType(DataExporterError.GENERAL_NO_DATA);
        	return "Reason: No data to export";
        }
        
        // get the httpentity containing the data
        HttpEntity httpEntity = getHttpEntity(module, fromSessionIndex, "trialData", tmpTrialFile);
        
        if (httpEntity == null) {
        	this.exporterError.setErrorType(DataExporterError.SERVER_ONLINE_SETUP_MISSING);
        	return "Reason: Module is not properly configured for Tatool online.";
        }
        
        // upload the entity
        String error = uploadFiles(parentFrame, module, httpEntity);
        
        // delete the temporary file
        tmpTrialFile.delete();
        
        return error;
    }
    
    private HttpEntity getHttpEntity(Module module, int fromSessionIndex, String filename, File tmpFile) {
        // we use a multipart entity to send over the files
        MultipartEntity entity = new MultipartEntity();
        
        // get tatool online information for module
        Map<String, String> moduleProperties = module.getModuleProperties();
        String subjectCode= moduleProperties.get(Module.PROPERTY_MODULE_CODE);
        if ( subjectCode == null || subjectCode.isEmpty() ) {
        	subjectCode = module.getUserAccount().getName();
        }
      
        String moduleID = moduleProperties.get(Module.PROPERTY_MODULE_ID);
        
        // InputStreamBody does not provide a content length (-1), so the server complains about the request length not
        // being defined. FileBody does not allow setting the name (which is different from the temp file name
        // Easiest solution: subclass of FileBody that allows overwriting the filename with a new one
        FileBodyExt fileBody = new FileBodyExt(tmpFile);
        fileBody.setFilename(filename);
        entity.addPart("file", fileBody);
        
        // add some additional data
        try {
            Charset utf8CharSet = Charset.forName("UTF-8");
            entity.addPart("fromSessionIndex", new StringBody(String.valueOf(fromSessionIndex), utf8CharSet));
            entity.addPart("subjectCode", new StringBody(subjectCode, utf8CharSet));
            entity.addPart("moduleID", new StringBody(moduleID, utf8CharSet));
        } catch (UnsupportedEncodingException e) {
            // should not happen
            logger.error("Unable to set write form parts", e);
        } catch (IllegalArgumentException e) {
        	// should not happen
            logger.error("Missing settings for tatool online", e);
            return null;
        }
        
        return entity;
    }
    
    private String uploadFiles(Component parentFrame, Module module, HttpEntity httpEntity) {
        // get the server upload configuration
        String serverUrl = getUrl();
        if (serverUrl == null) {
        	exporterError.setErrorType(DataExporterError.SERVER_ONLINE_URL_MISSING);
        	return "Reason: No upload server url defined";
        }
        String username = module.getModuleProperties().get(PROPERTY_EXPORT_USERNAME);
        if (username == null) {
            username = "anonymous";
        }
        String password = module.getModuleProperties().get(PROPERTY_EXPORT_PASSWORD);
        if (password == null) {
            password = "anonymous";
        }
        
        HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 5000);
		HttpConnectionParams.setSoTimeout(params, 0); 
		
        // Create a httpclient instance
        DefaultHttpClient  httpclient = new DefaultHttpClient (params);
        
        // set the credentials we got (might or might not get used
        //httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        
        // setup a POST call
        HttpPost httpPost = new HttpPost(serverUrl);
        httpPost.setEntity(httpEntity);       

        // execute the post and gather the result
        try {
            HttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            	exporterError.setErrorType(DataExporterError.SERVER_ONLINE_HTTP);
            	exporterError.setErrorReason(response.getStatusLine().getReasonPhrase()+ " (" + response.getStatusLine().getStatusCode() + ")");
                return "Reason: " + response.getStatusLine().getReasonPhrase() 
 			   + " (" + response.getStatusLine().getStatusCode() + ")";
            } else {
                // all ok
                String result = IOUtils.toString(response.getEntity().getContent());
                if (logger.isDebugEnabled()) {
                	logger.debug("Upload server response: {}", result);
                }
            }
        } catch (IOException ioe) {
        	httpclient.getConnectionManager().shutdown();  
            logger.error("Unable to upload data", ioe);
            exporterError.setErrorType(DataExporterError.GENERAL_UNKNOWN);
            exporterError.setErrorReason(ioe.toString() + ": " + ioe.getMessage());
            return "Reason: " + ioe.toString() 
			   + ": " + ioe.getMessage();
        } finally {
	        // When HttpClient instance is no longer needed, 
	        // shut down the connection manager to ensure
	        // immediate deallocation of all system resources
        	
	        httpclient.getConnectionManager().shutdown();
        }
        return null;
    }
    
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
      }

    /**
     * FileBody class that allows setting the filename to use
     */
    class FileBodyExt extends FileBody {
        String fileNameOverwrite = null;
        
        public FileBodyExt(final File file) {
            super(file);
        }
        
        public void setFilename(String fileNameOverwrite) {
            this.fileNameOverwrite = fileNameOverwrite;
        }
        
        public String getFilename() {
            if (fileNameOverwrite != null) {
                return fileNameOverwrite;
            } else {
                return super.getFilename();
            }
        }
    }
    
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String  getExporterName() {
		return "Upload";
	}
}
