package Boundary;

import Control.GestoreUtenti;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormRegistrazioneTest {

    private FormRegistrazione formRegistrazione;
    private GestoreUtenti gestoreUtentiMock;

    private JComboBox<String> cmbRuolo;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JTextField txtDisciplina;
    private JTextField txtCodiceAssociazione;
    private JLabel lblEsito;

    @BeforeEach
    void setUp() throws Exception {
        gestoreUtentiMock = mock(GestoreUtenti.class);
        formRegistrazione = new FormRegistrazione(gestoreUtentiMock);

        // Estrazione dei campi privati tramite Reflection per impostare i dati del test
        Class<?> clazz = FormRegistrazione.class;

        cmbRuolo = (JComboBox<String>) getPrivateField(clazz, "cmbRuolo");
        txtNome = (JTextField) getPrivateField(clazz, "txtNome");
        txtCognome = (JTextField) getPrivateField(clazz, "txtCognome");
        txtEmail = (JTextField) getPrivateField(clazz, "txtEmail");
        txtPassword = (JPasswordField) getPrivateField(clazz, "txtPassword");
        txtDisciplina = (JTextField) getPrivateField(clazz, "txtDisciplina");
        txtCodiceAssociazione = (JTextField) getPrivateField(clazz, "txtCodiceAssociazione");
        lblEsito = (JLabel) getPrivateField(clazz, "lblEsito");
    }

    private Object getPrivateField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(formRegistrazione);
    }

    @Test
    void testRegistrazioneAtleta_CodiceAllenatoreInesistente() throws Exception {
        // Setup: compiliamo i campi per un atleta con un codice finto
        cmbRuolo.setSelectedItem("Atleta");
        txtNome.setText("Fabio");
        txtCognome.setText("Campese");
        txtEmail.setText("fabio@email.com");
        txtPassword.setText("secure123");
        txtDisciplina.setText("Informatica");
        txtCodiceAssociazione.setText("CODICE_FALSO");

        // Configuriamo il mock: diciamo che questo codice NON esiste nel DB
        when(gestoreUtentiMock.esisteCodiceAllenatore("CODICE_FALSO")).thenReturn(false);

        // Esecuzione del metodo privato effettuaRegistrazione
        Method metodo = FormRegistrazione.class.getDeclaredMethod("effettuaRegistrazione");
        metodo.setAccessible(true);
        metodo.invoke(formRegistrazione);

        // Verifica: deve bloccarsi e mostrare l'errore sulla Label
        assertEquals("Errore: Il codice allenatore inserito non esiste.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());

        // Verifichiamo che la registrazione vera e propria NON sia stata invocata
        verify(gestoreUtentiMock, never()).registraAtleta(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testRegistrazioneAllenatore_CodiceVuoto_MostraErrore() throws Exception {
        // Setup: ruolo Allenatore ma senza inventare un codice associativo
        cmbRuolo.setSelectedItem("Allenatore");
        txtNome.setText("Marco");
        txtCognome.setText("Rossi");
        txtEmail.setText("marco@coach.com");
        txtPassword.setText("coachpass");
        txtDisciplina.setText("Ciclismo");
        txtCodiceAssociazione.setText(""); // Vuoto!

        Method metodo = FormRegistrazione.class.getDeclaredMethod("effettuaRegistrazione");
        metodo.setAccessible(true);
        metodo.invoke(formRegistrazione);

        // Verifica
        assertEquals("Errore: Inserire Codice Associazione.", lblEsito.getText());
        assertEquals(Color.RED, lblEsito.getForeground());
    }
}