package prac0.ast;
import ast.protocols.tcp.TCPSegment;

public class ProtocolRecv {
    private Channel canal;
    private TCPSegment segment;
    private byte[] array_temporal;
    private int count;
    
    public ProtocolRecv(Channel input){
        this.canal = input;
    }
    
    public int receiveData(byte[] data, int offset, int length){
        this.segment = this.canal.receive();
        this.array_temporal = this.segment.getData();
        offset = this.segment.getDataOffset();
        length = this.segment.getDataLength();
        
        for(count = 0;count<(length-offset);count++){
            data[count+offset] = array_temporal[count+offset];
        }
        return count;
    }
}
