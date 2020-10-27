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
	static String env = "development"; // environment 
	static String path = "/home/lawliet/Desktop/check/working/"; // working path location
	final static int PORT = 8080; // port number to start http server 
	public static void main(String[] args) {

		String peerId = "default"; // unique peer id 
		String broadcastIp = "192.168.43.255"; // broadcast ip of the network
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

		if ( args.length == 5 ) {
			
			// first argument will be peer id
			peerId = args[0];

			// second argument will be path to working directory
			Main.path = args[1];

			// third argument will be broadcast ip
			broadcastIp = args[2];

			// fourth argument will be port number for broadcast sender
			port = Integer.parseInt(args[3]);
			
			// fifth argument will be for environment
			if ( ! args[4].equals("development") || ! args[4].equals("production") ) {
				System.out.println("Mode can only be (development or production)");
				return;
			}

			env = args[4];
		} else {
			System.out.println("java -jar nanohttp <PEER_ID> <ABSOLUTE_PATH_TO_WORKING_FOLDER_WITH_TRAILING_SLASH> <BROADCAST_IP> <PORT> <MODE(production/development)>");
		}

		File syncDir = new File(Main.path + "Sync");
		// if sync file is not present in working directory program will create one.
		if ( !syncDir.exists() ) {
			syncDir.mkdir();
		}

		// Starting logger modul
		new Logger();
		
		// Creating Discoverer object
		Discoverer d=new Discoverer(peerId,broadcastIp,port);
		
		// Creating server object
		Server server = new Server();
		
		try {
			// starting server
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Logging to console only if running in development environment
		if ( Main.env.equals("development") ) {
			System.out.println("[SERVER STARTED] : Press Enter To Stop");
		}

		// starting discoverer module
		d.startDiscoverer(); 
		
		scan.nextLine();
		
		// stoping server module
		server.stop();

		// stoping discoverer module
		d.stopDiscoverer();
		
		// Logging to console only if running in development environment
		if ( Main.env.equals("development") ) {
			System.out.println("[SERVER STOPED]");
		}
		scan.close();
	}
}