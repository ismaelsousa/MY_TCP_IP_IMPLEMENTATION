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
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;
import pacote.Tread10secsCliente;

/**
 *
 * @author ismae
 */
public class OuviServidor extends Thread {

    Cliente c;

    public OuviServidor(Cliente c) {
        this.c = c;
        this.start();
    }

    @Override
    public void run() {
        int acksRepetidos = 0;
        Pacote ultimoPac = null;
        boolean ciclo = true;
        while (ciclo) {
            byte dataReceive[] = new byte[675];
            DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
            try {
                //vai esperar 10 secs por resposta
                Timer dezSecs = new Timer();
                dezSecs.schedule(new Tread10secsCliente(c), 10000, 10000);
                
                c.clienteUDP.receive(receive);
                
                dezSecs.cancel();
                dezSecs.purge();
                
                        
                
                Pacote p = Pacote.converterByteParaPacote(dataReceive);
                //aqui ele coloca o pacote disponivel 

                if (ultimoPac == null) {//primeiro pacote                    
                    ultimoPac = p;
                    c.ArrayDeRecebimento.acessarArray(1, p);
                    //pq eu pergunto se ele é maio que a minha base? para mesmo que ele seja repetido ele possa entrar para atualizar
                } else if (ultimoPac.getAckNumber() != p.getAckNumber() && p.getAckNumber() > c.pacotes.get(c.base).getSequenceNumber()) {// //se o cara novo que chegouu for diferente do que já tinha                   
                    //chegado eu atualizo  ele e coloco na lista e coloco repetidos = 0
                    acksRepetidos = 0;
                    ultimoPac = p;
                    c.ArrayDeRecebimento.acessarArray(1, p);
                    
                    //controle de congestionamento de ack repetidos 
                } else if (ultimoPac.getAckNumber() == p.getAckNumber()) {
                    if (p.isAck()) {
                        c.ArrayDeRecebimento.acessarArray(1, p);
                    } else {
                        acksRepetidos++;
                        if (acksRepetidos >= 3) {
                            System.err.println("###############################################################################");
                            System.err.println("                                   3 Acks repetidos");
                            System.err.println("###############################################################################");
                            
                            c.thress = c.tamanho_da_janela; //thress cai para janela                        
                            c.tamanho_da_janela = 1; //a janela cai para 1
                            c.nextSeqNum = new Integer(c.base); // vai ocorrer que se sendo igual a minha verificação vai criar a nova janela                             
                            acksRepetidos = 0; //zera para começar a verificar novamene
                        }
                    }
                } else if (p.isFyn() && p.isAck()) {
                    c.ArrayDeRecebimento.acessarArray(1, p);
                }else if(p.isFyn()){//se receber do nada um fim sem ter sido enviado todo arquivo então foi algum problema que o servidor desligou
                    System.out.println("Servidor se desligou");
                    c.finalizarTudo();
                }

                //se for um ack entao vou olhar o array list para ver se bate com algum 
                //tem q arrumar um meio de verificar onde esta o pacote pq dependendo onde esteja eu confirmo os outros 
            } catch (IOException ex) {
                System.out.println("erro ao tentar receber pacote na thread");
            }catch (Throwable ex) {
            }
        }
    }
}
