/**
 *
 * @author h_kassae
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPReceiver extends Thread
{
  
    int serverPort;
    JavaHowTo color;
    /** Creates a new instance of handleRemoteTransferReq */
    public UDPReceiver(int serverPort) {
        this.serverPort = serverPort;        
        color=new JavaHowTo();
       }
    
    public void run()
    {
        try
        {
            System.out.println("*****************************************************");
            color.keepColors();
            color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
            System.out.print("Starting UDP Receiver: ");
            color.restoreColors();
            color.keepColors();
            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
            System.out.println("               [ok]");
            color.restoreColors();
            System.out.println("*****************************************************");

            //System.out.println("Waiting for requests...");
            DatagramSocket socket = new DatagramSocket(serverPort);
            byte[] receiveBuf = new byte[8000];
            DatagramPacket packet = new DatagramPacket(receiveBuf, receiveBuf.length);
            byte[] buf;
            
            while (true)
            {
            
            /*Receive the message from the remore branch server and spawn a 
             * new thread to handle that request
             */ 
            socket.receive(packet);
            String message = new String (packet.getData(), 0, packet.getLength());
            //System.out.println(message);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String packetData=new String(packet.getData()).trim();
            //System.out.println("Port no is: " + packet.getPort() + "and data is :" +packetData);
            //Thread t=new Thread(new ProcessMessage(packet));        
            ProcessMessage PM = new ProcessMessage(packetData,address);
            PM.start();
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
