package Boundary;

import Entity.AppSport;
import Control.GestoreSessioni;
import Control.GestoreUtenti;
import Entity.Atleta;
import Entity.Esercizio;
import Entity.SessioneAllenamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuiDettaglioSessione extends JFrame {

    private JPanel mainPanel;
    private JTable tblEsercizi;
    private JTextField txtRipetizioniEffettive;
    private JTextField txtTempoImpiegato;
    private JTextArea areaNota;
    private JButton btnRegistra;
    private JLabel lblInfoSessione;

    private Atleta atletaLoggato;
    private SessioneAllenamento sessioneCorrente;

    private GestoreSessioni gestoreSessioni;
    private GestoreUtenti gestoreUtenti;

    public GuiDettaglioSessione(Atleta atleta, SessioneAllenamento sessione) {
        this.atletaLoggato = atleta;
        this.sessioneCorrente = sessione;

        // Inizializziamo i controllori
        this.gestoreSessioni = new GestoreSessioni(new AppSport());
        this.gestoreUtenti = new GestoreUtenti(new AppSport());

        setTitle("Dettaglio Sessione - " + sessioneCorrente.getTitolo());
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Chiude solo questa finestra
        setLocationRelativeTo(null);

        // 1. Costruzione manuale della GUI per assicurarci che i nomi siano quelli giusti
        inizializzaInterfaccia();

        // 2. Popolamento tabella esercizi
        aggiornaTabellaEsercizi();

        // 3. Logica di sblocco e registrazione
        configuraEventi();

        // 4. Stato iniziale: Campi bloccati
        setCampiEditable(false);
    }

    private void inizializzaInterfaccia() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Intestazione
        lblInfoSessione = new JLabel("Sessione: " + sessioneCorrente.getTitolo());
        lblInfoSessione.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(lblInfoSessione, BorderLayout.NORTH);

        // Tabella Esercizi (Centro)
        tblEsercizi = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblEsercizi);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Pannello Input (Sud)
        JPanel pnlSud = new JPanel(new GridLayout(0, 1, 5, 5));

        txtRipetizioniEffettive = new JTextField();
        txtTempoImpiegato = new JTextField();
        areaNota = new JTextArea(3, 20);
        areaNota.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        btnRegistra = new JButton("Registra Esecuzione");
        btnRegistra.setFont(new Font("Arial", Font.BOLD, 14));

        pnlSud.add(new JLabel("Ripetizioni Effettive:"));
        pnlSud.add(txtRipetizioniEffettive);
        pnlSud.add(new JLabel("Tempo Impiegato (min):"));
        pnlSud.add(txtTempoImpiegato);
        pnlSud.add(new JLabel("Nota Testuale:"));
        pnlSud.add(new JScrollPane(areaNota));
        pnlSud.add(btnRegistra);

        mainPanel.add(pnlSud, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void aggiornaTabellaEsercizi() {
        String[] colonne = {"ID", "Nome Esercizio", "Rip. Previste", "Tempo Previsto"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // Recuperiamo gli esercizi della sessione passata
        List<Esercizio> lista = gestoreSessioni.getEserciziSessione(sessioneCorrente.getIdSessione());
        for (Esercizio e : lista) {
            model.addRow(new Object[]{
                    e.getIdEsercizio(),
                    e.getNome(),
                    e.getRipetizioniPreviste(),
                    e.getDurataPrevista()
            });
        }
        tblEsercizi.setModel(model);

        // Nascondi colonna ID
        tblEsercizi.getColumnModel().getColumn(0).setMinWidth(0);
        tblEsercizi.getColumnModel().getColumn(0).setMaxWidth(0);
    }

    private void setCampiEditable(boolean editable) {
        txtRipetizioniEffettive.setEditable(editable);
        txtTempoImpiegato.setEditable(editable);
        areaNota.setEditable(editable);
        btnRegistra.setEnabled(editable);

        // Effetto visivo per capire che è bloccato
        Color sfondo = editable ? Color.WHITE : new Color(230, 230, 230);
        txtRipetizioniEffettive.setBackground(sfondo);
        txtTempoImpiegato.setBackground(sfondo);
        areaNota.setBackground(sfondo);
    }

    private void configuraEventi() {
        // SBLOCCO DINAMICO: I campi si sbloccano SOLO quando l'atleta seleziona un esercizio dalla tabella
        tblEsercizi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rigaSelezionata = tblEsercizi.getSelectedRow() != -1;
                setCampiEditable(rigaSelezionata);
            }
        });

        // REGISTRAZIONE PERFORMANCE ATLETA
        btnRegistra.addActionListener(e -> {
            int riga = tblEsercizi.getSelectedRow();
            if (riga == -1) return; // Sicurezza se non c'è selezione

            // Validazione: Controlla che i campi obbligatori non siano vuoti
            if (txtRipetizioniEffettive.getText().trim().isEmpty() || txtTempoImpiegato.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Compila i campi Ripetizioni e Tempo prima di registrare!", "Dati Mancanti", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // 1. Recuperiamo l'ID dell'esercizio selezionato dalla colonna 0 (nascosta)
                Long idEsercizio = (Long) tblEsercizi.getModel().getValueAt(riga, 0);

                // 2. Estraiamo i dati effettivi inseriti dall'atleta nei campi
                int ripEffettive = Integer.parseInt(txtRipetizioniEffettive.getText().trim());
                int tempoImpiegato = Integer.parseInt(txtTempoImpiegato.getText().trim());
                String notaAtleta = areaNota.getText().trim();

                // 3. CHIAMATA AL CONTROLLER: Usiamo il tuo gestoreSessioni con la firma esatta
                gestoreSessioni.registraEsecuzioneEsercizio(
                        atletaLoggato.getIdAtleta(),
                        idEsercizio,
                        ripEffettive,
                        tempoImpiegato,
                        notaAtleta
                );

                JOptionPane.showMessageDialog(this, "Esecuzione registrata con successo!");

                // 4. Pulizia campi e reset selezione (questo ribloccherà i campi in automatico)
                txtRipetizioniEffettive.setText("");
                txtTempoImpiegato.setText("");
                areaNota.setText("");
                tblEsercizi.clearSelection();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Inserisci numeri validi per Ripetizioni e Tempo.", "Errore Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore durante il salvataggio: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}