/*
 * DBSServant.java
 *
 * Created on October 22, 2007, 4:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */

import DBSApp.*;
import org.omg.CORBA_2_3.ORB;
import org.omg.CosNaming.*;
import java.util.*;
import java.net.*;
import java.lang.Thread.*;



public class DBSServant extends DBSInterfacePOA {
    
    private org.omg.CORBA.ORB orb;
    private String pathString;
    //private Account[] accounts;
    private int accountsLength;
     static private DBSServant _instance = null;
     LoadAccountInfo ld;
     GParameters gp;
     FIFOQueue fQueue;
     
     XMLParser parser = new XMLParser();
     UDPSender sender;
     Transaction t;
     
    
    public void setORB(org.omg.CORBA.ORB orb_val){
        orb = orb_val;
       }
    
            
    /** Creates a new instance of DBSServant ,
    * and also creates an instance of startup which initializez the database
    * by reading the accounts info and storing them in an array in the memory
    */
    protected DBSServant() 
    {
        fQueue=FIFOQueue.getFIFOQueue();
        t=new Transaction();
        gp = GParameters.instance();
        gp.getInfoList().clear();
        if (gp.getMode()==0)//if it is running in Primary mode
        {
        ld = new LoadAccountInfo();
        ld.LoadAccountInfo();
        System.out.println("account info loaded");
        }
             
    }
    
    static public DBSServant instance() {
      if(null == _instance) {
         _instance = new DBSServant();
      }
      return _instance;
   }
        
    
    public float deposit(int acnt, float amt) {
        
        /*find the corresponding record for the account number acnt
         *in the arraylist "accounts" and add amt to its balance field
         *Then return the new balance
         */

        int i=0;
        float newAmount=0;
        AccountInfo current;
        while(i < gp.getInfoList().size()){
            current = (AccountInfo)gp.getInfoList().get(i);
            if (current.getAccountNo()==acnt)
                newAmount = current.depositAmount(amt);
            ++i;
        }
        if (i<gp.getInfoList().size()+1)
        {
            try {
                fQueue.rQueue.put(parser.BuildFIFODepositRequest(String.valueOf(acnt),String.valueOf(amt)));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return(newAmount);
        }
        
        return(-1);
    }
    
    public float withdraw(int acnt, float amt) {
        /*find the corresponding record for the account number acnt
         *in the arraylist "accounts" and subtract amt from its balance field
         *Then return the new balance
         */
        
        int i=0;
        float newAmount=0;
        AccountInfo current;
        while(i < gp.getInfoList().size()){
            current = (AccountInfo)gp.getInfoList().get(i);
            if (current.getAccountNo()==acnt)
                newAmount = current.withdrawAmount(amt);
           
            ++i;
        }
        if (i<gp.getInfoList().size()+1)
        {
            try {
                fQueue.rQueue.put(parser.BuildFIFOWithdrawRequest(String.valueOf(acnt),String.valueOf(amt)));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            return(newAmount);
        }
        
        return(-1);
        
    }
    
    public float balance (int acnt)
    {
        /*find the corresponding record for the account number acnt 
         *in the arraylist "accounts" and return its balance field
         */
       int i=0;
        float newAmount=0;
        AccountInfo current;
//
        while(i < gp.getInfoList().size()){
            current = (AccountInfo)gp.getInfoList().get(i);
            if (current.getAccountNo()==acnt)
                newAmount = current.getBalance();
            ++i;
        }
        if (i<gp.getInfoList().size()+1)
            return(newAmount);
        
        return(-1);
    }
    
    public float transfer (int src_acnt, int dest_acnt, float amt)
    {
        /*Find the source account in the array "accounts", then, lock it and
         *send a message to the server branch having the destination account 
         */
        return t.Transfer( src_acnt,  dest_acnt,  amt);
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
