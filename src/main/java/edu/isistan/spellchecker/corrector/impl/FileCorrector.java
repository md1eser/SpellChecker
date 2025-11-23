package edu.isistan.spellchecker.corrector.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;

/**
 * Corrector basado en un archivo.
 * 
 */
public class FileCorrector extends Corrector {

	private final Map<String, Set<String>> dictionary;

	/** Clase especial que se utiliza al tener 
	 * algún error de formato en el archivo de entrada.
	 */
	public static class FormatException extends Exception {
		public FormatException(String msg) {
			super(msg);
		}
	}


	/**
	 * Constructor del FileReader
	 *
	 * Utilice un BufferedReader para leer el archivo de definición
	 *
	 * <p> 
	 * Cada línea del archivo del diccionario tiene el siguiente formato: 
	 * misspelled_word,corrected_version
	 *
	 * <p>
	 *Ejemplo:<br>
	 * <pre>
	 * aligatur,alligator<br>
	 * baloon,balloon<br>
	 * inspite,in spite<br>
	 * who'ev,who've<br>
	 * ther,their<br>
	 * ther,there<br>
	 * </pre>
	 * <p>
	 * Estas líneas no son case-insensitive, por lo que todas deberían generar el mismo efecto:<br>
	 * <pre>
	 * baloon,balloon<br>
	 * Baloon,balloon<br>
	 * Baloon,Balloon<br>
	 * BALOON,balloon<br>
	 * bAlOon,BALLOON<br>
	 * </pre>
	 * <p>
	 * Debe ignorar todos los espacios vacios alrededor de las palabras, por lo
	 * que estas entradas son todas equivalentes:<br>
	 * <pre>
	 * inspite,in spite<br>
	 *    inspite,in spite<br>
	 * inspite   ,in spite<br>
	 *  inspite ,   in spite  <br>
	 * </pre>
	 * Los espacios son permitidos dentro de las sugerencias. 
	 *
	 * <p>
	 * Debería arrojar <code>FileCorrector.FormatException</code> si se encuentra algún
	 * error de formato:<br>
	 * <pre>
	 * ,correct<br>
	 * wrong,<br>
	 * wrong correct<br>
	 * wrong,correct,<br>
	 * </pre>
	 * <p>
	 *
	 * @param r Secuencia de caracteres 
	 * @throws IOException error leyendo el archivo
	 * @throws FileCorrector.FormatException error de formato
	 * @throws IllegalArgumentException reader es null
	 */
	public FileCorrector(Reader r) throws IOException, FormatException {
		if (r == null) {
			throw new IllegalArgumentException("Reader no puede ser null");
		}
		
		dictionary = new HashMap<>();

		BufferedReader br = new BufferedReader(r);

		String line;
		while ((line = br.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue; 
			}

			// El -1 en split sirve para detectar casos como "wrong,correct,"
			String[] parts = line.split(",", -1);

			// Formato inválido si no están las 2 partes wrong y correct
			if (parts.length != 2) {
				throw new FormatException("Formato inválido (se esperaban 2 tokens): " + line);
			}

			String wrong = parts[0].trim();
			String correct = parts[1].trim();

			// Validar que no sean cadenas vacías después del trim (ej: ",correct" o "wrong,")
			if (wrong.isEmpty() || correct.isEmpty()) {
				throw new FormatException("Formato inválido (token vacío): " + line);
			}

			// Normalizamos la clave a minúsculas para soportar case-insensitivity en la búsqueda.
			// La corrección ('correct') se guarda tal cual viene (respetando mayúsculas/minúsculas).
			dictionary.computeIfAbsent(wrong.toLowerCase(), k -> new HashSet<>()).add(correct);
		}
	
	}

	/** Construye el Filereader.
	 *
	 * @param filename 
	 * @throws IOException 
	 * @throws FileCorrector.FormatException 
	 * @throws FileNotFoundException 
	 */
	public static FileCorrector make(String filename) throws IOException, FormatException {
		Reader r = new FileReader(filename);
		FileCorrector fc;
		try {
			fc = new FileCorrector(r);
		} finally {
			if (r != null) { r.close(); }
		}
		return fc;
	}

	/**
	 * Retorna una lista de correcciones para una palabra dada.
	 * Si la palabra mal escrita no está en el diccionario el set es vacio.
	 * <p>
	 * Ver superclase.
	 *
	 * @param wrong 
	 * @return retorna un conjunto (potencialmente vacío) de sugerencias.
	 * @throws IllegalArgumentException si la entrada no es una palabra válida 
	 */
	public Set<String> getCorrections(String wrong) {
		if (wrong == null) {
			throw new IllegalArgumentException("La palabra a corregir no puede ser null.");
		}

		Set<String> corrections = dictionary.get(wrong.toLowerCase());

		if (corrections == null) {
			return Collections.emptySet();
		} else {
			// evita que pueda modificarse el set de correcciones desde afuera.
			return Collections.unmodifiableSet(corrections);  
		}
	}
}
