package edu.isistan.spellchecker.corrector;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * El diccionario maneja todas las palabras conocidas.
 * El diccionario es case insensitive 
 * 
 * Una palabra "válida" es una secuencia de letras (determinado por Character.isLetter) 
 * o apostrofes.
 */
public class Dictionary {

	private final Set<String> words = new HashSet<>();

	/**
	 * Construye un diccionario usando un TokenScanner
	 * <p>
	 * Una palabra válida es una secuencia de letras (ver Character.isLetter) o apostrofes.
	 * Toda palabra no válida se debe ignorar
	 *
	 * <p>
	 *
	 * @param ts 
	 * @throws IOException Error leyendo el archivo
	 * @throws IllegalArgumentException el TokenScanner es null
	 */
	public Dictionary(TokenScanner ts) throws IOException {
		if(ts==null) throw new IllegalArgumentException("TokenScanner no puede ser null");
		// Consumir todos los tokens del TokenScanner y guardar solo las palabras válidas, en minúsculas porque debe ser case-insensitive
        while (ts.hasNext()) {
            String tok = ts.next();
            if (TokenScanner.isWord(tok)) { // TokenScanner.isWord() ya descarta null/empty y tokens con chars inválidos
                String lower = tok.toLowerCase();
                if (!lower.isEmpty()) {
                    words.add(lower);
                }
            }
        }
	}

	/**
	 * Construye un diccionario usando un archivo.
	 *
	 *
	 * @param filename 
	 * @throws FileNotFoundException si el archivo no existe
	 * @throws IOException Error leyendo el archivo
	 */
	public static Dictionary make(String filename) throws IOException {
		Reader r = new FileReader(filename);
		Dictionary d = new Dictionary(new TokenScanner(r));
		r.close();
		return d;
	}

	/**
	 * Retorna el número de palabras correctas en el diccionario.
	 * Recuerde que como es case insensitive si Dogs y doGs están en el 
	 * diccionario, cuentan como una sola palabra.
	 * 
	 * @return número de palabras únicas
	 */
	public int getNumWords() {
		return words.size();
	}

	/**
	 * Testea si una palabra es parte del diccionario. Si la palabra no está en
	 * el diccionario debe retornar false. null debe retornar falso.
	 * Si en el diccionario está la palabra Dog y se pregunta por la palabra dog
	 * debe retornar true, ya que es case insensitive.
	 *
	 *Llamar a este método no debe reabrir el archivo de palabras.
	 *
	 * @param word verifica si la palabra está en el diccionario. 
	 * Asuma que todos los espacios en blanco antes y despues de la palabra fueron removidos.
	 * @return si la palabra está en el diccionario.
	 */
	public boolean isWord(String word) {
		if (word == null) 
			return false;
        if (!TokenScanner.isWord(word)) 
			return false;
        return words.contains(word.toLowerCase());
	}
}
