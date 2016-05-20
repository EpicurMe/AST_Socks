package prac1.ast;


import java.nio.charset.StandardCharsets;

public class Receiver implements Runnable{
    private ProtocolRecv protoRecv;
    
    public Receiver(Channel canal){
        protoRecv = new ProtocolRecv(canal);
    }
        
    public void run(){
        byte[] text_rebut = new byte[100];
        int offset = 0, bytes_rebuts = 0;
        int long_rebuda = protoRecv.receiveData(text_rebut, offset, bytes_rebuts);
        
        
        byte[] text_definitiu = new byte[long_rebuda];
        for(int j=0;j < long_rebuda;j++){
            text_definitiu[j]=text_rebut[j];
        }
        String missatge_rebut = new String(text_definitiu, StandardCharsets.UTF_8);
        System.out.println(missatge_rebut);   
    }
}
