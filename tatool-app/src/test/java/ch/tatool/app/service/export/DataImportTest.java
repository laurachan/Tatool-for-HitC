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
package ch.tatool.app.service.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Ignore;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.InputStreamResource;

import ch.tatool.element.Node;

@Ignore // not an element test as such
public class DataImportTest {

	private static String serverUrl = "http://www.tatool.ch/tatool/download.php";

	public static void main(String[] args) {
		// get the httpentity containing the data
		HttpEntity httpEntity = getHttpEntity();
		// Create a httpclient instance
		DefaultHttpClient httpclient = new DefaultHttpClient();
		// setup a POST call
		HttpGet httpGet = new HttpGet(serverUrl);



		try {
			HttpResponse response = httpclient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.out.println("Unable to download data: "
						+ response.getStatusLine().getReasonPhrase());
				System.out.println(response.getStatusLine().getStatusCode());
			} else {
				// all ok
				HttpEntity enty = response.getEntity();
				InputStream is = enty.getContent();

				XmlBeanFactory beanFactory = null;
		        try {

		            beanFactory = new XmlBeanFactory(new InputStreamResource(is));
	
		        } catch (BeansException be) {
		            // TODO: inform user that training configuration is broken
		            throw new RuntimeException("Unable to load module configuration", be);
		        }
		        
		        // check whether we have the mandatory beans (rootElement)
		        if (! beanFactory.containsBean("rootElement")) {
		            // TODO: inform user that training configuration is broken
		            throw new RuntimeException("No rootElement bean found in the module configuration file");            
		        }
		        
		        // fetch the rootElement
		        Node rootElement = (Node) beanFactory.getBean("rootElement");
		        
		        System.out.println(rootElement.getId());
		        
			}
		} catch (IOException ioe) {

		}

		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();
	}

	private static HttpEntity getHttpEntity() {
		// we use a multipart entity to send over the files
		MultipartEntity entity = new MultipartEntity();

		// get participant information for training (anonymous coded data)
		String participant = "1";

		// add some additional data
		try {
			Charset utf8CharSet = Charset.forName("UTF-8");
			entity.addPart("participant", new StringBody(participant,
					utf8CharSet));
		} catch (UnsupportedEncodingException e) {
			// should not happen
		}

		return entity;
	}

}
