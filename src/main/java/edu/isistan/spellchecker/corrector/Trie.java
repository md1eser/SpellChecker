package edu.isistan.spellchecker.corrector;

import java.util.HashMap;
import java.util.Map;

/**
 * Estructura de datos Trie genérica.
 * Sabe guardar cadenas y permite navegar por sus ramas.
 */
public class Trie {

    private static class Node {
        Map<Character, Node> children;
        boolean isEndOfWord;
    }

    // La raíz de esta instancia (puede ser la raíz global o un sub-nodo)
    private final Node root;
    private int size = 0;

    /**
     * Constructor público: crea un Trie vacío.
     */
    public Trie() {
        this.root = new Node();
    }

    /**
     * Constructor privado: crea una "vista" de un sub-árbol.
     */
    private Trie(Node node) {
        this.root = node;
    }

    /**
     * Inserta una palabra en el Trie.
     */
    public void insert(String word) {
        Node current = root;
        boolean isNew = false;
        
        for (char c : word.toCharArray()) {
            if (current.children == null) {
                current.children = new HashMap<>();
            }
            // Si creamos un nodo nuevo, potencialmente es una palabra nueva
            current = current.children.computeIfAbsent(c, k -> new Node());
        }

        if (!current.isEndOfWord) {
            current.isEndOfWord = true;
            size++;
        }
    }

    /**
     * Verifica si la palabra existe desde el nodo actual.
     */
    public boolean contains(String word) {
        if (word == null) return false;
        
        Node current = root;
        for (char c : word.toCharArray()) {
            if (current.children == null) return false;
            
            current = current.children.get(c);
            if (current == null) return false;
        }
        return current.isEndOfWord;
    }

    /**
     * Devuelve el sub-árbol que comienza con el caracter dado.
     * Útil para algoritmos de búsqueda recursiva (como Levenshtein optimizado).
     * @return null si no existe camino con ese caracter.
     */
    public Trie getSubTrie(char c) {
        if (root.children == null) return null;
        
        Node child = root.children.get(c);
        if (child == null) return null;

        return new Trie(child);
    }
    
    public int size() {
        return size;
    }
}
