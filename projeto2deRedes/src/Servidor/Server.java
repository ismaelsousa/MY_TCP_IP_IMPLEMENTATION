/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import cliente.NoCliente;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class Server {

    public static int idDosClientes = 0;
    public static int portaUDPs;
    private DatagramSocket servidorUDP;

    public Server(int porta, String caminho) {
        try {
            portaUDPs = porta;
            System.out.println(portaUDPs);
            servidorUDP = new DatagramSocket(porta);
        } catch (SocketException ex) {
            System.out.println("Erro ao tentar criar o servidor na porta " + porta);
        }
    }

    public static void main(String[] args) {
        //criando a propria class
        Server server = new Server(5555, "");
        System.out.println("criei o servidor");
        //lista de threads 
        ArrayList<ConexaoComCliente> threads = new ArrayList();
        
        byte dataReceive[] = new byte[675];
        DatagramPacket pkt = new DatagramPacket(dataReceive, dataReceive.length);  
        System.out.println("criei o pacote que eu vou esperar");
        try {
            while (true) {
                /////////////////////////////espera a conexao
                System.out.println("estou esperando conexao");
                server.servidorUDP.receive(pkt);
                System.out.println("a porta que chegou foi:" + pkt.getPort());
                System.out.println("chegou vou converter");
                Pacote p = converterByteParaPacote(pkt.getData());
                System.out.println("   seq:" + p.getSequenceNumber() + " Ack:" + p.getAckNumber() + " id:" + p.getConnectionID());
                System.out.println("<-------------------------------------------");
                
                /////////////////////se for um syn entao vamos estabelecer a conexao
                if (p.isSyn()) {
                  NoCliente novo = new NoCliente(++idDosClientes, pkt.getPort(), pkt.getAddress(), p.getSequenceNumber());
                  ConexaoComCliente thread = new ConexaoComCliente(novo);                                  
                }else{
                   
                    
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
