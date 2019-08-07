//import daemon.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
/*
 * handleRemote.java
 *
 * Created on October 24, 2007, 3:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class UDPReceiver extends Thread
{
  
    int serverPort;
    /** Creates a new instance of handleRemoteTransferReq */
    public UDPReceiver(int serverPort) {
        this.serverPort = serverPort;        
       }
    
    public void run()
    {
        try
        {
            System.out.println("Daemon is up and running");
            System.out.println("Waiting for requests...");
            DatagramSocket socket = new DatagramSocket(serverPort);
            byte[] receiveBuf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(receiveBuf, receiveBuf.length);
            byte[] buf;
            
            while (true)
            {
            
            /*Receive the message from the remore branch server and spawn a 
             * new thread to handle that request
             */ 
            socket.receive(packet);
            String message = new String (packet.getData(), 0, packet.getLength());
            System.out.println(message);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            requestHandler rh = new requestHandler(address, port, message);
            rh.start();
            System.out.println("Handler thread spawned to handle the incoming request");
            
            }
            
               
            
        }
        catch(SocketException e)
        {
            System.out.println("socket exception: " + e);
        }
        catch (IOException e)
        {
            System.out.println("io exception:" + e);
        }
        
    }
    
}
