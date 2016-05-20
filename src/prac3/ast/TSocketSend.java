package prac3.ast;

import ast.protocols.tcp.TCPSegment;
import static java.lang.Math.min;

/**
 * @author AST's teachers
 */
public class TSocketSend extends TSocketBase {

  protected int sndMSS;       // Send maximum segment size

  /**
   * Create an endpoint bound to the local IP address and the given TCP port. The local IP address is determined by the
   * networking system.
   *
   * @param ch
   */
  protected TSocketSend(ProtocolSend p, int localPort, int remotePort) {
    super(p, localPort, remotePort);
    sndMSS = p.channel.getMMS() - TCPSegment.HEADER_SIZE; // IP maximum message size - TCP header size
  }

  public void sendData(byte[] data, int offset, int length) {
    TCPSegment segment = new TCPSegment();
    int s=0;
    while(s<length){
        int l = min(length-s, sndMSS);
        segment = segmentize(data, offset+s, l);
        segment.setPorts(localPort, remotePort);
        sendSegment(segment);
        s=s+l;
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
    proto.channel.send(segment);
  }

}
