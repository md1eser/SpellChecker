package edu.isistan.spellchecker;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import java.io.IOException;



/** Cree sus propios tests. */
public class MyTests {
    @Test
    public void pruebaEntradaVacia() throws IOException {
        String entrada = "";
        TokenScanner scanner = new TokenScanner(new StringReader(entrada));
        assertFalse(scanner.hasNext());
    }

    @Test
    public void pruebaUnSoloTokenPalabra() throws IOException {
        String entrada = "Hola";
        TokenScanner scanner = new TokenScanner(new StringReader(entrada));
        assertTrue(scanner.hasNext());
        assertEquals("Hola", scanner.next());
        assertFalse(scanner.hasNext());
    }

    @Test
    public void pruebaUnSoloTokenNoPalabra() throws IOException {
        String entrada = "!!!";
        TokenScanner scanner = new TokenScanner(new StringReader(entrada));
        assertTrue(scanner.hasNext());
        assertEquals("!!!", scanner.next());
        assertFalse(scanner.hasNext());
    }

    @Test
    public void pruebaTokensPalabraNoPalabraFinalPalabra() throws IOException {
        String entrada = "They aren't brown, are they?";
        TokenScanner scanner = new TokenScanner(new StringReader(entrada));

        String[] esperado = {"They", " ", "aren't", " ", "brown", ", ", "are", " ", "they", "?"};

        for (String tokenEsperado : esperado) {
            assertTrue(scanner.hasNext());
            assertEquals(tokenEsperado, scanner.next());
        }
        assertFalse(scanner.hasNext());
    }

    @Test
    public void pruebaTokensPalabraNoPalabraFinalNoPalabra() throws IOException {
        String entrada = "It's time\n2 e-mail!";
        TokenScanner scanner = new TokenScanner(new StringReader(entrada));

        String[] esperado = {"It's", " ", "time", "\n2 ", "e", "-", "mail", "!"};

        for (String tokenEsperado : esperado) {
            assertTrue(scanner.hasNext());
            assertEquals(tokenEsperado, scanner.next());
        }
        assertFalse(scanner.hasNext());
    }
}


