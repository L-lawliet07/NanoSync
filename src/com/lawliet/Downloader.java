///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

package com.lawliet;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;

/**************************************************************
 * Downloader module : The downloader module is used to download data from the connected stream and this module is also responsible for making a get request to the server.
 */
public class Downloader implements Runnable {

    private String link;
    private File file;
	// link : link to make get request to the server
	// file : file object for current file
    public Downloader(String link, File file) {
        this.link = link;
        this.file = file;
    }

    public void run() {
		
		BufferedInputStream bufferedInputStream = null;
       	BufferedOutputStream bufferedOutputStream = null;
        FileOutputStream fileOutputStream = null;
 
		try {
			// creating url object of the link
			URL url = new URL(link);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			bufferedInputStream = new BufferedInputStream(http.getInputStream());
			fileOutputStream = null;
			// checking if current file exists
			if(file.exists()) {
				fileOutputStream = new FileOutputStream(file,true);
			}
           	else{
				fileOutputStream = new FileOutputStream(file);   
			}

			// skipping already present bytes
			bufferedInputStream.skip(file.length());
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream,1024);
           	byte[] buffer = new byte[1024];
			int read = 0;
			// reading file
			while((read = bufferedInputStream.read(buffer,0,1024)) >=0) {
        	   bufferedOutputStream.write(buffer,0,read);
           	}
           	bufferedOutputStream.close();
	   
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch ( IOException e ) {
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		} finally {
			if ( bufferedInputStream != null ) {
				try {
					bufferedInputStream.close();
				} catch ( IOException e ) {}
			}
			if ( bufferedOutputStream != null ) {
				try {
					bufferedOutputStream.close();
				} catch ( IOException e ) {}
			}
		}
    }
}