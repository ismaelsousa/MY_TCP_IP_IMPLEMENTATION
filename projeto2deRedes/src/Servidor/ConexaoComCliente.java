/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import static Servidor.Server.idDosClientes;
import cliente.NoCliente;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class ConexaoComCliente extends Thread {

    private NoCliente noCliente;
    private DatagramSocket datagram;
    public static int portaUDP = 7000;

    public ConexaoComCliente(NoCliente noCliente) {
        portaUDP++;
        try {            
            System.out.println(portaUDP);
            this.datagram = new DatagramSocket(portaUDP);
        } catch (SocketException ex) {
            Logger.getLogger(ConexaoComCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.noCliente = noCliente;
    }

    @Override
    public void run() {
        ////////////pacote com o id do cliente, synAck , numero de sequencia do serve, num do ack

        Pacote p1 = new Pacote();
        p1.setConnectionID(idDosClientes);
        p1.setSyn(true);
        p1.setAck(true);
        p1.setSequenceNumber(4321);
        p1.setAckNumber(noCliente.getNumSecInicial() + 1);
        p1.setNotUsed(portaUDP);
        System.out.println("   seq:" + p1.getSequenceNumber() + " ack:" + p1.getAckNumber() + " id:" + p1.getConnectionID() + " syn | ack :" + p1.isSyn() + "|" + p1.isAck());
        System.out.println("------------------------------------------->");

        byte dataReceive2[] = converterPacoteEmByte(p1);
        DatagramPacket pkt2 = new DatagramPacket(dataReceive2, dataReceive2.length, noCliente.getIPAddress(), noCliente.getPorta());
        try {
            datagram.send(pkt2);
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:"+noCliente.getId());
        }

        //////esperar o ack do cliente 
        byte dataReceive3[] = new byte[675];
        DatagramPacket pkt3 = new DatagramPacket(dataReceive3, dataReceive3.length);
        try {
            datagram.receive(pkt3);
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:"+noCliente.getId());
        }

        Pacote pAckC = converterByteParaPacote(dataReceive3);
        System.out.println("   seq=" + pAckC.getSequenceNumber() + " ack:" + pAckC.getAckNumber() + " id:" + pAckC.getConnectionID() + " é ack:" + pAckC.isAck());
        System.out.println("<-------------------------------------------");

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
