/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ismae
 */
public class testeTarefa {
    public static void main(String ags[]){
        Timer timeOut = new Timer();
        
        Tarefa t = new Tarefa();
        
        timeOut.schedule(t, 0, 1000);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(testeTarefa.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.err.println("acordou");
        timeOut.cancel();
        timeOut.purge();
               
        System.out.println("cancelou a tarefa agora vou colocar outra");
        timeOut = new Timer();
        timeOut.schedule(new Tarefa(), 0,3000);
        
        
    }
}
