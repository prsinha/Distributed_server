import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Sinha
 */
public class FIFO1 extends Thread{
    private static FIFO1 uniqueFIFO1Instance;
    XMLParser parser;
    //boolean primary;
    int MOD_VAlUE=10;
    FIFOQueue q;
    GParameters param;
    private InetAddress aHost;
    private int send_Seq = 0;
    private int rec_Seq = 0;
    UDPSender sender;
    Transaction t;
    JavaHowTo color;

    public FIFO1() {
        parser = new XMLParser();
        color=new JavaHowTo();
        t=new Transaction();
        q=FIFOQueue.getFIFOQueue();
        param=GParameters.instance();
        try {
            //System.out.println("FIFO: getting other server name ");    
            aHost = InetAddress.getByName(param.getOtherHostName());
            //System.out.println("FIFO: other server name is : "+ aHost);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        try {
            sender=new UDPSender();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    public static FIFO1 getFIFO1() {
        if (uniqueFIFO1Instance == null) {
            uniqueFIFO1Instance = new FIFO1();
        }
        return uniqueFIFO1Instance;
    }


        public void run() {
            String message=null;
            String pMsg = null;
            String bMsg = null;
            int noOfTry = 1;
            int new_Seq = 0;
            int n;
            
            System.out.println("*****************************************************");
            color.keepColors();
            color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
            System.out.print("Starting FIFO ");
            color.restoreColors();
            color.keepColors();
            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
            System.out.println("                       [ok] ");
            color.restoreColors();
            System.out.println("*****************************************************");
            
            
         while(true){
                /**********************************primary loop*************************/
                while(param.getMode()==0){
                    if(bMsg!=null){
                     bMsg=null;   
                    }
                
                    //this loop is for primary server.
                    if (noOfTry == 1 && pMsg == null) {
                    //System.out.println("FIFO: waiting for the message in queue..");    
                    try{
                    pMsg = (String)q.rQueue.take();
                    }catch(InterruptedException e){
                    System.out.println(e.getMessage());
                    }

                    System.out.println("FIFO: One message received..");    
                    System.out.println("FIFO: message sequence number is:"+q.getSendSeq());
                    //System.out.println("FIFO:"+rec_Seq);
                    //System.out.println("FIFO: sented sequence no is "+send_Seq);
                    new_Seq = (q.getSendSeq() + 1) % MOD_VAlUE;
                    }
                    
                    if (noOfTry == 3) {
                    
                        //it fails for the second time ..do something ..
                    }
                    //String From = parser.parseData(pMsg, "from");
                    //change the from tag value from servant to network.
                    message=parser.changeTagValue(pMsg, "from", "network");
                    message=parser.insertTag(message, "xml", "seq_No", String.valueOf(q.getSendSeq()));
                    sender.SendUDPDatagram(message,aHost,param.getServerPortNo());
                    //now check the aQueue for the incoming Acknowladgement.
                    System.out.println("FIFO: waiting for the Ack..");    
                    try{
                    message=(String)q.aQueue.poll(5,TimeUnit.SECONDS);
                    if(message.equals(null)){
                        //timeout occurs send the message again...
                        noOfTry = noOfTry + 1;
                        continue;
                        
                    }
                    }catch(InterruptedException e){
                    System.out.println(e.getMessage());
                }

                    n=Integer.parseInt(parser.parseData(message,"seq_No"));
                    if (n == q.getSendSeq()) {
                        //packet send and ack received successfully..received successfully
                        System.out.println("FIFO: FIFO got the correct message");    
                        //send_Seq = new_Seq;
                        q.setSendSeq(new_Seq);
                        noOfTry = 1;
                        pMsg = null;
                        continue;
                    }
                    if (n != q.getSendSeq()) {
                        //this is not the appropriate acknowladgement..
                        //increment the no_of_try field 
                        noOfTry = noOfTry + 1;
                        continue;
                    }
         
                }//end of loop
                /***********************************************************************************/
                         /**************Backup loop*****************/
            while(param.getMode()==1){
                //this loop is for backup server.
                //set the pMsg to null.
                if (noOfTry == 3) {
                    //it fails for the second time ..do something ..
                }

                if(pMsg!=null){
                    pMsg=null;
                }
                //String From = parser.parseData(msg, "from");
                int seq = 0;
                System.out.println("FIFO (Backup): waiting for the request in queue..");    
                try{
                bMsg = (String)q.rQueue.take();
                }catch(InterruptedException e){
                    System.out.println(e.getMessage());
                }

                System.out.println("FIFO (Backup): one message received..");    
                    if (parser.parseData(bMsg, "from").equals("servant")) {
                    //this is the transition time from backup to primary..
                    //put the message into primary loop's variable. then go out of the loop.
                    pMsg=bMsg;
                    q.setRecSeq(0);
                    q.setSendSeq(0);
                    noOfTry=1;
                    break;
                    }
                    //parse the seq no from the message.
                    n=Integer.parseInt(parser.parseData(bMsg, "seq_No"));
                    System.out.println("FIFO: Received sequence no is "+n);
                    
                    new_Seq = (q.getRecSeq() + 1) % MOD_VAlUE;

                //check the seq no of the message and compare it if it is ok
                //then ack or ack the previous one.
                if (n==q.getRecSeq()) {
                    //right packet arrived send the ack..
                    //build the message here......
                    //System.out.println("FIFO (Backup): great !! right message.go ahead..");    
                    String ackMessage=parser.BuildFIFOAck(String.valueOf(q.getRecSeq()));
                    //send the acknowladgement message
                    sender.SendUDPDatagram(ackMessage,aHost,param.getServerPortNo());
                    //now update the memory.
                    //TODO...................do the updatess.....*******************************************
                    if(parser.parseData(bMsg,"purpose").equals("deposit")){
                        int accountNo=Integer.parseInt(parser.parseData(bMsg,"accNo"));
                        float amount=Float.parseFloat(parser.parseData(bMsg,"amount"));
                        float newBalance=t.Deposit(accountNo,amount);
                        System.out.println("FIFO: deposit successful,The new balance is: "+newBalance);
                    }
                    else if(parser.parseData(bMsg,"purpose").equals("withdraw")){
                        int accountNo=Integer.parseInt(parser.parseData(bMsg,"accNo"));
                        float amount=Float.parseFloat(parser.parseData(bMsg,"amount"));
                        float newBalance=t.Withdraw(accountNo,amount);
			System.out.println("FIFO: withdraw successful,The new balance is: " +newBalance);
                        
                    }
                    noOfTry = 1;
                    q.setRecSeq(new_Seq);
                    //rec_Seq = new_Seq;
                    bMsg=null;
//                    firstTime = false;
                    continue;
                }
                if (n != q.getRecSeq()) {
                    noOfTry = noOfTry + 1;
                    //send the ack for the old seq no.
                   // System.out.println("FIFO (Backup): ooh wrong message.....");    
                    String ackMessage=parser.BuildFIFOAck(String.valueOf(rec_Seq));
                    sender.SendUDPDatagram(ackMessage,aHost,param.getServerPortNo());
                    continue;
                }
            
            }//end of BackUp Server loop.
                
}//main while loop
}//end of run
}//end of class
         
