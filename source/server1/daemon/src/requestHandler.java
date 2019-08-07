/*
 * requestHandler.java
 *
 * Created on November 23, 2007, 4:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//import daemon.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

/**
 *
 * @author h_kassae
 */
public class requestHandler extends Thread {
    
    /** Creates a new instance of requestHandler */
    String xmlmsg;
    String BMHostName;
    InetAddress address;
    int portNo;
    GParameters gp;
    public requestHandler(InetAddress address, int portNo, String msg) {
        this.xmlmsg = msg;
        this.address = address;
        this.portNo = portNo;
        gp = GParameters.instance();
     }
    
    public void run()
    {
        XMLParser parser = new XMLParser();
        if (parser.parseData(xmlmsg, "type").compareTo("control")== 0 )
        {
            String value = parser.parseData(xmlmsg,"value");
            if (value.compareTo("Server")== 0)
            {
                //Run primary
                System.out.println("A Server will be created shortly... ");
                Runtime rt = Runtime.getRuntime();
                String[] command = { "cmd.exe", "/C", "Start","Main.bat"};
                try {
                    Process proc = rt.exec(command);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //store BM hostname for later use
          
                gp.setBMHostName(address.getHostName());
                //System.out.println(BMHostName);
            }
                
          
            else if (value.compareTo("BranchMonitor")==0) 
            {
                //Run BranchMonitor
                 System.out.println("A BranchMonitor will be created shortly... ");
                Runtime rt = Runtime.getRuntime();
                String[] command = { "cmd.exe", "/C", "Start","BranchMonitor.bat"};
                try {
                    Process proc = rt.exec(command);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
            }
              
            else if (value.compareTo("GetBMHostName")==0) 
            {
                //return the hostname of BM
                //System.out.println(BMHostName);
                String hostnameReply = parser.BuildHostNameReply(gp.getBMHostName());
                UDPSender sender;
                try {
                    ///3333 should be changed to server object's UDPReceiver Port
                    sender = new UDPSender(address, gp.getServerPortNo(), hostnameReply);
                    sender.SendUDPDatagram();
                    sender.CloseSocket();
                } catch (SocketException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
    }//end run
        
       
}//end class
    

