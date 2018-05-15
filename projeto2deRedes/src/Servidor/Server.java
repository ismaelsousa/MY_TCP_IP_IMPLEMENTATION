/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
    
    private DatagramSocket servidorUDP;
    
    public Server(int porta, String caminho) {
        try {           
            servidorUDP = new DatagramSocket(porta);
        } catch (SocketException ex) {
            System.out.println("Erro ao tentar criar o servidor na porta "+ porta);
        }
    }
    
    public static void main(String[] args) {
       
        Server server = new Server(5555, "");
        byte dataReceive[] = new byte[100000];
        DatagramPacket pkt = new DatagramPacket(dataReceive, dataReceive.length);
        
        try {
            server.servidorUDP.receive(pkt);
            
            Pacote p = converterByteParaPacote(pkt.getData());
            System.out.println("o pacote deu certo :"+ p.getCabecalho().getNotused());
        } catch (IOException ex) {
            System.out.println("erro ao receber o pacote");
        }
        
    }
   
     private static Pacote converterByteParaPacote(byte[] clienteAsByte) {
         
               try {
                      ByteArrayInputStream bao = new ByteArrayInputStream(clienteAsByte);
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
