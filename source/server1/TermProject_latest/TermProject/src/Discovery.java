import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
/*
 * Discovery.java
 *
 * Created on November 25, 2007, 10:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
public class Discovery {
    
    UDPSender sender;
    XMLParser parser = new XMLParser();
    GParameters gp = GParameters.instance();
    private String BranchMonitorName;
    private String OtherServerName;
    private String RunMode;
    JavaHowTo color;
    
    /** Creates a new instance of Discovery */
    public Discovery() {
        color=new JavaHowTo();
    }
    
    public void discover_BM_OtherServer()
    {
       
        try {
             //1. send a message to the local daemon to ask the hostname of BranchMonitor
            BranchMonitorName = parser.BuildGetBMHostname();
            InetAddress localhostAddress = InetAddress.getLocalHost();
            sender = new UDPSender(localhostAddress, gp.getDaemonPortNo(), BranchMonitorName);
            sender.SendUDPDatagram();
            //wait until BMHostname is resolved and set in Global Parameters
            while (gp.getBMHostName().isEmpty()){
                System.out.println("*****************************************************");
                color.keepColors();
                color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                System.out.println("Resolving BranchMonitor's hostname...");
                color.restoreColors();
                Thread.sleep(1000);
            }
            color.keepColors();
            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
            System.out.println("Branch Monitor's host name resolved successfully.");
            System.out.print("BranchMonitor's hostname is:");
            color.restoreColors();
            color.keepColors();
            color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
            System.out.println("         "+ gp.getBMHostName());
            color.restoreColors();
            System.out.println("*****************************************************");
            //2. Now contact the BranchMonitor to ask the hostname of the other server Object
            OtherServerName = parser.BuildOtherServerNameRequest();
            InetAddress BMaddress = InetAddress.getByName(gp.getBMHostName());
			sender = new UDPSender(BMaddress, gp.getBranchMonitorPortNo(), OtherServerName);
            sender.SendUDPDatagram();
            //wait until OtherServerName is resolved and set in global Parameters
            while (gp.getOtherHostName().isEmpty()){
                color.keepColors();
                color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                System.out.println("Resolving other server's hostname...");
                color.restoreColors();
                Thread.sleep(1000);
            }
            color.keepColors();
            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
            System.out.println("The other server's host name resolved successfully.");
            System.out.print("Other server hostname is:");
            color.restoreColors();
            color.keepColors();
            color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
            System.out.println("          "+gp.getOtherHostName());
            color.restoreColors();
            System.out.println("*****************************************************");
            //3. send a message to the BranchMonitor to ask the RunMode
            RunMode = parser.BuildRunModeRequest();
			sender = new UDPSender(BMaddress, gp.getBranchMonitorPortNo(), RunMode);
            sender.SendUDPDatagram();
            //wait until BranchMonitor send a reply telling the runmode
            while (gp.getMode() == -1){
                color.keepColors();
                color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                System.out.println("Resolving Function Mode...");
                color.restoreColors();
                Thread.sleep(2000);
            }
            color.keepColors();
            color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
            System.out.println("Function mode resolved successfully!");
            color.restoreColors();
            
            if (gp.getMode()==0){
                //if primary, then run EP
                color.keepColors();
                color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
                System.out.print("The function mode is: ");
                color.restoreColors();
                color.keepColors();
                color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                System.out.println("    Primary");
                color.restoreColors();
                System.out.println("*****************************************************");
                EnablePrimary ep = new EnablePrimary();
                ep.start();
            }
           else //Run as Backup
           {
                color.keepColors();
                color.setColor(color.FOREGROUND_GREEN, color.BACKGROUND_BLACK);
                System.out.print("The function mode is: ");
                color.restoreColors();
                color.keepColors();
                color.setColor(color.FOREGROUND_RED, color.BACKGROUND_BLACK);
                System.out.println("    Backup");
                color.restoreColors();
                System.out.println("*****************************************************");
                DBSServant dbs = DBSServant.instance();
           }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        
        catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        catch (Exception e){}
        

    }
}
