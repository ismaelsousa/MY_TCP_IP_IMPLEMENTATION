/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class Thread_Envia_Pacote extends TimerTask {

    Cliente c;
    Pacote envio;
    private final String m;
    boolean umaVez = true;

    public Thread_Envia_Pacote(Cliente c, Pacote envio, String m) {
        this.c = c;
        this.envio = envio;
        this.m = m;
    }

    @Override
    public void run() {
        byte bytes[] = Pacote.converterPacoteEmByte(envio);
        //criar o datagram para enviar para o servidor
        DatagramPacket pkt = new DatagramPacket(bytes, bytes.length, c.IPAddress, c.portaDoServidor);
        try {
            c.clienteUDP.send(pkt);
            if (umaVez) {
                System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                System.out.println("                   " + m + "                        ");
                
                umaVez = false;
            }
        } catch (IOException ex) {
            System.err.println("Erro ao enviar o pac na Thread_Envia_Pacote");
        }

    }

}
