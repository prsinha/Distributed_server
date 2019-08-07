import java.net.InetAddress;
import java.net.SocketException;
/*
 * ObjectCreator.java
 *
 * Created on November 25, 2007, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class ObjectCreator {
    
    /** Creates a new instance of ObjectCreator */
    private String objectName = "";
    private InetAddress address = null;
    private int portNo;
    UDPSender sender;
    XMLParser parser = new XMLParser();
    String XMLmessage;
    public ObjectCreator() {
//        this.objectName = objectName;              
    }
    
    public void create(InetAddress address, int portNo){
        this.address = address;
        this.portNo = portNo;
//        if (objectName.compareTo("Primary")== 0){
//            XMLmessage = parser.BuildRunAsPrimary();
//            try {
//                sender = new UDPSender(address, portNo,XMLmessage);
//                sender.SendUDPDatagram();
//            } catch (SocketException ex) {
//                ex.printStackTrace();
//            }            
//            
//        }
//        else {
            XMLmessage = parser.BuildServerRequest();
            try {
                sender = new UDPSender(address, portNo, XMLmessage);
                sender.SendUDPDatagram();
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
            
        //}
            
        
    }
    
}
