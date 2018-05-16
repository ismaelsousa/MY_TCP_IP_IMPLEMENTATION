/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import cliente.NoCliente;

/**
 *
 * @author ismae
 */
public class ConexaoComCliente extends Thread{
    private NoCliente noCliente;

    public ConexaoComCliente(NoCliente noCliente) {
        this.noCliente = noCliente;
    }
    
    @Override
    public void run() {
    }
    
    
}
