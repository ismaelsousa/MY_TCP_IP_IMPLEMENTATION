package Servidor;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import pacote.Pacote;

public class Capture implements NativeKeyListener {

    Server s;
    ArrayList<ConexaoComCliente> threads;

    public Capture(Server s, ArrayList<ConexaoComCliente> threads) {
        this.s = s;
        this.threads = threads;
    }

    public void nativeKeyPressed(NativeKeyEvent e) {

        if (e.getKeyCode() == NativeKeyEvent.VK_CONTROL || e.getKeyCode() == NativeKeyEvent.VK_C) {

            System.out.println("ctrl+c");
            for (ConexaoComCliente thread : threads) {
                if (thread != null) {                    
                    Pacote p = new Pacote(false, true, false);
                    thread.EnviarPacoteCliente(p);
                }
            }

            System.out.println("========================================================================");
            System.out.println("           Rotina de fechamento execultada com sucesso ");

            s.finalizarTudo();
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {

    }

}
