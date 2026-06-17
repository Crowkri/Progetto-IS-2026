package Boundary;

import Entity.AppSport;
import Control.GestoreSessioni;
import Control.GestoreUtenti;
import Entity.Allenatore;
import Entity.Atleta;
import Entity.SessioneAllenamento;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GuiGestioneSessioni extends JFrame {

    // --- FIELD NAMES (Mappati col GUI Designer) ---
    private JPanel mainPanel;

    // Campi Sessione
    private JComboBox<String> cmbAtleti;
    private JTextField txtTitolo;
    private JTextField txtDescrizioneSessione;
    private JTextField txtData;
    private JTextField txtDurataPrevista;
    private JButton btnCreaSessione;

    // Campi Esercizio
    private JScrollPane scrollEsercizi;
    private JTable tblEsercizi;
    private JTextField txtNomeEsercizio;
    private JTextField txtDescrizioneEsercizio;
    private JTextField txtRipetizioni;
    private JTextField txtDurataEsercizio;
    private JButton btnAggiungiEsercizio;

    private Allenatore allenatoreLoggato;
    private SessioneAllenamento sessioneAppenaCreata;
    private DefaultTableModel tableModel;

    // Istanze dei Controller
    private GestoreUtenti gestoreUtenti;
    private GestoreSessioni gestoreSessioni;

    public GuiGestioneSessioni(Allenatore allenatore) {
        this.allenatoreLoggato = allenatore;

        // Inizializza l'architettura: un solo Facade passato ai vari Controller
        AppSport facade = new AppSport();
        this.gestoreUtenti = new GestoreUtenti(facade);
        this.gestoreSessioni = new GestoreSessioni(facade);

        // Configurazione Finestra
        setTitle("Creazione Nuova Sessione");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        inizializzaInterfaccia();
        gestisciEventiBottoni();
    }

    private void inizializzaInterfaccia() {
        setStatoAreaEsercizi(false);

        // Setup della tabella degli esercizi
        String[] colonne = {"Nome Esercizio", "Descrizione", "Ripetizioni", "Durata (min)"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblEsercizi.setModel(tableModel);

        // Popolamento tendina tramite il GestoreUtenti
        cmbAtleti.removeAllItems();
        cmbAtleti.addItem("Seleziona Atleta...");

        try {
            // Delega al GestoreUtenti (ricordati di creare questo metodo nel controller se manca!)
            List<Atleta> mieiAtleti = gestoreUtenti.getAtletiAssociati(allenatoreLoggato.getIdAllenatore());

            if (mieiAtleti != null && !mieiAtleti.isEmpty()) {
                for (Atleta a : mieiAtleti) {
                    cmbAtleti.addItem(a.getIdAtleta() + " - " + a.getNome() + " " + a.getCognome());
                }
            } else {
                cmbAtleti.addItem("Nessun atleta associato trovato.");
            }
        } catch (Exception ex) {
            System.err.println("Errore caricamento atleti: " + ex.getMessage());
            cmbAtleti.addItem("Errore caricamento.");
        }

        txtData.setToolTipText("Formato: gg/mm/aaaa");
        btnCreaSessione.setText("Crea Sessione");
        btnAggiungiEsercizio.setText("Aggiungi Esercizio");
    }

    private void gestisciEventiBottoni() {

        // --- CREAZIONE SESSIONE ---
        btnCreaSessione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titolo = txtTitolo.getText().trim();
                String descrizione = txtDescrizioneSessione.getText().trim();
                String dataString = txtData.getText().trim();
                String durataString = txtDurataPrevista.getText().trim();

                // Controlli visivi Boundary
                if (cmbAtleti.getSelectedIndex() <= 0) {
                    JOptionPane.showMessageDialog(mainPanel, "Seleziona un atleta valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String atletaSelezionato = (String) cmbAtleti.getSelectedItem();
                if (atletaSelezionato.equals("Nessun atleta associato trovato.") || atletaSelezionato.equals("Errore caricamento.")) {
                    return;
                }

                try {
                    // Preparazione dati
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    format.setLenient(false);
                    Date dataSvolgimento = format.parse(dataString);
                    int durata = Integer.parseInt(durataString);

                    String idString = atletaSelezionato.split(" - ")[0];
                    Long idAtleta = Long.parseLong(idString);

                    // CHIAMATA AL CONTROLLER: GestoreSessioni
                    sessioneAppenaCreata = gestoreSessioni.creaNuovaSessione(
                            allenatoreLoggato.getIdAllenatore(),
                            idAtleta,
                            titolo,
                            descrizione,
                            durata,
                            dataSvolgimento
                    );

                    if (sessioneAppenaCreata != null) {
                        JOptionPane.showMessageDialog(mainPanel, "Sessione Creata con successo! Ora puoi aggiungere gli esercizi.");

                        // Aggiornamento interfaccia
                        btnCreaSessione.setEnabled(false);
                        cmbAtleti.setEnabled(false);
                        txtTitolo.setEnabled(false);
                        txtData.setEnabled(false);
                        txtDurataPrevista.setEnabled(false);
                        txtDescrizioneSessione.setEnabled(false);
                        setStatoAreaEsercizi(true);
                    }

                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Formato data errato. Usa gg/mm/aaaa", "Errore", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "La durata deve essere un numero valido.", "Errore", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    // Cattura le eccezioni di validazione lanciate dal Controller
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Errore Compilazione", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Errore di sistema: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // --- AGGIUNTA ESERCIZIO ---
        btnAggiungiEsercizio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nome = txtNomeEsercizio.getText().trim();
                String descrizione = txtDescrizioneEsercizio.getText().trim();
                String repString = txtRipetizioni.getText().trim();
                String durataString = txtDurataEsercizio.getText().trim();

                try {
                    int ripetizioni = Integer.parseInt(repString);
                    int durata = Integer.parseInt(durataString);

                    // CHIAMATA AL CONTROLLER: GestoreSessioni
                    gestoreSessioni.aggiungiEsercizioASessione(
                            allenatoreLoggato.getIdAllenatore(),
                            sessioneAppenaCreata.getIdSessione(),
                            nome,
                            descrizione,
                            ripetizioni,
                            durata
                    );

                    tableModel.addRow(new Object[]{nome, descrizione, ripetizioni, durata});

                    txtNomeEsercizio.setText("");
                    txtDescrizioneEsercizio.setText("");
                    txtRipetizioni.setText("");
                    txtDurataEsercizio.setText("");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Ripetizioni e Durata devono essere numeri validi.", "Errore", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    // Cattura le eccezioni di validazione lanciate dal Controller
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Dati Non Validi", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Errore nel salvataggio dell'esercizio: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void setStatoAreaEsercizi(boolean stato) {
        txtNomeEsercizio.setEnabled(stato);
        txtDescrizioneEsercizio.setEnabled(stato);
        txtRipetizioni.setEnabled(stato);
        txtDurataEsercizio.setEnabled(stato);
        btnAggiungiEsercizio.setEnabled(stato);
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
        mainPanel.setLayout(new GridLayoutManager(12, 2, new Insets(0, 0, 0, 0), -1, -1));
        cmbAtleti = new JComboBox();
        mainPanel.add(cmbAtleti, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtTitolo = new JTextField();
        mainPanel.add(txtTitolo, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtDescrizioneSessione = new JTextField();
        mainPanel.add(txtDescrizioneSessione, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtData = new JTextField();
        mainPanel.add(txtData, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtDurataPrevista = new JTextField();
        mainPanel.add(txtDurataPrevista, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnCreaSessione = new JButton();
        btnCreaSessione.setText("Button");
        mainPanel.add(btnCreaSessione, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tblEsercizi = new JTable();
        mainPanel.add(tblEsercizi, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        txtNomeEsercizio = new JTextField();
        txtNomeEsercizio.setText("");
        mainPanel.add(txtNomeEsercizio, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtDescrizioneEsercizio = new JTextField();
        mainPanel.add(txtDescrizioneEsercizio, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtRipetizioni = new JTextField();
        mainPanel.add(txtRipetizioni, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtDurataEsercizio = new JTextField();
        txtDurataEsercizio.setText("");
        mainPanel.add(txtDurataEsercizio, new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnAggiungiEsercizio = new JButton();
        btnAggiungiEsercizio.setText("Button");
        mainPanel.add(btnAggiungiEsercizio, new GridConstraints(11, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Titolo:");
        mainPanel.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Descrizione:");
        mainPanel.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Data");
        mainPanel.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("DurataPrevista");
        mainPanel.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Nome Esercizio:");
        mainPanel.add(label5, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Descrizione:");
        mainPanel.add(label6, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Ripetizioni");
        mainPanel.add(label7, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Durata Esercizio");
        mainPanel.add(label8, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}