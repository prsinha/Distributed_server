import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
/*
 * Main.java
 *
 * Created on November 26, 2007, 9:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class Main {
    
    
    /** Creates a new instance of Main */
    public Main() {
    }
    public static void main(String[] args){
        //1. save the arguments in GP (ORBInitialHost and ORBInitialPort
        GParameters gp = GParameters.instance();
        gp.setORBParam(args);
        //2. start the UDP receiver
        UDPReceiver receiver = new UDPReceiver(gp.getServerPortNo());
        receiver.start();
        //3. start the discovery
        //at this point the server locates the BranchMonitor and the other server object
        //then negotiates with the BranchMonitor to find out the RunMode
        Discovery d = new Discovery();
        d.discover_BM_OtherServer();    
        //4. Start the FailureDetection Module
        //System.out.println("Starting Failure Detection Module... ");
        FailureDetection fd = new FailureDetection();
        fd.start();
        //5.....FIFO starts working here....
 //       FIFOQueue fQueue=FIFOQueue.getFIFOQueue();
        FIFO1 fifo=new FIFO1();
        fifo.start();
        //if in backup mode, ask for state transfer
        //it is running in Backup mode
        if (gp.getMode()==1)
        {
            String StateTransfer="";
            XMLParser parser = new XMLParser();
            StateTransfer = parser.BuildStateTransferRequest();
            
            try {
                //send a state transfer request to the primary
                System.out.println("sending the state transfer request to the primary...");
                InetAddress OtherServerAddress;
                OtherServerAddress = InetAddress.getByName(gp.getOtherHostName());
                UDPSender sender = new UDPSender();
                sender.SendUDPDatagram(StateTransfer, OtherServerAddress, gp.getServerPortNo());
                //wait until primary sends the state and it is loaded into memory
                while (gp.getInfoList().isEmpty()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }//once I get out of this loop, the state has been successfully transfered
                //and loaded into memory
                
                System.out.println("state transferred successfully, account info loaded");
                int i=0;
                for (i=0; i<gp.getInfoList().size();++i)
                {
                    AccountInfo current = (AccountInfo) gp.getInfoList().get(i);
                    System.out.println(current.getAccountNo()+"  "+current.getBalance());
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
            }catch (UnknownHostException ex){}
            
        }
    }
    
}
