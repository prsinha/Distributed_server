/*
 * GParameters.java
 *
 * Created on November 17, 2007, 7:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author h_kassae
 */
public class GParameters {
    private int primary=-1;
    private String BMHostname = "";
    
   //these are private variables accessed by getter() and setter(); 
    /***************************************************************************/
    private int BMPortNo=8888;
    private int ServerPortNo=7777;
    private int DaemonPortNo=5555;
    private int FDPort=6666;
    private String OtherBranchServer="gambia.encs.concordia.ca";
    private int OtherBranchServerPort=24444;
    /***************************************************************************/
    
    private String OtherServerName = "";
    static private GParameters _instance = null;
    
    private String[] ORBParam;
    private boolean FDS_Flag = false;
    private boolean FDR_Flag = false;
    private boolean FD_Flag = false;
    
    public BlockingQueue FD1Queue;
    public BlockingQueue FD2Queue;
    public BlockingQueue BMQueue;
    
    //array to keep all the account objects
    private ArrayList<AccountInfo> InfoList = new ArrayList();
    /** Creates a new instance of GParameters */
   protected GParameters() {
        FD1Queue=new LinkedBlockingQueue(5);
        FD2Queue=new LinkedBlockingQueue(5);
        BMQueue=new LinkedBlockingQueue(5);
       
    }

   
   static public GParameters instance() {
      if(null == _instance) {
         _instance = new GParameters();
      }
      return _instance;
   }
   public void setBMHostName(String bmHostName){
       this.BMHostname=bmHostName;
   }
   public String getBMHostName(){
       return this.BMHostname;
   }
   public void setOtherServerName(String otherServerName){
       this.OtherServerName=otherServerName;
   }
   public String getOtherHostName(){
       return this.OtherServerName;
   }
   public int getServerPortNo(){
       return this.ServerPortNo;
   }
   public int getDaemonPortNo(){
       return this.DaemonPortNo;
   }
   public int getFDPortNo(){
       return this.FDPort;
   }

   public int getBranchMonitorPortNo(){
       return this.BMPortNo;
   }

   public void setMode(int a){
        this.primary = a;
    }
   public int getMode(){
       return this.primary;
   }
   public void setORBParam(String[] args )
   {
       this.ORBParam = args;
   }
   public String[] getORBParam()
   {
       return this.ORBParam;
   }
   public void set_FDS_Flag()
   {
       this.FDS_Flag = true;
   }
   public boolean get_FDS_Flag()
   {
       return this.FDS_Flag;
   }
   public void set_FDR_Flag()
   {
       this.FDR_Flag = true;
   }
   public boolean get_FDR_Flag()
   {
       return this.FDR_Flag;
   }
   
   public void set_FD_Flag()
   {
       this.FD_Flag = true;
   }
   public boolean get_FD_Flag()
   {
       return this.FD_Flag;
   }
   public ArrayList getInfoList()
   {
       return this.InfoList;
   }
   public String getOtherBranchServer()
   {
       return this.OtherBranchServer;
   }
   public void setOtherBranchServer(String name)
   {
        this.OtherBranchServer=name;
   }
   public int getOtherBranchServerPort()
   {
       return this.OtherBranchServerPort;
   }
   public void setOtherBranchServerPort(int portNo)
   {
        this.OtherBranchServerPort=portNo;
   }

}
