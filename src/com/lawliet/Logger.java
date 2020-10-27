///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

package com.lawliet;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

/*
 * Logger Module : The logger module will log important information and events is a CSV file.
 */

public class Logger {
	
	static FileWriter logFileWriter = null;
	// Initializing variables
	Logger() {
		try {
			String logFilename = "Log_" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +".csv";
			File logPath = new File(Main.path + "Log");
			if(!logPath.exists()) {
				logPath.mkdir();
			}
			logFileWriter = new FileWriter(logPath + "/" + logFilename,true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// This static method will provide interface to write info to logfile
	public static synchronized void write(String logMsg) {
		
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String log = String.format("%s, %s,",timeStamp,logMsg);
		System.out.println("[LOG] : "+log);
		try {
			logFileWriter.write(log+"\n");
			logFileWriter.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}