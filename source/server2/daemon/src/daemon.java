/*
 * daemon.java
 *
 * Created on November 23, 2007, 7:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class daemon {
    
    /** Creates a new instance of daemon */
    public daemon() {
       
    }
    
    public static void main(String[] args) {
        
         GParameters gp = GParameters.instance();
        //start the UDP Receiver to listen for remote requests.
        UDPReceiver receiver = new UDPReceiver(gp.getDaemonPortNo());
        receiver.start();
    }
    
}
    

