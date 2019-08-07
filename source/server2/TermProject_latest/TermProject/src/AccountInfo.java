/**
 *
 * @author Sinha
 */
public class AccountInfo {
    String name;
    int accountNo;
    float balance;

//    public AccountInfo(String name,int accountNo) {
//        this.name=name;
//        this.accountNo=accountNo;
//    }
    public AccountInfo(){
        
    }
    public AccountInfo(int accountNo,float balance) {
        this.accountNo=accountNo;
        this.balance=balance;
    }

    public float depositAmount(float damount) {
        synchronized(this){
            this.balance=this.balance+damount;
        }
        return this.balance;
    }
    public float withdrawAmount(float wamount){
        synchronized(this){
            this.balance=this.balance-wamount;
        }
        return this.balance;
    }
    int getAccountNo(){
        return this.accountNo;
        }
    void setAccountNo(int accountNo){
        this.accountNo=accountNo;
        }
    float getBalance(){
        synchronized (this){
            return this.balance;
        }
        }
    void setBalance(float balance){
        this.balance=balance;
        }
}
