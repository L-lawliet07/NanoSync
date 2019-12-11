///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

package com.lawliet;

import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Receiver implements Runnable{

    private String ip;
    private String peerId;
    public Receiver(InetAddress inetAddress, String peerId) {
        this.ip=inetAddress.toString();
        this.peerId = peerId;
    }
    public void run() {
        String urlString = "http:/"+this.ip.toString()+":" + Main.PORT;
        Collection<String> collection = new ArrayList<>();
//        Collection B = new ArrayList<>();
        String filename = new String();
//        String fileURL = new String();
//        String saveDir = new String();
//        lection diff = new ArrayList(A);
//        diff.removeAll(B);

       URL url = null;
       BufferedReader bufferedReader = null;
       String line = "";
       try {
           url = new URL(urlString);
           bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
           line = bufferedReader.readLine();
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
    	   e.printStackTrace();
       } finally {
    	   if ( bufferedReader != null ) {
    		   try {
    			   bufferedReader.close();
    		   } catch( IOException e ) {}
    	   }
       }
       int i;
       for (i = 0; i < line.length(); i++) {

            if (line.charAt(i) == 'e' && line.charAt(i + 1) == '=') {

                i = i + 2;
                while (line.charAt(i) != '"') {

                    filename += line.charAt(i);
                    i++;

                }
         collection.add(filename);
         filename="";
            }
        }
//        System.out.println("Files in Server\n"+A);
//        Collection diff = new ArrayList(A);
//        diff.removeAll(B);
//        System.out.println("File To be Taken from Server\n"+diff);
//        Iterator it = diff.iterator();
//        while (it.hasNext()) {
//            String x = (String) it.next();
//            fileURL = "http:/" + this.ip + ":" + Main.PORT +"/get?name=" + x;
//            saveDir = "./Downloads/"+x;
//            File out = new File(saveDir);
//            new Thread(new Download(fileURL, out)).start();
//        }
        for ( String fileName : collection ) {
            String fileUrl = "http:/" + this.ip + ":" + Main.PORT +"/get?name=" + fileName;
            new Thread( new Downloader(fileUrl, new File(Main.path + fileName)) ).start();
        }
        Discoverer.connectedPeerInfo.remove(peerId);
   }
}