/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class Thread_Envia_Pacote extends Thread {
    public static int idDaThread = 0;
    ArrayList<Pacote> pacParaEnvio;
    Cliente c;
    int id;
    boolean ciclo = true;

    public Thread_Envia_Pacote(ArrayList<Pacote> PacParaEnvio, Cliente c) {
        id= idDaThread;
        idDaThread++;
        this.pacParaEnvio = PacParaEnvio;
        this.c = c;
        this.start();
    }

    @Override
    public void run() {        
        while (ciclo) {                        
            for (int j = 0; j < pacParaEnvio.size(); j++) {
                System.out.println(id+": enviei o pacote : " + pacParaEnvio.get(j).getSequenceNumber());
                byte pkt[] = Pacote.converterPacoteEmByte(pacParaEnvio.get(j));
                DatagramPacket Dack = new DatagramPacket(pkt, pkt.length, c.IPAddress, c.portaDoServidor);
                try {
                    c.clienteUDP.send(Dack);

                } catch (IOException ex) {
                    System.out.println("erro ao tentar enviar a janela de pacotes");
                }
            }
            System.out.println("já enviei os pacotes vou esperar 0.5 secundo");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                System.err.print("erro ao tentar dormir na thread");
            }
        }
        System.out.println(id+": ------------------------------------thread envia pacote fechouuuuuu");
        
    }

}
