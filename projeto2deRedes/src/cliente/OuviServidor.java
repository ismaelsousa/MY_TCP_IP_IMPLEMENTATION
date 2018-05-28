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
    private String name = "ouvidorr";

    public OuviServidor(Cliente c) {
        this.c = c;
        System.out.println("fui criado");
        this.start();
    }

    @Override
    public void run() {
        System.out.println("ouviidor rodando");
        Pacote ultimoPac = null;
        while (true) {
            byte dataReceive[] = new byte[675];
            DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
            try {
                c.clienteUDP.receive(receive);
                Pacote p = Pacote.converterByteParaPacote(dataReceive);
                //aqui ele coloca o pacote disponivel 
                if (ultimoPac == null) {//primeiro pacote 
                    System.out.println("add o primeiro pacote");
                    ultimoPac = p;
                    c.ArrayDeRecebimento.acessarArray(1, p);
                } else if (ultimoPac.getAckNumber() != p.getAckNumber()) {
                    //se o cara novo que chegouu for diferente do que já tinha chegado eu atualizo  ele e coloco na lista
                    ultimoPac = p;
                    c.ArrayDeRecebimento.acessarArray(1, p);
                    System.out.println("chegou pacote já add na fila com ack:" + p.getAckNumber());
                } else if (p.isAck() == true) {//caso que o servidor confirma o encerramento
                    System.out.println("chegou o ack de confirmação do fyn");                             
                    //preciso tratar aqui para vê se realmente vai chegar e enviar o fyn                                        
                } else if (p.isFyn() == true) {
                    Pacote pacote = new Pacote();
                    //coloca o id de conexao
                    pacote.setConnectionID(c.id);
                    pacote.setSequenceNumber(c.meuNumeroDeSeq + 1);//coloca no pacote o numero de seq
                    c.setMeuNumeroDeSeq(c.getMeuNumeroDeSeq() + 1);//aqui atualiza o meu numero de seq
                    pacote.setAck(true);

                    byte pkt[] = Pacote.converterPacoteEmByte(pacote);
                    DatagramPacket Dack = new DatagramPacket(pkt, pkt.length, c.IPAddress, c.portaDoServidor);
                    try {
                        c.clienteUDP.send(Dack);
                    } catch (IOException ex) {
                        System.out.println("erro ao tentar enviar a janela de pacotes");
                    }
                }

                //se for um ack entao vou olhar o array list para ver se bate com algum 
                //tem q arrumar um meio de verificar onde esta o pacote pq dependendo onde esteja eu confirmo os outros 
            } catch (IOException ex) {
                System.out.println("erro ao tentar receber pacote na thread");
            }

        }
    }

}
