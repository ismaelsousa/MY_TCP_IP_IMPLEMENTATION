/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import cliente.NoCliente;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.TimerTask;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class Thread_Envia_Pacote_conexao extends TimerTask {

    DatagramSocket d;
    Pacote p;
    NoCliente noCliente;

    public Thread_Envia_Pacote_conexao(DatagramSocket d, Pacote p, NoCliente noCliente) {
        this.d = d;
        this.p = p;
        this.noCliente = noCliente;

    }

    @Override
    public void run() {
        byte dataReceive2[] = Pacote.converterPacoteEmByte(p);
        DatagramPacket pkt2 = new DatagramPacket(dataReceive2, dataReceive2.length, noCliente.getIPAddress(), noCliente.getPorta());
        try {
            d.send(pkt2);
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:" + noCliente.getId());
        }

    }

}
