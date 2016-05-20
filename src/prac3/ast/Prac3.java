/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prac3.ast;

public class Prac3 {

    public static void main(String[] args){
            Channel c = new Channel();

            ProtocolSend proto2 = new ProtocolSend(c);
            new Thread(new Host2(proto2)).start();
            
            ProtocolRecv proto1 = new ProtocolRecv(c);
            new Thread(new Host1(proto1)).start();
    }    

}


class Host1 implements Runnable {

    public static final int PORT = 10;

    protected ProtocolRecv proto;

    public Host1(ProtocolRecv proto) {
        this.proto = proto;
    }

    public void run() {
        TSocketRecv sock1 = proto.openForInput(PORT, Host2.PORT1);
        Receiver re1 = new Receiver(sock1);
        TSocketRecv sock2 = proto.openForInput(PORT, Host2.PORT2);
        Receiver re2 = new Receiver(sock2);
        new Thread(re1).start();
        new Thread(re2).start();
      //arranca dos fils receptors, cadascun amb el seu socket de recepcio
      //fes servir els ports apropiats
      //...
    }
}


class Host2 implements Runnable {

    public static final int PORT1 = 10;
    public static final int PORT2 = 50;

    protected ProtocolSend proto;
    
    public Host2(ProtocolSend proto) {
        this.proto = proto;
    }
    
    public void run() {
        TSocketSend sock1 = proto.openForOutput(PORT1, Host1.PORT);
        Sender se1 = new Sender(sock1);
        TSocketSend sock2 = proto.openForOutput(PORT2, Host1.PORT);
        Sender se2 = new Sender(sock2);
        new Thread(se1).start();
        new Thread(se2).start();
      //arranca dos fils emissors, cadascun amb el seu socket de transmissio
      //fes servir els ports apropiats
      //...
    }
    
}