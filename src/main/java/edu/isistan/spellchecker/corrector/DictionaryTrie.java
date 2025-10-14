package edu.isistan.spellchecker.corrector;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import edu.isistan.spellchecker.tokenizer.TokenScanner;

public class DictionaryTrie {
    // Nodo interno del Trie
    private static class NodoTrie {
        NodoTrie[] hijos = new NodoTrie[27]; // 26 letras + apostrofe
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
            int index = charToIndex(c);
            if (index == -1) return; // ignorar caracteres inválidos
            if (nodo.hijos[index] == null) {
                nodo.hijos[index] = new NodoTrie();
            }
            nodo = nodo.hijos[index];
        }
        if (!nodo.finPalabra) { // si es la primera vez que aparece
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
            int index = charToIndex(c);
            if (index == -1 || nodo.hijos[index] == null) {
                return false;
            }
            nodo = nodo.hijos[index];
        }
        return nodo.finPalabra;
    }

    // Mapear char → índice (0–25 letras, 26 apóstrofe)
    private int charToIndex(char c) {
        if (c == '\'') return 26;
        if (Character.isLetter(c)) return c - 'a';
        return -1; // carácter inválido
    }
}

