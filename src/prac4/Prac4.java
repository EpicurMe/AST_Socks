/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prac4;

/**
 *
 * @author Epicur
 */
import ast.logging.Log;
import ast.logging.LogFactory;


public class Prac4 {

    public static void main(String[] args){
        FDuplexChannel c = new FDuplexChannel();

        new Thread(new Host1(c.getLeft())).start();
        new Thread(new Host2(c.getRight())).start();
    }    

}


class Host1 implements Runnable {
    public static Log log = LogFactory.getLog(Host1.class);

    public static final int PORT = 10;

    protected Protocol proto;

    public Host1(FDuplexChannel.Peer peer) {
        this.proto = new Protocol(peer);
    }

    public void run() {
        log.info("Server started");
        TSocket serverTSocket = proto.openListen(Host1.PORT);
        while (true) {
            TSocket sc = serverTSocket.accept();
            new Thread(new Service(sc)).start();
        }
    }

    class Service implements Runnable {
        TSocket tSocket;
        Service(TSocket pcb) {
            this.tSocket = pcb;
        }
        public void run() {
            log.info("Service connected");
            tSocket.close();
            log.info("Service disconnected");
        }
    }

}


class Host2 implements Runnable {
    public static Log log = LogFactory.getLog(Host2.class);

    protected Protocol proto;

    public Host2(FDuplexChannel.Peer peer) {
        this.proto = new Protocol(peer);
    }

    public void run() {
        try { Thread.sleep(1000); } catch (InterruptedException ie) {} // Espera l'inici del servidor
        new Thread(new Client()).start();
        new Thread(new Client()).start();
    }

    class Client implements Runnable {
        public void run() {
            log.info("Client started");
            TSocket ts = proto.openConnect(Host1.PORT);
            log.info("Client connected");
            ts.close();
            log.info("Client disconnected");
        }
    }

}



