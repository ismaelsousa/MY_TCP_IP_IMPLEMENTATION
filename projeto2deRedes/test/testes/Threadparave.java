/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.util.ArrayList;

/**
 *
 * @author ismae
 */
public class Threadparave extends Thread {

    ArrayList i;

    public Threadparave(ArrayList i) {
        this.i = i;
    }

    @Override
    public void run() {
        i.remove(0);
        System.out.println("removi agor ao array esta com:" + i.size());

        System.out.println(i);
    }

}
