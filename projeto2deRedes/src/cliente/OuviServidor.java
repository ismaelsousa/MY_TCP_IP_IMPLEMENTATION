/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class OuviServidor extends Thread {

    Cliente c;
    ArrayList<Pacote> n;

    public OuviServidor(Cliente c, ArrayList<Pacote> n) {
        this.c = c;
        this.n = n;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            byte dataReceive[] = new byte[675];
            DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
            try {
                
                c.clienteUDP.receive(receive);
                Pacote p = Pacote.converterByteParaPacote(dataReceive);
                //aqui ele coloca o pacote disponivel 
                n.add(p);
                System.out.println("chegou pacote já add na fila com ack:"+ p.getAckNumber());
                //se for um ack entao vou olhar o array list para ver se bate com algum 
                //tem q arrumar um meio de verificar onde esta o pacote pq dependendo onde esteja eu confirmo os outros 
                
            } catch (IOException ex) {
                System.out.println("erro ao tentar receber pacote na thread");
            }

        }
    }

}
