package Boundary;

import Entity.Atleta;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class GuiEvoluzione extends JFrame {

    private JPanel mainPanel;
    private JLabel lblTitolo;
    private JPanel pnlGrafico; // Contenitore del grafico
    private JLabel lblStatistiche;
    private JButton btnChiudi;

    private Atleta atleta;
    private Map<Date, Integer> datiEvoluzione;

    public GuiEvoluzione(Atleta atleta, Map<Date, Integer> datiEvoluzione) {
        // Inizializza la grafica (risolve il problema del null)
        $$$setupUI$$$();

        this.atleta = atleta;
        this.datiEvoluzione = datiEvoluzione;

        setTitle("Evoluzione Storica - " + atleta.getNome());
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);

        configuraInterfaccia();
        disegnaGraficoCustom(); // Metodo magico per il grafico!
    }

    private void configuraInterfaccia() {
        lblTitolo.setText("Andamento Performance: " + atleta.getNome() + " " + atleta.getCognome());
        lblTitolo.setFont(new Font("SansSerif", Font.BOLD, 20));

        if (datiEvoluzione != null && !datiEvoluzione.isEmpty()) {
            int max = datiEvoluzione.values().stream().max(Integer::compare).orElse(0);
            lblStatistiche.setText("Record Personale: " + max + " ripetizioni totali in una sessione.");
        } else {
            lblStatistiche.setText("Dati insufficienti per il calcolo dei record.");
        }

        btnChiudi.addActionListener(e -> this.dispose());
    }

    // --- LOGICA DI DISEGNO DEL GRAFICO (PURO JAVA) ---
    private void disegnaGraficoCustom() {
        pnlGrafico.removeAll(); // Pulisce il pannello
        pnlGrafico.setLayout(new BorderLayout());

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (datiEvoluzione == null || datiEvoluzione.isEmpty()) return;

                Graphics2D g2 = (Graphics2D) g;
                // Attiva l'antialiasing per rendere le linee morbide
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int padding = 50;
                int width = getWidth() - 2 * padding;
                int height = getHeight() - 2 * padding;

                // Trova il valore massimo per scalare l'asse Y
                int maxY = datiEvoluzione.values().stream().max(Integer::compare).orElse(1);
                if (maxY == 0) maxY = 1;

                // Disegna gli assi
                g2.setColor(Color.BLACK);
                g2.drawLine(padding, getHeight() - padding, getWidth() - padding, getHeight() - padding); // Asse X
                g2.drawLine(padding, padding, padding, getHeight() - padding); // Asse Y

                // Configura i punti del grafico
                int n = datiEvoluzione.size();
                int pointGap = n > 1 ? width / (n - 1) : width / 2;

                int i = 0;
                int prevX = 0, prevY = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

                for (Map.Entry<Date, Integer> entry : datiEvoluzione.entrySet()) {
                    int x = padding + i * pointGap;
                    int y = getHeight() - padding - (entry.getValue() * height / maxY);

                    // Disegna la linea che unisce i punti
                    g2.setColor(Color.BLUE);
                    if (i > 0) {
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawLine(prevX, prevY, x, y);
                    }

                    // Disegna il punto (pallino rosso)
                    g2.setColor(Color.RED);
                    g2.fillOval(x - 5, y - 5, 10, 10);

                    // Scrive la data sotto l'asse X e il valore sopra il punto
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(sdf.format(entry.getKey()), x - 15, getHeight() - padding + 20);
                    g2.drawString(String.valueOf(entry.getValue()), x - 10, y - 10);

                    prevX = x;
                    prevY = y;
                    i++;
                }
            }
        };

        chartPanel.setBackground(Color.WHITE); // Sfondo bianco per far risaltare il grafico
        pnlGrafico.add(chartPanel, BorderLayout.CENTER);
        pnlGrafico.revalidate();
        pnlGrafico.repaint();
    }

    {
        $$$setupUI$$$();
    }

    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(4, 1, new Insets(15, 15, 15, 15), -1, -1));

        lblTitolo = new JLabel();
        mainPanel.add(lblTitolo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        pnlGrafico = new JPanel();
        pnlGrafico.setLayout(new BorderLayout(0, 0));
        mainPanel.add(pnlGrafico, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 300), null, 0, false));

        lblStatistiche = new JLabel();
        mainPanel.add(lblStatistiche, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        btnChiudi = new JButton();
        btnChiudi.setText("Chiudi");
        mainPanel.add(btnChiudi, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}