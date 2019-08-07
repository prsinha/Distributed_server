//import daemon.*;
import java.net.InetAddress;
/**
 *
 * @author Sinha
 */
public class XMLParser
{
    public String parseData(String xmlStr, String tag){
        String data;
        int start;

        String startTag="<"+tag+">";
        String endTag="</"+tag+">";
        try{
            if(!xmlStr.equals("") || !startTag.equals("")){
                if(xmlStr.indexOf(startTag) != -1){
                    start=xmlStr.indexOf(startTag)+startTag.length();
                    data=xmlStr.substring(start, xmlStr.indexOf(endTag));
                    return data;
                }
                return null;
            }
            else {
                System.out.println("Invalid input !!");
                return null;
            }
        }
        catch(Exception e){
            System.out.println("Invalid input !! \n" + e);
            return null;
        }
    }

    public String insertTag(String xmlStr,String tag,String iTag,String iTagValue){
        StringBuffer xmlStrBuf=new StringBuffer(xmlStr);
        String data;
        int start;
        String startTag="<"+tag+">";
        String endTag="</"+tag+">";
        try{
            if(!xmlStr.equals("") || !startTag.equals("")){
                if(xmlStr.indexOf(startTag) != -1){
                    start=xmlStr.indexOf(startTag)+startTag.length();
                    xmlStrBuf.insert(start,this.BuildTag(iTag,iTagValue));
                    data=xmlStrBuf.toString();
                    return data;
                }
                return null;
            }
            else {
                System.out.println("Invalid input !!");
                return null;
            }
        }
        catch(Exception e){
            System.out.println("Invalid input !! \n" + e);
            return null;
        }
    }
    public String changeTagValue(String xmlStr,String tag,String newValue){
        StringBuffer xmlStrBuf=new StringBuffer(xmlStr);
        String data;
        int start;
        int end;
        String startTag="<"+tag+">";
        //String endTag="</"+tag+">";
        try{
            if(!xmlStr.equals("") || !startTag.equals("")){
                if(xmlStr.indexOf(startTag) != -1){
                    start=xmlStr.indexOf(startTag)+startTag.length();
                    end=start+this.parseData(xmlStr, tag).length();
                    xmlStrBuf.replace(start, end, newValue);
                    //xmlStrBuf.insert(start,this.BuildTag(iTag,iTagValue));
                    data=xmlStrBuf.toString();
                    return data;
                }
                return null;
            }
            else {
                System.out.println("Invalid input !!");
                return null;
            }
        }
        catch(Exception e){
            System.out.println("Invalid input !! \n" + e);
            return null;
        }
    }    
    public String BuildTag(String TagName, String strValue)
    {
        return "<"+TagName+">"+strValue+"</"+TagName+">";
    }

    public String BuildDepositRequest(String accNo, String amount,String type)
    {
        return BuildTag("xml",(BuildTag("type",type)+BuildTag("purpose","deposit")
                +BuildTag("accNo",accNo)+ BuildTag("amount",amount)));
    }
    public String BuildWithdrawRequest(String accNo, String amount,String type)
    {
        return BuildTag("xml",(BuildTag("type",type)+BuildTag("purpose","withdraw")
                +BuildTag("accNo",accNo)+ BuildTag("amount",amount)));
    }
    public String BuildFDRequest()
    {
        return BuildTag("xml",(BuildTag("type","internal")+BuildTag("purpose","fd")
                +BuildTag("req/rep","request")));
    }
    public String BuildFDRereply()
    {
        return BuildTag("xml",(BuildTag("type","internal")+BuildTag("purpose","fd")
                +BuildTag("req/rep","reply")));
    }

    

