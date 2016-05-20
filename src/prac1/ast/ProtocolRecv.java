package prac1.ast;


import ast.protocols.tcp.TCPSegment;

public class ProtocolRecv {
    private Channel canal;
    private TCPSegment segment;
    private byte[] array_temporal;
    public ProtocolRecv(Channel input){
        this.canal = input;
    }
    public int receiveData(byte[] data, int offset, int length){
        this.segment = this.canal.receive();
        this.array_temporal = this.segment.getData();
        offset = this.segment.getDataOffset();
        length = this.segment.getDataLength();
        
        int i = 0;
        while(i<(length-offset)){
            data[i+offset] = array_temporal[i+offset];
            i++;
        }
        return i;
    }
}
