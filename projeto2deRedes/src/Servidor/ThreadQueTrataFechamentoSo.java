/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.util.ArrayList;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class ThreadQueTrataFechamentoSo extends Thread {
    ArrayList<ConexaoComCliente> threads;
    public ThreadQueTrataFechamentoSo(ArrayList<ConexaoComCliente> threads) {
        this.threads = threads;
    }
    

    @Override
    public void run() {
        for (ConexaoComCliente thread : threads) {
            Pacote p = new Pacote(false, true, false);
            thread.EnviarPacoteCliente(p);
        }
     
        System.err.println("====================================");
        System.err.println("====================================");
        System.err.println("==     A Deus !!!!              ==");
        System.err.println("====================================");
        System.err.println("====================================");
    }
    
}
