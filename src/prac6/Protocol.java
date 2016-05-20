
package prac6;

import prac4.FDuplexChannel;
import ast.protocols.tcp.TCPSegment;

import ast.logging.Log;
import ast.logging.LogFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;


/**
 * @author AST's teachers
 */
public class Protocol {
    public static Log log = LogFactory.getLog(Protocol.class);

    protected Lock lk;
    protected FDuplexChannel.Peer net;
    protected Thread task;
    protected ArrayList<TSocket> sockets;

    public Protocol(FDuplexChannel.Peer ch) {
        lk = new ReentrantLock();
        net = ch;
        sockets = new ArrayList<TSocket>();
        task = new Thread(new ReceiverTask());
        task.start();
    }

    public TSocket openWith(int localPort, int remotePort) {
        lk.lock();
        try {
            Protocol proto = new Protocol(net);
            TSocket sock = new TSocket(proto, localPort, remotePort);
        for (int i = 0; i < sockets.size(); i++){
            if (sockets.contains(sock)) {
                return null;
            }
        }  
        sockets.add(sock);
        return sock;
        } finally {
            lk.unlock();
        }
    }

    protected void ipInput(TCPSegment segment) {
        TSocket socket = getMatchingTSocket(segment.getDestinationPort(),segment.getSourcePort());
        socket.processReceivedSegment(segment);
    }

    protected TSocket getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for (int i = 0; i < sockets.size(); i++){
                if ((sockets.get(i).remotePort == remotePort) && (sockets.get(i).localPort == localPort)) {
                    return sockets.get(i);
                }
            }
            return null;
        } finally {
            lk.unlock();
        }
    }


    class ReceiverTask implements Runnable {
        public void run() {
            while (true) {
                TCPSegment rseg = net.receive();
                ipInput(rseg);
            }
        }
    }

}
