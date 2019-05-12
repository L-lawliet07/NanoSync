package com.lawliet;
import java.util.*;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;

public class Server extends NanoHTTPD {


    public Server() {
        super(8080);
    }
    
    @SuppressWarnings("deprecation")
	public Response serve(String uri, Method method,
                          Map<String, String> header, Map<String, String> parameters,
                          Map<String, String> files) {

        File folder = new File("./Downloads/");


        Map<Integer, List<String>> prio = new HashMap<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        List<String> list4 = new ArrayList<>();
        List<String> list5 = new ArrayList<>();


        for (File file : folder.listFiles()) {
            //System.out.println(file.getName());

            if (GetFileExtension.getFileExtension(file).equals("txt")) {
                list1.add(file.getName());
                prio.put(new Integer(1), list1);

            }

            if (GetFileExtension.getFileExtension(file).equals("pdf")) {
                list2.add(file.getName());
                prio.put(new Integer(2), list2);
            }

            if (GetFileExtension.getFileExtension(file).equals("jpg")||GetFileExtension.getFileExtension(file).equals("png") || GetFileExtension.getFileExtension(file).equals("jpeg"))
            {     list3.add(file.getName());
                prio.put(new Integer(3), list3);
            }

            if (GetFileExtension.getFileExtension(file).equals("mp3")) {
                list4.add(file.getName());
                prio.put(new Integer(4), list4);
            }

            if (GetFileExtension.getFileExtension(file).equals("mp4")) {
                list5.add(file.getName());
                prio.put(new Integer(5), list5);
            }
        }

        String filename = "";

        //TO CREATE THE WEBPAGE
        if (uri.equals("/")) {
           // System.out.println(uri);

            String st = "";
            String x = "";

            for (Map.Entry<Integer, List<String>> en : prio.entrySet()) {
                for (String obj : en.getValue()) {
                    st = st + "<a href=\"/get?name=" + obj + "\">" + obj + "</a><br>";
                }
            }


            return new Response(Response.Status.OK, MIME_HTML, st);


        }

        //TO DOWNLOAD
        else if (uri.equals("/get"))

        {
//            System.out.println(uri);
//            String x = header.get("referer");
//            System.out.println("THE REFERER"+x);


            FileInputStream fis = null;
            File f = null;
            try {
                f = new File("./Downloads/" + parameters.get("name")); //path exists and its correct
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();

            String mimeType = mimeTypesMap.getContentType(filename);
            return new NanoHTTPD.Response(Response.Status.OK, mimeType, fis);


        }




        else {
//            System.out.println(uri);

            return new NanoHTTPD.Response("404 File Not Found");
        }

    }
}
