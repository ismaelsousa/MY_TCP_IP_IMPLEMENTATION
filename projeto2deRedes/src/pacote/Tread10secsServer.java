/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacote;

import Servidor.ConexaoComCliente;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class Tread10secsServer extends TimerTask {

    ConexaoComCliente conexao;

    public Tread10secsServer(ConexaoComCliente conexao) {
        this.conexao = conexao;
    }

    @Override
    public void run() {
        conexao.CriarArquivo(2);
        conexao.datagram.close();
        System.err.println("10 secs sem comunicação");
        try {
            this.cancel();
            this.finalize();
        } catch (Throwable ex) {
            
        }
    }

}
