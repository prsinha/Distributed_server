/*
 * EnablePrimary.java
 *
 * Created on November 25, 2007, 11:20 PM
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
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import java.util.Properties;
import java.io.*;

public class EnablePrimary extends Thread{
    
    GParameters gp = GParameters.instance();
    String[] args;
    JavaHowTo color;
    
    /** Creates a new instance of EnablePrimary */
    public EnablePrimary() {
        gp.setMode(0);
        color=new JavaHowTo();
        //System.arraycopy(this.args,0,args,0,args.length);
    }
    
    public void run()
    {
         try{
      //creare and initialize the ORB
           
           org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init(gp.getORBParam(), null);
          // System.out.println("here :"+ args[0]+"==="+args[1]+"==="+args[2]+"==="+args[3]);
            
            //get reference to rootpoa & activate the POAManager
            
            POA rootpa = (POA) orb.resolve_initial_references("RootPOA");
            rootpa.the_POAManager().activate();
            
            //create servant and register it with the ORB
            //Also gets the database path from the admin to initialize the servant
            
            // Prompts for the database path
            //System.out.println("Enter the database path for this branch server:");
            //databasePath = "G:/My Documents/JavaPractices/DBS_CORBA/accounts.txt";//(br.readLine());
            //System.out.println(databasePath);
            DBSServant dbs = DBSServant.instance();
            dbs.setORB(orb);
            //get object reference from the servant
            
            org.omg.CORBA.Object ref = rootpa.servant_to_reference(dbs);
            
            //and cast the reference to a CORBA reference
            
            DBSInterface dref = DBSInterfaceHelper.narrow(ref);
            
            //get the root naming context
            
            org.omg.CORBA.Object objref = orb.resolve_initial_references("NameService");
            
            //Use NamingContextExt, which is part of the interoperable Naming
            //service (INS) specification.
            
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objref);
            
            //bind the object reference in naming
            
            String name = "BranchServer2";
            NameComponent path[] = ncRef.to_name(name);
            
            ncRef.rebind(path,dref);
            //if Failure Detection Module is not running, then run it
//            if (! gp.get_FDS_Flag()){
//                gp.set_FDS_Flag();
//                FailureDetectionSender FDS = new FailureDetectionSender(gp.getOtherHostName(), 7777);
//                FDS.start();
//            }
//            if (! gp.get_FDR_Flag()){
//                gp.set_FDR_Flag();
//                FailureDetectionReceiver FDR = new FailureDetectionReceiver(gp.getOtherHostName(), 7777);
//                FDR.start();
//            }
            //if Failure Detection Module is not running, then run it
//            if (! gp.get_FD_Flag()){
//                FailureDetection fd = new FailureDetection();
//                fd.start();
//            }
            System.out.println("------------------------------------------------------");
            color.keepColors();
            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
            System.out.println("Branch Server2 ready and waiting for client requests...");
            color.restoreColors();
            System.out.println("-------------------------------------------------------");
            
            
            
            //wait for invocations
            orb.run();
                 
            
    } 
	
      catch (Exception e) {
        System.err.println("ERROR: " + e);
        e.printStackTrace(System.out);
      }
	  
      System.out.println("DBS Server Exiting ...");
    }
}
