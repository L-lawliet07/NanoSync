package com.lawliet;


import java.io.BufferedReader;
import java.io.File;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Client implements Runnable{

    String IP;
    public Client(InetAddress ip)
    {
        this.IP=ip.toString();
    }
   public void run() {
        System.out.println("ip is "+ IP);
        String urlString = "http:/"+IP+":8080";

        Collection A = new ArrayList<>();

        Collection B = new ArrayList();
        String filename = new String();
        String fileURL = new String();
        String saveDir = new String();


        String name = "./Downloads/"; // File Directory to be Synced.
        File folder = new File(name);

        for (File file : folder.listFiles()) {
            B.add(file.getName());
        }
        System.out.println("Files in My system\n"+B);

       URL url = null;
       try {
           url = new URL(urlString);
       } catch (MalformedURLException e) {
//           e.printStackTrace();
       }


       BufferedReader reader = null;
       try {
           reader = new BufferedReader(new InputStreamReader(url.openStream()));
       } catch (IOException e) {
 //          e.printStackTrace();
       }
       String line = null;
       try {
           line = reader.readLine();
       } catch (IOException e) {
   //        e.printStackTrace();
       }
       int i;
        for (i = 0; i < line.length(); i++) {

            if (line.charAt(i) == 'e' && line.charAt(i + 1) == '=') {

                i = i + 2;
                while (line.charAt(i) != '"') {

                    filename += line.charAt(i);
                    i++;

                }
         A.add(filename);
         filename="";
            }
        }
        System.out.println("Files in Server\n"+A);
        Collection diff = new ArrayList(A);
        diff.removeAll(B);
        System.out.println("File To be Taken from Server\n"+diff);
        Iterator it = diff.iterator();
        while (it.hasNext()) {
            String x = (String) it.next();
            fileURL = "http:/" + IP + ":8080/get?name=" + x;
            saveDir = "./Downloads/"+x;
            File out = new File(saveDir);
            new Thread(new Download(fileURL, out)).start();
        }
       try {
           reader.close();
       } catch (IOException e) {
     //      e.printStackTrace();
       }


   }
}