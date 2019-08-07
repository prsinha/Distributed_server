import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
/*
 * FailureDetection.java
 *
 * Created on November 28, 2007, 11:22 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class FailureDetection extends Thread {
    XMLParser parser;
    UDPSender sender;
    String failureRequest;
    String branchMonitorFailure;
    String backupFailure;
    GParameters gp = GParameters.instance();  
    InetAddress BMaddress;
    InetAddress OtherServer;
    DatagramSocket socket;
    DatagramPacket packet;
    FIFOQueue fifoQ;
    JavaHowTo color;
    /** Creates a new instance of FailureDetection */
    public FailureDetection() {
        //set the flag
        gp.set_FD_Flag();
        fifoQ=FIFOQueue.getFIFOQueue();
        parser=new XMLParser();
        color=new JavaHowTo();
        try {
            sender=new UDPSender();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        failureRequest=parser.BuildFDRequest();
        branchMonitorFailure=parser.BuildBranchMonitorRequest();  
        backupFailure=parser.BuildBackupRequest();
        //sender = new UDPSender();
        try {
            BMaddress = InetAddress.getByName(gp.getBMHostName());
            OtherServer = InetAddress.getByName(gp.getOtherHostName());
            socket = new DatagramSocket(gp.getFDPortNo());
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    public void run(){
        String replyFD=parser.BuildFDRereply();
        String msg=parser.BuildFDRequest();
        int counter=0;
        byte[] receiveBuf=new byte[1024];
        System.out.println("*****************************************************");
        color.keepColors();
        color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
        System.out.print("Starting Failure Detection ");
        color.restoreColors();
        color.keepColors();
        color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
        System.out.println("             [ok] ");
        color.restoreColors();
        System.out.println("*****************************************************");
        while(true){
           

            while(true){
                //if not primary break
                if(gp.getMode()==1){
                    //System.out.println("FailureDetection:  break");
                    break;
                }
                try{
                Thread.sleep(5000);
                //System.out.println("=============failure request:=========="+failureRequest);
                sender.SendUDPDatagram(failureRequest, OtherServer, gp.getFDPortNo());  
                
                
                packet = new DatagramPacket(receiveBuf, receiveBuf.length);
                socket.setSoTimeout(7000);
                socket.receive(packet);
                Thread.sleep(5000);
                }catch(SocketTimeoutException e){
                    //call the branch monitor
                    //to do
                    color.keepColors();
                    color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                    System.out.println("A crash failure in peer server detected !!!");
                    System.out.println("Requesting the Branch Monitor to recreate a Backup...");
                    color.restoreColors();
                    sender.SendUDPDatagram(backupFailure, BMaddress, gp.getBranchMonitorPortNo());
                    try {//receive the ack from BranchMonitor
                        socket.setSoTimeout(5000);
                        socket.receive(packet);
                        //wait for the other server to come up
                        fifoQ.setRecSeq(0);
                        fifoQ.setSendSeq(0);
                        
                        Thread.sleep(10000);
                        color.keepColors();
                        color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
                        System.out.println("A new Backup successfully detected!");
                        System.out.println("The Primary now proceeds...");
                        color.restoreColors();
                    }catch(SocketTimeoutException ex) {
                        //call the BMHostname daemon to create a BranchMonitor
                        color.keepColors();
                        color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                        System.out.println("Oooooooops! Branch Monitor has also crashed!!!");
                        System.out.println("Requesting for a new Branch Monitor...");
                        color.restoreColors();
                        sender.SendUDPDatagram(branchMonitorFailure, BMaddress, gp.getDaemonPortNo());
                        
                        
                        try {
                            Thread.sleep(4000);
                            color.keepColors();
                            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
                            System.out.println("A new Branch Monitor successfully detected!");
                            System.out.println("Resending the request for a new Backup");
                            color.restoreColors();
                            //resend the backup request to the new branchmonitor
                            sender.SendUDPDatagram(backupFailure, BMaddress, gp.getBranchMonitorPortNo());
                            try {//receive the ack from BranchMonitor
                                socket.setSoTimeout(4000);
                                socket.receive(packet);
                                //wait for the other server to come up
                                fifoQ.setRecSeq(0);
                                fifoQ.setSendSeq(0);
                                Thread.sleep(10000);
                                color.keepColors();
                                color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
                                System.out.println("A new Backup successfully detected!");
                                System.out.println("The Primary now proceeds...");
                                color.restoreColors();
                            }catch (Exception exp){}
                            
                            
                        } catch (InterruptedException exp) {
                            exp.printStackTrace();
                        }
                        
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch(Exception ex){}
                }catch(SocketException e){
                    
                }catch(IOException e){
                    
                }catch(Exception ex){
                    
                }
                
        }

            while(true){
                //if not backup break
                if(gp.getMode()==0)
                    break;
                packet = new DatagramPacket(receiveBuf, receiveBuf.length);
                try {
                    socket.setSoTimeout(15000);
                    socket.receive(packet);
                    //send an acknowlegement
                    sender.SendUDPDatagram(failureRequest, OtherServer, gp.getFDPortNo());
                    
                }catch(SocketTimeoutException e){
                    //reconfigure as primary
                    color.keepColors();
                    color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                    System.out.println("A crash failure in peer server detected!!!");
                    System.out.println("reconfiguring as Primary...");
                    color.restoreColors();
                        fifoQ.setRecSeq(0);
                        fifoQ.setSendSeq(0);
                   
                    EnablePrimary ep = new EnablePrimary();
                    ep.start();
                    color.keepColors();
                    color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                    System.out.println("Requesting the Branch Monitor to create a new Backup...");
                    color.restoreColors();
                    //call the branch monitor
                    sender.SendUDPDatagram(backupFailure, BMaddress, gp.getBranchMonitorPortNo());
                    try {
                        //receiving BranchMonitor's ack...
                        socket.setSoTimeout(4000);
                        socket.receive(packet);
                        //wait for the other server to be up and running
                        Thread.sleep(5000);
                        color.keepColors();
                        color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
                        System.out.println("A new Backup successfully detected!");
                        System.out.println("The Primary now proceeds...");
                        color.restoreColors();
                    }catch(SocketTimeoutException ex){
                        //ask the BMHostname daemon to create a BranchMonitor
                        
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }catch (Exception ex){}
                } 
                catch (IOException ex) {
                    ex.printStackTrace();
                }
             
        }
            
        }
    }
 }

