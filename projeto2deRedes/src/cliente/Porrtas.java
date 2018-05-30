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

    private static Porrtas instancia;
    int portasClientes = 20000;

    private Porrtas() {

    }

    public static synchronized Porrtas getIntance() {
        if(instancia == null){
            instancia = new Porrtas();
        }
        return instancia;
    }
    
    

    public static void main(String[] args) {       
        System.out.println("porta:" + Porrtas.getIntance().portasClientes);
        Porrtas.getIntance().portasClientes++;
        
        System.out.println("porta:" + Porrtas.getIntance().portasClientes);
        Porrtas.getIntance().portasClientes++;
        
        System.out.println("porta:" + Porrtas.getIntance().portasClientes);
        Porrtas.getIntance().portasClientes++;
        
        System.out.println("porta:" + Porrtas.getIntance().portasClientes);
        Porrtas.getIntance().portasClientes++;
        
        System.out.println("porta:" + Porrtas.getIntance().portasClientes);
        Porrtas.getIntance().portasClientes++;
        
    }

}
