import Entity.AppSport;
import Control.GestoreUtenti;
import Control.GestoreSessioni;
import Control.GestoreDashboard;
import Boundary.GuiAccessoRegistrazione;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppSport facade = new AppSport();

                GestoreUtenti gestoreUtenti = new GestoreUtenti(facade);
                GestoreSessioni gestoreSessioni = new GestoreSessioni(facade);
                GestoreDashboard gestoreDashboard = new GestoreDashboard(facade);

                JFrame frame = new JFrame("Benvenuto - Sistema Allenamenti");


                GuiAccessoRegistrazione guiIniziale = new GuiAccessoRegistrazione(gestoreUtenti);
                frame.setContentPane(guiIniziale.getContentPane());

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);
                frame.setLocationRelativeTo(null); // Centra la finestra sullo schermo
                frame.setVisible(true);
            }
        });
    }
}