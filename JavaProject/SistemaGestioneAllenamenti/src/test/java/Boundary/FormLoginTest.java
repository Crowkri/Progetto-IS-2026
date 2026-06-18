package Boundary;

import Control.GestoreUtenti;
import Entity.Allenatore;
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
        // Mock del Controller (GestoreUtenti)
        gestoreUtentiMock = mock(GestoreUtenti.class);
        formLogin = new FormLogin(gestoreUtentiMock);

        // Estrazione del pannello principale private
        Field contentPaneField = FormLogin.class.getDeclaredField("contentPane");
        contentPaneField.setAccessible(true);
        JPanel contentPane = (JPanel) contentPaneField.get(formLogin);

        // Inizializzazione gerarchia Swing agganciando il contentPane a un frame fittizio.
        // Questo previene una NullPointerException quando il FormLogin invoca SwingUtilities.getWindowAncestor()
        JFrame dummyFrame = new JFrame();
        dummyFrame.add(contentPane);
        dummyFrame.pack();

        // Estrazione campi privati tramite Reflection per la manipolazione nei test
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
    void testLoginAtleta_Successo() throws Exception {
        txtEmail.setText("atleta@email.it");
        txtPassword.setText("Password123!");

        // Setup del mock per simulare il ritorno di un'entità Atleta
        Atleta atletaFinto = mock(Atleta.class);
        when(atletaFinto.getNome()).thenReturn("Mario");
        when(gestoreUtentiMock.autenticaUtente("atleta@email.it", "Password123!")).thenReturn(atletaFinto);

        invocaEffettuaLogin();

        // Asserzioni (Basate sul testo finale impostato nel tuo FormLogin)
        assertEquals("Login Atleta riuscito!", lblEsito.getText());
        assertEquals(Color.GREEN, lblEsito.getForeground());
    }

    @Test
    void testLoginAllenatore_Successo() throws Exception {
        txtEmail.setText("coach@email.it");
        txtPassword.setText("CoachPass!");

        // Setup del mock per simulare il ritorno di un'entità Allenatore
        Allenatore allenatoreFinto = mock(Allenatore.class);
        when(allenatoreFinto.getNome()).thenReturn("Luigi");
        when(gestoreUtentiMock.autenticaUtente("coach@email.it", "CoachPass!")).thenReturn(allenatoreFinto);

        invocaEffettuaLogin();

        // Asserzioni
        assertEquals("Login Allenatore riuscito!", lblEsito.getText());
        assertEquals(Color.GREEN, lblEsito.getForeground());
    }

    @Test
    void testCredenzialiErrate_RitornaNull() throws Exception {
        txtEmail.setText("sbagliata@email.it");
        txtPassword.setText("Errata!");

        // Simuliamo che il Controller non trovi l'utente
        when(gestoreUtentiMock.autenticaUtente(anyString(), anyString())).thenReturn(null);

        invocaEffettuaLogin();

        assertEquals("Errore: Credenziali errate.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());
    }

    @Test
    void testCampiVuoti_SoloPassword() throws Exception {
        txtEmail.setText("mario.rossi@email.it");
        txtPassword.setText("");

        invocaEffettuaLogin();

        assertEquals("Errore: compila tutti i campi.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());

        // Verifica che il Controller non venga mai interpellato se la validazione Boundary fallisce
        verifyNoInteractions(gestoreUtentiMock);
    }

    @Test
    void testCampiVuoti_SoloEmail() throws Exception {
        txtEmail.setText("");
        txtPassword.setText("Password123!");

        invocaEffettuaLogin();

        assertEquals("Errore: compila tutti i campi.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());
        verifyNoInteractions(gestoreUtentiMock);
    }


}