    public String BuildTransferRequest(String accNo, String amount)
    {
        return BuildTag("xml",(BuildTag("type","external")+BuildTag("purpose","transfer")
                +BuildTag("accNo",accNo)+ BuildTag("amount",amount)));
    }
    public String BuildStateTransferRequest()
    {
        return BuildTag("xml",(BuildTag("type","ST")+BuildTag("purpose","request")));
    }
    //This message is used by the BranchMonitor. BranchMonitor sends this request to 
    //daemon thereby asking it to run a Primary server
    public String BuildPrimaryRequest()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value","Primary")));
    }
    //This message is used by the BranchMonitor. BranchMonitor sends this request to 
    //daemon thereby asking it to run a Backup server
    public String BuildBackupRequest()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value","Backup")));
    }
    //This message is used by the BranchMonitor. BranchMonitor sends this message 
    //as an acknowledgement to the survivng object's request for creating a new
    //backup.
    public String BuildAck()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value", "BMAck")));
    }
        
    //This message is used by the surviving server object (primary or backup). The server
    //sends this request to daemon thereby asking it to run a BranchMonitor
    public String BuildBranchMonitorRequest()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value","BranchMonitor")));
    }
    //This message is used by both server objects (primary and backup). The server
    //sends this request to daemon thereby asking it to return the hostname of BranchMonitor
    //for communication
    public String BuildGetBMHostname()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value","GetBMHostName")));
    }
    //This message is sent by daemon in response to server's request of the BranchMonitor hostname
    //upon startup
    public String BuildHostNameReply(String hostname)
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value", "BMHostName")+BuildTag("content",hostname)));
    }
    //This message is used by both server objects. Each server object sends this request to
    //BranchMonitor to ask the host name on which the other server object is running.
    public String BuildOtherServerNameRequest()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value", "OtherServerNameRequest")));
    }
    //This message is used by the BranchMonitor. BranchMonitor sends this message as a reply
    //to each of the serever object's request for the name of the other object. It contains
    //the hostname on which the requested server object resides.
    public String BuildOtherServerNameReply(String hostname)
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value", "OtherServerNameReply")+
                BuildTag("content", hostname)));
    }
    //This message is used by server objects. Server objects send this message to the BranchMonitor thereby
    //asking it the RunMode(whether to run as primary or backup0
    public String BuildRunModeRequest()
    {
        return BuildTag("xml", (BuildTag("type", "control")+BuildTag("value", "RunMode")));
    }
    //This message is used by the BranchMonitor. BranchMonitor sends this message to server objects as a reply
    //to RunMode request. It tells the server object to configure itself as primary. (in the beginning)
    public String BuildRunAsPrimary()
    {
        return BuildTag("xml",(BuildTag("type", "control")+BuildTag("value", "RunAsPrimary")));
    }
    //This message is used by the BranchMonitor. BranchMonitor sends this message to server objects as a reply
    //to RunMode request. It tells the server object to configure itself as primary. (in the beginning)
    public String BuildRunAsBackup()
    {
        return BuildTag("xml",(BuildTag("type", "control")+BuildTag("value", "RunAsBackup")));
    }
///////////////////////////////////////////////////
    
    public String BuildFIFOAck(String seq_No)
    {
        return BuildTag("xml", (BuildTag("type", "FIFO")+BuildTag("purpose", "Ack")+BuildTag("seq_No",seq_No)));
    }
    public String BuildFIFODepositRequest(String acc_No,String amount)
    {
        return BuildTag("xml", (BuildTag("type", "FIFO")+BuildTag("from", "servant")+BuildTag("purpose","deposit")+
                BuildTag("accNo",acc_No)+ BuildTag("amount",amount)));
    }
    public String BuildFIFOWithdrawRequest(String acc_No,String amount)
    {
        return BuildTag("xml", (BuildTag("type", "FIFO")+BuildTag("from", "servant")+BuildTag("purpose","withdraw")+
                BuildTag("accNo",acc_No)+ BuildTag("amount",amount)));
    }
/*************************************Transfer messages*******************************************/    
    public String BuildTransferRequest(String acc_No,String amount, String ownHostName)
    {
        return BuildTag("xml", (BuildTag("type", "transfer")+BuildTag("value", "request")+BuildTag("sender", ownHostName)
        +BuildTag("accNo",acc_No)+BuildTag("amount",amount)));
    }
    public String BuildTransferReplyAck()
    {
        return BuildTag("xml", (BuildTag("type", "transfer")+BuildTag("value", "reply")+BuildTag("content", "Ack")));
    }
    public String BuildTransferReplyNack(String altHostName)
    {
        return BuildTag("xml", (BuildTag("type", "transfer")+BuildTag("value", "reply")+BuildTag("content", "Nack"))+
                BuildTag("altHost",altHostName));
    }


}
