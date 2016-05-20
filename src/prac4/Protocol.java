
package prac4;

import ast.protocols.tcp.TCPSegment;

import ast.logging.Log;
import ast.logging.LogFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;


public class Protocol {
    public static Log log = LogFactory.getLog(Protocol.class);

    protected Lock lk;
    protected Thread task;
    protected FDuplexChannel.Peer net;
    protected ArrayList<TSocket> listenTSocks;    /* All unbound TSockets (in state LISTEN) */
    protected ArrayList<TSocket> activeTSocks;    /* All bound TSockets (in active states) */
    private int nextFreePort;

    protected Protocol(FDuplexChannel.Peer ch) {
        lk = new ReentrantLock();
        net = ch;
        listenTSocks = new ArrayList<TSocket>();
        activeTSocks = new ArrayList<TSocket>();
        nextFreePort = 0xc000;
        task = new Thread(new ReceiverTask());
        task.start();
    }

    public TSocket openListen(int localPort) {
        // Comprobar que el port no esta ocupat
        if (portInUse(localPort, listenTSocks) || portInUse(localPort, activeTSocks)) {
            log.error("openListen: port %d is in use", localPort);
            return null;
        }
        TSocket socket = new TSocket(this, localPort);
        socket.listen();
        return socket;
    }

    public TSocket openConnect(int remotePort) {
        int localPort = newPort();
        TSocket sock = new TSocket(this, localPort);
        sock.connect(remotePort);
        return sock;
    }

    public void ipInput(TCPSegment segment) {
        // Search matching TSocket

        // Completar
        
        // Fet en l'anterior practica
        
        throw new RuntimeException("Falta completar");
   }

    //-------------------------------------------
    // Internals:

    /**
     * Add a PCB to list of listen PCBs.
     * We assume the PCB is in state LISTEN.
     */
    protected void addListenTSocket(TSocket tcb) {
        lk.lock();
        listenTSocks.add(tcb);
        lk.unlock();
    }

    /**
     * Add a PCB to list of active PCBs.
     * We assume the PCB is in an active state.
     */
    protected void addActiveTSocket(TSocket tcb) {
        lk.lock();
        activeTSocks.add(tcb);
        lk.unlock();
    }

    /**
     * Remove a PCB from list of listen PCBs.
     * We assume the PCB is in CLOSED state.
     */
    protected void removeListenTSocket(TSocket tcb) {
        lk.lock();
        listenTSocks.remove(tcb);
        lk.unlock();
    }

    /**
     * Remove a PCB from list of active PCBs.
     * We assume the PCB is in CLOSED state.
     */
    protected void removeActiveTSocket(TSocket sc) {
        lk.lock();
        activeTSocks.remove(sc);
        lk.unlock();
    }

    protected TSocket getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try{
            for (int i = 0; i < listenTSocks.size(); i++){
                if ((listenTSocks.get(i).remotePort == remotePort) && (listenTSocks.get(i).localPort == localPort)) {
                    return listenTSocks.get(i);
                }
            }
            for (int j = 0; j < activeTSocks.size(); j++){
                if ((activeTSocks.get(j).remotePort == remotePort) && (activeTSocks.get(j).localPort == localPort)) {
                    return activeTSocks.get(j);
                }
            }
            return null;
        } finally {
            lk.unlock();
        }
    }

    /**
     * Used from method 'listen'.
     */
    protected boolean portInUse(int localPort, ArrayList<TSocket> list) {
        lk.lock();
        try {
            // Search in active PCBs
            for (TSocket c : list) {
                if (c.localPort == localPort) {
                    return true;
                }
            }
            return false;
        } finally {
            lk.unlock();
        }
    }

    /**
     * Allocate a new (free) local port.
     */
    protected int newPort() {
        lk.lock();
        try {
            int base = nextFreePort & 0x3fff;
            nextFreePort = 0xc000 | ((base + 1) & 0x3fff);
            for (int i = 0; i <= 0x3fff; i++) {
                int port = 0xc000 | ((base + i) & 0x3fff);
                if (! portInUse(port, activeTSocks) && ! portInUse(port, listenTSocks)) {
                    return port;
                }
            }
            log.error("newPort: resources exhausted");
            return -1;
        } finally {
            lk.unlock();
        }
    }


    //-------------------------------------------

    class ReceiverTask implements Runnable {
        public void run() {
            while (true) {
                TCPSegment rseg = net.receive();
                ipInput(rseg);
            }
        }
    } 

}
