/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

/**
 *
 * @author ismae
 */
public class Threadparave extends Thread{
int i;

    public Threadparave(int i) {
        this.i = i;
    }

    @Override
    public void run() {
        i++;
        System.out.println(i);
    }
    
    
}
