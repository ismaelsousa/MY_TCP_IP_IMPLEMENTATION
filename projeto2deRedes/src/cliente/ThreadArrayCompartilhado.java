/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.util.ArrayList;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class ThreadArrayCompartilhado extends Thread {

    private static ArrayList<Pacote> PacoteRecebidosDoServer = new ArrayList<>();

    public static synchronized Pacote acessarArray(int op, Pacote pacote) {
        System.out.println("a thread que está acessando é:" + Thread.currentThread().getName());
        if (op == 1 && pacote != null) {
            PacoteRecebidosDoServer.add(pacote);
        } else if (op == 2) {
            if (!PacoteRecebidosDoServer.isEmpty()) {
                return PacoteRecebidosDoServer.remove(0);
            }
        }
        return null;
    }
    
    
}
