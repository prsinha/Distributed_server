import java.io.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
/*
 * requestHandler.java
 *
 * Created on November 25, 2007, 5:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */

public class requestHandler extends Thread{
    
    private String hostname = "";
    private String XMLmesssage = "";
    private String hostname1 = "";
    private String hostname2 = "";
    private InetAddress address1 = null;
    private InetAddress address2 = null;
    InputStreamReader ins = new InputStreamReader(System.in);
    FileInputStream finstream, finstream1;
    BufferedReader br = new BufferedReader(ins);
    FileOutputStream foutStream;
    DataOutputStream out;
    BufferedWriter buffwriter;
    UDPSender sender;
    XMLParser parser = new XMLParser();
    GParameters gp;
    ObjectCreator creator;
    
    /** Creates a new instance of requestHandler */
    public requestHandler(String hostname, String XMLmessage) {
        this.hostname = hostname;
        this.XMLmesssage = XMLmessage;
        gp = GParameters.instance();
     
    }
    
    
    public void run(){
        try {
            System.out.println("Request handler is running.....");
            //Read hostname1 and hostname2 from the file
//            System.out.println("inside request handler...");
//            finstream = new FileInputStream("config.txt");
//            DataInputStream in = new DataInputStream(finstream);
//            BufferedReader fbr = new BufferedReader(new InputStreamReader(in));
//            hostname1 = fbr.readLine();
//            hostname2 = fbr.readLine();
//            fbr.close();
            hostname1 = gp.getHostName1();
            hostname2 = gp.getHostName2();
            //convert the hostnames to IP addresses
            address1 = InetAddress.getByName(hostname1);
            address2 = InetAddress.getByName(hostname2);
            System.out.println(hostname1);
            System.out.println(hostname2);
            System.out.println(hostname);
            
            //if XMLmessage is a buildbackup request
            if (parser.parseData(XMLmesssage, "value").compareTo("Backup") == 0) {
                //if hostname == hostname1.This means that the server object on
                //hostname2 has failed. Therefore, recraete a server object(Backup)
                //on hostname2 and send an acknowledgement to hostname 1.
                if (hostname.compareTo(hostname1) == 0){
                    //send a buildbckup request to the daemon on hostname2
                    creator = new ObjectCreator();
                    creator.create(address2, 9999);
                    //wait for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //send an acknowledgement to hostname1
                    String ack = parser.BuildAck();
                    sender = new UDPSender(address1, 6666, ack);
                    sender.SendUDPDatagram();
                }
                
                //else hostname == hostname2. This means the server object on
                //hostname1 has failed. Therefore, recreate a server object(Backup)
                //on hostname1 and send an acknowledgement to hostname2.
                else {
                    //send a buildbckup request to the daemon on hostname1
                    creator = new ObjectCreator();
                    creator.create(address1, 9999);
                    //wait for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //send an acknowledgement to hostname2
                    String ack = parser.BuildAck();
                    sender = new UDPSender(address2, 6666, ack);
                    sender.SendUDPDatagram();
                }
            }
            //else: XMLmessage is from a server object asking for the hostname
            //of the other server objects
            else if (parser.parseData(XMLmesssage, "value").compareTo("OtherServerNameRequest")==0) {
                //determine which server object has made the request, and send a
                //reply containing the hostname of the other server object.
                System.out.println("sending the reply to ..."+ hostname1);
                if (hostname.compareTo(hostname1) == 0) {
                    String OtherServer = parser.BuildOtherServerNameReply(hostname2);
                    sender = new UDPSender(address1, 7777, OtherServer);
                    sender.SendUDPDatagram();
                    
                }
                if (hostname.compareTo(hostname2) == 0) {
                    String OtherServer = parser.BuildOtherServerNameReply(hostname1);
                    sender = new UDPSender(address2, 7777, OtherServer);
                    sender.SendUDPDatagram();
                }
                
            }
            //else: XMLMessage is from a server object asking for the run mode
            //If the file "config.txt" is empty, this means this is a firs-round
            //request. In this case, by default, BranchMonitor runs server object
            //on hostname1 as primary and runs the one on hostname2 as backup. If the
            //file "round.txt" is not empty, this means it is not a first-round request.
            //Therefore, the server object, no matter on which host should run an backup.
            else if (parser.parseData(XMLmesssage, "value").compareTo("RunMode")==0) {
                
                System.out.println(hostname + "asked for RunMode...");
                System.out.println("round: "+gp.getRound());
                //if BranchMonitor has not crashed before (this is the very first time
                //it's running and this is the first request from server objects
               // if (! gp.getFailed()) {
                    if (gp.round<3) {
                        
                        if (hostname.compareTo(hostname1)==0){
                            //send RunAsPrimary
                            String RunMode = parser.BuildRunAsPrimary();
                            sender = new UDPSender(address1, 7777,  RunMode);
                            sender.SendUDPDatagram();
                            System.out.println("RunMode "+ RunMode +" granted" );
                            
                        } else if (hostname.compareTo(hostname2) == 0){
                            //send RunAsBackup
                            String RunMode = parser.BuildRunAsBackup();
                            sender = new UDPSender(address2, 7777, RunMode);
                            sender.SendUDPDatagram();
                            System.out.println("RunMode "+ RunMode +" granted" );
                        }
//                    //write something to round.txt
//                    fbr1.close();
//                    foutStream = new FileOutputStream("round.txt");
//                    out = new DataOutputStream(foutStream);
//                    buffwriter = new BufferedWriter(new OutputStreamWriter(out));
//                    buffwriter.write("not first round!");
//                    buffwriter.close();
                        gp.RaiseRound();
                        System.out.println("Now, the round is: "+gp.getRound());
                    }
                //}
                
                else{//no the first round
                    if (hostname.compareTo(hostname1)==0){
                        //send RunAsBackup
                        String RunMode = parser.BuildRunAsBackup();
                        sender = new UDPSender(address1, 7777, RunMode);
                        sender.SendUDPDatagram();
                        
                    } else if (hostname.compareTo(hostname2) == 0){
                        //send RunAsBackup
                        String RunMode = parser.BuildRunAsBackup();
                        sender = new UDPSender(address2, 7777, RunMode);
                        sender.SendUDPDatagram();
                    }
                    gp.RaiseRound();
                    System.out.println("created a new backup, now the round is:"+gp.getRound());
                }
            }
            
        } //catch (FileNotFoundException ex) {
        //ex.printStackTrace();
        //}
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
