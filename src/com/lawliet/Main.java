///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

package com.lawliet;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
//java -jar nanohttp <PEER_ID> <ABSOLUTE_PATH_TO_WORKING_FOLDER_WITH_TRAILING_SLASH> <BROADCAST_IP> <PORT> <MODE(production/development)>

public class Main {
	
	static Scanner scan = new Scanner(System.in);
	static String env = "production";
	static String path = "/home/lawliet/Desktop/working/";
	final static int PORT = 8081;
	public static void main(String[] args) {

		String peerId = "default";
		String broadcastIp = "192.168.0.255";
		int port = 7777;
		if ( args.length < 2 ) {
//			System.out.println("Enter Peer Id : ");
//			peerId = scan.nextLine();
//			System.out.println("[DEBUG] : " + peerId);
//			System.out.println("Enter Absolute Path to working folder <WITH TRAILING SLASH> : ");
//			Main.path = scan.nextLine();
//			System.out.println("[DEBUG] : " + Main.path);
//			System.out.println("Enter Broadcast IP : ");
//			broadcastIp = scan.nextLine();
//			System.out.println("[DEBUG] : " + broadcastIp);
//			System.out.println("Enter Port Number (except 8080) : ");
//			port = Integer.parseInt(scan.nextLine());
//			System.out.println("[DEBUG] : " + port);
//			System.out.println("Enter mode(development or production) : ");
//			env = scan.nextLine();
//			System.out.println("[DEBUG] : " + env);
//			if ( ! env.equals("development") && ! env.equals("production") ) {
//				System.out.println("Mode can only be (development or production)");
//				return;
//			}
		}
		if ( args.length >= 2 ) {
			peerId = args[0];
			Main.path = args[1];
		}
		if ( args.length >= 3 ) {
			broadcastIp = args[2];
		}
		if ( args.length >= 4 ) {
			port = Integer.parseInt(args[3]);
		}
		if ( args.length >= 5 ) {
			if ( ! args[4].equals("development") || ! args[4].equals("production") ) {
				System.out.println("Mode can only be (development or production)");
				return;
			}
			env = args[4];
		}
		
		File syncDir = new File(Main.path + "Sync");
		if ( !syncDir.exists() ) {
			syncDir.mkdir();
		}
		new Logger();
		Discoverer d=new Discoverer(peerId,broadcastIp,port);
		Server server = new Server();
		
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[SERVER STARTED] : Press Enter To Stop");
		d.startDiscoverer(); 
		scan.nextLine();
		server.stop();
		System.out.println("[SERVER STOPED]");
		d.stopDiscoverer();
		scan.close();
	}
}