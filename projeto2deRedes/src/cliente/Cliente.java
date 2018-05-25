/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.awt.Event;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacote.Pacote;

//bibliotecas para pode salvar o arquivo 
import java.io.*;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Timer;

/**
 *
 * @author ismae
 */
public class Cliente {

    public static int portasClientes = 5556;
    public static int tamanhoDeUmPacote = 661;
    private ArrayList<byte[]> pedacoDoArq = new ArrayList<>();
    private ArrayList<Pacote> pacotes = new ArrayList<>();

    InetAddress IPAddress = null;
    private String hostName;
    private int porta;
    public DatagramSocket clienteUDP;
    private String caminho;
    int id;
    int meuNumeroDeSeq = 1;
    int numSeqServer;

    int portaDoServidor;

    public Cliente(String hostName, int porta, String caminho) {
        this.hostName = hostName;
        this.porta = porta;
        this.caminho = caminho;

        //pega o ip      
        try {
            IPAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException ex) {
            System.out.println("erro a tentar traduzir o nome em ip");
        }

        quebrarArquivo();

        try {
            clienteUDP = new DatagramSocket(++portasClientes);
        } catch (SocketException ex) {
            System.out.println("erro: não foi possivel abrir o datagramSocket no cliente na porta" + porta);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //criando a propria instancia da classe cliente        
        Cliente c = new Cliente("localhost", ++portasClientes, "C:\\Users\\ismae\\Google Drive\\ufc\\4 semestre\\redes\\parei pag 22.txt");
        try {
            c.handShake();
        } catch (IOException ex) {
            System.out.println("erro ao fazer handshake");
        }

        System.out.println("o numero do servidor é :" + c.numSeqServer);
        byte dataReceive[] = new byte[tamanhoDeUmPacote];
        DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
        c.clienteUDP.receive(receive);
        Pacote pedidoDeDados = converterByteParaPacote(dataReceive);

        System.out.println("seq:" + pedidoDeDados.getSequenceNumber() + " ack:" + pedidoDeDados.getAckNumber() + " id:" + pedidoDeDados.getConnectionID());
        System.out.println("<-------------------------------------------");

        System.out.println("o meu numero de seq esta em;" + c.getMeuNumeroDeSeq());
        System.out.println("o numero de seq do servidor esta em:" + c.getNumSeqServer());

        //criei os pacotess 
        for (int i = 0; i < c.pedacoDoArq.size(); i++) {//se repete até acabar todos os pacotes             
            Pacote pacote = new Pacote();
            //coloca o id de conexao
            pacote.setConnectionID(c.id);
            //coloca no pacote o numero de seq
            pacote.setSequenceNumber(c.meuNumeroDeSeq + tamanhoDeUmPacote);
            //aqui atualiza o meu numero de seq
            c.setMeuNumeroDeSeq(c.getMeuNumeroDeSeq() + tamanhoDeUmPacote);
            //pega os bytes e coloca no pacote
            pacote.setPayload(c.pedacoDoArq.get(i));
            System.out.println("coloquei o pacote com numero de seq:" + c.getMeuNumeroDeSeq() + " espero ack:" + pacote.getAckNumber());
            c.pacotes.add(pacote);
        }

        System.out.println("pacote: " + c.pacotes.size());
        //*******************************************************************************************************************************************
        //*******************************************************************************************************************************************
        //****************                                                                                      *************************************
        //*****************                precisdoooo mudarrrrrr issoooooooooooooooooooooo                    **************************************
        //*****************                    fazer uma nova janela                                            *************************************
        //*****************                                                                                    **************************************
        //*******************************************************************************************************************************************
        //*******************************************************************************************************************************************
        //*******************************************************************************************************************************************
        //*******************************************************************************************************************************************

        //essa thread vai ficar ouvindo na porta, todos pacotes que chegarem serão encaminhados para o array
        System.out.println("criei o ouvindo");
        OuviServidor thread = new OuviServidor(c);
        int base = 0;
        int nextSeqNum = 0;
        int tamanho_da_janela = 1;
        int thress = 10000;
        boolean primeiraVez = true;//serve para saber se já enviou o ultimo pacote
        Timer timeOut = new Timer();
        Thread_Envia_Pacote enviador;

        while (nextSeqNum < c.pacotes.size()) {

            System.out.println("*********************************************************************");
            System.out.println("                                    base:" + base);
            System.out.println("                                    next:" + nextSeqNum);
            System.out.println("*********************************************************************");

            if (base == 0) {
                System.out.println("entrou no if de base == 0");
                nextSeqNum++;
                enviador = new Thread_Envia_Pacote(base, nextSeqNum, c);
                //envia os pacote e enicia o temporizador
                timeOut.schedule(enviador, 0, 500);

            } else if (base == c.pacotes.size() - 1) {//NO ULTIMO CASO ELE N ENVIA O ULTIMO PACOTE
                System.out.println("entrou na base igual ao final");
                enviador = new Thread_Envia_Pacote(base, nextSeqNum, c);
                //envia os pacote e enicia o temporizador
                timeOut = new Timer();
                timeOut.schedule(enviador, 0, 500);

            } else {
                System.out.println("aumentar o nextseqnum");
                int i = 0;
                //se nextseqnum + 1 for igual ao tamanho do arraylist então n entra
                while (i < tamanho_da_janela && (nextSeqNum + 1) < c.pacotes.size()) {
                    nextSeqNum++;
                    i++;
                }

                enviador = new Thread_Envia_Pacote(base, nextSeqNum, c);
                //envia os pacote e enicia o temporizador
                timeOut = new Timer();
                timeOut.schedule(enviador, 0, 500);
            }

            while (base != nextSeqNum || base == c.pacotes.size() - 1) {

                Pacote p = ThreadArrayCompartilhado.acessarArray(2, null);
                //coloca a threadd aquiiii
                if (p == null) {
                    Thread.sleep(100);
                }
                if (p != null) {//se tiver ack                     

                    System.out.println("removi o pacote ack");
                    if((base == (c.pacotes.size()-1)) && p.getAckNumber() > c.pacotes.get(base).getSequenceNumber()){
                        System.out.println("ultimo pacote confirmado");
                        break;
                    }

                    //aqui eu preciso atualizar a minha base de acordo com o ack, se for acumulativo ele vai ficar aumentando a base até o ack esperado
                    while (p.getAckNumber() > c.pacotes.get(base).getSequenceNumber() && p.getAckNumber() <= c.pacotes.get(c.pacotes.size() - 1).getSequenceNumber()) {
                        System.out.println("ack:" + p.getAckNumber() + " > base:" + c.pacotes.get(base).getSequenceNumber());
                        //para cada pacote confirmado eu aumento a janela 
                        if (base == c.pacotes.size() - 1) {
                            break;
                        }
                        tamanho_da_janela++;
                        base++;
                    }
                    if (base == c.pacotes.size() - 1 && primeiraVez == true) {
                        primeiraVez = false;
                        System.out.println(" 183 -Agora a a base está no ultimo pacote");
                        timeOut.cancel();
                        timeOut.purge();
                        //aumentar o nextseqnum para sair do while de envio
                        nextSeqNum++;
                        System.out.println("fim do envio ");
                        System.out.println("next:" + nextSeqNum);
                        System.out.println("base:" + base);

                        System.out.println("numero de sequencia do ultimo enviado:" + c.pacotes.get(base).getSequenceNumber());

                        enviador = new Thread_Envia_Pacote(base, nextSeqNum, c);
                        timeOut = new Timer();
                        timeOut.schedule(enviador, 0, 500);

                    } else if (base == nextSeqNum) {
                        System.out.println("195 -Parei temporizador ");
                        //para o envio repetitivo porque agora eu vou atualizar a janela
                        timeOut.cancel();
                        timeOut.purge();

                        System.out.println("vai cria uma nova janela");
                    } else {//se não é porque tem pacote sem ser reconhecidos ainda então preciso reenviar, então dou start para enviar esperando o 0.5 sec
                        System.out.println("reinicia o temporizador");
                        timeOut.cancel();
                        timeOut.purge();

                        enviador = new Thread_Envia_Pacote(base, nextSeqNum, c);
                        timeOut = new Timer();
                        timeOut.schedule(enviador, 500, 500);

                    }

                }
                
            }
            //para cancelar o envio repetitivo
            System.out.println("deu uma volta no while de fora ");
            timeOut.cancel();
            timeOut.purge();
        }
        System.out.println("terminou o envio uffa");
        timeOut.cancel();
        timeOut.purge();
        System.exit(0);
    }

    //n vou mais usar esse método , fiz na thread
    public void EnviarPacoteDeDados(ArrayList<Pacote> d) {
        for (int j = 0; j < d.size(); j++) {
            byte pkt[] = converterPacoteEmByte(d.get(j));
            DatagramPacket Dack = new DatagramPacket(pkt, pkt.length, IPAddress, portaDoServidor);
            try {
                clienteUDP.send(Dack);
            } catch (IOException ex) {
                System.out.println("erro ao tentar enviar a janela de pacotes");
            }
        }
    }

    public void handShake() throws IOException {
        //vou começar enviando um pacote com o numero de sequencia, ack = 0, id=0 e SYN ativo
        Pacote pacoteDeSicro = new Pacote();
        pacoteDeSicro.setSyn(true);
        pacoteDeSicro.setSequenceNumber(meuNumeroDeSeq++);
        System.out.println("seq:" + pacoteDeSicro.getSequenceNumber() + " ack:" + pacoteDeSicro.getAckNumber() + " id:" + pacoteDeSicro.getConnectionID() + " syn:" + pacoteDeSicro.isSyn());
        System.out.println("------------------------------------------->");
        byte envio[] = converterPacoteEmByte(pacoteDeSicro);

        //criar o datagram para enviar para o servidor
        DatagramPacket pkt = new DatagramPacket(envio, envio.length, IPAddress, 5555);

        clienteUDP.send(pkt);

        ///////////////////////esperar o retorno 
        byte dataReceive[] = new byte[tamanhoDeUmPacote];
        DatagramPacket receive = new DatagramPacket(dataReceive, dataReceive.length);
        clienteUDP.receive(receive);
        Pacote confirmacao = converterByteParaPacote(dataReceive);

        // passo o numerro da porta que irei usar para enviar o arq
        portaDoServidor = receive.getPort();
        id = confirmacao.getConnectionID();
        numSeqServer = confirmacao.getSequenceNumber();
        System.out.println("seq:" + confirmacao.getSequenceNumber() + " ack:" + confirmacao.getAckNumber() + " id:" + confirmacao.getConnectionID() + " ack:" + confirmacao.isAck() + " syn:" + confirmacao.isSyn());
        System.out.println("<-------------------------------------------");

        ////////////////////////////// envia o ack de confirmação
        Pacote ackDeSyn = new Pacote();
        ackDeSyn.setAck(true);
        ackDeSyn.setSequenceNumber(meuNumeroDeSeq);
        ackDeSyn.setAckNumber(++numSeqServer);
        ackDeSyn.setConnectionID(id);

        byte ack[] = converterPacoteEmByte(ackDeSyn);

        DatagramPacket Dack = new DatagramPacket(ack, ack.length, IPAddress, portaDoServidor);
        clienteUDP.send(Dack);

        System.out.println("seq:" + ackDeSyn.getSequenceNumber() + " ack:" + ackDeSyn.getAckNumber() + " id:" + ackDeSyn.getConnectionID() + " ack:" + ackDeSyn.isAck());
        System.out.println("------------------------------------------->");
    }

    public void CriarArquivo() {
        //crio o arquivo na pasta 
        File test = new File("parei pag 22.txt");
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
            Files.write(test.toPath(), junto);
            System.out.println("salvei o arquivo");

        } catch (IOException ex) {
            System.out.println("erro ao tentar criar arquivo");
        }

    }

