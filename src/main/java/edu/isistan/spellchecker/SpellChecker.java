package edu.isistan.spellchecker;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Scanner;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import java.util.Iterator;

/**
 * El SpellChecker usa un Dictionary, un Corrector, and I/O para chequear
 * de forma interactiva un stream de texto. Despues escribe la salida corregida
 * en un stream de salida. Los streams pueden ser archivos, sockets, o cualquier
 * otro stream de Java.
 * <p>
 * Nota:
 * <ul>
 * <li> La implementación provista provee métodos utiles para implementar el SpellChecker.
 * <li> Toda la salida al usuario deben enviarse a System.out (salida estandar)
 * </ul>
 * <p>
 * El SpellChecker es usado por el SpellCkecherRunner. Ver:
 * @see SpellCheckerRunner
 */
public class SpellChecker {
	private Corrector corr;
	private Dictionary dict;

	/**
	 * Constructor del SpellChecker
	 * 
	 * @param c un Corrector
	 * @param d un Dictionary
	 */
	public SpellChecker(Corrector c, Dictionary d) {
		corr = c;
		dict = d;
	}

	/**
	 * Returna un entero desde el Scanner provisto. El entero estará en el rango [min, max].
	 * Si no se ingresa un entero o este está fuera de rango, repreguntará.
	 *
	 * @param min
	 * @param max
	 * @param sc
	 */
	private int getNextInt(int min, int max, Scanner sc) {
		while (true) {
			try {
				int choice = Integer.parseInt(sc.next());
				if (choice >= min && choice <= max) {
					return choice;
				}
			} catch (NumberFormatException ex) {
				// Was not a number. Ignore and prompt again.
			}
			System.out.println("Entrada invalida. Pruebe de nuevo!");
		}
	}

	/**
	 * Retorna el siguiente String del Scanner.
	 *
	 * @param sc
	 */
	private String getNextString(Scanner sc) {
		return sc.next();

	}



    /**
     * Short description
     * 
     * @return 
     */
    public String elegirOpciones(Scanner sc, String token, Set<String> correcciones) {
        LinkedList<String> options = new LinkedList<>(correcciones);
        System.out.println("La palabra \"" + token + "\" no se encuentra en el diccionario.");
        System.out.println("Por favor, ingrese el número correspondiente a la acción deseada:");
        System.out.println("0: Ignorar y continuar");
        System.out.println("1: Reemplazar manualmente por otra palabra");

        // Mostrar sugerencias si existen
        if (!correcciones.isEmpty()) {
            for (int i = 0; i < options.size(); i++) {
                // i + 2 porque 0 y 1 ya están ocupados
                System.out.println((i + 2) + ": Reemplazar por \"" + options.get(i) + "\"");
            }
        }

        // Obtener la opción del usuario
        int opt = getNextInt(0, correcciones.size() + 1, sc);

        if (opt == 0) {
            return token; 
        } else if (opt == 1) {
            System.out.print("Ingrese la corrección manual: "); 
            return getNextString(sc); 

        } else {
            return options.get(opt - 2); 
        }

    }


	/**
	 * checkDocument interactivamente chequea un archivo de texto..  
	 * Internamente, debe usar un TokenScanner para parsear el documento.  
	 * Tokens de tipo palabra que no se encuentran en el diccionario deben ser corregidos
	 * ; otros tokens deben insertarse tal cual en en documento de salida.
	 * <p>
	 *
	 * @param in stream donde se encuentra el documento de entrada.
	 * @param input entrada interactiva del usuario. Por ejemplo, entrada estandar System.in
	 * @param out stream donde se escribe el documento de salida.
	 * @throws IOException si se produce algún error leyendo el documento.
	 */
	public void checkDocument(Reader in, InputStream input, Writer out) throws IOException {
		Scanner sc = new Scanner(input);

        Iterator<String> it = new TokenScanner(in);

        while (it.hasNext()) {
            String token = it.next(); 
            // dict.isWord: si no es token -> false,  si es token pero no esta en el diccionario -> false
            if (!dict.isWord(token) && TokenScanner.isWord(token) ) {
                Set<String> correcciones = corr.getCorrections(token);
                out.write(elegirOpciones(sc, token, correcciones));
                continue;
            } 
            out.write(token); 
        }
        out.flush();
	}
}
