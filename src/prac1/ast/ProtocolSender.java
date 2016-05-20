package prac1.ast;


import ast.protocols.tcp.TCPSegment;

public class ProtocolSender {
    private Channel canal = new Channel(15);
    private TCPSegment segment = new TCPSegment();
    private byte[] close = new byte[1];
    public ProtocolSender(Channel input){
        this.canal = input;
    }
    public void sendData(byte[] data, int offset, int length){
        this.segment.setData(data, offset, length);
        this.canal.send(segment);
    }
    public void close(){
        close[0]= -1;
        this.segment.setData(close);
    }
}
