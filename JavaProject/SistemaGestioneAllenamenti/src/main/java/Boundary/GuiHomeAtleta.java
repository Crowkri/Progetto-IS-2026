package Boundary;

import Control.AppSport;
import Control.GestoreSessioni;
import Control.GestoreUtenti;
import Entity.Allenatore;
import Entity.Atleta;
import Entity.SessioneAllenamento;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GuiHomeAtleta extends JFrame {

    // --- FIELD NAMES ---
    private JPanel mainPanel;
    private JLabel lblBenvenuto;
    private JComboBox<String> cmbFiltroStato;
    private JTextField txtRicercaTitolo; // Nuovo campo di ricerca
    private JTextField txtFiltroData;    // Nuovo campo data
    private JScrollPane tbscorrere;
    private JTable tblSessioni;
    private JButton btnVisualizzaDettagli;
    private JButton btnRegistraEsecuzione;
    private JButton btnProfilo;
    private JButton allenatorbtneAssociatoButton;

    private Atleta atletaLoggato;
    private GestoreSessioni gestoreSessioni;
    private GestoreUtenti gestoreUtenti;

    public GuiHomeAtleta(Atleta atleta) {
        // 1. Inizializzazione IDE e Scudo Protettivo
        $$$setupUI$$$();
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.add(new JLabel("Errore IDE: Form non compilato. Controlla il GUI Designer.", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        this.atletaLoggato = atleta;
        this.gestoreSessioni = new GestoreSessioni(new AppSport());
        this.gestoreUtenti = new GestoreUtenti(new AppSport());

        setTitle("Area Personale - " + atletaLoggato.getNome() + " " + atletaLoggato.getCognome());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);

        // Previene NullPointerException se la grafica non è pronta
        if (lblBenvenuto != null) {
            inizializzaDatiDellaHome();
            gestisciEventiBottoni();
        }
    }

    private void inizializzaDatiDellaHome() {
        lblBenvenuto.setText("Bentornato, " + atletaLoggato.getNome() + "!");
        lblBenvenuto.setFont(new Font(lblBenvenuto.getFont().getName(), Font.BOLD, 18));

        // Inizializza filtri
        cmbFiltroStato.addItem("Tutte le sessioni");
        cmbFiltroStato.addItem("ASSEGNATA");
        cmbFiltroStato.addItem("IN_CORSO");
        cmbFiltroStato.addItem("COMPLETATA");

        // Al primo avvio, carica la tabella senza nessun filtro attivo
        applicaFiltri();
    }

    private void applicaFiltri() {
        // 1. Estrapola lo Stato
        String stato = (String) cmbFiltroStato.getSelectedItem();
        if ("Tutte le sessioni".equals(stato)) {
            stato = null; // Il Facade interpreterà null come "ignora filtro"
        }

        // 2. Estrapola il Titolo
        String titolo = txtRicercaTitolo.getText().trim();

        // 3. Estrapola la Data (con conversione sicura)
        Date dataFiltro = null;
        String dataStr = txtFiltroData.getText().trim();
        if (!dataStr.isEmpty() && !dataStr.equals("gg/mm/aaaa")) {
            try {
                SimpleDateFormat sdfParse = new SimpleDateFormat("dd/MM/yyyy");
                sdfParse.setLenient(false); // Evita date impossibili (es. 32/13/2026)
                dataFiltro = sdfParse.parse(dataStr);
            } catch (Exception e) {
                // Se la data è parziale o mal formattata, ignoriamo il filtro senza bloccare l'app
                dataFiltro = null;
            }
        }

        // 4. Delega il recupero delle sessioni filtrate al Controller
        List<SessioneAllenamento> sessioniFiltrate = gestoreSessioni.getSessioniAtletaFiltrate(atletaLoggato.getIdAtleta(), stato, titolo, dataFiltro);

        // 5. Aggiorna la grafica
        popolaTabella(sessioniFiltrate);
    }

    private void popolaTabella(List<SessioneAllenamento> sessioni) {
        String[] colonne = {"ID", "Data", "Titolo Sessione", "Durata (min)", "Stato"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat sdfFormat = new SimpleDateFormat("dd/MM/yyyy");

        for (SessioneAllenamento s : sessioni) {
            String dataFormattata = (s.getDataSvolgimento() != null) ? sdfFormat.format(s.getDataSvolgimento()) : "Non definita";
            model.addRow(new Object[]{
                    s.getIdSessione(),
                    dataFormattata,
                    s.getTitolo(),
                    s.getDurataPrevista(),
                    s.getStato()
            });
        }

        tblSessioni.setModel(model);
        tblSessioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Nasconde la colonna ID (serve solo come riferimento logico interno)
        tblSessioni.getColumnModel().getColumn(0).setMinWidth(0);
        tblSessioni.getColumnModel().getColumn(0).setMaxWidth(0);
        tblSessioni.getColumnModel().getColumn(0).setWidth(0);
    }

    private void gestisciEventiBottoni() {

        // Listener 1: Cambio stato nella tendina
        cmbFiltroStato.addActionListener(e -> applicaFiltri());

        // DocumentListener per aggiornare la tabella ad ogni lettera digitata nel Titolo
        txtRicercaTitolo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                applicaFiltri();
            }

            public void removeUpdate(DocumentEvent e) {
                applicaFiltri();
            }

            public void changedUpdate(DocumentEvent e) {
                applicaFiltri();
            }
        });

        // DocumentListener per aggiornare la tabella ad ogni numero digitato nella Data
        txtFiltroData.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                applicaFiltri();
            }

            public void removeUpdate(DocumentEvent e) {
                applicaFiltri();
            }

            public void changedUpdate(DocumentEvent e) {
                applicaFiltri();
            }
        });

        // FocusListener per ripulire il campo data (se c'è scritto gg/mm/aaaa)
        txtFiltroData.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (txtFiltroData.getText().equals("gg/mm/aaaa")) {
                    txtFiltroData.setText("");
                }
            }
        });

        // Bottoni Standard
        btnVisualizzaDettagli.addActionListener(e -> {
            // 1. Recuperiamo la riga selezionata dall'atleta nella JTable
            int riga = tblSessioni.getSelectedRow();

            // 2. Controllo di sicurezza: se l'utente non ha selezionato nessuna riga
            if (riga == -1) {
                JOptionPane.showMessageDialog(mainPanel, "Seleziona prima una sessione dalla tabella!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // 3. Estraiamo l'ID della sessione dalla colonna 0 (quella che abbiamo nascosto)
                Long idSessione = (Long) tblSessioni.getModel().getValueAt(riga, 0);

                // 4. Chiediamo al controller l'istanza dell'Entity completa
                // Assicurati che nel tuo GestoreSessioni ci sia il metodo getSessioneById(id) che delega al Facade
                SessioneAllenamento sessioneSelezionata = gestoreSessioni.getSessioneById(idSessione);

                if (sessioneSelezionata != null) {
                    // 5. Apriamo la nuova GUI passando l'atleta loggato e l'oggetto sessione appena recuperato
                    new GuiDettaglioSessione(atletaLoggato, sessioneSelezionata).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Errore: Impossibile recuperare i dati della sessione.", "Errore", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel, "Errore durante l'apertura del dettaglio: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnProfilo.addActionListener(e -> {
            // Apriamo la GuiProfiloAtleta passando l'atletaLoggato sia come soggetto che come visualizzatore
            new GuiProfiloAtleta(atletaLoggato, atletaLoggato).setVisible(true);
        });
        allenatorbtneAssociatoButton.addActionListener(e -> {
            // 1. Istanziamo il controller corretto
            GestoreUtenti gestoreUtenti = new GestoreUtenti(new AppSport());

            // 2. Recuperiamo l'allenatore attuale
            // Assicurati che l'entità Atleta abbia il metodo getAllenatoreAssociato()
            Allenatore allenatoreAttuale = atletaLoggato.getAllenatoreAssociato();

            if (allenatoreAttuale != null) {
                // --- SCENARIO A: GIÀ ASSOCIATO (Dissociazione) ---
                int scelta = JOptionPane.showConfirmDialog(mainPanel,
                        "Revocare associazione con " + allenatoreAttuale.getNome() + "?",
                        "Gestione Allenatore",
                        JOptionPane.YES_NO_OPTION);

                if (scelta == JOptionPane.YES_OPTION) {
                    try {
                        // Chiamata al metodo corretto del GestoreUtenti
                        gestoreUtenti.dissociaAllenatore(atletaLoggato.getIdAtleta());

                        // Aggiornamento entità locale (passiamo null per rimuovere il legame)
                        atletaLoggato.associaAllenatore(null);

                        JOptionPane.showMessageDialog(mainPanel, "Dissociazione avvenuta.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainPanel, "Errore: " + ex.getMessage());
                    }
                }
            } else {
                // --- SCENARIO B: NON ASSOCIATO (Associazione tramite codice) ---
                String codice = JOptionPane.showInputDialog(mainPanel, "Inserisci il codice del tuo allenatore:");

                if (codice != null && !codice.trim().isEmpty()) {
                    try {
                        Allenatore nuovoCoach = gestoreUtenti.associaConCodice(atletaLoggato.getIdAtleta(), codice.trim());
                        // NOTA ARCHITETTURALE (Pattern BCED & Session Management):
// Viene invocato esplicitamente 'atletaLoggato.associaAllenatore()' all'interno della Boundary
// poiché l'oggetto 'atletaLoggato' agisce come State Holder (Sessione Locale) dell'interfaccia.
// Sebbene l'associazione venga interamente persistita sul Database dal Controller (GestoreUtenti)
// tramite la Facade, l'istanza in memoria della GUI rimarrebbe disallineata.
// Questa chiamata esplicita sincronizza lo stato della memoria locale dell'interfaccia grafica
// in tempo reale, evitando una query di "refresh" ridondante verso il database per ricaricare l'entità.
                        if (nuovoCoach != null) {
                            atletaLoggato.associaAllenatore(nuovoCoach);
                            JOptionPane.showMessageDialog(mainPanel, "Associazione completata!");

                        } else {
                            JOptionPane.showMessageDialog(mainPanel, "Codice non valido.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainPanel, "Errore: " + ex.getMessage());
                    }
                }
            }
        });
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
        mainPanel.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        lblBenvenuto = new JLabel();
        lblBenvenuto.setText("Label");
        mainPanel.add(lblBenvenuto, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        cmbFiltroStato = new JComboBox();
        mainPanel.add(cmbFiltroStato, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnVisualizzaDettagli = new JButton();
        btnVisualizzaDettagli.setText("VisualizzaDettagli");
        mainPanel.add(btnVisualizzaDettagli, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnProfilo = new JButton();
        btnProfilo.setText("Visualizza Profilo");
        mainPanel.add(btnProfilo, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tbscorrere = new JScrollPane();
        mainPanel.add(tbscorrere, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblSessioni = new JTable();
        tbscorrere.setViewportView(tblSessioni);
        txtRicercaTitolo = new JTextField();
        mainPanel.add(txtRicercaTitolo, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtFiltroData = new JTextField();
        mainPanel.add(txtFiltroData, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Titolo:");
        mainPanel.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Data:");
        mainPanel.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        allenatorbtneAssociatoButton = new JButton();
        allenatorbtneAssociatoButton.setText("AllenatoreAssociato");
        mainPanel.add(allenatorbtneAssociatoButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}