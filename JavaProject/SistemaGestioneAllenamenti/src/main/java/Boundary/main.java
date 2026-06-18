package Boundary;

import Entity.AppSport;
import Control.GestoreUtenti;
import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // 1. Inizializzazione dell'architettura di base (Facade e Controller)
        AppSport facade = new AppSport();
        GestoreUtenti controller = new GestoreUtenti(facade);

        // 2. Lancio della prima interfaccia usando il thread sicuro di Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Benvenuto - Sistema Allenamenti");

                // Istanzia la GUI iniziale passandole il controller
                GuiAccessoRegistrazione guiIniziale = new GuiAccessoRegistrazione(controller);

                frame.setContentPane(guiIniziale.getContentPane());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(350, 250); // Dimensione fissa
                frame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
                frame.setVisible(true);
            }
        });
    }
}