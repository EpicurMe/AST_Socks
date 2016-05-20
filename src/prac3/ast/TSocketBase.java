
package prac3.ast;

import ast.logging.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author upcnet
 */
public class TSocketBase {
    public static Log log = ProtocolBase.log;

    protected ProtocolBase proto;
    protected Lock lk;
    protected Condition appCV;

    protected int localPort;
    protected int remotePort;
    
    protected TSocketBase(ProtocolBase p, int localPort, int remotePort) {
        lk = new ReentrantLock();
        appCV = lk.newCondition();
        proto = p;
        this.localPort = localPort;
        this.remotePort = remotePort;
    }

}


