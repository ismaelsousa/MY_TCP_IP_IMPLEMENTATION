/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import static Servidor.Server.idDosClientes;
import cliente.Cliente;
import cliente.NoCliente;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class ConexaoComCliente extends Thread {

    //vou guardar aqui todos os pedaços que me for enviado
    private ArrayList<byte[]> pedacoDoArq = new ArrayList<>();
    private NoCliente noCliente;
    private int meuNumSeq = 1;
    private DatagramSocket datagram;
    public static int portaUDP = 7000;
    private int id;

    public ConexaoComCliente(NoCliente noCliente) {

        //atualiza para cada novo cliente
        portaUDP++;

        try {
            System.out.println(portaUDP);
            this.datagram = new DatagramSocket(portaUDP);
        } catch (SocketException ex) {
            System.out.println("erro ao tentar abri cliente nessa porta:" + portaUDP);
        }
        this.noCliente = noCliente;
        //colocar o id da comunicaão aqui
        this.id = noCliente.getId();
        this.start();
    }

    @Override
    public void run() {

        handShake();

        //vou começar a reeber os pacotes de dados 
        Pacote p = new Pacote(true, false, false);
        p.setConnectionID(id);
        p.setSequenceNumber(meuNumSeq);
        meuNumSeq += Cliente.tamanhoDeUmPacote;
        //mando o pacote pedindo os dados
        p.setAckNumber(noCliente.getNumSecCliente() + Cliente.tamanhoDeUmPacote);
        //atualizo qual eu vou esperar receber
        noCliente.setNumSecCliente(noCliente.getNumSecCliente() + Cliente.tamanhoDeUmPacote);

        EnviarPacoteCliente(p);
        Pacote ack;
        while (true) {
            Pacote dado = EsperaPacote();
            //aqui eu terei que fazer um while que vai receber varios pacotes direto 
            //para cada pacote eu irei criar um ack 

            System.out.println("estou esperando :" + noCliente.getNumSecCliente() + "\n chegou: " + dado.getSequenceNumber());
            if (noCliente.getNumSecCliente() == dado.getSequenceNumber()) {
                //add no arryalist os dados que vao ser convertidos
                pedacoDoArq.add(dado.getPayload());

                //atualizo qual eu vou esperar receber
                noCliente.setNumSecCliente(noCliente.getNumSecCliente() + Cliente.tamanhoDeUmPacote);

                p =ack = new Pacote(true, false, false);
                ack.setSequenceNumber((meuNumSeq += Cliente.tamanhoDeUmPacote));
                ack.setAckNumber(noCliente.getNumSecCliente());
                EnviarPacoteCliente(p);
                System.out.println("recebi vamos para outro");

            } else if(p != null){
                EnviarPacoteCliente(p);
                System.out.println("enviei o ack repetido: "+p.getAckNumber());
            }
        }
    }

    public Pacote EsperaPacote() {
        byte dataReceive3[] = new byte[Cliente.tamanhoDeUmPacote];
        DatagramPacket pkt3 = new DatagramPacket(dataReceive3, dataReceive3.length);
        try {
            datagram.receive(pkt3);
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:" + noCliente.getId());
        }

        return converterByteParaPacote(dataReceive3);
    }

    public void EnviarPacoteCliente(Pacote p) {
        byte byteDoAck[] = Pacote.converterPacoteEmByte(p);

        try {
            datagram.send(new DatagramPacket(byteDoAck, byteDoAck.length, noCliente.getIPAddress(), noCliente.getPorta()));
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:" + noCliente.getId());
        }
    }

    public void handShake() {
        ////////////pacote com o id do cliente, synAck , numero de sequencia do serve, num do ack
        Pacote p1 = new Pacote();
        //colocar o id dele
        p1.setConnectionID(idDosClientes);
        //setar as flags
        p1.setSyn(true);
        p1.setAck(true);

        //informo o numero de sequencia que eu quero
        p1.setSequenceNumber(meuNumSeq);

        //aqui eu pego o numero de sequencia que ele me mandou + 1 e coloco no ack para indicar a resposta 
        p1.setAckNumber(noCliente.getNumSecCliente() + 1);

        //atualizo o numero de sequencia para saber qual o pacote irei mandar vai para 12346
        noCliente.setNumSecCliente(noCliente.getNumSecCliente() + 1);

        //vai para 4322
        meuNumSeq++;

        //estou usando os bits notUsed para informar a porta que ele deve mandar os pacotes de dados        
       
        System.out.println("   seq:" + p1.getSequenceNumber() + " ack:" + p1.getAckNumber() + " id:" + p1.getConnectionID() + " syn | ack :" + p1.isSyn() + "|" + p1.isAck());
        System.out.println("------------------------------------------->");

        byte dataReceive2[] = converterPacoteEmByte(p1);
        DatagramPacket pkt2 = new DatagramPacket(dataReceive2, dataReceive2.length, noCliente.getIPAddress(), noCliente.getPorta());
        try {
            datagram.send(pkt2);
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:" + noCliente.getId());
        }

        //////esperar o ack do cliente 
        byte dataReceive3[] = new byte[Cliente.tamanhoDeUmPacote];
        DatagramPacket pkt3 = new DatagramPacket(dataReceive3, dataReceive3.length);
        try {
            datagram.receive(pkt3);
        } catch (IOException ex) {
            System.out.println("erro ao tentar enviar o pacote ao cliente:" + noCliente.getId());
        }

        Pacote pAckC = converterByteParaPacote(dataReceive3);
        //espero receber meu numero de sequencia no ack , estou esperando o 4322
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

    private void CriarArquivo() {
        //crio o arquivo na pasta 
        File SalvaNoDiretorio = new File(id + ".mp4");
        //crio um array para guardar os dados por completo
        byte junto[] = new byte[pedacoDoArq.size() * 512];
        //esse i vai contar cada pedaco de pacote 
        int i = 0;
        //posicao vai andar de acordo com cada byte que vai ser colocado no vetor de byte completo
        int posicao = 0;
        while (i < pedacoDoArq.size()) {
            for (int j = 0; j < pedacoDoArq.get(i).length; j++) {
                junto[posicao] = pedacoDoArq.get(i)[j];
                posicao++;
            }
            i++;
        }
        try {
            //chamo essa funcao da biblioteca para salvar todo arquivo
            Files.write(SalvaNoDiretorio.toPath(), junto);
            System.out.println("salvei o arquivo");
        } catch (IOException ex) {
            System.out.println("erro ao tentar criar arquivo");
        }

    }

}
