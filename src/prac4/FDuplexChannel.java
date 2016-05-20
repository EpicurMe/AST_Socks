
package prac4;

import ast.protocols.tcp.TCPSegment;

import ast.util.CircularQueue;


import ast.logging.Log;
import ast.logging.LogFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Full-duplex channel
 *
 */
public class FDuplexChannel {
    public static Log log = LogFactory.getLog(FDuplexChannel.class);

    public static final int MAX_MSG_SIZE = 1480; // Link MTU - IP header
    public static final int QUEUE_SIZE = 8;

    protected Peer left, right;
    protected double t_rate;	// in segments per second
    protected double lossRatio;	// in [0, 1)

    public FDuplexChannel(double rate, double lossRatio) {
        this.t_rate = rate;
        this.lossRatio = lossRatio;
        left = new Peer(1);
        right = new Peer(2);        
    }

    public FDuplexChannel() {
        this(100.0, 0.0);
    }

    public Peer getLeft() {
        return left;
    }
    
    public Peer getRight() {
        return right;
    }
    

    public class Peer {

      protected int addr;
      protected CircularQueue<TCPSegment> txQueue, rxQueue;
      protected Lock lk;
      protected Condition txNotFull, txNotEmpty, rxNotEmpty;
      protected Thread txThread;

      protected Peer(int addr) {
        this.addr = addr;
        txQueue = new CircularQueue<TCPSegment>(1);
        rxQueue = new CircularQueue<TCPSegment>(QUEUE_SIZE);
        lk = new ReentrantLock();
        txNotFull = lk.newCondition();
        txNotEmpty = lk.newCondition();
        rxNotEmpty = lk.newCondition();
        txThread = new Thread(new TxThreadTask(), "IP-" + addr + "-TX");
        txThread.start();
      }

      public int getAddr() {
        return addr;
      }

      /**
       * Get maximum transport message size
       */
      public int getMMS() {
            return MAX_MSG_SIZE;
      }

      public void send(TCPSegment seg) {
        if (Math.random() < lossRatio) {
            log.warn("---------->Channel.send: Segment to transmit is lost");
            return;
        }
        try {
            lk.lock();
            while (txQueue.full()) {
                txNotFull.await();
            }
            txQueue.put(seg);
            log.debug("channel send");
            txNotEmpty.signal();
        } catch (InterruptedException ex) {
            log.error(ex);
        } finally {
            lk.unlock();
        }
      }
    
      public TCPSegment receive() {
        TCPSegment resultat = null;
        try {
            lk.lock();
            while (rxQueue.empty()) {
                rxNotEmpty.await();
            }
            log.debug("channel receive");
            resultat = rxQueue.get();
        } catch (InterruptedException ex) {
            log.error(ex);
        } finally {
            lk.unlock();
        } 
        return resultat;
      }

      class TxThreadTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    TCPSegment p;
                    lk.lock();
                    try {
                        while (txQueue.empty()) { txNotEmpty.await(); }
                        p = txQueue.get();
                        txNotFull.signal();
                    } finally {
                        lk.unlock();
                    }
                    Thread.sleep((int) (1000.0 / t_rate));
                    deliverPacket(p);
                } catch (InterruptedException e) {
                    // Interrupted when shutdown this node (see shutdown())
                }
            }
        }

        private void deliverPacket(TCPSegment p) {
            Peer dest = (Peer.this == left) ? right : left;
            if (dest != null) {
                dest.lk.lock();
                if (!dest.rxQueue.full()) {
                    log.debug("Peer(addr=%s)->deliverPacket(%s)", dest.addr, p);
                    dest.rxQueue.put(p);
                    dest.rxNotEmpty.signal();
                } else {
                    log.warn("---------->Peer(addr=%s)->Congestion: Segment to transmit is lost", addr);
                }
                dest.lk.unlock();
            }
        }

      } // End of class TxThreadTask

    } // End of class Peer

} // End of class FDuplexChannel


