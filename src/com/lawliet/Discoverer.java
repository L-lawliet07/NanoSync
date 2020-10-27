///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

package com.lawliet;

import java.io.IOException;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.concurrent.ConcurrentHashMap;

/**************************************************************
 * Discoverer : Discoverer Module is used to discover nearby nodes within a network.
 */

public class Discoverer {

    final private String PEER_ID;     // PEER_ID is the unique id of the node
    final private String BROADCAST_ADDRESS;     // Address at which broadcast sender will send shouter packets.
    final private int PORT;     // PORT number at which Broadcast sender will send packet Note: all nodes should have same PORT number.
    public static ConcurrentHashMap<String,InetAddress> connectedPeerInfo = null;     // connectedPeerInfo will save all the nearby node information
    private BroadcastSender broadcastSender = null; 
    private BroadcastReceiver broadcastReceiver = null;
    final private Thread[] thread = new Thread[2];     // Thread object to start BroadcastSender and BroadcastReceiver at different thread.
    
    public Discoverer(String peerId,String broadcastAddress,int port) {
        this.PEER_ID = peerId;
        this.BROADCAST_ADDRESS = broadcastAddress;
        this.PORT = port;
        connectedPeerInfo = new ConcurrentHashMap<>();
        broadcastSender = new BroadcastSender();
        broadcastReceiver = new BroadcastReceiver();
    }

    // This function will return hashmap that store connected peer info
    public ConcurrentHashMap<String,InetAddress> getConnectedPeerInfo() {
        return connectedPeerInfo;
    }

    // Method to start Broadcast sender 
    void startBroadcastSender() {
        if(!broadcastSender.isRunning) {
            thread[0] = new Thread(broadcastSender);
            thread[0].start();
        }
    }

    // Method to start Broadcast receiver
    void startBroadcastReceiver() {
        if(!broadcastReceiver.isRunning) {
            thread[1] = new Thread(broadcastReceiver);
            thread[1].start();
        }
    }

    // Method to stop broadcast sender
    void stopBroadcastSender() {
        broadcastSender.stop();
    }

    // Method to stop broadcast receiver
    void stopBroadcastReceiver() {
        broadcastReceiver.stop();
    }

    // Method to start Discoverer
    void startDiscoverer() {
        startBroadcastSender();
        startBroadcastReceiver();
        if ( Main.env.equals("development") ) { //Only Displaying debug info when env is development.
            Logger.write("[STARTED] : Discoverer started");
        }
    }

    // Method to stop Discoverer
    void stopDiscoverer() {
        stopBroadcastSender();
        stopBroadcastReceiver();
        if ( Main.env.equals("development") ) { //Only Displaying debug info when env is development.
            Logger.write("[STOPED] : Discoverer stoped");
        }
    }
    
    /*
     * BroadcastSender : The broadcast sender sends a broadcast packet every 5 seconds. 
     */
    class BroadcastSender implements Runnable {

        volatile boolean exit;
        volatile boolean isRunning;
        private DatagramSocket datagramSocket = null;
        private DatagramPacket datagramPacket = null;

        BroadcastSender() {
            isRunning = false;
            exit = false;
        }

        // Overridden method to start thread.
        public void run() {
            
            // this block is responsible for sending broadcast packet to the network.
            try {
                // creating DatagramSocket object
                datagramSocket = new DatagramSocket();
                // enabling Broadcast option
                datagramSocket.setBroadcast(true);
                byte[] buffer =  PEER_ID.getBytes();
                
                // creating datagram packet
                datagramPacket = new DatagramPacket(buffer,buffer.length, InetAddress.getByName(BROADCAST_ADDRESS), PORT);
                isRunning =  true;
                while (!exit) {
                    if ( Main.env.equals("development") ) {
                        System.out.println("[SENDING BROADCAST PACKET]");
                    }
                    // sending datagram packet
                    datagramSocket.send(datagramPacket);
                    try {
                        Thread.sleep(5000);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch(SocketException e) {
                e.printStackTrace();
            } catch(UnknownHostException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                datagramSocket.close();
            }
            isRunning = false;
        }

        // function to stop Broadcast sender thread
        void stop() {
            if(isRunning) {
                this.exit = true;
            }
        }
    }

    /*
     * BroadcastReceiver : The broadcast receiver receives broadcast packets from nearby nodes and stores their information in a hashmap.
     */
    class BroadcastReceiver implements Runnable {

        volatile boolean exit;
        volatile boolean isRunning;
        private DatagramSocket datagramSocket = null;
        private DatagramPacket datagramPacket = null;

        BroadcastReceiver() {
            isRunning = false;
            exit = false;
        }

        // Overridden method to start thread.
        public void run() {
            try{
                // creating datagram socket object
                datagramSocket = new DatagramSocket(PORT,InetAddress.getByName("0.0.0.0"));
                datagramSocket.setSoTimeout(200);
                datagramSocket.setBroadcast(true);
                byte[] buffer = new byte[1000];
                isRunning =true;
                while(!exit){
                    // creating datagram packet object
                    datagramPacket = new DatagramPacket(buffer,buffer.length);
                    try{
                        // receiving datagram packet
                        datagramSocket.receive(datagramPacket);
                        // extracting peername
                        String peerName = new String(datagramPacket.getData(),0, datagramPacket.getLength());
                        // checking if peer name is unique and does not match users peer name
                        if((peerName.compareTo(PEER_ID)!=0) && (!connectedPeerInfo.contains(datagramPacket.getAddress()))) {
                            if ( Main.env.equals("development") ) {
                                Logger.write("[CONNECTED] : "+datagramPacket.getAddress());
                            }
                            // saving peer info in hashmap
                            connectedPeerInfo.put(peerName, datagramPacket.getAddress());
                            // starting receiver
                            new Thread(new Receiver(datagramPacket.getAddress(), peerName));
                        }
                    }catch(IOException e){
                        //pass
                    }
                }
            }catch(SocketException e){
                e.printStackTrace();
            }catch(UnknownHostException e){
                e.printStackTrace();
            }finally {
                if ( datagramSocket !=null ) {
                    datagramSocket.close();
                }
            }
        }

        // function to stop Broadcast receiver thread
        void stop(){
            if(isRunning) {
                this.exit = true;
            }
        }
    }
}
