package prac3.ast;

import ast.protocols.tcp.TCPSegment;
import java.util.ArrayList;

/**
 *
 * @author upcnet
 */
public class ProtocolRecv extends ProtocolBase {

  protected Thread task;
  protected ArrayList<TSocketRecv> sockets;

  public ProtocolRecv(Channel ch) {
    super(ch);
    sockets = new ArrayList<TSocketRecv>();
    task = new Thread(new ReceiverTask());
    task.start();
  }

  public TSocketRecv openForInput(int localPort, int remotePort) {
    lk.lock();
    try {
	ProtocolRecv proto = new ProtocolRecv(super.channel);
        TSocketRecv sock = new TSocketRecv(proto, localPort, remotePort);
        for (int i = 0; i < sockets.size(); i++){
            if ((sockets.get(i).remotePort == remotePort) && (sockets.get(i).localPort == localPort)) {
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
    TSocketRecv socket = getMatchingTSocket(segment.getDestinationPort(),segment.getSourcePort());
    socket.processReceivedSegment(segment);
  }

  protected TSocketRecv getMatchingTSocket(int localPort, int remotePort) {
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
        TCPSegment rseg = channel.receive();
        ipInput(rseg);
      }
    }
  }

}
