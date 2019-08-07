import java.util.Properties;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;

public class dumpMessage {
    
    private ArrayList<AccountInfo> InfoList;
    private String host;
    private int port;
    private InetAddress address;
    GParameters gp;
    
    public dumpMessage(String host) {
        gp = GParameters.instance();
        port = gp.getServerPortNo();
        address = null;
        // this.InfoList = InfoList;
        this.host = host;
        
        //this.InfoList = gp.getInfoList();
    }
    
    public void run() {
        AccountInfo current;
        float amount;
        int i = 0;
        String msg = "";
        int ListSize = 0;
        
        try {
            while(gp.getInfoList().isEmpty()) {
                Thread.sleep(1000);
            }
            ListSize = gp.getInfoList().size();
            //System.out.println("Size = " + ListSize);
            msg = msg +"<xml>\n";
            msg = msg +"\t<type>ST</type>\n";
            msg = msg +"\t<purpose>reply</purpose>\n";
            msg = msg +"\t<no_of_clients>"+ListSize+"</no_of_clients>\n";
            while (i < ListSize) {
                current = (AccountInfo)gp.getInfoList().get(i);
                msg = msg + "\t<client " + (i + 1) + ">\n";
                msg = msg + "\t\t<acc_no> " + current.getAccountNo() + " </acc_no>\n";
                msg = msg + "\t\t<amount> " + current.getBalance() + " </amount>\n";
                msg = msg + "\t</client " + (i + 1) + ">\n";
                i++;
            }
            msg = msg + "</xml>";
        } catch (Exception e) {
            System.out.println(e);
        }
        
        // xml message ready for transfer
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        
        try {
            //System.out.print(msg);
            UDPSender sender = new UDPSender(address,port,msg);
            sender.SendUDPDatagram();
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }
    
}
