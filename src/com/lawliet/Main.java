package com.lawliet;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException{
		Discoverer d=new Discoverer("192.168.100.255","PEER1",9999);
		Server server = new Server();
		server.start();
		System.out.println("[SERVER STARTED] : Press Enter To Stop");
		d.startDiscoverer();
		Scanner scan = new Scanner(System.in); 
		scan.nextLine();
		server.stop();
		System.out.println("[SERVER STOPED]");
		d.stopDiscoverer();
		scan.close();
	}
}