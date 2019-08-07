
//import daemon.*;
import java.net.*;
import java.io.*;
/**
 *
 * @author h_kassae
 */
public class UDPSender {
    InetAddress address;
    int portNo;
    DatagramSocket aSocket = null;
    String msg;
    public UDPSender()throws SocketException{
        this.address = address;
        this.portNo=portNo;
        aSocket = new DatagramSocket();
        
    
    }
//this constructor will receive the IP address, port number and the sending message.
    public UDPSender(InetAddress address, int portNo,String message) throws SocketException{
        this.address = address;
        this.portNo=portNo;
        aSocket = new DatagramSocket();
        msg=message;
        
    }
    public void CloseSocket(){
        if(aSocket!=null){
            aSocket.close();
        }
    }

    public void SendUDPDatagram() {
        try {
            byte[] m = msg.getBytes();
            DatagramPacket request =new DatagramPacket(m, m.length, address, portNo);
            aSocket.send(request);
          

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

}

