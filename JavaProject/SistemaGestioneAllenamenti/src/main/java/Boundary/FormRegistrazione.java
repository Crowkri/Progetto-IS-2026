package Boundary;

import Entity.AppSport;
import Control.GestoreUtenti;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormRegistrazione {
    private JPanel contentPane;
    private JComboBox<String> cmbRuolo;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JTextField txtDisciplina;
    private JTextField txtCodiceAssociazione;
    private JButton btnRegistrati;
    private JLabel lblEsito;

    private GestoreUtenti gestoreUtenti;

    public FormRegistrazione(GestoreUtenti gestoreUtenti) {
        this.gestoreUtenti = gestoreUtenti;

        cmbRuolo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ruolo = (String) cmbRuolo.getSelectedItem();
                if ("Atleta".equals(ruolo)) {
                    txtCodiceAssociazione.setBackground(Color.WHITE);
                    txtCodiceAssociazione.setText("Opzionale: Codice Allenatore");
                    txtCodiceAssociazione.setForeground(Color.GRAY);
                } else if ("Allenatore".equals(ruolo)) {
                    txtCodiceAssociazione.setBackground(new Color(255, 255, 224));
                    txtCodiceAssociazione.setText("Obbligatorio: Crea il tuo Codice");
                    txtCodiceAssociazione.setForeground(Color.GRAY);
                }
            }
        });

        // Listener per il pulsante di registrazione
        btnRegistrati.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                effettuaRegistrazione();
            }
        });
    }

    private void effettuaRegistrazione() {
        String ruolo = (String) cmbRuolo.getSelectedItem();
        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());
        String disciplina = txtDisciplina.getText();
        String codice = txtCodiceAssociazione.getText();

        if (codice.equals("Opzionale: Codice Allenatore") || codice.equals("Obbligatorio: Crea il tuo Codice")) {
            codice = "";
        }

        if (ruolo == null || ruolo.equals("Seleziona...")) {
            lblEsito.setText("Errore: Seleziona Atleta o Allenatore.");
            lblEsito.setForeground(Color.RED);
            return;
        }

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty() || disciplina.isEmpty()) {
            lblEsito.setText("Errore: compila tutti i campi base.");
            lblEsito.setForeground(Color.RED);
            return;
        }

        try {
            if (ruolo.equals("Atleta")) {
                // Controllo esistenza codice Allenatore
                if (!codice.isEmpty()) {
                    if (!gestoreUtenti.esisteCodiceAllenatore(codice)) {
                        lblEsito.setText("Errore: Il codice allenatore inserito non esiste.");
                        lblEsito.setForeground(Color.RED);
                        return;
                    }
                }

                gestoreUtenti.registraAtleta(nome, cognome, email, password, disciplina, codice);

                // Mostra popup di successo e vai al login
                JOptionPane.showMessageDialog(contentPane, "Registrazione Atleta completata con successo!\nVerrai reindirizzato al login.", "Successo", JOptionPane.INFORMATION_MESSAGE);
                apriSchermataLogin();

            } else if (ruolo.equals("Allenatore")) {
                if (codice.isEmpty()) {
                    lblEsito.setText("Errore: Inserire Codice Associazione.");
                    lblEsito.setForeground(Color.RED);
                    return;
                }

                // Controllo inesistenza codice Allenatore
                if (gestoreUtenti.esisteCodiceAllenatore(codice)) {
                    lblEsito.setText("Errore: Questo codice è già in uso.");
                    lblEsito.setForeground(Color.RED);
                    return;
                }

                gestoreUtenti.registraAllenatore(nome, cognome, email, password, disciplina, codice);

                // Mostra popup di successo e vai al login
                JOptionPane.showMessageDialog(contentPane, "Registrazione Allenatore completata con successo!\nVerrai reindirizzato al login.", "Successo", JOptionPane.INFORMATION_MESSAGE);
                apriSchermataLogin();
            }

        } catch (Exception ex) {
            lblEsito.setText("Errore: " + ex.getMessage());
            lblEsito.setForeground(Color.RED);
        }
    }

    // NUOVO METODO PER APRIRE IL LOGIN E CHIUDERE LA REGISTRAZIONE
    private void apriSchermataLogin() {
        JFrame frameLogin = new JFrame("Accesso Sistema Allenamenti");
        frameLogin.setContentPane(new FormLogin(gestoreUtenti).$$$getRootComponent$$$());
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLogin.pack();
        frameLogin.setLocationRelativeTo(null); // Centra la finestra
        frameLogin.setVisible(true);

        // Chiude la finestra di registrazione attuale
        SwingUtilities.getWindowAncestor(contentPane).dispose();
    }

    private void pulisciCampi() {
        txtNome.setText("");
        txtCognome.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtDisciplina.setText("");
        txtCodiceAssociazione.setText("");
        cmbRuolo.setSelectedIndex(0);
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
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(10, 2, new Insets(0, 0, 0, 0), -1, -1));
        cmbRuolo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Atleta");
        defaultComboBoxModel1.addElement("Allenatore");
        cmbRuolo.setModel(defaultComboBoxModel1);
        contentPane.add(cmbRuolo, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        contentPane.add(spacer1, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtNome = new JTextField();
        contentPane.add(txtNome, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtCognome = new JTextField();
        contentPane.add(txtCognome, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtEmail = new JTextField();
        txtEmail.setText("");
        contentPane.add(txtEmail, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtPassword = new JPasswordField();
        txtPassword.setText("");
        contentPane.add(txtPassword, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtDisciplina = new JTextField();
        txtDisciplina.setText("");
        contentPane.add(txtDisciplina, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        txtCodiceAssociazione = new JTextField();
        contentPane.add(txtCodiceAssociazione, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnRegistrati = new JButton();
        btnRegistrati.setText("Registrati");
        contentPane.add(btnRegistrati, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblEsito = new JLabel();
        lblEsito.setText("Label");
        contentPane.add(lblEsito, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Nome:");
        contentPane.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Cognome:");
        contentPane.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Mail:");
        contentPane.add(label3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Password");
        contentPane.add(label4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Disciplina:");
        contentPane.add(label5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("CodiceAssociativo:");
        contentPane.add(label6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}