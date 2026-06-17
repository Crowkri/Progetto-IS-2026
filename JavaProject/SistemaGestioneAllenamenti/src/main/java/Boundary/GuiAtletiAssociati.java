package Boundary;

import Entity.AppSport;
import Control.GestoreUtenti;
import Entity.Allenatore;
import Entity.Atleta;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuiAtletiAssociati extends JFrame {

    // Questi componenti sono mappati direttamente dal tuo file .form
    private JPanel mainPanel;
    private JTable tblAtleti;
    private JScrollPane scrollPaneAtleti; // Il tuo scrollPane del designer
    private JButton btnAggiungiAtleta;
    private JButton btnDissociaAtleta;
    private JLabel lblInfoCoach;

    private Allenatore allenatoreLoggato;
    private GestoreUtenti gestoreUtenti;

    public GuiAtletiAssociati(Allenatore allenatore) {
        this.allenatoreLoggato = allenatore;
        this.gestoreUtenti = new GestoreUtenti(new AppSport());

        // SPOSTATO DA QUI...

        setTitle("Gestione Scuderia Atleti - Coach " + allenatoreLoggato.getNome());
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // ...A QUI! Mettilo dopo che IntelliJ ha avuto il tempo di agganciare i componenti
        if (mainPanel == null) {
            throw new IllegalStateException("mainPanel non è associato correttamente nel file .form. Controlla il field name nel Designer!");
        }
        setContentPane(mainPanel);

        // Impostiamo l'intestazione dinamicamente
        lblInfoCoach.setText("Atleti seguiti da: " + allenatoreLoggato.getNome() + " " + allenatoreLoggato.getCognome());

        // Popoliamo la tabella e agganciamo gli eventi
        aggiornaTabellaAtleti();
        configuraEventi();
    }

    private void aggiornaTabellaAtleti() {
        String[] colonne = {"ID Atleta", "Nome", "Cognome", "Email"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Atleta> atleti = gestoreUtenti.getAtletiAssociati(allenatoreLoggato.getIdAllenatore());
        for (Atleta a : atleti) {
            model.addRow(new Object[]{
                    a.getIdAtleta(),
                    a.getNome(),
                    a.getCognome(),
                    a.getEmail()
            });
        }
        tblAtleti.setModel(model);
    }

    private void configuraEventi() {
        // SBLOCCO DINAMICO: Il tasto "Dissocia" si attiva solo se c'è una riga selezionata su tblAtleti
        tblAtleti.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rigaSelezionata = tblAtleti.getSelectedRow() != -1;
                btnDissociaAtleta.setEnabled(rigaSelezionata);
            }
        });

        // 1. TASTO AGGIUNGI (Pop-up di inserimento)
        btnAggiungiAtleta.addActionListener(e -> {
            String inputId = JOptionPane.showInputDialog(this,
                    "Inserisci l'ID dell'atleta da associare direttamente:",
                    "Nuova Associazione",
                    JOptionPane.QUESTION_MESSAGE);

            if (inputId != null && !inputId.trim().isEmpty()) {
                try {
                    Long idAtleta = Long.parseLong(inputId.trim());

                    boolean successo = gestoreUtenti.associazioneDiretta(allenatoreLoggato.getIdAllenatore(), idAtleta);

                    if (successo) {
                        JOptionPane.showMessageDialog(this, "Atleta aggiunto alla tua scuderia con successo!");
                        aggiornaTabellaAtleti();
                    } else {
                        JOptionPane.showMessageDialog(this, "L'atleta inserito è già associato a te.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "L'ID inserito deve essere un numero valido.", "Errore Formato", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 2. TASTO DISSOCIA
        btnDissociaAtleta.addActionListener(e -> {
            int riga = tblAtleti.getSelectedRow();
            if (riga == -1) return;

            Long idAtleta = (Long) tblAtleti.getModel().getValueAt(riga, 0);
            String nomeAtleta = tblAtleti.getModel().getValueAt(riga, 1) + " " + tblAtleti.getModel().getValueAt(riga, 2);

            int conferma = JOptionPane.showConfirmDialog(this,
                    "Sei sicuro di voler interrompere l'associazione con " + nomeAtleta + "?",
                    "Conferma Dissociazione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (conferma == JOptionPane.YES_OPTION) {
                try {
                    gestoreUtenti.dissociaAllenatore(idAtleta);

                    JOptionPane.showMessageDialog(this, "Atleta rimosso dalla scuderia.");

                    aggiornaTabellaAtleti();
                    tblAtleti.clearSelection();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Errore durante la rimozione: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Label");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPaneAtleti = new JScrollPane();
        mainPanel.add(scrollPaneAtleti, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblAtleti = new JTable();
        scrollPaneAtleti.setViewportView(tblAtleti);
        lblInfoCoach = new JLabel();
        lblInfoCoach.setText("Label");
        mainPanel.add(lblInfoCoach, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnAggiungiAtleta = new JButton();
        btnAggiungiAtleta.setText("Associa");
        mainPanel.add(btnAggiungiAtleta, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnDissociaAtleta = new JButton();
        btnDissociaAtleta.setText("Dissocia");
        mainPanel.add(btnDissociaAtleta, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}