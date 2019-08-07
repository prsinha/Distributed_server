import java.io.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;

/*
 * BranchMonitor.java
 *
 * Created on November 25, 2007, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class BranchMonitor {
    
    /** Creates a new instance of BranchMonitor */
    GParameters gp;
    JavaHowTo jht; 
    public BranchMonitor() {
        gp = GParameters.instance();
        
        //prompt the user two enter the two host name on which he wants to
        //run the two server objects (Primary and Backup)
        String hostname1;   //string to keep hostname 1 entered in the commandline
        String hostname2;   //string to keep hostname 1 entered in the commandline
        InetAddress address1 = null;
        InetAddress address2 = null;
       
        InputStreamReader ins = new InputStreamReader(System.in);
        FileInputStream finstream;
        BufferedReader br = new BufferedReader(ins);
        
        FileOutputStream foutStream;
        DataOutputStream out;
        BufferedWriter buffwriter;
        
        ObjectCreator creator;
        jht = new JavaHowTo();
        
        //This block gets the two hostnames form the user and creates the batch file 
                
        try {
            //check the config file for the host names
            finstream = new FileInputStream("config.txt");
            DataInputStream in = new DataInputStream(finstream);
            BufferedReader fbr = new BufferedReader(new InputStreamReader(in));
            String strLine;
            if ((hostname1 = strLine = fbr.readLine()) != null) {
                hostname2 = fbr.readLine();
                fbr.close();
                //save to global parameters
                gp.setHostName1(hostname1);
                gp.setHostName2(hostname2);
                //set the failed parameter for later use
                //if the file is not empty, it means BranchMonitor has crashed before
                gp.setFailed();
            }
            //if config file is empty, prompt the user to enter the hostnames
            else{
                System.out.println("");
                jht.cls();
                jht.keepColors();
		jht.setColor(jht.FOREGROUND_WHITE, jht.BACKGROUND_RED);
                System.out.println("=============================== <BRANCH MONITOR1> ==============================");
                jht.restoreColors();
                System.out.println("The first host name you enter will run as the primary");
                System.out.print("Please enter the first host name:  ");
                hostname1 = br.readLine().trim();
                System.out.println("");
                System.out.println("The second host name you enter will run as the backup");
                System.out.print("Please enter the second host name:  ");
                hostname2 = br.readLine().trim();
                System.out.println("");
                jht.keepColors();
		jht.setColor(jht.FOREGROUND_WHITE, jht.BACKGROUND_RED);
                System.out.println("================================================================================");
                jht.restoreColors();
                //write the host names to config.txt for subsequent references
                foutStream = new FileOutputStream("config.txt");
                out = new DataOutputStream(foutStream);
                buffwriter = new BufferedWriter(new OutputStreamWriter(out));
                buffwriter.write(hostname1);
                buffwriter.newLine();
                buffwriter.write(hostname2);
                buffwriter.close();
                //save them in global parametrs
                gp.setHostName1(hostname1);
                gp.setHostName2(hostname2);
               // System.out.println(hostname1);
               // System.out.println(hostname2);
                address1 = InetAddress.getByName(hostname1);
                address2 = InetAddress.getByName(hostname2);
                //create the objects on designated hosts
                System.out.println("BranchMonitor is creating the " +
                        "objects on designated hosts...");
                //Now, BranchMonitor creates two server objects on the two hosts.
                //1.create the first server object
                creator = new ObjectCreator();
                creator.create(address1, gp.getDaemonPortNo());
                //create the second server object
                creator = new ObjectCreator();
                creator.create(address2, gp.getDaemonPortNo());
                System.out.println("objects created successfully");
            }
 
            UDPReceiver receiver = new UDPReceiver(gp.getBMPortNo());
            receiver.start();            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    

        
    }
    public static void main(String args[]){
    BranchMonitor bm=new BranchMonitor();
}
}