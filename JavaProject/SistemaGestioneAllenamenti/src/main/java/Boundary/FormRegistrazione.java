package Boundary;

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
                gestoreUtenti.registraAtleta(nome, cognome, email, password, disciplina,codice);
                lblEsito.setText("Registrazione Atleta completata!");
                lblEsito.setForeground(Color.GREEN);
            } else if (ruolo.equals("Allenatore")) {
                if (codice.isEmpty()) {
                    lblEsito.setText("Errore: Inserire Codice Associazione.");
                    lblEsito.setForeground(Color.RED);
                    return;
                }
                gestoreUtenti.registraAllenatore(nome, cognome, email, password, disciplina, codice);
                lblEsito.setText("Registrazione Allenatore completata!");
                lblEsito.setForeground(Color.GREEN);
            }
            pulisciCampi();
        } catch (Exception ex) {
            lblEsito.setText("Errore durante il salvataggio.");
            lblEsito.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    private void pulisciCampi() {
        txtNome.setText("");
        txtCognome.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtDisciplina.setText("");
        txtCodiceAssociazione.setText("");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Registrazione Sistema Allenamenti");
        frame.setContentPane(new FormRegistrazione(null).contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    { $$$setupUI$$$(); }

    private void $$$setupUI$$$() {
        // Inizializza contentPane invece di creare un panel1 locale
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(10, 1, new Insets(10, 10, 10, 10), -1, -1));

        cmbRuolo = new JComboBox<>(new DefaultComboBoxModel<>(new String[]{"Atleta", "Allenatore"}));
        contentPane.add(cmbRuolo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        txtNome = new JTextField();
        contentPane.add(txtNome, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        txtCognome = new JTextField();
        contentPane.add(txtCognome, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        txtEmail = new JTextField();
        contentPane.add(txtEmail, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        txtPassword = new JPasswordField();
        contentPane.add(txtPassword, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        txtDisciplina = new JTextField();
        contentPane.add(txtDisciplina, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        txtCodiceAssociazione = new JTextField();
        contentPane.add(txtCodiceAssociazione, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

        btnRegistrati = new JButton("Registrati");
        contentPane.add(btnRegistrati, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        lblEsito = new JLabel("In attesa...");
        contentPane.add(lblEsito, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        contentPane.add(new Spacer(), new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }
}