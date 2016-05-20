
package prac6;

import ast.logging.LogFactory;
import ast.logging.Log;


public class Sender implements Runnable {
    public static Log log = LogFactory.getLog(Sender.class);

    protected TSocket output;
    protected int sendNum, sendSize, sendInterval;

    
    public Sender(TSocket pcb, int sendNum, int sendSize, int sendInterval) {
        this.output = pcb;
        this.sendNum = sendNum;
        this.sendSize = sendSize;
        this.sendInterval = sendInterval;
    }
    
    public Sender(TSocket pcb) {
        this(pcb, 20, 500, 10);
    }
    
    public void run() {
        try {
            byte n = 0;
            byte[] buf = new byte[sendSize];
            for (int i = 0; i < sendNum; i++) {
                Thread.sleep(sendInterval);
                // stamp data to send
                for (int j = 0; j < sendSize; j++) {
                    buf[j] = n;
                    n = (byte) (n + 1);
                }
                output.sendData(buf, 0, buf.length);
            }
            log.info("Sender: transmission finished");
        } catch (Exception e) {
            log.error("Excepcio a Sender: %s", e);
            e.printStackTrace(System.err);
        }
    }

}


