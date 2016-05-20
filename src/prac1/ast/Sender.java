package prac1.ast;


import ast.protocols.tcp.TCPSegment;
import java.lang.*;

public class Sender implements Runnable{
    private ProtocolSender protoSend;
    String text = "Pràctica 1 acabada";
    byte[] text_bytes = text.getBytes();
    
    public Sender(Channel canal){
        protoSend = new ProtocolSender(canal);
    }
    public void run(){
        protoSend.sendData(text_bytes, 0, text.length()+1);
    }
}
