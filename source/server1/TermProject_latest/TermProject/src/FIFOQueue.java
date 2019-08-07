/**
 *
 * @author p_sinh
 */
import java.util.concurrent.*;

public class FIFOQueue{
    public BlockingQueue rQueue;
    public BlockingQueue aQueue;
    public BlockingQueue tQueue;
    private static FIFOQueue p;
    private int send_Seq=0;
    private int rec_Seq=0;
    
    void setSendSeq(int i){
        this.send_Seq=i;
    }
    int getSendSeq(){
        return this.send_Seq;
    }
    
    
    void setRecSeq(int i){
        this.rec_Seq=i;
    }
    int getRecSeq(){
        return this.rec_Seq;
    }
    
    public FIFOQueue(){
        this.rQueue=new LinkedBlockingQueue();
        this.aQueue=new LinkedBlockingQueue();
        this.tQueue=new LinkedBlockingQueue();
    }
    
    public static FIFOQueue getFIFOQueue() {
        if (p == null) {
            p = new FIFOQueue();
        }
        return p;
    }
    
}