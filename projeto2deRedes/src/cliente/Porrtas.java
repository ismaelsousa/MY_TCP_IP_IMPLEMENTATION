/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

/**
 *
 * @author ismae
 */
public class Porrtas {

    public static int portasClientes = 20000;
    
    public static void main(String[] args) {
        Porrtas p = new Porrtas();
        System.out.println("porta:"+Porrtas.portasClientes);
        Porrtas.portasClientes++;
        System.out.println("porta:"+Porrtas.portasClientes);
        Porrtas.portasClientes++;
        Porrtas p2 = new Porrtas();
        System.out.println("porta:"+Porrtas.portasClientes);
        Porrtas.portasClientes++;
        System.out.println("porta:"+Porrtas.portasClientes);
        Porrtas.portasClientes++;
        System.out.println("porta:"+Porrtas.portasClientes);
    }

}
