
package prac3.ast;

import ast.protocols.tcp.TCPSegment;
import ast.util.CircularQueue;

/**
 * Socket for receiving endpoint.
 *
 * @author upcnet
 */
public class TSocketRecv extends TSocketBase {

    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcvSegUnc;

    /**
     * Create an endpoint bound to the local IP address and the given TCP port.
     * The local IP address is determined by the networking system.
     * @param ch
     */
    protected TSocketRecv(ProtocolRecv p, int localPort, int remotePort) {
        super(p, localPort, remotePort);
        rcvQueue = new CircularQueue<TCPSegment>(20);
        rcvSegUnc = 0;
    }

    /**
     * Places received data in buf
     */
    public int receiveData(byte[] buf, int offset, int length) throws InterruptedException {
        lk.lock();
        int bytes_utilitzats = 0;
        try {
            while (rcvQueue.empty()) {
                appCV.await();
            }
            while(length>bytes_utilitzats && !rcvQueue.empty()){
                int consu = consumeSegment(buf, offset+bytes_utilitzats, length-bytes_utilitzats);
                bytes_utilitzats +=  consu;
            }
            return bytes_utilitzats;
        } finally {
            lk.unlock();
            
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        // assertion: !rcvQueue.empty() && rcvQueue.peekFirst().getDataLength() > rcvSegConsumedBytes
        TCPSegment segment = rcvQueue.peekFirst();
        // get data from seg and copy to receiveData's buffer
        int n = segment.getDataLength() - rcvSegUnc;
        if (n > length) {
            // receiveData's buffer is small. Consume a fragment of the received segment
            n = length;
        }
        // n == min(length, seg.getDataLength() - rcvSegConsumedBytes)
        System.arraycopy(segment.getData(), segment.getDataOffset() + rcvSegUnc, buf, offset, n);
        rcvSegUnc += n;
        if (rcvSegUnc == segment.getDataLength()) {
            // segment is totally consumed. Remove from rcvQueue
            rcvQueue.get();
            rcvSegUnc = 0;
        }
        return n;
    }

    /**
     * Segment arrival.
     * @param rseg segment of received packet
     */
    protected void processReceivedSegment(TCPSegment rseg) {
        lk.lock();
        try {
            if(!rcvQueue.full()){
                rcvQueue.put(rseg);
                appCV.signal();
            }
            //Aquí podria posar un else que digues quelcom com:
            // Descartem el segment ja que la cua està plena
        } finally {
            lk.unlock();
        }
    }

}




