package edu.isistan.spellchecker.corrector;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
//import java.util.HashMap;
//import java.util.Map;

import edu.isistan.spellchecker.tokenizer.TokenScanner;




public class DictionaryTrie {

    private final Trie trie;

    public DictionaryTrie(TokenScanner ts) throws IOException {
        this.trie = new Trie();

        if (ts == null)
            throw new IllegalArgumentException("TokenScanner no puede ser null");

        while (ts.hasNext()) {
            String tok = ts.next();
            if (TokenScanner.isWord(tok)) {
                trie.insert(tok.toLowerCase());
            }
        }
    }

    public static DictionaryTrie make(String filename) throws IOException {
        Reader r = new FileReader(filename);
        DictionaryTrie d = new DictionaryTrie(new TokenScanner(r));
        r.close();
        return d;
    }

    public boolean isWord(String word) {
        if (word == null || !TokenScanner.isWord(word)) return false;
        return trie.contains(word.toLowerCase());
    }

    public int getNumWords() {
        return trie.size();
    }
    
    // Método necesario para exponer la estructura al algoritmo LevenshteinTrie
    public Trie getTrie() {
        return this.trie;
    }
}

