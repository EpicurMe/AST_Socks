
package prac5;

import ast.logging.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ast.protocols.tcp.TCPSegment;

import static java.lang.Math.min;
/**
 * @author AST's teachers
 */
public class TSocket {
    public static Log log = Protocol.log;

    protected Protocol proto;
    protected Lock lk;
    protected Condition sender;
    protected Condition receiver;

    protected int localPort;
    protected int remotePort;
    
    // Sender variables:
    protected int sndMSS;       // Send maximum segment size
    protected boolean sndIsUna; // segment not yet acknowledged ?

    // Receiver variables:
    protected TCPSegment rcvSegment;
    protected int rcvSegUnc;


    /**
     * Create an endpoint bound to the given TCP ports.
     */
    protected TSocket(Protocol p, int localPort, int remotePort) {
        lk = new ReentrantLock();
        sender = lk.newCondition();
        receiver = lk.newCondition();
        proto = p;
        this.localPort = localPort;
        this.remotePort = remotePort;
        // init sender variables
        sndMSS = p.net.getMMS() - TCPSegment.HEADER_SIZE; // IP maximum message size - TCP header size
        sndIsUna = false;
        // init receiver variables
        rcvSegment = null;
        rcvSegUnc = 0;
    }


    // -------------  SENDER PART  ---------------
    public void sendData(byte[] data, int offset, int length) {
        lk.lock();
        try {
            log.debug("%s->sendData(length=%d)", this, length);
            // A completar per l'estudiant:
            TCPSegment segment = new TCPSegment();
            int s=0;
            while(s<length){
                while(sndIsUna = true){
                    sender.await();
                }
                int l = min(length-s, sndMSS);
                segment = segmentize(data, offset+s, l);
                segment.setPorts(localPort, remotePort);
                sendSegment(segment);
                s=s+l;
            }
            // for each segment to send
                // wait until the sender is not expecting an acknowledgement
                // create a data segment and send it
        } catch (InterruptedException ex) {
            log.error(ex);
        } finally {
            lk.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        int i=offset;
        byte[] nova_array = new byte[length-offset];
        while(i < length-offset){
            nova_array[i] = data[i+offset];
            i++;
        }
        //Afegim les noves dades al nou segment
        seg.setData(nova_array, 0, length-offset);
        return seg;
    }

    protected void sendSegment(TCPSegment segment) {
        log.debug("%s->sendSegment(%s)", this, segment);
        proto.net.send(segment);
    }


    // -------------  RECEIVER PART  ---------------
    /**
     * Places received data in buf
     */
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lk.lock();
        try {
            log.debug("%s->receiveData(maxlen=%d)", this, maxlen);
            lk.lock();
            int bytes_utilitzats = 0;
            while (rcvSegment == null) {
                receiver.await();
            }
            while(maxlen>bytes_utilitzats){
                int consu = consumeSegment(buf, offset+bytes_utilitzats, maxlen-bytes_utilitzats);
                bytes_utilitzats +=  consu;
            }
            return bytes_utilitzats;
            // wait until it's a received segment
            // get data from the received segment and decide when to send an ACK
        } catch (InterruptedException ex) {
            log.error(ex);
            return 0;
        } finally {
            lk.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int maxlen) {
        // assertion: rcvSegment != null && rcvSegment.getDataLength() > rcvSegUnc
        // get data from rcvSegment and copy to receiveData's buffer
        int n = rcvSegment.getDataLength() - rcvSegUnc;
        if (n > maxlen) {
            // receiveData's buffer is small. Consume a fragment of the received segment
            n = maxlen;
        }
        // n == min(length, rcvSegment.getDataLength() - rcvSegUnc)
        System.arraycopy(rcvSegment.getData(), rcvSegment.getDataOffset() + rcvSegUnc, buf, offset, n);
        rcvSegUnc += n;
        if (rcvSegUnc == rcvSegment.getDataLength()) {
            // rcvSegment is totally consumed. Remove it
            rcvSegment = null;
            rcvSegUnc = 0;
        }
        return n;
    }

    protected void sendAck() {
        TCPSegment ack = new TCPSegment();
        ack.setSourcePort(localPort);
        ack.setDestinationPort(remotePort);
        ack.setFlags(TCPSegment.ACK);
        log.debug("%s->sendAck(%s)", this, ack);
	proto.net.send(ack);
    }


    // -------------  SEGMENT ARRIVAL  -------------
    /**
     * Segment arrival.
     * @param rseg segment of received packet
     */
    protected void processReceivedSegment(TCPSegment rseg) {
        lk.lock();
        try {
            // Check ACK
            if (rseg.isAck()) {
                sndIsUna = false;
                logDebugState();
            } else if (rseg.getDataLength() > 0) {
                // Process segment data
                if (rcvSegment != null) {
                    log.warn("%s->processReceivedSegment: no free space: %d lost bytes",
                                this, rseg.getDataLength());
                    return;
                }
                rcvSegment = rseg;
                receiver.signal();
                logDebugState();
            }
        } finally {
            lk.unlock();
        }
    }


    // -------------  LOG SUPPORT  ---------------
    protected void logDebugState() {
        if (log.debugEnabled()) {
            log.debug("%s=> state: %s", this, stateToString());
        }
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(proto.net.getAddr()).append("/{local=").append(localPort);
        buf.append(",remote=").append(remotePort).append("}");
        return buf.toString(); 
    }

    public String stateToString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{sndIsUna=").append(sndIsUna);
        if (rcvSegment == null) {
            buf.append(",rcvSegment=null");
        } else {
            buf.append(",rcvSegment.dataLength=").append(rcvSegment.getDataLength());
            buf.append(",rcvSegUnc=").append(rcvSegUnc);
        }
        return buf.append("}").toString();
    }

}
