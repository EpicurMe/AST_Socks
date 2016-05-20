package prac2.ast;

public class prac2 {

    public static void main(String[] args) {
        Channel c = new Channel();
        new Thread(new Sender(c)).start();
        new Thread(new Receiver(c)).start();      
    }
    
}

