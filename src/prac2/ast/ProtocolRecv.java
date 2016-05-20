package prac2.ast;


import ast.protocols.tcp.TCPSegment;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class ProtocolRecv extends ProtocolBase{
    protected ReentrantLock lk;
    protected Condition cond;
    private TCPSegment segment;
    protected Thread thread;
    protected CircularQueue rcvQueue;
    protected int rcvSegConsumedBytes;
    
    public ProtocolRecv(Channel ch){
        super(ch);
        rcvQueue = new CircularQueue(20);
        rcvSegConsumedBytes = 0;
        thread = new Thread(new ReceiverTask());
        thread.start();
        this.lk = new ReentrantLock();
        this.cond = lk.newCondition();
    }
    
    public int receiveData(byte[] data, int offset, int length){
        lk.lock();
        int bytes_utilitzats = 0;
        try {
            if(!rcvQueue.empty()){
                bytes_utilitzats = consumeSegment(data, offset, length);
            }else{
                thread.sleep(1000);
            }
        }catch(InterruptedException e){
            
        } finally {
            lk.unlock();
            return bytes_utilitzats;
        }
    }
    
    protected int consumeSegment(byte[] buf, int offset, int length) {
        // assertion: !rcvQueue.empty() && rcvQueue.peekFirst().getDataLength() > rcvSegConsumedBytes
        TCPSegment segment = rcvQueue.peekFirst();
        // get data from seg and copy to receiveData's buffer
        int n = segment.getDataLength() - rcvSegConsumedBytes;
        if (n > length) {
            // receiveData's buffer is small. Consume a fragment of the received segment
            n = length;
        }
        // n == min(length, seg.getDataLength() - rcvSegConsumedBytes)
        System.arraycopy(segment.getData(), segment.getDataOffset() + rcvSegConsumedBytes, buf, offset, n);
        rcvSegConsumedBytes += n;
        if (rcvSegConsumedBytes == segment.getDataLength()) {
            // segment is totally consumed. Remove from rcvQueue
            rcvQueue.get();
            rcvSegConsumedBytes = 0;
        }
        return n;
    }
    
    protected void processReceivedSegment(TCPSegment rseg) {
        lk.lock();
        try {
            if(!rcvQueue.full()){
                rcvQueue.put(rseg);
            }
            //Aquí podria posar un else que digues quelcom com:
            // Descartem el segment ja que la cua està plena
        } finally {
            lk.unlock();
        }
    }
    
    class ReceiverTask implements Runnable {
        public void run() {
            while (true) {
                TCPSegment rseg = channel.receive();
                processReceivedSegment(rseg);
            }
        }
    }
}
