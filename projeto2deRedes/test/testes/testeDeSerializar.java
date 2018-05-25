/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import pacote.Pacote;

/**
 *
 * @author ismae
 */
public class testeDeSerializar {

    public static void main(String[] args) {
        Pacote p = new Pacote();
        p.setAckNumber(123);
        p.setSequenceNumber(321);
        byte pconver[] = converterPacoteEmByte(p);
        System.out.println("agora o p tem:"+pconver.length);
        System.out.println(pconver.length);
        System.out.println("passei para bytes");
        Pacote n = converterByteParaPacote(pconver);
        System.out.println("passei para pacote");
        System.out.println(n.getAckNumber());
        System.out.println(n.getSequenceNumber());

    }

    private static byte[] converterPacoteEmByte(Pacote pkt) {
        try {
            //cria um  array de byte  que irei passar para o objectOutput para retornar o byte[] , 
            //o pacote tem q implementar o Serializable
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ObjectOutputStream ous;
            ous = new ObjectOutputStream(bao);//o objectOutputStream recebe um byteArrayOutputStream que vai retornar o array de bytes
            ous.writeObject(pkt);//converte e coloca no bao
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
