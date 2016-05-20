package prac2.ast;


import ast.logging.Log;
import ast.logging.LogFactory;
import java.nio.charset.StandardCharsets;

public class Receiver implements Runnable{
    public static Log log = LogFactory.getLog(Receiver.class);
    
    protected ProtocolRecv input;
    protected int recvBuf, recvInterval;
    
    public Receiver(Channel c, int recvBuf, int recvInterval) {
        this.input = new ProtocolRecv(c);
        this.recvBuf = recvBuf;
        this.recvInterval = recvInterval;
    }
    
    public Receiver(Channel c) {
        this(c, 25, 10);
    }
    public void run(){
        try {
            byte n = 0;
            byte[] buf = new byte[recvBuf];
            while (true) {
                int r = input.receiveData(buf, 0, buf.length);
                // check received data stamps
                for (int j = 0; j < r; j++) {
                    if (buf[j] != n) {
                        throw new Exception("ReceiverTask: Received data is corrupted");
                    }
                    n = (byte) (n+1);
                }
                log.info("Receiver: received %d bytes", r);
                Thread.sleep(recvInterval);
            }
        } catch (Exception e) {
            log.error("Excepcio a Receiver: %d", e);
            e.printStackTrace(System.err);
        }
    }
}
