/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testeDethreadsss;

/**
 *
 * @author ismae
 */
public class ThreadQueColocaNoArray extends Thread {

    private ThreadArrayCompartilhado t;
    String nome = "colocador";

    public ThreadQueColocaNoArray(ThreadArrayCompartilhado t) {
        this.t = t;
        this.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("coloquei:"+("abc"));
            
            t.acessarArray("abc");
        }
    }

    
}
