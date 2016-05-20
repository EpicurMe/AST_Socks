package prac2.ast;


import ast.protocols.tcp.TCPSegment;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Channel {
    private CircularQueue cua = new CircularQueue(25);
    private double lossRatio;
    protected ReentrantLock lk;
    protected Condition cond;
    public static final int MAX_MSG_SIZE = 1480; //Link MTU - IP Header
    
    public Channel() {
        lk = new ReentrantLock();
        cond = lk.newCondition();
        lossRatio = 0;
    }
    
    public Channel(double lossRatio) {
        lk = new ReentrantLock();
        cond = lk.newCondition();
        this.lossRatio = lossRatio;
    }
    
    public int getMMS(){
        return MAX_MSG_SIZE;
    }
    public void send(TCPSegment seg) { 
        while(this.cua.full()){
            try{
              cond.await();  
            } catch (InterruptedException ex) {
                Logger.getLogger(Channel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        double factorPerdues = (double) Math.random();
        if(factorPerdues < this.lossRatio){
            return;
        } else {
            this.cua.put(seg);
        }
    }
    public TCPSegment receive() { 
        TCPSegment temp = this.cua.get();
//        cond.signal();
        return temp;
    }
}
