/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testeDethreadsss;

import cliente.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class ThreadArrayCompartilhado {

    private ArrayList<String> PacoteRecebidosDoServer = new ArrayList<>();
    boolean usando = false;

    public synchronized String remover() {
        if (usando == true) {
            try {
                System.out.println("fui acessaar o colocador estava usando");
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadArrayCompartilhado.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            usando = true;
            String r = null;
            if (PacoteRecebidosDoServer.size() > 0) {
                r = PacoteRecebidosDoServer.remove(0);
            }
            usando = false;
            notify();
            return r;
        }
        return null;
    }

    public synchronized void acessarArray( String pacote) {
        
        if (usando == true) {
            try {
                System.out.println("fui acessaar o removedor estava usando");
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadArrayCompartilhado.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            usando = true;
            if (pacote != null) {
                PacoteRecebidosDoServer.add(pacote);                
            }
            usando = false;
            notify();
        }
    }
}
