package edu.isistan.spellchecker.corrector.impl;

import java.util.HashSet;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.corrector.DictionaryTrie;
import edu.isistan.spellchecker.corrector.Trie;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

/**
 * Un corrector inteligente que utiliza "edit distance" y un Trie optimizado.
 * Implementa la estrategia de Poda (Pruning): Si el prefijo no existe en el árbol,
 * deja de buscar correcciones para esa rama.
 */
public class TrieLevenshtein extends Corrector {

    private final Trie trie;

    /**
     * Construye un Levenshtein Corrector usando un DictionaryTrie.
     * @param dict El diccionario que contiene las palabras válidas.
     */
    public LevenshteinTrie(DictionaryTrie dict) {
        if (dict == null) {
            throw new IllegalArgumentException("El DictionaryTrie no puede ser null");
        }
        this.trie = dict.getTrie();
    }

    /**
     * Busca palabras resultantes de borrar 1 caracter.
     */
    Set<String> getDeletions(String s) {
        Set<String> deletions = new HashSet<>();
        
        // 'valid' es un puntero que navega el árbol paso a paso con la palabra original
        Trie valid = trie;

        for (int i = 0; i < s.length(); i++) {
            // Intentamos borrar el caracter actual (i).
            // Si desde donde estamos (valid), el resto de la palabra existe, es candidato.
            if (valid.contains(s.substring(i + 1))) {
                deletions.add(s.substring(0, i) + s.substring(i + 1));
            }

            // PODA: Intentamos avanzar al siguiente nodo (el caracter i).
            // Si no existe camino para s.charAt(i), rompemos el bucle.
            valid = valid.getSubTrie(s.charAt(i));
            if (valid == null) {
                break; 
            }
        }
        return deletions;
    }

    /**
     * Busca palabras resultantes de sustituir 1 caracter.
     */
    public Set<String> getSubstitutions(String s) {
        Set<String> substitutions = new HashSet<>();
        Trie valid = trie;

        for (int i = 0; i < s.length(); i++) {
            // Para la posición actual i, probamos todas las letras del alfabeto
            for (char c = 'a'; c <= 'z'; c++) {
                // Si la letra es distinta a la original
                if (c != s.charAt(i)) {
                    // Verificamos: ¿Existe la letra 'c' seguida del resto de la palabra?
                    if (valid.contains(c + s.substring(i + 1))) {
                        substitutions.add(s.substring(0, i) + c + s.substring(i + 1));
                    }
                }
            }

            // PODA: Avanzamos con la letra original. Si no existe, chau.
            valid = valid.getSubTrie(s.charAt(i));
            if (valid == null) {
                break;
            }
        }
        return substitutions;
    }

    /**
     * Busca palabras resultantes de insertar 1 caracter.
     */
    public Set<String> getInsertions(String s) {
        Set<String> insertions = new HashSet<>();
        Trie valid = trie;

        for (int i = 0; i <= s.length(); i++) {
            // En la posición i, intentamos meter cualquier letra
            for (char c = 'a'; c <= 'z'; c++) {
                // Chequeamos si (letra_insertada + resto_palabra) existe desde 'valid'
                if (valid.contains(c + s.substring(i))) {
                    insertions.add(s.substring(0, i) + c + s.substring(i));
                }
            }

            // PODA: Avanzamos con la letra original (si no llegamos al final)
            if (i < s.length()) {
                valid = valid.getSubTrie(s.charAt(i));
                if (valid == null) {
                    break;
                }
            }
        }
        return insertions;
    }

    @Override
    public Set<String> getCorrections(String wrong) {
        if (!TokenScanner.isWord(wrong))
            throw new IllegalArgumentException("La entrada debe ser una palabra válida");

        // Obtenemos los candidatos en crudo (todo minúsculas)
        Set<String> result = new HashSet<>();
        result.addAll(getDeletions(wrong));
        result.addAll(getSubstitutions(wrong));
        result.addAll(getInsertions(wrong));

        // matchCase ajusta mayúsculas/minúsculas
        return matchCase(wrong, result);
    }
}
