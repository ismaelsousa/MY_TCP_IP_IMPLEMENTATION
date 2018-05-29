/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
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
    boolean ciclo = true;
    int base;
    int nextSeqNum;

    public Thread_Envia_Pacote(int base, int nextSeqNum, Cliente c) {
        this.c = c;
        //por que fiz novo, para não passar a refencia e sim criar um novo 
        this.base = new Integer(base);
        this.nextSeqNum = new Integer(nextSeqNum);
        System.out.println("tarefa criada");

    }

    @Override
    public void run() {
        if (ciclo == true) {
            for (int j = base; j < nextSeqNum; j++) {
                if (j < c.getPacotes().size()) {
                    if (ciclo == true) {
                        System.out.println("enviei:" + c.getPacotes().get(j).getSequenceNumber());
                    }

                    byte pkt[] = Pacote.converterPacoteEmByte(c.getPacotes().get(j));
                    DatagramPacket Dack = new DatagramPacket(pkt, pkt.length, c.IPAddress, c.portaDoServidor);
                    try {
                        c.clienteUDP.send(Dack);

                    } catch (IOException ex) {
                        System.out.println("erro ao tentar enviar a janela de pacotes");
                    }
                }
            }
            ciclo = false;
        } else {
            System.err.println("###############################################################################");
            System.err.println("                                    Estorou o tempo");
            System.err.println("###############################################################################");
            c.thress = c.tamanho_da_janela; //thress cai pela metade da janela                        
            c.tamanho_da_janela = 1; //a janela cai para 1
            c.nextSeqNum = c.base; // vai ocorrer que se sendo igual a minha verificação vai criar a nova janela 
            System.err.println("base:"+c.base);
            System.err.println("novo next:"+c.nextSeqNum);
        }
    }

}
