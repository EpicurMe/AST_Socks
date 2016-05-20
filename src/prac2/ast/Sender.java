package prac2.ast;


import ast.logging.Log;
import ast.logging.LogFactory;
import ast.protocols.tcp.TCPSegment;
import java.lang.*;

public class Sender implements Runnable{
    public static Log log = LogFactory.getLog(Sender.class);
    protected ProtocolSender output;
    protected int sendNum, sendSize, sendInterval;
    
    public Sender(Channel c, int sendNum, int sendSize, int sendInterval){
        this.output = new ProtocolSender(c);
        this.sendNum = sendNum;
        this.sendSize = sendSize;
        this.sendInterval = sendInterval;
    }
    public Sender(Channel c){
        this(c, 20, 50, 100);
    }
    public void run(){
        try {
            byte n = 0;
            byte[] buf = new byte[sendSize];
            for (int i = 0; i < sendNum; i++) {
                Thread.sleep(sendInterval*10);
                // stamp data to send
                for (int j = 0; j < sendSize; j++) {
                    buf[j] = n;
                    n = (byte) (n+1);
                }
                output.sendData(buf, 0, buf.length);
            }
            log.info("Sender: trnsmission finished");
        } catch (Exception e) {
            log.error("Excepcio a Sender: %", e);
            e.printStackTrace(System.err);
        }
    }
}
