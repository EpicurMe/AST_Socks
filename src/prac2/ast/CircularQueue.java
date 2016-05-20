package prac2.ast;



import ast.util.Queue;
import ast.protocols.tcp.TCPSegment;
import java.util.Iterator;

public class CircularQueue implements Queue<TCPSegment> {

    private TCPSegment[] space; // Inicialitzo una array de tipus TCPSegment
    private int count;  // Conta el número d'elements que tindré a l'array
    private int firstp; // Dedueixo que vol dir primera posició
    private int mida;
    private int last;
    
    public CircularQueue(int capacity) {
       this.space = new TCPSegment[capacity];
       this.count = 0;
       this.firstp = 0;
       this.mida = capacity;
       this.last = 0;
    }

    public int size() {
        return this.mida;
    }

    public boolean hasFree(int n) {
        return (this.mida-this.count)>=n;
    }

    public int free() {
        return this.mida-this.count;
    }

    public boolean empty() {
        return this.count==0;
    }

    public boolean full() {
        return this.mida==this.count;
    }

    public TCPSegment peekFirst() {
        if(!empty()){
            return this.space[this.firstp];
        }else{
            return null;
        }
    }

    public TCPSegment peekLast() {
        if(!empty()){
            return this.space[this.last];
        }else{
            return null;
        }
    }

    public void put(TCPSegment value) {
        if(!full()){
            if(!(this.last==(this.mida-1))){
                this.last++;
            }else{
                this.last=0;
            }
            this.space[this.firstp] = value;
            count++;
        }else{
            System.out.println("L'element no s'ha pogut introduïr, ja que la cua està plena.");
        }
    }

    public TCPSegment get()  {
        if(!empty()){
            TCPSegment sortida = this.space[this.firstp];
            if(!(this.firstp==(this.mida-1))){
                this.firstp++;
                count--;
            }else{
                this.firstp=0;
                count--;
            }
            return sortida;
        }else{
            System.out.println("La cua està buida.");
            return null;
        }
    }

    public Iterator<TCPSegment> iterator() {
        return null;
    }
}

