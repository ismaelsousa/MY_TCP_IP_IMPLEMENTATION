/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class testeSeUDPfazVariasCoisasAoMesmoTempo extends Thread {

    DatagramSocket c;

    public testeSeUDPfazVariasCoisasAoMesmoTempo(DatagramSocket c) {
        this.c = c;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            InetAddress IPAddress;
            try {
                IPAddress = InetAddress.getByName("localhost");
                byte envio[] = new byte[675];
                //criar o datagram para enviar para o servidor
                DatagramPacket pkt = new DatagramPacket(envio, envio.length, IPAddress, 5555);
                System.out.println("enviei mesmo assim");
            } catch (UnknownHostException ex) {
                System.out.println("ip errado");
            }

        }

    }

}