    public void quebrarArquivo() {
        try {
            FileInputStream in = new FileInputStream(new File(caminho));
            BufferedInputStream bufferMusica = new BufferedInputStream(in);
            int n = 0;
            int i = 0;

            while (n != -1) {
                byte[] byteDoArquivo = new byte[512];
                n = bufferMusica.read(byteDoArquivo);
                pedacoDoArq.add(byteDoArquivo);
                System.out.println("quebrei pedaço do arquivo:" + i);
                i++;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Erro ao pegar o arquivo" + ex);
        } catch (IOException ex) {
            System.out.println("Erro IO" + ex);
        }

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

    public static int getPortasClientes() {
        return portasClientes;
    }

    public static void setPortasClientes(int portasClientes) {
        Cliente.portasClientes = portasClientes;
    }

    public ArrayList<byte[]> getPedacoDoArq() {
        return pedacoDoArq;
    }

    public void setPedacoDoArq(ArrayList<byte[]> pedacoDoArq) {
        this.pedacoDoArq = pedacoDoArq;
    }

    public ArrayList<Pacote> getPacotes() {
        return pacotes;
    }

    public void setPacotes(ArrayList<Pacote> pacotes) {
        this.pacotes = pacotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMeuNumeroDeSeq() {
        return meuNumeroDeSeq;
    }

    public void setMeuNumeroDeSeq(int meuNumeroDeSeq) {
        this.meuNumeroDeSeq = meuNumeroDeSeq;
    }

    public int getNumSeqServer() {
        return numSeqServer;
    }

    public void setNumSeqServer(int numSeqServer) {
        this.numSeqServer = numSeqServer;
    }

    public int getPortaDoServidor() {
        return portaDoServidor;
    }

    public void setPortaDoServidor(int portaDoServidor) {
        this.portaDoServidor = portaDoServidor;
    }

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

}
