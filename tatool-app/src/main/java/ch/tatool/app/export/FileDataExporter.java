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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.tatool.data.Module;
import ch.tatool.export.DataExporterError;

/**
 * Exports module data into a directory chosen by the uesr.
 * 
 * @author Michael Ruflin
 */
public class FileDataExporter extends DataExporterImpl {

	Logger logger = LoggerFactory.getLogger(FileDataExporter.class);

	private String path;

	private boolean relativeToHome = false;

	public FileDataExporter() {
		super();
	}

	/**
	 * Exports the module data to a file
	 */
	public String exportData(Component parentFrame, Module module,
			int fromSessionIndex, DataExporterError exporterError) {

		String filename = "";

		// get path
		String path = getPath();

		if (path == null) {
			// ask the user where he wants to export the data to
			filename = getStorageDirectory(parentFrame);
		} else {
			Date d = new Date();
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
			String account = removeSpecialCharacters(module.getUserAccount()
					.getName());
			String moduleName = removeSpecialCharacters(module.getName());

			if (relativeToHome) {
				filename = System.getProperty("user.home")
						+ System.getProperty("file.separator") + path
						+ System.getProperty("file.separator") + moduleName
						+ "_" + account + "_" + dateformat.format(d) + ".csv";
			} else {
				filename = path + System.getProperty("file.separator")
						+ moduleName + "_" + account + "_"
						+ dateformat.format(d) + ".csv";
			}

		}
		
		// create a new csv exporter
		
		CSVTrialDataExport csvExport = new CSVTrialDataExport(dataService);
		
		// change CSV separator for English language to comma
		if (dataService.getMessages().getLocale().getLanguage().equals("en")) {
			csvExport.setCSVParameters(',');
		} else {
			csvExport.setCSVParameters(';');
		}
		
		File tmpFile = csvExport.exportData(module, fromSessionIndex);

		if (filename == null) {
			exporterError.setErrorType(DataExporterError.GENERAL_CANCEL);
			return "The export has been cancelled.";
		}
		
		// copy/move the file to the destination
		try {
			File moduleDataFile = new File(filename);
			FileUtils.copyFile(tmpFile, moduleDataFile);
			tmpFile.delete();
		} catch (IOException ioe) {
			logger.error("Unable to move exported file to destination", ioe);
			exporterError.setErrorType(DataExporterError.FILE_GENERAL);
			exporterError.setErrorReason(ioe.getMessage());
			return "Export failed.\n" + ioe.getMessage();
		}
		return null;
	}

	public static String removeSpecialCharacters(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			if ((str.charAt(i) >= '0' && str.charAt(i) <= '9')
					|| (str.charAt(i) >= 'A' && str.charAt(i) <= 'z' || (str
							.charAt(i) == '.' || str.charAt(i) == '_')))
				sb.append(str.charAt(i));
		}
		return sb.toString();
	}

	private String getStorageDirectory(Component parentFrame) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "CSV (*.csv)";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				String extension = getExtension(f);
				if (extension != null) {
					if (extension.equals("csv")) {
						return true;
					} else {
						return false;
					}
				}

				return false;

			}
		});
		chooser.setAcceptAllFileFilterUsed(false);
		// chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		int returnVal = chooser.showSaveDialog(parentFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			File f = chooser.getSelectedFile();
			String filename = "";
			if (f == null) {
				throw new RuntimeException();
			}
			String extension = getExtension(f);
			if (extension == null || !getExtension(f).equals("csv")) {
				filename = f + ".csv";
			} else {
				filename = f.getPath();
			}
			return filename;
		} else {
			return null;
		}
	}

	/*
	 * Get the extension of a file.
	 */
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isRelativeToHome() {
		return relativeToHome;
	}

	public void setRelativeToHome(boolean relativeToHome) {
		this.relativeToHome = relativeToHome;
	}

	/**
	 * Get the action name
	 */
	public String getExporterName() {
		return "Export";
	}

}
