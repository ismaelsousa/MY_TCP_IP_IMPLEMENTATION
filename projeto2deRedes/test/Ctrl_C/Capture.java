package Ctrl_C;

import Servidor.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Capture implements NativeKeyListener {

    public Capture() {
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VK_CONTROL || e.getKeyCode() == NativeKeyEvent.VK_C) {
            System.out.println("apertou Ctrl + c");
            System.exit(0);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.getInstance().addNativeKeyListener(new Servidor.Capture());
        } catch (NativeHookException ex) {
         
        }
    }
}
