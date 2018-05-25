/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WaitNotify;

/**
 *
 * @author ismae
 */
public class ThreadTiqueTaque implements Runnable {

    TiqueTaque tt;
    Thread t;

    public ThreadTiqueTaque(String nome, TiqueTaque tt) {
        this.tt = tt;
        t = new Thread(this, nome);
        t.start();
    }

    @Override
    public void run() {
        if (t.getName().equalsIgnoreCase("Tique")) {
            for (int i = 0; i < 10; i++) {
                tt.tique(true);
            }
            tt.tique(false);
        } else {
            for (int i = 0; i < 10; i++) {
                tt.taque(true);
            }
            tt.taque(false);

        }
    }

}
