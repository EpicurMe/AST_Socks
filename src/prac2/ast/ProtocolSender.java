package prac2.ast;


import ast.protocols.tcp.TCPSegment;
import static java.lang.Integer.min;

public class ProtocolSender extends ProtocolBase{
    private TCPSegment segment = new TCPSegment();
    private byte[] close = new byte[1];
    protected int sndMSS;
    
    public ProtocolSender(Channel input){
        super(input);
        this.sndMSS = channel.getMMS(); // IP maximum message size - TCP header size
    }
    
    public void sendData(byte[] data, int offset, int length){
        int s=0;
        while(s<length){
          int l = min(length-s, sndMSS);
          segment = segmentize(data, offset+s, l);
          sendSegment(segment);
          s=s+l;
        }
    }
    
    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        // Crea un segment que en el seu payload (camp) de dades
        // conté length bytes que hi ha a data a partir
        // de la posició de l'offset
        
        //Fem el nou segment
        int i=offset;
        byte[] nova_array = new byte[length-offset];
        while(i < length-offset){
            nova_array[i] = data[i+offset];
            i++;
        }
        TCPSegment segment_nooff = new TCPSegment();
        
        //Afegim les noves dades al nou segment
        segment_nooff.setData(nova_array, 0, length-offset);
        //El retornem
        return segment_nooff;
    }
    
    protected void sendSegment(TCPSegment segment) {
        channel.send(segment);
    }
    public void close(){
        close[0]= -1;
        this.segment.setData(close);
    }
}
