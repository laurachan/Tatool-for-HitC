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
package ch.tatool.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for the application
 * 
 * @author Michael Ruflin
 */
public class AppUtils {

	private static Logger logger = LoggerFactory.getLogger(AppUtils.class);

	/**
	 * Set a startup property value.
	 * 
	 * @param key
	 *            the key to set
	 * @param value
	 *            the value to set, null to remove the key
	 */
	public static void setStartupProperty(String key, String value) {
		Properties properties = loadAppProperties();
		if (value != null) {
			properties.put(key, value);
		} else {
			properties.remove(key);
		}
		saveAppProperties(properties);
	}

	/**
	 * Get a startup property
	 */
	public static String getStartupProperty(String key, String defaultValue) {
		Properties properties = loadAppProperties();
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		} else {
			return defaultValue;
		}
	}

	private static Properties loadAppProperties() {
		Properties properties = new Properties();
		File file = getStartupPropertiesFile();
		file = file.getAbsoluteFile();
		if (file.exists()) {
			try {
				InputStream is = new FileInputStream(file);
				properties.load(is);
				is.close();
			} catch (IOException ioe) {
				logger.warn("Unable to read startup properties file", ioe);
			}
		}
		return properties;
	}

	private static void saveAppProperties(Properties properties) {
		File file = getStartupPropertiesFile();
		try {
			OutputStream os = new FileOutputStream(file);
			properties.store(os, "Tatool application startup properties");
			os.close();
		} catch (IOException ioe) {
			logger.warn("Unable to write startup properties file", ioe);
		}
	}

	private static File getStartupPropertiesFile() {
		return new File(".appStartupProperties.xml");
	}

	/**
	 * Instantiates a bean given the bean class.
	 * 
	 * @param className
	 *            the name of the class to instantiate
	 * @param failWithError
	 *            whether an error should be thrown if the instantiation fails
	 */
	public static Object createInstance(String className, Class<?> targetClass,
			boolean failWithError) {
		// make sure we got some input
		if (className == null || className.length() < 1) {
			if (failWithError) {
				throw new RuntimeException(
						"No class name to instantiate defined.");
			} else {
				return null;
			}
		}

		// find the class
		Class<?> c = null;

		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			if (failWithError) {
				throw new RuntimeException(e);
			} else {
				return null;
			}
		}

		// check for correct instance type
		if (c != null) {
			if (!targetClass.isAssignableFrom(c)) {
				if (failWithError) {
					throw new RuntimeException("Provided class " + c.getName()
							+ " is not of type " + targetClass.getName());
				} else {
					return null;
				}
			}
		}

		// try to instantiate a bean
		Object bean = null;
		if (c != null) {
			// create a new instance of that session scheduler
			try {
				bean = c.newInstance();
			} catch (InstantiationException e) {
				if (failWithError) {
					throw new RuntimeException(e);
				} else {
					return null;
				}
			} catch (IllegalAccessException e) {
				if (failWithError) {
					throw new RuntimeException(e);
				} else {
					return null;
				}
			}
		}

		// return created bean
		return bean;
	}

}
