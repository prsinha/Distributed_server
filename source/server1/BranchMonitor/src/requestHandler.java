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

public class requestHandler extends Thread {
    
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
    JavaHowTo jht; 
    
    /** Creates a new instance of requestHandler */
    public requestHandler(String hostname, String XMLmessage) {
        this.hostname = hostname;
        this.XMLmesssage = XMLmessage;
        gp = GParameters.instance();
        jht = new JavaHowTo();
        
    }
    
    
    public void run() {
        try {
            finstream = new FileInputStream("recovery.txt");
            DataInputStream in = new DataInputStream(finstream);
            BufferedReader fbr = new BufferedReader(new InputStreamReader(in));
            //            hostname1 = fbr.readLine();
            //            hostname2 = fbr.readLine();
            //            fbr.close();
            hostname1 = gp.getHostName1();
            hostname2 = gp.getHostName2();
            //convert the hostnames to IP addresses
            address1 = InetAddress.getByName(hostname1);
            address2 = InetAddress.getByName(hostname2);
            
            
            //if XMLmessage is a buildbackup request
            if (parser.parseData(XMLmesssage, "value").compareTo("Backup") == 0) {
                //if hostname == hostname1.This means that the server object on
                //hostname2 has failed. Therefore, recraete a server object(Backup)
                //on hostname2 and send an acknowledgement to hostname 1.
                if (hostname.compareTo(hostname1) == 0) {
                    //send a buildbckup request to the daemon on hostname2
                    jht.keepColors();
		    jht.setColor(jht.FOREGROUND_RED, jht.BACKGROUND_BLACK);
                    System.out.println("*************************************************************");
                    jht.restoreColors();
                    System.out.println(hostname1+" reported a failure on "+hostname2);
                    System.out.println("creating a backup on host: "+hostname2);
                    jht.keepColors();
		    jht.setColor(jht.FOREGROUND_GREEN, jht.BACKGROUND_BLACK);
                    System.out.println("*************************************************************");
                    jht.restoreColors();
                    creator = new ObjectCreator();
                    creator.create(address2, gp.getDaemonPortNo());
                    //wait for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //send an acknowledgement to hostname1
                    String ack = parser.BuildAck();
                    sender = new UDPSender(address1, gp.getServerFDPortNo(), ack);
                    sender.SendUDPDatagram();
                }
                
                //else hostname == hostname2. This means the server object on
                //hostname1 has failed. Therefore, recreate a server object(Backup)
                //on hostname1 and send an acknowledgement to hostname2.
                else {
                    //send a buildbckup request to the daemon on hostname1
                    jht.keepColors();
		    jht.setColor(jht.FOREGROUND_RED, jht.BACKGROUND_BLACK);
                    System.out.println("*************************************************************");
                    jht.restoreColors();
                    System.out.println(hostname2+" reported a failure on "+hostname1);
                    System.out.println("creating a backup on host: "+hostname1);
                    jht.keepColors();
		    jht.setColor(jht.FOREGROUND_GREEN, jht.BACKGROUND_BLACK);
                    System.out.println("*************************************************************");
                    jht.restoreColors();
                    creator = new ObjectCreator();
                    creator.create(address1, gp.getDaemonPortNo());
                    //wait for 1 second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //send an acknowledgement to hostname2
                    String ack = parser.BuildAck();
                    sender = new UDPSender(address2, gp.getServerFDPortNo(), ack);
                    sender.SendUDPDatagram();
                }
            }
            //else: XMLmessage is from a server object asking for the hostname
            //of the other server objects
            else if (parser.parseData(XMLmesssage, "value").compareTo("OtherServerNameRequest") == 0) {
                //determine which server object has made the request, and send a
                //reply containing the hostname of the other server object.
                
                if (hostname.compareTo(hostname1) == 0) {
                    String OtherServer = parser.BuildOtherServerNameReply(hostname2);
                    sender = new UDPSender(address1, gp.getServerPortNo(), OtherServer);
                    sender.SendUDPDatagram();
                    
                }
                if (hostname.compareTo(hostname2) == 0) {
                    String OtherServer = parser.BuildOtherServerNameReply(hostname1);
                    sender = new UDPSender(address2, gp.getServerPortNo(), OtherServer);
                    sender.SendUDPDatagram();
                }
                
            }
            //else: XMLMessage is from a server object asking for the run mode
            //If the file "config.txt" is empty, this means this is a firs-round
            //request. In this case, by default, BranchMonitor runs server object
            //on hostname1 as primary and runs the one on hostname2 as backup. If the
            //file "round.txt" is not empty, this means it is not a first-round request.
            //Therefore, the server object, no matter on which host should run an backup.
            else if (parser.parseData(XMLmesssage, "value").compareTo("RunMode") == 0) {
                
                //this is the first request that BM receives from server objects
                if (gp.round < 3) {
                    //if BM is running for the very first time
                    String str = fbr.readLine();
                    if (str == null) {
                        
                        if (hostname.compareTo(hostname1) == 0) {
                            //send RunAsPrimary
                            String RunMode = parser.BuildRunAsPrimary();
                            sender = new UDPSender(address1, gp.getServerPortNo(), RunMode);
                            sender.SendUDPDatagram();
                            
                        } else if (hostname.compareTo(hostname2) == 0) {
                            //send RunAsBackup
                            String RunMode = parser.BuildRunAsBackup();
                            sender = new UDPSender(address2, gp.getServerPortNo(), RunMode);
                            sender.SendUDPDatagram();
                        }
                        //write something to recovery.txt
                        // fbr1.close();
                        
                        gp.RaiseRound();
                        if (gp.getRound()>2){
                            foutStream = new FileOutputStream("recovery.txt");
                            out = new DataOutputStream(foutStream);
                            buffwriter = new BufferedWriter(new OutputStreamWriter(out));
                            buffwriter.write("recovered from a crash!");
                            buffwriter.close();
                            gp.setFailed();
                        }
                        
                        
                    } else {
                        //BM has recovered from a crash
                        if (hostname.compareTo(hostname1) == 0) {
                            //send RunAsBackup
                            String RunMode = parser.BuildRunAsBackup();
                            sender = new UDPSender(address1, gp.getServerPortNo(), RunMode);
                            sender.SendUDPDatagram();
                            
                            
                        } else if (hostname.compareTo(hostname2) == 0) {
                            //send RunAsBackup
                            String RunMode = parser.BuildRunAsBackup();
                            sender = new UDPSender(address2, gp.getServerPortNo(), RunMode);
                            sender.SendUDPDatagram();
                            
                        }
                        gp.RaiseRound();
                                             
                    }
                }
                
                else {//no the first round
                    if (hostname.compareTo(hostname1) == 0) {
                        //send RunAsBackup
                        String RunMode = parser.BuildRunAsBackup();
                        sender = new UDPSender(address1, gp.getServerPortNo(), RunMode);
                        sender.SendUDPDatagram();
                        
                    } else if (hostname.compareTo(hostname2) == 0) {
                        //send RunAsBackup
                        String RunMode = parser.BuildRunAsBackup();
                        sender = new UDPSender(address2, gp.getServerPortNo(), RunMode);
                        sender.SendUDPDatagram();
                    }
                    gp.RaiseRound();
                    
                }
            }
            
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
