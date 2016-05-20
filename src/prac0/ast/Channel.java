package prac0.ast;
import ast.protocols.tcp.TCPSegment;

public class Channel {
    private CircularQueue cua;
    
    public Channel(int capacity) {
        this.cua = new CircularQueue(capacity);
    }
    public void send(TCPSegment seg) { 
        this.cua.put(seg);
    }
    public TCPSegment receive() { 
        return this.cua.get();
    }
}
