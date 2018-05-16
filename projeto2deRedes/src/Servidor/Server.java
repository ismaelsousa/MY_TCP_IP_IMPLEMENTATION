/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class Server {

    public static int idDosClientes = 0;

    private DatagramSocket servidorUDP;

    public Server(int porta, String caminho) {
        try {
            servidorUDP = new DatagramSocket(porta);
        } catch (SocketException ex) {
            System.out.println("Erro ao tentar criar o servidor na porta " + porta);
        }
    }

    public static void main(String[] args) {

        Server server = new Server(5555, "");
        System.out.println("criei o servidor");
        byte dataReceive[] = new byte[661];
        DatagramPacket pkt = new DatagramPacket(dataReceive, dataReceive.length);
        System.out.println("criei o pacote que eu vou esperar");
        try {
            while (true) {
                /////////////////////////////espera a conexap
                System.out.println("estou esperando conexao");
                server.servidorUDP.receive(pkt);
                System.out.println("a porta que chegou foi:" + pkt.getPort());
                System.out.println("chegou vou converter");
                Pacote p = converterByteParaPacote(pkt.getData());
                System.out.println("   seq:" + p.getSequenceNumber() + " Ack:" + p.getAckNumber() + " id:" + p.getConnectionID());
                System.out.println("<-------------------------------------------");
                
                /////////////////////se for um syn entao vamos estabelecer a conexao
                if (p.isSyn()) {
                    ////////////pacote com o id do cliente, synAck , numero de sequencia do serve, num do ack
                    Pacote p1 = new Pacote();
                    p1.setConnectionID(++idDosClientes);
                    p1.setSyn(true);
                    p1.setAck(true);
                    p1.setSequenceNumber(4321);
                    p1.setAckNumber(p.getSequenceNumber() + 1);
                    System.out.println("   seq:" + p1.getSequenceNumber() + " ack:" + p1.getAckNumber() + " id:" + p1.getConnectionID() + " syn | ack :" + p1.isSyn() + "|" + p1.isAck());
                    System.out.println("------------------------------------------->");

                    byte dataReceive2[] = converterPacoteEmByte(p1);
                    DatagramPacket pkt2 = new DatagramPacket(dataReceive2, dataReceive2.length, pkt.getAddress(), pkt.getPort());
                    server.servidorUDP.send(pkt2);

                    //////esperar o ack do cliente 
                    byte dataReceive3[] = new byte[661];
                    DatagramPacket pkt3 = new DatagramPacket(dataReceive3, dataReceive3.length);
                    server.servidorUDP.receive(pkt);

                    Pacote pAckC = converterByteParaPacote(dataReceive);
                    System.out.println("   seq=" + pAckC.getSequenceNumber() + " ack:" + pAckC.getAckNumber() + " id:" + pAckC.getConnectionID() + " é ack:" + pAckC.isAck());
                    System.out.println("<-------------------------------------------");

                }else{
                    ///////vou repassar o pacote para a thread com identificador semelhannte 
                    
                }
                
            }
        } catch (IOException ex) {
            System.out.println("erro ao receber o pacote");
        }

    }

    private static byte[] converterPacoteEmByte(Pacote pkt) {
        try {
            //cria um  array de byte  que irei passar para o objectOutput para retornar o byte[] , 
            //o pacote tem q implementar o Serializable
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream ous;
            ous = new ObjectOutputStream(bao);
            ous.writeObject(pkt);
            return bao.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("erro ao converte pacote em byte");
            e.printStackTrace();
        }

        return null;
    }

    private static Pacote converterByteParaPacote(byte[] pacote) {

        try {
            ByteArrayInputStream bao = new ByteArrayInputStream(pacote);
            ObjectInputStream ous;
            ous = new ObjectInputStream(bao);
            return (Pacote) ous.readObject();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
