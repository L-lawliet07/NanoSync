///////////////////////////////////////////////////////////////
// @author Mandeep Bisht(L-lawliet07) /////////////////////////
///////////////////////////////////////////////////////////////

/*
 * Discoverer : Discoverer Module is used to discover nodes within a network.
 */

package com.lawliet;

import java.io.IOException;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.util.concurrent.ConcurrentHashMap;

public class Discoverer {

    final private String PEER_ID;
    final private String BROADCAST_ADDRESS;
    final private int PORT;
    public static ConcurrentHashMap<String,InetAddress> connectedPeerInfo = null;
    private BroadcastSender broadcastSender = null;
    private BroadcastReceiver broadcastReceiver = null;
    final private Thread[] thread = new Thread[2];
        
    public Discoverer(String peerId,String broadcastAddress,int port) {
        this.PEER_ID = peerId;
        this.BROADCAST_ADDRESS = broadcastAddress;
        this.PORT = port;
        connectedPeerInfo = new ConcurrentHashMap<>();
        broadcastSender = new BroadcastSender();
        broadcastReceiver = new BroadcastReceiver();
    }

    public ConcurrentHashMap<String,InetAddress> getConnectedPeerInfo() {
        return connectedPeerInfo;
    }

    void startBroadcastSender() {
        if(!broadcastSender.isRunning) {
            thread[0] = new Thread(broadcastSender);
            thread[0].start();
        }
    }

    void startBroadcastReceiver() {
        if(!broadcastReceiver.isRunning) {
            thread[1] = new Thread(broadcastReceiver);
            thread[1].start();
        }
    }

    void stopBroadcastSender() {
        broadcastSender.stop();
    }

    void stopBroadcastReceiver() {
        broadcastReceiver.stop();
    }

    void startDiscoverer() {
        startBroadcastSender();
        startBroadcastReceiver();
        if ( Main.env == "development" ) {
            Logger.write("[STARTED] : Discoverer started");
        }
    }

    void stopDiscoverer() {
        stopBroadcastSender();
        stopBroadcastReceiver();
        if ( Main.env == "development" ) {
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

        public void run() {
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.setBroadcast(true);
                byte[] buffer =  PEER_ID.getBytes();
                datagramPacket = new DatagramPacket(buffer,buffer.length, InetAddress.getByName(BROADCAST_ADDRESS), PORT);
                isRunning =  true;
                while (!exit) {
                    System.out.println("[SENDING BROADCAST PACKET]");
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

        public void run() {
            try{
                datagramSocket = new DatagramSocket(PORT,InetAddress.getByName("0.0.0.0"));
                datagramSocket.setSoTimeout(200);
                datagramSocket.setBroadcast(true);
                byte[] buffer = new byte[1000];
                isRunning =true;
                while(!exit){
                    datagramPacket = new DatagramPacket(buffer,buffer.length);
                    try{
                        datagramSocket.receive(datagramPacket);
                        String peerName = new String(datagramPacket.getData(),0, datagramPacket.getLength());
                        if((peerName.compareTo(PEER_ID)!=0) && (!connectedPeerInfo.contains(datagramPacket.getAddress()))) {
                            if ( Main.env == "development" ) {
                                Logger.write("[CONNECTED] : "+datagramPacket.getAddress());
                            }
                            connectedPeerInfo.put(peerName, datagramPacket.getAddress());
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
        void stop(){
            if(isRunning) {
                this.exit = true;
            }
        }
    }
}
