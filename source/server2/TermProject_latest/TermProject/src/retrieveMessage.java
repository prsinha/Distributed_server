import java.util.Properties;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;

public class retrieveMessage {
    
    String msg;
    LoadAccountInfo ld;
    //Constructor
    public retrieveMessage(String msg) {
        this.msg = msg;
        ld = new LoadAccountInfo();
    }
    
    // runnable thread
    public void run() {
        int startIndex = 0;
        int endIndex = 0;
        int startIndex2 = 0;
        int endIndex2 = 0;
        String inputString = "";
        PrintWriter outputStream = null;
        //System.out.println("retriveve message class..."+msg);
        
        try {
            // Create a stream to output the extracted XML file information.
            outputStream = new PrintWriter(new FileOutputStream("AccountInfo.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Error opening file.");
        }
        
        // Extract the account and amount information and write them back to its original format.
        while (true) {
            endIndex = msg.indexOf("\n", startIndex);
            if (endIndex == -1)
                break;
            inputString = msg.substring(startIndex, endIndex);
            startIndex = endIndex + 1;
            
            startIndex2 = inputString.indexOf("<acc_no>");
            if (startIndex2 != -1) {
                endIndex2 = inputString.indexOf("</acc_no>");
                if (endIndex2 != -1) {
                    outputStream.print((inputString.substring(startIndex2 + 8, endIndex2)).trim());
                }
            }
            startIndex2 = inputString.indexOf("<amount>");
            if (startIndex2 != -1) {
                endIndex2 = inputString.indexOf("</amount>");
                if (endIndex2 != -1) {
                    outputStream.println(" " + (inputString.substring(startIndex2 + 8, endIndex2)).trim());
                    
                }
            }
        }
        
        // close input and output files
        outputStream.close();
        //now load the file into memory using the LoadAccountInfo
        ld.LoadAccountInfo();
    } // end run
    
} // end retrieveMessage