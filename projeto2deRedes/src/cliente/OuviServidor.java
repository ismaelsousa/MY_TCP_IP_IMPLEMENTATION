/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class OuviServidor extends Thread {

    DatagramSocket c;

    public OuviServidor(DatagramSocket c) {
        this.c = c;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            byte dataReceive[] = new byte[675];
            DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
            try {
                System.out.println("estou ouvindo");
                c.receive(receive);
                
            } catch (IOException ex) {
                System.out.println("erro ao tentar receber pacote na thread");
            }
            
        }
    }

}
