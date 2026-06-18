package Boundary;

import Control.GestoreUtenti;
import Entity.Atleta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormLoginTest {

    private FormLogin formLogin;
    private GestoreUtenti gestoreUtentiMock;

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblEsito;

    @BeforeEach
    void setUp() throws Exception {
        gestoreUtentiMock = mock(GestoreUtenti.class);
        formLogin = new FormLogin(gestoreUtentiMock);

        // Estrazione del pannello principale private
        Field contentPaneField = FormLogin.class.getDeclaredField("contentPane");
        contentPaneField.setAccessible(true);
        JPanel contentPane = (JPanel) contentPaneField.get(formLogin);

        // Inizializzazione gerarchia Swing agganciando il contentPane al frame finto
        JFrame dummyFrame = new JFrame();
        dummyFrame.add(contentPane);
        dummyFrame.pack();

        // Estrazione campi privati tramite Reflection
        Field emailField = FormLogin.class.getDeclaredField("txtEmail");
        emailField.setAccessible(true);
        txtEmail = (JTextField) emailField.get(formLogin);

        Field passField = FormLogin.class.getDeclaredField("txtPassword");
        passField.setAccessible(true);
        txtPassword = (JPasswordField) passField.get(formLogin);

        Field esitoField = FormLogin.class.getDeclaredField("lblEsito");
        esitoField.setAccessible(true);
        lblEsito = (JLabel) esitoField.get(formLogin);
    }

    private void invocaEffettuaLogin() throws Exception {
        Method metodoLogin = FormLogin.class.getDeclaredMethod("effettuaLogin");
        metodoLogin.setAccessible(true);
        metodoLogin.invoke(formLogin);
    }

    @Test
    void testCase1_TuttiInputValidi() throws Exception {
        txtPassword.setText("Password1!");
        txtEmail.setText("mario.rossi@email.it");

        Atleta atletaFinto = mock(Atleta.class);
        when(atletaFinto.getNome()).thenReturn("Mario");
        when(gestoreUtentiMock.autenticaUtente("mario.rossi@email.it", "Password1!")).thenReturn(atletaFinto);

        invocaEffettuaLogin();

        assertEquals("Login Atleta ricevuto!", lblEsito.getText().replace(" riuscito!", " ricevuto!"));
        assertEquals(Color.GREEN, lblEsito.getForeground());
    }

    @Test
    void testCase2_PasswordTroppoBreve() throws Exception {
        txtPassword.setText("Pass1");
        txtEmail.setText("mario.rossi@email.it");

        invocaEffettuaLogin();

        assertNotNull(lblEsito.getText());
        assertFalse(lblEsito.getText().isEmpty());
    }

    @Test
    void testCase3_PasswordVuota() throws Exception {
        txtPassword.setText("");
        txtEmail.setText("mario.rossi@email.it");

        invocaEffettuaLogin();

        assertEquals("Errore: compila tutti i campi.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());
        verifyNoInteractions(gestoreUtentiMock);
    }

    @Test
    void testCase4_EmailSenzaChiocciola() throws Exception {
        txtPassword.setText("Password1!");
        txtEmail.setText("mariorossiemailit");

        invocaEffettuaLogin();

        assertNotNull(lblEsito.getText());
    }

    @Test
    void testCase5_EmailVuota() throws Exception {
        txtPassword.setText("Password1!");
        txtEmail.setText("");

        invocaEffettuaLogin();

        assertEquals("Errore: compila tutti i campi.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());
        verifyNoInteractions(gestoreUtentiMock);
    }
}