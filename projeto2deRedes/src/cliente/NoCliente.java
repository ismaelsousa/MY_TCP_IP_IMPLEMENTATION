/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.net.InetAddress;

/**
 *
 * @author ismae
 */
public class NoCliente {

    private int id;
    private int porta;
    private InetAddress IPAddress;
    private int numSecCliente;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public InetAddress getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(InetAddress IPAddress) {
        this.IPAddress = IPAddress;
    }

    public int getNumSecCliente() {
        return numSecCliente;
    }

    public void setNumSecCliente(int numSecCliente) {
        this.numSecCliente = numSecCliente;
    }

    

    public NoCliente(int id, int porta, InetAddress IPAddress, int num) {
        this.id = id;
        this.porta = porta;
        this.IPAddress = IPAddress;
        this.numSecCliente = num;
    }

}
