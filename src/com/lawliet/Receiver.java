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


/**************************************************************
 * Receiver : Receiver Module will extract all the file name from the connected peer and make a get reqeust.
 */
public class Receiver implements Runnable{

    private String ip; // string to store ip address
    private String peerId; // string to store peer id

    public Receiver(InetAddress inetAddress, String peerId) {
        this.ip=inetAddress.toString();
        this.peerId = peerId;
    }
    
    public void run() {
        // creaging url string 
        String urlString = "http:/"+this.ip.toString()+":" + Main.PORT;
        // collection to store filename
        Collection<String> collection = new ArrayList<>();
        String filename = new String();

        // url object to store url
        URL url = null;
        BufferedReader bufferedReader = null;
        String line = "";

        try {
            url = new URL(urlString);
            // creating a stream connection with another user
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            // reading line
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

        // extracting file names
        int i;
        for (i = 0; i < line.length(); i++) {

            if (line.charAt(i) == 'e' && line.charAt(i + 1) == '=') {

                i = i + 2;
                while (line.charAt(i) != '"') {

                    filename += line.charAt(i);
                    i++;

                }
                // storing file name to the collection    
                collection.add(filename);
                filename="";
            }
        }

        for ( String fileName : collection ) {
            // creating url to make get request
            String fileUrl = "http:/" + this.ip + ":" + Main.PORT +"/get?name=" + fileName;
            new Thread( new Downloader(fileUrl, new File(Main.path + fileName)) ).start();
        }
        // removing current node from the connectedPeerInfo
        Discoverer.connectedPeerInfo.remove(peerId);
   }
}