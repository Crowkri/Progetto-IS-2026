package Boundary;

import Entity.AppSport;
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

    private JPanel mainPanel;
    private JLabel lblBenvenuto;
    private JComboBox<String> cmbFiltroStato;
    private JTextField txtRicercaTitolo;
    private JTextField txtFiltroData;
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

        if (lblBenvenuto != null) {
            inizializzaDatiDellaHome();
            gestisciEventiBottoni();
        }
    }

    private void inizializzaDatiDellaHome() {
        lblBenvenuto.setText("Bentornato, " + atletaLoggato.getNome() + "!");
        lblBenvenuto.setFont(new Font(lblBenvenuto.getFont().getName(), Font.BOLD, 18));

        // Filtri
        cmbFiltroStato.addItem("Tutte le sessioni");
        cmbFiltroStato.addItem("ASSEGNATA");
        cmbFiltroStato.addItem("IN_CORSO");
        cmbFiltroStato.addItem("COMPLETATA");

        // Al primo avvio, carica la tabella senza nessun filtro attivo
        applicaFiltri();
    }

    private void applicaFiltri() {
        String stato = (String) cmbFiltroStato.getSelectedItem();
        if ("Tutte le sessioni".equals(stato)) {
            stato = null; // Facade: null = ignora filtro
        }

        String titolo = txtRicercaTitolo.getText().trim();

        Date dataFiltro = null;
        String dataStr = txtFiltroData.getText().trim();
        if (!dataStr.isEmpty() && !dataStr.equals("gg/mm/aaaa")) {
            try {
                SimpleDateFormat sdfParse = new SimpleDateFormat("dd/MM/yyyy");
                sdfParse.setLenient(false); // Evita date impossibili (es. 32/13/2026)
                dataFiltro = sdfParse.parse(dataStr);
            } catch (Exception e) {
                dataFiltro = null;
            }
        }

        List<SessioneAllenamento> sessioniFiltrate = gestoreSessioni.getSessioniAtletaFiltrate(atletaLoggato.getIdAtleta(), stato, titolo, dataFiltro);

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

        cmbFiltroStato.addActionListener(e -> applicaFiltri());

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

        txtFiltroData.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (txtFiltroData.getText().equals("gg/mm/aaaa")) {
                    txtFiltroData.setText("");
                }
            }
        });

        btnVisualizzaDettagli.addActionListener(e -> {
            int riga = tblSessioni.getSelectedRow();

            if (riga == -1) {
                JOptionPane.showMessageDialog(mainPanel, "Seleziona prima una sessione dalla tabella!", "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Long idSessione = (Long) tblSessioni.getModel().getValueAt(riga, 0);

                SessioneAllenamento sessioneSelezionata = gestoreSessioni.getSessioneById(idSessione);

                if (sessioneSelezionata != null) {
                    new GuiDettaglioSessione(atletaLoggato, sessioneSelezionata).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Errore: Impossibile recuperare i dati della sessione.", "Errore", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel, "Errore durante l'apertura del dettaglio: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnProfilo.addActionListener(e -> {
            new GuiProfiloAtleta(atletaLoggato, atletaLoggato).setVisible(true);
        });
        allenatorbtneAssociatoButton.addActionListener(e -> {
            GestoreUtenti gestoreUtenti = new GestoreUtenti(new AppSport());

            Allenatore allenatoreAttuale = atletaLoggato.getAllenatoreAssociato();

            if (allenatoreAttuale != null) {
                int scelta = JOptionPane.showConfirmDialog(mainPanel,
                        "Revocare associazione con " + allenatoreAttuale.getNome() + "?",
                        "Gestione Allenatore",
                        JOptionPane.YES_NO_OPTION);

                if (scelta == JOptionPane.YES_OPTION) {
                    try {
                        gestoreUtenti.dissociaAllenatore(atletaLoggato.getIdAtleta());

                        atletaLoggato.associaAllenatore(null);

                        JOptionPane.showMessageDialog(mainPanel, "Dissociazione avvenuta.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainPanel, "Errore: " + ex.getMessage());
                    }
                }
            } else {
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