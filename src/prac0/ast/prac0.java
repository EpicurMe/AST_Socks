package prac0.ast;
import java.nio.charset.StandardCharsets;

public class prac0 {

    public static void main(String[] args) {
        Channel canal = new Channel(10);
        ProtocolSender enviar = new ProtocolSender(canal);
        ProtocolRecv rebre = new ProtocolRecv(canal);
        String text = "Pràctica 0 acabada";
        byte[] text_bytes = text.getBytes();        
        byte[] text_rebut = new byte[100];
        int offset = 0,length, bytes_enviats_long;

        enviar.sendData(text_bytes, offset,text_bytes.length);
        bytes_enviats_long = rebre.receiveData(text_rebut, offset, text_rebut.length);
        
        byte[] text_definitiu = new byte[bytes_enviats_long];
        for(int j=0;j<bytes_enviats_long;j++){
            text_definitiu[j]=text_rebut[j];
        }
        String missatge_rebut = new String(text_definitiu, StandardCharsets.UTF_8);
        System.out.println(missatge_rebut);
    }
    
}
