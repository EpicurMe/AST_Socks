package prac3.ast;

import java.util.ArrayList;

/**
 *
 * @author upcnet
 */
public class ProtocolSend extends ProtocolBase {

  protected ArrayList<TSocketSend> sockets;

  public ProtocolSend(Channel ch) {
    super(ch);
    sockets = new ArrayList<TSocketSend>();
  }

  public TSocketSend openForOutput(int localPort, int remotePort) {
    lk.lock();
    try {
        ProtocolSend proto = new ProtocolSend(super.channel);
        TSocketSend sock = new TSocketSend(proto, localPort, remotePort);
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
}