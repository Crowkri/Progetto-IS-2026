package Boundary;

import Entity.Atleta;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiHomeAtleta extends JFrame {

    // Variabili mappate con il GUI Designer
    private JPanel mainPanel;
    private JLabel lblBenvenuto;
    private JComboBox<String> cmbFiltroStato;
    private JButton btnVisualizzaDettagli;
    private JButton btnRegistraEsecuzione;
    private JButton btnStatistiche;
    private JScrollPane tbscorrere; // Il pannello di scorrimento (JScrollPane)
    private JTable tblSessioni;     // La tabella vera e propria (JTable)

    private Atleta atletaLoggato;

    public GuiHomeAtleta(Atleta atleta) {
        this.atletaLoggato = atleta;

        // 1. Configurazione base della Finestra
        setTitle("Area Personale - " + atletaLoggato.getNome() + " " + atletaLoggato.getCognome());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500); // Leggermente più grande per far respirare la tabella
        setLocationRelativeTo(null);

        // 2. Inizializzazione dei dati (Tabella, Testi, Tendina)
        inizializzaDatiDellaHome();

        // 3. Configurazione dei click sui bottoni
        gestisciEventiBottoni();
    }

    private void inizializzaDatiDellaHome() {
        // Imposta il messaggio di benvenuto personalizzato
        lblBenvenuto.setText("Bentornato, " + atletaLoggato.getNome() + "!");
        lblBenvenuto.setFont(new Font(lblBenvenuto.getFont().getName(), Font.BOLD, 18));

        // Popola la tendina con i filtri di stato
        cmbFiltroStato.addItem("Tutte le sessioni");
        cmbFiltroStato.addItem("Assegnata");
        cmbFiltroStato.addItem("In_corso");
        cmbFiltroStato.addItem("Completata");

        // Configura le colonne della Tabella
        String[] colonne = {"Data", "Titolo Sessione", "Durata (min)", "Stato"};

        // DefaultTableModel ci permette di gestire facilmente le righe e le colonne
        DefaultTableModel tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non modificabile con il doppio click
            }
        };

        tblSessioni.setModel(tableModel);
        tblSessioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permette di selezionare solo una riga alla volta

        // Cambia i testi base dei bottoni per sicurezza
        btnVisualizzaDettagli.setText("Visualizza Esercizi");
        btnRegistraEsecuzione.setText("Registra Esecuzione");
        btnStatistiche.setText("Le Mie Statistiche");
    }

    private void gestisciEventiBottoni() {

        btnVisualizzaDettagli.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tblSessioni.getSelectedRow();
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(mainPanel, "Seleziona prima una sessione dalla tabella!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Apertura dettagli della sessione selezionata...");
                    // TODO: Passare l'ID della sessione selezionata alla nuova schermata
                }
            }
        });

        btnRegistraEsecuzione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rigaSelezionata = tblSessioni.getSelectedRow();
                if (rigaSelezionata == -1) {
                    JOptionPane.showMessageDialog(mainPanel, "Seleziona prima la sessione da eseguire!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Apertura schermata di inserimento performance...");
                    // TODO: Chiamare l'interfaccia per inserire rip. effettive e tempo
                }
            }
        });

        btnStatistiche.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainPanel, "Apertura Dashboard Statistiche dell'atleta...");
                // TODO: Creare e aprire la GuiDashboard
            }
        });

        // Evento quando l'atleta cambia il filtro dal menu a tendina
        cmbFiltroStato.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filtroSelezionato = (String) cmbFiltroStato.getSelectedItem();
                System.out.println("Filtro applicato: " + filtroSelezionato);
                // TODO: Chiedere al Facade/Controller di restituire solo le sessioni con questo stato
            }
        });
    }

    {
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(7, 1, new Insets(10, 10, 10, 10), -1, 10));

        lblBenvenuto = new JLabel();
        lblBenvenuto.setText("Label");
        mainPanel.add(lblBenvenuto, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        cmbFiltroStato = new JComboBox<String>();
        mainPanel.add(cmbFiltroStato, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        tbscorrere = new JScrollPane();
        mainPanel.add(tbscorrere, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 200), null, 0, false));

        tblSessioni = new JTable();
        tbscorrere.setViewportView(tblSessioni);

        btnVisualizzaDettagli = new JButton();
        btnVisualizzaDettagli.setText("Button");
        mainPanel.add(btnVisualizzaDettagli, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 35), null, 0, false));

        btnRegistraEsecuzione = new JButton();
        btnRegistraEsecuzione.setText("Button");
        mainPanel.add(btnRegistraEsecuzione, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 35), null, 0, false));

        btnStatistiche = new JButton();
        btnStatistiche.setText("Button");
        mainPanel.add(btnStatistiche, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 35), null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}