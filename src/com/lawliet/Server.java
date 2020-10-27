///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

package com.lawliet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**************************************************************
 * Server : Server Module will start a nanohttp server and serve the get request.
 */
public class Server extends NanoHTTPD {

	private ConcurrentHashMap<String,Integer> priority;
	File folder = new File(Main.path + "Sync");
	public Server() {
        // calling super class to initialize port value
        super(Main.PORT);
        priority = new ConcurrentHashMap<>();
        // Higher the value Higher the priority.
        priority.put("txt",9);
        priority.put("jpeg",8);
        priority.put("png",8);
        priority.put("jpg",8);
        priority.put("mp3",6);
        priority.put("mp4",5);
        priority.put("pdf",7);
    }
    
    // Method to return extension of the filename
    private String getExtension(String fileName){
        int lastIndex = fileName.lastIndexOf(".");
        if(lastIndex != -1 && lastIndex != 0){
        	return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else return "";
    }
    
    // Method to return priority of the file name according to the extension
    int getPriority(String filename){
        String extension = getExtension(filename);
        int priority_value = priority.get(extension)==null?1:priority.get(extension);
        return priority_value;
    }
    
    // overridding server method of nanohttp class
    public Response serve(String uri, Method method,
                          Map<String, String> header, Map<String, String> parameters,
                          Map<String, String> files) {
    	
    	// Arraylist to store present filename
    	ArrayList<String> list = new ArrayList<>();
        for (File file : folder.listFiles()) {
        	list.add(file.getName());
        }
        // sorting according to the priority
        Collections.sort(list, ( left, right ) -> {
        	Integer lhs = getPriority((String)left);
            Integer rhs = getPriority((String)right);
            return lhs.compareTo(rhs);
        } );

        // if there is / request
        if (uri.equals("/")) {
            String html = "";
            for (String fileName : list) {
                html = html + "<a href=\"/get?name=" + fileName + "\">" + fileName + "</a><br>";
            }
            // Serving http page
            return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, html);
        } 
        // if there is a get request
        else if (uri.equals("/get")) {

            FileInputStream fileInputStream = null;
            File file = null;
            try {
                file = new File(this.folder +"/"+ parameters.get("name"));
                
                fileInputStream = new FileInputStream(file);
                // serving the file
                return new NanoHTTPD.Response(Response.Status.OK, "application/octet-stream", fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return new NanoHTTPD.Response("404 File Not Found");
        } else {
            return new NanoHTTPD.Response("404 File Not Found");
        }

    }
}