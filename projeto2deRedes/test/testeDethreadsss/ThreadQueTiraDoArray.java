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
public class ThreadQueTiraDoArray extends Thread {

    private ThreadArrayCompartilhado t;
    String nome = "tirador";

    public ThreadQueTiraDoArray(ThreadArrayCompartilhado t) {
        this.t = t;
        this.start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("tirei do array:");
            t.remover();            
        }
        System.out.println("terminei de tirar ");
    }

}
