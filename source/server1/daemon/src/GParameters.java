/*
 * GParameters.java
 *
 * Created on November 17, 2007, 7:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */




/**
 *
 * @author h_kassae
 */
public class GParameters {
     
    static private GParameters _instance = null;
    public String BMHostName = "";
    private int daemonPortNo = 5555;
    private int serverPortNo = 7777;
    private int BMPortNo = 8888;
     
    /** Creates a new instance of GParameters */
   protected GParameters() {
        
    }

   
   static public GParameters instance() {
      if(null == _instance) {
         _instance = new GParameters();
      }
      return _instance;
   }
   public String getBMHostName()
   {
       return this.BMHostName;
   }
   public void setBMHostName (String bmhostname)
   {
       this.BMHostName = bmhostname;
   }
   public void setDaemonPortNo(int portNo)
   {
       this.daemonPortNo = portNo;
   }
   public int getDaemonPortNo()
   {
       return this.daemonPortNo;
   }
    public void setServerPortNo(int portNo)
   {
       this.serverPortNo = portNo;
   }
   public int getServerPortNo()
   {
       return this.serverPortNo;
   }
   public void setBMPortNo(int portNo)
   {
       this.BMPortNo = portNo;
   }
   public int getBMPortNo()
   {
       return this.BMPortNo;
   }
   
      
}
