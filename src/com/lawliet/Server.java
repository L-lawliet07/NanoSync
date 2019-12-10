///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07)
///////////////////////////////////////////////////////////////

package com.lawliet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import javax.activation.MimetypesFileTypeMap;

public class Server extends NanoHTTPD {


	private ConcurrentHashMap<String,Integer> priority;
	File folder = new File(Main.path + "Sync");
	public Server() {
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
    
    private String getExtension(String fileName){
        int lastIndex = fileName.lastIndexOf(".");
        if(lastIndex != -1 && lastIndex != 0){
        	return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else return "";
    }
    
    int getPriority(String filename){
        String extension = getExtension(filename);
        int priority_value = priority.get(extension)==null?1:priority.get(extension);
        return priority_value;
    }
    
//    @SuppressWarnings("deprecation")
	public Response serve(String uri, Method method,
                          Map<String, String> header, Map<String, String> parameters,
                          Map<String, String> files) {

    	
    	
    	ArrayList<String> list = new ArrayList<>();
        for (File file : folder.listFiles()) {
        	list.add(file.getName());
        }
        Collections.sort(list, ( left, right ) -> {
        	Integer lhs = getPriority((String)left);
            Integer rhs = getPriority((String)right);
            return lhs.compareTo(rhs);
        } );
        if (uri.equals("/")) {
            String html = "";
            for (String fileName : list) {
                html = html + "<a href=\"/get?name=" + fileName + "\">" + fileName + "</a><br>";
            }
            return new NanoHTTPD.Response(Response.Status.OK, MIME_HTML, html);
        } else if (uri.equals("/get")) {

            FileInputStream fileInputStream = null;
            File file = null;
            try {
                file = new File(this.folder +"/"+ parameters.get("name"));
                
                fileInputStream = new FileInputStream(file);
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