/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testeDethreadsss;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class Main {

    public static void main(String[] args) {
        ThreadArrayCompartilhado array = new ThreadArrayCompartilhado();
        ThreadQueColocaNoArray coloca = new ThreadQueColocaNoArray(array);
        ThreadQueTiraDoArray tira = new ThreadQueTiraDoArray(array);

        try {
            coloca.join();
            tira.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
