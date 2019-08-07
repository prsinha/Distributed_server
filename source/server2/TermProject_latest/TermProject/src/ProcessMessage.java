/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.*;
/**
 *
 * @author Sinha
 */
public class ProcessMessage  extends Thread{
    XMLParser parser;
    DatagramPacket packet;
    GParameters param;
    String message;
    dumpMessage dm;
    retrieveMessage rm;
    FIFOQueue q;
    Transaction t;
    InetAddress address;
    public ProcessMessage(String msg,InetAddress address){
        this.message = msg;
        this.address=address;
        parser=new XMLParser();
        param=GParameters.instance();
        q=FIFOQueue.getFIFOQueue();
    }
    
    public void run() {
        
        //String message=null;
        //get the message as a string.
        //message=new String(packet.getData());
        String type=parser.parseData(message,"type");
        if(type.equals("transfer")){
            if(parser.parseData(message,"value").equals("request")){
                //System.out.println("ProcessMessage: request message reply..");
                String newMessage=parser.changeTagValue(message,"sender",address.getHostName());
                t = new Transaction();
                t.handleTransferRequets(newMessage);
            } else{//processing of reply.....
                try {
                    q.tQueue.put(message);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            
        } else if(type.equals("FIFO")){
            if(parser.parseData(message,"purpose").equals("Ack")){
                try {
                    q.aQueue.put(message);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }else if(parser.parseData(message,"purpose").equals("deposit")){
                try {
                    q.rQueue.put(message);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } else if(parser.parseData(message,"purpose").equals("withdraw")){
                try {
                    q.rQueue.put(message);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                
            }
            
            //fifo.ReceiveMessage(message);
        } else if(type.equals("FD")){
            try {
                if(parser.parseData(message,"req/rep").equals("request")){
                    param.FD1Queue.put(message);
                } else{
                    param.FD2Queue.put(message);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else if(type.equals("control")){
            String value=parser.parseData(message,"value");
            if(value.equals("RunAsPrimary")){
                //0 means it should run as primary
                param.setMode(0);
            }
            if(value.equals("RunAsBackup")){
                //1 means it should run as backup.
                param.setMode(1);
            }
            if(value.equals("OtherServerNameReply")){
                //set the name of the other host name in the Global parameter class.
                param.setOtherServerName(parser.parseData(message,"content"));
            }
            if(value.equals("BMHostName")){
                //
                //System.out.println("message from process message  inside BMHostName...");
                //System.out.println(parser.parseData(message,"content"));
                param.setBMHostName(parser.parseData(message,"content"));
                //System.out.println(parser.parseData(message,"content"));
            }
            if(value.equals("BMAck")){
                try {
                    //
                    param.BMQueue.put(message);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }else if (type.equals("ST")){
            //System.out.println("inside ST");
            String value=parser.parseData(message,"purpose");
            if(value.equals("request")){
                
                //this is a state transfer request from the backup
                dm = new dumpMessage(param.getOtherHostName());
                dm.run();
                
            }else if(value.equals("reply")){
                //this is the state transfer reply form the Primary
                //System.out.println("inside reply");
                rm = new retrieveMessage(message);
                rm.run();
            }
        }
        
    }
    
}
