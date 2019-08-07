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
    public String hostname1 = "";
    public String hostname2 = ""; 
    public int round =1 ;
    private boolean failed=false;
    /** Creates a new instance of GParameters */
   protected GParameters() {
        
    }

   
   static public GParameters instance() {
      if(null == _instance) {
         _instance = new GParameters();
      }
      return _instance;
   }
   public String getHostName1()
   {
       return this.hostname1;
   }
   public void setHostName1 (String hostname)
   {
       this.hostname1 = hostname;
   }
    public String getHostName2()
   {
       return this.hostname2;
   }
   public void setHostName2 (String hostname)
   {
       this.hostname2 = hostname;
   }
   public void RaiseRound ()
   {
       this.round = ++ round;
   }
   public int getRound()
   {
       return this.round;
   }
   public void setFailed()
   {
       this.failed = true;
   }
   public boolean getFailed()
   {
       return this.failed;
   }
   
      
}
