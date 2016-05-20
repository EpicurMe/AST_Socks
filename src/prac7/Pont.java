/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prac7;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 *
 * @author Epicur
 */
public class Pont {
    
    public boolean direccio;
    protected Lock lk;
    protected Condition appCV;
    
    public Pont() {
        lk = new ReentrantLock();
        appCV = lk.newCondition();
    }
    
    public void entrar(boolean sentit){
        while(sentit == direccio){
            appCV.signal();
        }
        direccio = sentit;
    }
    
    public void sortir(){
        appCV.signal();
    }
    
    
}
