/*
 * LoadAccountInfo.java
 *
 * Created on November 29, 2007, 11:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author h_kassae
 */
import java.util.*;
import java.io.*;
public class LoadAccountInfo {
    GParameters gp;
    
    /** Creates a new instance of LoadAccountInfo */
    public LoadAccountInfo() {
        gp = GParameters.instance();
    }
    
     public void LoadAccountInfo(){
        
        int i = 0;
        
        gp.getInfoList().clear();
        
        Scanner inputStream = null;
        
        try {
            inputStream = new Scanner(new FileInputStream("AccountInfo.txt"));
        } catch (FileNotFoundException e) {
        }
        
        while (inputStream.hasNextLine()) {
            if (inputStream.hasNextInt()) {
                //System.out.println(inputStream.nextInt() + " " + inputStream.nextFloat());
                gp.getInfoList().add(new AccountInfo(inputStream.nextInt(), inputStream.nextFloat()));
            } else {
                inputStream.nextLine();
            }
        }
        
        inputStream.close();
    }
    
}
