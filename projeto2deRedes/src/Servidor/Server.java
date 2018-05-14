/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class Server {
    DatagramSocket servidor;
    public Server(int porta, String caminho) {
        try {           
            servidor = new DatagramSocket(porta);
        } catch (SocketException ex) {
            System.out.println("Erro ao tentar criar o servidor na porta "+ porta);
        }
    }
    
}
