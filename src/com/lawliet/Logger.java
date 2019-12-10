package com.lawliet;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

public class Logger {
	
	static FileWriter logFileWriter = null;
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