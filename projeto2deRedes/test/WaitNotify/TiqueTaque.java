/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WaitNotify;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class TiqueTaque {

    boolean tique;

    public synchronized void tique(boolean estaExecutando) {
        if (!estaExecutando) {
            tique = true;
            notify();
            return;

        }

        System.out.print("tique ");

        tique = true;

        notify();

        try {
            while (tique) {

                wait();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TiqueTaque.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    
      public synchronized void taque(boolean estaExecutando) {
        if (!estaExecutando) {
            tique = false;
            notify();
            return;

        }

        System.out.println("taque");

        tique = false;

        notify();

        try {
            while (tique == false) {
                
                wait();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TiqueTaque.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
