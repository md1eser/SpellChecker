package edu.isistan.spellchecker.tokenizer;

import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * Dado un archivo provee un método para recorrerlo.
 */
public class TokenScanner implements Iterator<String> {

   private Reader reader;
   private int nextChar;
   private StringBuilder nextToken; 


  /**
   * Crea un TokenScanner.
   * <p>
   * Como un iterador, el TokenScanner solo debe leer lo justo y
   * necesario para implementar los métodos next() y hasNext(). 
   * No se debe leer toda la entrada de una.
   * <p>
   *
   * @param in fuente de entrada
   * @throws IOException si hay algún error leyendo.
   * @throws IllegalArgumentException si el Reader provisto es null
   */
  public TokenScanner(java.io.Reader in) throws IOException {
      if (in == null) {
        throw new IllegalArgumentException(); 
      }
      this.reader = in;
      this.nextToken = new StringBuilder();  
      this.nextChar = in.read();
  }

  /**
   * Determina si un carácer es una caracter válido para una palabra.
   * <p>
   * Un caracter válido es una letra (
   * Character.isLetter) o una apostrofe '\''.
   *
   * @param c 
   * @return true si es un caracter
   */
  public static boolean isWordCharacter(int c) {
    return Character.isLetter(c) || c == '\'';
  }


   /**
   * Determina si un string es una palabra válida.
   * Null no es una palabra válida.
   * Un string que todos sus caracteres son válidos es una 
   * palabra. Por lo tanto, el string vacío es una palabra válida.
   * @param s 
   * @return true si el string es una palabra.
   */
  public static boolean isWord(String s) {
        if (s == null) {
            return false;
        }
        if (s.equals("")) {
            return false;
        }

        for (char c : s.toCharArray()) {
            if (!TokenScanner.isWordCharacter(c)) {
                return false;
            }
        }    
		return true;
  }

  /**
   * Determina si hay otro token en el reader.
   */
  public boolean hasNext() {
    return this.nextChar != -1;
  }

  /**
   * Retorna el siguiente token.
   *
   * @throws NoSuchElementException cuando se alcanzó el final de stream
   */
  public String next() {
    // "It's", " ", "time", "\n2 ", "e", "-", "mail", "!" 
    // dos modos
    // esta en palabra -> armar el token builder hasta terminar la palabra
    // no esta en palabra -> usar el token builder hasta llegar a una palabra
    if (!this.hasNext()) {
        throw new NoSuchElementException();        
    }
    this.nextToken.setLength(0);
    boolean isWord = TokenScanner.isWordCharacter(this.nextChar);
    while (this.nextChar != -1 && TokenScanner.isWordCharacter( this.nextChar ) == isWord ) {
        this.nextToken.append((char) this.nextChar);
        try {
            this.nextChar = this.reader.read(); // read next char
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            
        }

    }
    return this.nextToken.toString();
  }

}
