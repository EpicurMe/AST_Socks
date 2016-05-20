package prac2.ast;


import ast.logging.*;
import java.util.concurrent.locks.*;

public class ProtocolBase {
    public static Log log = LogFactory.getLog(ProtocolBase.class);
    
    protected Lock lk;
    protected Condition appCV;
    protected Channel channel;
    
    protected ProtocolBase(Channel ch) {
        lk = new ReentrantLock();
        appCV = lk.newCondition();
        channel = ch;
    }
}
