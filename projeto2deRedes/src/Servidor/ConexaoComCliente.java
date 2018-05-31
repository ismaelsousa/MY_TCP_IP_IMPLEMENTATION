/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import static Servidor.Server.idDosClientes;
import cliente.Cliente;
import cliente.NoCliente;
import cliente.Thread_Envia_Pacote;
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
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;
import pacote.Tread10secsServer;

/**
 *
 * @author ismae
 */
public class ConexaoComCliente extends Thread {

    //vou guardar aqui todos os pedaços que me for enviado
    public ArrayList<byte[]> pedacoDoArq = new ArrayList<>();
    public NoCliente noCliente;
    public int meuNumSeq = 4321;
    public DatagramSocket datagram;
    public static int portaUDP = 7000;
    public int id;
    boolean ciclo = true;

    public ConexaoComCliente(NoCliente noCliente) {

        //atualiza para cada novo cliente
        portaUDP++;

        try {            
            this.datagram = new DatagramSocket(portaUDP);
        } catch (SocketException ex) {
            System.err.println("erro ao tentar abri cliente nessa porta:" + portaUDP);
        }
        this.noCliente = noCliente;
        //colocar o id da comunicaão aqui
        this.id = noCliente.getId();
        this.start();
    }

    @Override
    public void run() {
        Pacote p = handShake();
        Timer timeOut = new Timer();
        timeOut.schedule(new Thread_Envia_Pacote_conexao(datagram, p, noCliente), 0, 500);
        boolean verificar = true;
        Pacote ack;
        while (ciclo) {
            //a verificação do 10 secs está neste método esperaPacote
            Pacote dado = EsperaPacote();

            if (verificar) {
                timeOut.cancel();
                timeOut.purge();
                timeOut = null;
                verificar = false;
            }
            //aqui eu terei que fazer um while que vai receber varios pacotes direto 
            //para cada pacote eu irei criar um ack 
            try {
                if (dado.isFyn()) {
                    //responde o fyn do cliente com ack
                    Pacote ackDofyn = new Pacote(true, true, false);
                    ackDofyn.setConnectionID(id);
                    ackDofyn.setSequenceNumber(meuNumSeq);

                    Timer time = new Timer();
                    time.schedule(new Thread_Envia_Pacote_conexao(datagram, ackDofyn, noCliente), 0, 300);

                    Pacote ackDoC = null;
                    while (ackDoC == null) {
                        ackDoC = EsperaPacote();
                        if (ackDoC.isAck() == true) {
                            break;
                        } else {
                            ackDoC = null;
                        }
                    }
                    time.cancel();
                    time.purge();
                    //chegou ack 
                    //agora eu crio o arquivo na pasta 
                    CriarArquivo(1);
                    System.out.println("arquivo salvo na pasta");
                    break;
                }
                
                if (noCliente.getNumSecCliente() == dado.getSequenceNumber()) {
                    //se for o numero de sequencia que eu estou esperando

                    //add no arryalist os dados que vao ser convertidos
                    pedacoDoArq.add(dado.getPayload());

                    noCliente.setNumSecCliente(noCliente.getNumSecCliente() + Cliente.tamanhoDeUmPacote);

                    p = ack = new Pacote(true, false, false);
                    ack.setSequenceNumber((meuNumSeq += Cliente.tamanhoDeUmPacote));
                    ack.setAckNumber(noCliente.getNumSecCliente());
                    EnviarPacoteCliente(p);         

                } else if (p != null) {//caso chegue outro pacotes que eu n esteja esperando eu reenvio                 
                    EnviarPacoteCliente(p);                    
                }
            } catch (Exception ex) {

            }
        }

        try {
            this.finalize();
        } catch (Throwable ex) {            
        }

    }

    public Pacote EsperaPacote() {
        byte dataReceive3[] = new byte[Cliente.tamanhoDeUmPacote];
        DatagramPacket pkt3 = new DatagramPacket(dataReceive3, dataReceive3.length);
        try {
            //inicia o tempozirador enquanto espera um pacote 
            Timer dezSecs = new Timer();
            dezSecs.schedule(new Tread10secsServer(this), 10000, 10000);
            
            datagram.receive(pkt3);
            
            //chegou cancela 
            dezSecs.cancel();
            dezSecs.purge();
            
        } catch (IOException ex) {
            System.err.println("conexao fechou com o cliente:" + noCliente.getId());
            ciclo = false;
        }

        return converterByteParaPacote(dataReceive3);
    }

    public void EnviarPacoteCliente(Pacote p) {
        byte byteDoAck[] = Pacote.converterPacoteEmByte(p);

        try {
            datagram.send(new DatagramPacket(byteDoAck, byteDoAck.length, noCliente.getIPAddress(), noCliente.getPorta()));
        } catch (IOException ex) {
            
            this.ciclo = false;
            try {
                this.finalize();
            } catch (Throwable ex1) {
               
            }            
            System.exit(0);
        }
    }

    public Pacote handShake() {
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
       
        Timer timeOut = new Timer();
        timeOut.schedule(new Thread_Envia_Pacote_conexao(datagram, p1, noCliente), 0, 500);

        Timer dezSecs = new Timer();
        dezSecs.schedule(new Tread10secsServer(this), 10000, 10000);

        Pacote pAckC;
        do {
            //////esperar o ack do cliente 
            byte dataReceive3[] = new byte[Cliente.tamanhoDeUmPacote];
            DatagramPacket pkt3 = new DatagramPacket(dataReceive3, dataReceive3.length);
            try {
                datagram.receive(pkt3);
            } catch (IOException ex) {
                
            }

            pAckC = converterByteParaPacote(dataReceive3);
            //espero receber meu numero de sequencia no ack , estou esperando o 4322         
        } while (pAckC != null && !pAckC.isAck());

        dezSecs.cancel();
        dezSecs.purge();

        timeOut.cancel();
        timeOut.purge();

        //vou começar a reeber os pacotes de dados 
        Pacote p = new Pacote(true, false, false);
        p.setConnectionID(id);
        p.setSequenceNumber(meuNumSeq);
        meuNumSeq += Cliente.tamanhoDeUmPacote;
        //mando o pacote pedindo os dados
        p.setAckNumber(noCliente.getNumSecCliente() + Cliente.tamanhoDeUmPacote);
        //atualizo qual eu vou esperar receber
        noCliente.setNumSecCliente(noCliente.getNumSecCliente() + Cliente.tamanhoDeUmPacote);
        return p;

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

        } catch (ClassNotFoundException e) {

        }

        return null;
    }

    public void CriarArquivo(int op) {
        if (op == 1) {
            //crio o arquivo na pasta 
            String nome = Server.caminho + "thread-" + id + "-criou esse arquivo" + ".pdf";
            System.out.println(nome);
            File SalvaNoDiretorio = new File(nome);
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
            } catch (IOException ex) {
                System.out.println("erro ao tentar criar arquivo");
            }

        } else {
            String nome = Server.caminho + "thread-" + id + "-ERRO" + ".err";
            System.out.println(nome);
            File SalvaNoDiretorio = new File(nome);
            byte a[] = "err".getBytes();
            try {
                Files.write(SalvaNoDiretorio.toPath(), a);
            } catch (IOException ex) {
                
            }
        }
    }

}
