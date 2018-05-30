/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacote;

import cliente.Cliente;
import java.util.TimerTask;

/**
 *
 * @author ismae
 */
public class Tread10secsCliente extends TimerTask {

    private Cliente c;

    public Tread10secsCliente(Cliente c) {
        this.c = c;
    }

    @Override
    public void run() {
        System.err.println("10 segundos se passaram sem comunicação");
        c.finalizarTudo();
    }

}
