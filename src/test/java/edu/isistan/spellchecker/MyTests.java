package edu.isistan.spellchecker;
import org.junit.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import java.util.Set;

import edu.isistan.spellchecker.corrector.DictionaryTrie;
import edu.isistan.spellchecker.corrector.impl.FileCorrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import java.io.IOException;



/** Cree sus propios tests. */
public class MyTests {
    private Dictionary dictionary;
    private DictionaryTrie dictionaryTrie;

    //TESTS de TOKEN SCANNER---------------------------------------------------------------
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

    //TEST de Dictionary (basado en HashSet) -------------------------------------------
    @Before
    public void setUpDictionary() throws IOException {
        String texto = "Palabra uno\ndos DOS tres";
        TokenScanner scanner = new TokenScanner(new StringReader(texto));
        dictionary = new Dictionary(scanner);
    }

    @Test
    public void pruebaPalabraDentroDiccionario() {
        assertTrue(dictionary.isWord("uno"));
    }

    @Test
    public void pruebaPalabraFueraDiccionario() {
        assertFalse(dictionary.isWord("cuatro"));
    }

    @Test
    public void pruebaTamanioDiccionario() {
        assertEquals(4, dictionary.getNumWords());
    }

    @Test
    public void pruebaPalabraVacia() {
        assertFalse(dictionary.isWord(""));
        assertFalse(dictionary.isWord(null));
    }

    @Test
    public void pruebaCaseInsensitive() {
        assertTrue(dictionary.isWord("PALABRA"));
        assertTrue(dictionary.isWord("PalAbRa"));
        assertTrue(dictionary.isWord("dos"));
        assertTrue(dictionary.isWord("DOS"));
    }

    //TEST de DictionaryTrie (basado en Trie) --------------------------------------------
    @Before
    public void setUpDictionaryTrie() throws IOException {
        String texto = "Palabra uno\ndos DOS tres";
        TokenScanner scanner = new TokenScanner(new StringReader(texto));
        dictionaryTrie = new DictionaryTrie(scanner);
    }

    @Test
    public void pruebaPalabraDentroDiccionarioTrie() {
        assertTrue(dictionaryTrie.isWord("uno"));
    }

    @Test
    public void pruebaPalabraFueraDiccionarioTrie() {
        assertFalse(dictionaryTrie.isWord("cuatro"));
    }

    @Test
    public void pruebaTamanioDiccionarioTrie() {
        assertEquals(4, dictionaryTrie.getNumWords());
    }

    @Test
    public void pruebaPalabraVaciaTrie() {
        assertFalse(dictionaryTrie.isWord(""));
        assertFalse(dictionaryTrie.isWord(null));
    }

    @Test
    public void pruebaCaseInsensitiveTrie() {
        assertTrue(dictionaryTrie.isWord("PALABRA"));
        assertTrue(dictionaryTrie.isWord("PaLaBra"));
        assertTrue(dictionaryTrie.isWord("dos"));
        assertTrue(dictionaryTrie.isWord("DOS"));
    }
    
    
    //TESTS de FileCorrector-----------------------------------------------------------------------------------------
    
    //Probar un archivo con espacios extras alrededor de las líneas y las comas
    @Test
    public void testArchivoConEspaciosExtras() throws Exception {
        String contenidoDelArchivo = 
            "   palabraMal  ,  palabraBien   \n" +  
            "otramal,   otrabien\n" +              
            "   finaL ,final   ";                  

        FileCorrector corrector = new FileCorrector(new StringReader(contenidoDelArchivo));
        Set<String> correcciones1 = corrector.getCorrections("palabraMal");
        assertTrue("Debería contener la corrección limpia", correcciones1.contains("palabraBien"));
        Set<String> correcciones2 = corrector.getCorrections("otramal");
        assertTrue("Debería ignorar espacios iniciales en la corrección", correcciones2.contains("otrabien"));        
        Set<String> correcciones3 = corrector.getCorrections("finaL");
        assertTrue(correcciones3.contains("final"));
    }

    //Pedir correcciones para una palabra sin correcciones.
    @Test
    public void testPalabraSinCorrecciones() throws Exception {
        String diccionario = "teh,the";
        FileCorrector corrector = new FileCorrector(new StringReader(diccionario));
        Set<String> resultado = corrector.getCorrections("palabraInexistente");
        assertNotNull("El set no debería ser null", resultado);
        assertTrue("El set debería estar vacío", resultado.isEmpty());
    }

    //Pedir correcciones para una palabra con múltiples correcciones.
    @Test
    public void testPalabraConMultiplesCorrecciones() throws Exception {
        //archivo donde 'teh' puede ser 'the', 'tea' o 'ten'
        String diccionario = 
            "teh,the\n" +
            "teh,tea\n" +
            "teh,ten";
            
        FileCorrector corrector = new FileCorrector(new StringReader(diccionario));
        Set<String> sugerencias = corrector.getCorrections("teh");
        assertEquals("Debería haber 3 sugerencias", 3, sugerencias.size());
        assertTrue(sugerencias.contains("the"));
        assertTrue(sugerencias.contains("tea"));
        assertTrue(sugerencias.contains("ten"));
    }

    
     //Probar correcciones para palabras con distintas capitalizaciones
    @Test
    public void testCaseInsensitivity() throws Exception {
        String diccionario = "baloon,balloon"; 
        FileCorrector corrector = new FileCorrector(new StringReader(diccionario));
        String[] variantes = {"baloon", "BALOON", "BaLoOn", "bAlOoN"};
        for (String variante : variantes) {
            Set<String> resultado = corrector.getCorrections(variante);
            assertFalse("No debería estar vacío para: " + variante, resultado.isEmpty());
            assertTrue("Debería contener 'balloon' para: " + variante, resultado.contains("balloon"));
        }
    }
}


