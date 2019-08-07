import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
/*
 * Transaction.java
 *
 * Created on November 30, 2007, 4:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author p_sinh
 */
public class Transaction {
    GParameters gp;
    UDPSender sender;
    XMLParser parser;
    FIFOQueue fq;
    JavaHowTo jht;
    
    /** Creates a new instance of Transaction */
    public Transaction() {
        gp=GParameters.instance();
         jht= new JavaHowTo();
        fq=FIFOQueue.getFIFOQueue();
        parser=new XMLParser();
        try {
            sender=new UDPSender();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    public float Deposit(int account_No, float amount){
        int pos=FindPosition(account_No);
        if(pos==-1){
            return pos;
        }
        AccountInfo account=(AccountInfo)gp.getInfoList().get(pos);
        float f=account.depositAmount(amount);
        return f;
    }
    public float Withdraw(int account_No, float amount){
        int pos=FindPosition(account_No);
        if(pos==-1){
            return pos;
        }
        AccountInfo account=(AccountInfo)gp.getInfoList().get(pos);
        float f=account.withdrawAmount(amount);
        return f;
    }
    
    public float Transfer(int srcAccountNo, int destAccountNo,float amount){
        int pos=FindPosition(srcAccountNo);
        if (pos == -1) {
            return -1;
        }
        float amountW = amount;
        AccountInfo account=(AccountInfo)gp.getInfoList().get(pos);
        Float newBal=account.getBalance();
        if(newBal<amountW){
            System.out.println("NO sufficient balance");
            return -1;
        }
        //he has sufficient amount...
        float newBalacne=account.withdrawAmount(amount);
        System.out.println("Transaction: Withdrawed successfully......");
        String queryMessage=parser.BuildTransferRequest(String.valueOf(destAccountNo),String.valueOf(amount),gp.getOtherBranchServer());
        InetAddress address;
        try {
            address = InetAddress.getByName(gp.getOtherBranchServer());
            sender.SendUDPDatagram(queryMessage,address ,gp.getOtherBranchServerPort());
            System.out.println("Transaction: request sent to the other server.....");
            //here it should wait 10 seconds for the ack to come in the queue.
            System.out.println("Transaction: waiting for the ack from the other branch.....");
            String st=(String)fq.tQueue.poll(8,TimeUnit.SECONDS);
            //System.out.println("Transaction: queue element is :"+st);
            if(st.equals(null)){
                //roll back withdraw...
                System.out.println("Transaction: Ack time out..rollback now.....");
                account.depositAmount(amount);
                return -1;
            }
            //System.out.println("Transaction: Ack received..");
            String ack=parser.parseData(st,"value");
            //System.out.println("Transaction: received reply is :"+ack);
            if(ack.equals("reply")){
                if(parser.parseData(st,"content").equals("Ack")){
                    //means other branch server successfully handle the transfer request.
                }else{
                    //we get the address of the other primary branch servers address.
                    String altHost=parser.parseData(st,"altHost");
                    System.out.println("Transaction: Got the address of the Primary .."+altHost);
                    address=InetAddress.getByName(altHost);
                    sender.SendUDPDatagram(queryMessage,address ,gp.getOtherBranchServerPort());
                    System.out.println("Transaction: waiting for the Ack.....");
                    String s=(String)fq.tQueue.poll(5,TimeUnit.SECONDS);
                    if(s.equals(null)){
                        //rollback because unsuccessfull
                        System.out.println("Transaction: Ack Timeout.....");
                        account.depositAmount(amount);
                        return -1;
                    }
                }
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        jht.keepColors();
        jht.setColor(jht.FOREGROUND_BLUE, jht.BACKGROUND_BLACK);
        System.out.println("Transaction: Transfer successful.....");
        jht.restoreColors();


        return newBalacne;
    }
    public void handleTransferRequets(String message)
    {   
        System.out.println("Transaction: inside handle transfer request...");
        String senderHostName = parser.parseData(message,"sender");
        InetAddress senderAddress = null;
        try {
            senderAddress = InetAddress.getByName(senderHostName);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        if (gp.getMode()==1)//if in backup mode
        {
            //send a Nack thereby redirecting the sender to the other host
            String Nack = parser.BuildTransferReplyNack(gp.getOtherHostName());
            System.out.println("Transaction: NAck send.....");
            sender.SendUDPDatagram(Nack, senderAddress,gp.getOtherBranchServerPort());
            
            System.out.println("message sent back to :"+senderHostName);
            System.out.println("Transaction:NAK:message should be sent to :"+gp.getOtherHostName());
            
        }else//in primary mode
        {
            //send an ack
            int acc=Integer.parseInt(parser.parseData(message,"accNo"));
            float f=Float.parseFloat(parser.parseData(message,"amount"));
            System.out.println("Transaction: now we are going to deposit.account no is :"+acc+"and amount is :"+f);
            float newBalance=this.Deposit(acc,f);
            System.out.println("Transacetion: Deposited..new balance is "+newBalance);
            String Ack = parser.BuildTransferReplyAck();
            System.out.println("Transaction: Ack send.....");
            sender.SendUDPDatagram(Ack, senderAddress, gp.getOtherBranchServerPort());
        }
    }

    private int FindPosition(int acnt){
        int accountNo=acnt;
        for(int i=0;i<gp.getInfoList().size();i++){
            AccountInfo account=(AccountInfo)gp.getInfoList().get(i);
            if(account.accountNo==accountNo)
                return i;
        }
        System.out.println("Invalid No !! There is no Account of account no : " + accountNo);
        return -1;
    }

}
