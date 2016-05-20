package prac1.ast;

public class prac1 {

    public static void main(String[] args) {
        Channel canal = new Channel(10);
        Thread enviar = new Thread(new Sender(canal), "Enviar");
        Thread rebre = new Thread(new Receiver(canal), "Rebre");
        enviar.start();
        rebre.start();        
    }
    
}
