package Boundary;

import Entity.AppSport;
import Control.GestoreDashboard;
import Control.GestoreUtenti;
import Entity.Allenatore;
import Entity.Atleta;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Map;

public class GuiDashboard extends JFrame {

    private JPanel mainPanel;
    private JLabel lblTotAtleti;
    private JLabel lblAssegnate;
    private JLabel lblCompletate;
    private JLabel lblInCorso;
    private JComboBox<String> cmbAtleti;
    private JButton btnVisualizzaProfilo;
    private JButton btnVisualizzaEvoluzione;
    private JButton btnVisualizzaConfronto;

    private Allenatore allenatoreLoggato;
    private GestoreDashboard gestoreDashboard;
    private GestoreUtenti gestoreUtenti;

    public GuiDashboard(Allenatore allenatore) {
        this.allenatoreLoggato = allenatore;

        AppSport facade = new AppSport();
        this.gestoreDashboard = new GestoreDashboard(facade);
        this.gestoreUtenti = new GestoreUtenti(facade);

        setTitle("Dashboard Monitoraggio Performance");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);

        inizializzaDati();
        gestisciEventi();
    }

    private void inizializzaDati() {
        try {
            Map<String, Integer> indicatori = gestoreDashboard.getIndicatoriAggregati(allenatoreLoggato.getIdAllenatore());
            lblTotAtleti.setText("Atleti: " + indicatori.getOrDefault("Totale Atleti", 0));
            lblAssegnate.setText("Assegnate: " + indicatori.getOrDefault("Sessioni Assegnate", 0));
            lblCompletate.setText("Completate: " + indicatori.getOrDefault("Sessioni Completate", 0));
            lblInCorso.setText("In Corso: " + indicatori.getOrDefault("Sessioni In Corso", 0));
        } catch (Exception e) {
            lblTotAtleti.setText("Errore dati");
        }

        cmbAtleti.addItem("Seleziona un atleta...");
        try {
            java.util.List<Atleta> atletiAssociati = gestoreUtenti.getAtletiAssociati(allenatoreLoggato.getIdAllenatore());
            for (Atleta a : atletiAssociati) {
                cmbAtleti.addItem(a.getIdAtleta() + " - " + a.getNome() + " " + a.getCognome());
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento tendina: " + e.getMessage());
        }
    }

    private void gestisciEventi() {
        btnVisualizzaProfilo.addActionListener(e -> {
            Long idAtleta = getSelectedAtletaId();
            if (idAtleta != null) {
                try {
                    java.util.List<Atleta> atletiAssociati = gestoreUtenti.getAtletiAssociati(allenatoreLoggato.getIdAllenatore());
                    Atleta atletaSelezionato = null;
                    for (Atleta a : atletiAssociati) {
                        if (a.getIdAtleta().equals(idAtleta)) {
                            atletaSelezionato = a;
                            break;
                        }
                    }
                    if (atletaSelezionato != null) {
                        new GuiProfiloAtleta(atletaSelezionato, allenatoreLoggato).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Errore: Atleta non trovato.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Errore di sistema: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnVisualizzaEvoluzione.addActionListener(e -> {
            Long idAtleta = getSelectedAtletaId();
            if (idAtleta != null) {
                try {
                    java.util.List<Atleta> atletiAssociati = gestoreUtenti.getAtletiAssociati(allenatoreLoggato.getIdAllenatore());
                    Atleta atletaSelezionato = null;
                    for (Atleta a : atletiAssociati) {
                        if (a.getIdAtleta().equals(idAtleta)) {
                            atletaSelezionato = a;
                            break;
                        }
                    }
                    if (atletaSelezionato != null) {
                        Map<Date, Integer> evoluzione = gestoreDashboard.getEvoluzioneAtleta(allenatoreLoggato.getIdAllenatore(), idAtleta);
                        new GuiEvoluzione(atletaSelezionato, evoluzione).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Errore: Atleta non trovato nel sistema.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Errore nel caricamento: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnVisualizzaConfronto.addActionListener(e -> {
            Long idAtleta = getSelectedAtletaId();
            if (idAtleta != null) {
                try {
                    java.util.List<Atleta> atletiAssociati = gestoreUtenti.getAtletiAssociati(allenatoreLoggato.getIdAllenatore());
                    Atleta atletaSelezionato = null;
                    for (Atleta a : atletiAssociati) {
                        if (a.getIdAtleta().equals(idAtleta)) {
                            atletaSelezionato = a;
                            break;
                        }
                    }
                    if (atletaSelezionato != null) {
                        Map<String, Double> confronto = gestoreDashboard.generaConfrontoPrevistoEffettivo(allenatoreLoggato.getIdAllenatore(), idAtleta);
                        new GuiConfronto(atletaSelezionato, confronto).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(mainPanel, "Errore: Impossibile recuperare i dati dell'atleta.", "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage(), "Attenzione", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private Long getSelectedAtletaId() {
        if (cmbAtleti.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(mainPanel, "Seleziona un atleta dalla lista.");
            return null;
        }
        String selected = (String) cmbAtleti.getSelectedItem();
        return Long.parseLong(selected.split(" - ")[0]);
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
        mainPanel.setLayout(new GridLayoutManager(2, 4, new Insets(0, 0, 0, 0), -1, -1));
        lblTotAtleti = new JLabel();
        lblTotAtleti.setText("Label");
        mainPanel.add(lblTotAtleti, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblAssegnate = new JLabel();
        lblAssegnate.setText("Label");
        mainPanel.add(lblAssegnate, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblCompletate = new JLabel();
        lblCompletate.setText("Label");
        mainPanel.add(lblCompletate, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblInCorso = new JLabel();
        lblInCorso.setText("Label");
        mainPanel.add(lblInCorso, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cmbAtleti = new JComboBox();
        mainPanel.add(cmbAtleti, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnVisualizzaProfilo = new JButton();
        btnVisualizzaProfilo.setText("Visualizza profilo");
        mainPanel.add(btnVisualizzaProfilo, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnVisualizzaEvoluzione = new JButton();
        btnVisualizzaEvoluzione.setText("Visualizza evoluzione");
        mainPanel.add(btnVisualizzaEvoluzione, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnVisualizzaConfronto = new JButton();
        btnVisualizzaConfronto.setText("Visualizza confronto");
        mainPanel.add(btnVisualizzaConfronto, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}