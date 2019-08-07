/*
 * DBSClient.java
 *
 * Created on October 22, 2007, 7:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
import DBSApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import java.io.*;

public class DBSClient {
    
    /** Creates a new instance of DBSClient */
    public DBSClient() {
        super();
    }
    
    static DBSInterface dbsImpl;

  public static void main(String args[]){
      
      InputStreamReader streader = new InputStreamReader(System.in);
       BufferedReader br = new BufferedReader(streader);
       
     try{
        // create and initialize the ORB
	     ORB orb = ORB.init(args, null);

        // get the root naming context
        org.omg.CORBA.Object objRef = 
	     orb.resolve_initial_references("NameService");
        // Use NamingContextExt instead of NamingContext, 
        // part of the Interoperable naming Service.  
        NamingContextExt ncRef = 
          NamingContextExtHelper.narrow(objRef);
 
        // resolve the Object Reference in Naming
        String name = "BranchServer2";
        dbsImpl = 
          DBSInterfaceHelper.narrow(ncRef.resolve_str(name));

//        System.out.println
//          ("Obtained a handle on server object: " 
//            + dbsImpl);
        ////////////////////////////////////////////////////////////////////
        /*Now the client is ready to display the appropriate user interface and
         *prompt the user to start his operations
         */
        System.out.println("Welcome to DBS! Please choose the type of operation:");
        System.out.println("To Deposit an amount type 1");
        System.out.println("To withdraw an amount type 2");
        System.out.println("To transfer an amount type 3");
        System.out.println("To obtain the balance type 4 ");
        
        int operationType = Integer.parseInt(br.readLine());
        int accountNumber, srcAccountNumber, destAccountNumber;
        String amount;
        float realAmount;
        float newBalance;
        
            switch (operationType)
           {
                case 1 : 
                {
                   System.out.println("Please enter the account number");
                   accountNumber = Integer.valueOf(br.readLine().trim());
                   
                   System.out.println("Please enter the amount to deposit");
                   amount = br.readLine();
                   realAmount = Float.valueOf(amount.trim());
                   
                  newBalance = dbsImpl.deposit(accountNumber, realAmount); 
                 
                  if (newBalance != -1)
                    System.out.println("The new balance is :" + 
                          Float.toString(newBalance));
                  else 
                      System.out.println("Invalid Account number! Please try again");
                  break;
                    
                }
                
                case 2:
                {
                  System.out.println("Please enter the account number");
                  accountNumber = Integer.valueOf(br.readLine());
                   
                  System.out.println("Please enter the amount to withdraw");
                  amount = br.readLine();
                  realAmount = Float.valueOf(amount.trim());
                   
                  newBalance = dbsImpl.withdraw(accountNumber, realAmount);
                  
                  if (newBalance != -1) 
                    System.out.println("The new balance is :" + 
                          Float.toString(newBalance));
                  else 
                      System.out.println("Invalid Account number! Please try again");
                  break;
                }
                
                case 3:
                {
                    System.out.println("Please enter the source account number"); 
                    srcAccountNumber = Integer.valueOf(br.readLine());
                    
                    System.out.println("Please enter the destination account number");
                    destAccountNumber = Integer.valueOf(br.readLine());
                    
                    System.out.println("Please enter the amount to transfer");
                    amount = br.readLine();
                    realAmount = Float.valueOf(amount.trim());
                    
                    newBalance = dbsImpl.transfer(srcAccountNumber, 
                            destAccountNumber, realAmount);
                    
                    if (newBalance == -1)
                        System.out.println("Sorry!Insufficient fund to complete " +
                                "the transfer");
                    else
                        System.out.println("The new balance of the source account is:"
                            + Float.toString(newBalance));
                    break;
                }
                
                case 4:
                {
                   System.out.println("Please enter the account number");
                   accountNumber = Integer.valueOf(br.readLine());
                   
                  newBalance = dbsImpl.balance(accountNumber);
                  
                  if (newBalance != -1)
                    System.out.println("The balance is :" + 
                          Float.toString(newBalance));
                  else 
                      System.out.println("Invalid Account number! Please try again");
                  break;
                
                }
                
                default: System.out.println("Invalid entry");
                    
        
           }

     } 
     catch (Exception e) {
        System.out.println("ERROR : " + e) ;
	     e.printStackTrace(System.out);
	  } 
  } //end main
    
}
