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
     GParameters gp;
     JavaHowTo jht; 
    /** Creates a new instance of handleRemoteTransferReq */
    public UDPReceiver(int serverPort) {
        this.serverPort = serverPort; 
        gp = GParameters.instance();
        jht = new JavaHowTo();
       }
    
    public void run()
    {
        try
        {
            if (gp.getFailed()){
                System.out.println("");
                jht.cls();
                jht.keepColors();
		jht.setColor(jht.FOREGROUND_WHITE, jht.BACKGROUND_RED);
                System.out.println("================================ <BRANCH MONITOR> ==============================");
                jht.restoreColors();
                jht.keepColors();
		jht.setColor(jht.FOREGROUND_RED, jht.BACKGROUND_BLACK);
                System.out.println("Recovering from a crash failure...");
                System.out.println("Reading form the log file...");
                jht.restoreColors();
                jht.keepColors();
		jht.setColor(jht.FOREGROUND_GREEN, jht.BACKGROUND_BLACK);
                System.out.println("Recovery successful...");
                jht.restoreColors();
            }
            System.out.println("BranchMonitor is now monitoring the objects...");
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
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String hostname = address.getCanonicalHostName();
            requestHandler rh = new requestHandler(hostname, message);
            rh.start();

                       
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
