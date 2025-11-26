package edu.isistan.spellchecker.corrector;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class DictionaryTrie {

    private static class NodoTrie {
        Map<Character, NodoTrie> hijos; 
        boolean finPalabra;
    }
    private final NodoTrie root = new NodoTrie();
    private int cantPalabras = 0;

    public DictionaryTrie(TokenScanner ts) throws IOException {
        if (ts == null)
            throw new IllegalArgumentException("TokenScanner no puede ser null");

        while (ts.hasNext()) {
            String tok = ts.next();
            if (TokenScanner.isWord(tok)) {
                insert(tok.toLowerCase());
            }
        }
    }

    public static DictionaryTrie make(String filename) throws IOException {
		Reader r = new FileReader(filename);
		DictionaryTrie d = new DictionaryTrie(new TokenScanner(r));
		r.close();
		return d;
	}

    private void insert(String word) {
        NodoTrie nodo = root;
        for (char c : word.toCharArray()) {
            if (!Character.isLetter(c) && c != '\'') continue;

            if (nodo.hijos == null) {
                nodo.hijos = new HashMap<>(); 
            }

            // si 'c' no existe, crea el nodo, lo guarda y lo devuelve. si ya existe, solo lo devuelve.
            nodo = nodo.hijos.computeIfAbsent(c, k -> new NodoTrie());
        }
        
        if (!nodo.finPalabra) { 
            nodo.finPalabra = true;
            cantPalabras++;
        }
    }

    public int getNumWords() {
        return cantPalabras;
    }

    public boolean isWord(String word) {
        if (word == null) return false;
        if (!TokenScanner.isWord(word)) return false;

        NodoTrie nodo = root;
        for (char c : word.toLowerCase().toCharArray()) {

            if (nodo.hijos == null) return false;

            nodo = nodo.hijos.get(c);
            if (nodo == null) {
                return false;
            }
        }
        return nodo.finPalabra;
    }
}

