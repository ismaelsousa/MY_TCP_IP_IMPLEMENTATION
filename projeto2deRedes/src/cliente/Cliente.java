/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class Cliente {

    String hostName;
    int porta;
    String caminho;
    DatagramSocket clienteUDP;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public DatagramSocket getClienteUDP() {
        return clienteUDP;
    }

    public void setClienteUDP(DatagramSocket clienteUDP) {
        this.clienteUDP = clienteUDP;
    }

    public Cliente(String hostName, int porta, String caminho) {
        this.hostName = hostName;
        this.porta = porta;
        this.caminho = caminho;
        try {
            clienteUDP = new DatagramSocket(porta);
        } catch (SocketException ex) {
            System.out.println("erro: não foi possivel abrir o datagramSocket no cliente na porta" + porta);
        }
    }

    public static void main(String[] args) throws IOException {
        //criando a propria instancia da classe cliente        
        Cliente c = new Cliente("localhost", 5556, "assd");
        
        //vou começar enviando um pacote com o numero de sequencia, ack = 0, id=0 e SYN ativo
        Pacote syn = new Pacote();
        syn.getCabecalho().setNotused("SYN");
        byte envio[] = converterPacoteEmByte(syn);
        //pega o ip
        InetAddress IPAddress = InetAddress.getByName("localhost");
        //criar o datagram para enviar para o servidor
        DatagramPacket pkt = new DatagramPacket(envio, envio.length, IPAddress, 5555);
        
        c.clienteUDP.send(pkt);
        
       
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

}
