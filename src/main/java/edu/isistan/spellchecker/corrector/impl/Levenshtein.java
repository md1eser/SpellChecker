package edu.isistan.spellchecker.corrector.impl;
import java.util.HashSet;
import java.util.Set;

import edu.isistan.spellchecker.corrector.Corrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;
import edu.isistan.spellchecker.corrector.Dictionary;

/**
 *
 * Un corrector inteligente que utiliza "edit distance" para generar correcciones.
 * 
 * La distancia de Levenshtein es el número minimo de ediciones que se deber
 * realizar a un string para igualarlo a otro. Por edición se entiende:
 * <ul>
 * <li> insertar una letra
 * <li> borrar una letra
 * <li> cambiar una letra
 * </ul>
 *
 * Una "letra" es un caracter a-z (no contar los apostrofes).
 * Intercambiar letras (thsi -> this) <it>no</it> cuenta como una edición.
 * <p>
 * Este corrector sugiere palabras que esten a edit distance uno.
 */
public class Levenshtein extends Corrector {

    private Dictionary dict;

	/**
	 * Construye un Levenshtein Corrector usando un Dictionary.
	 * Debe arrojar <code>IllegalArgumentException</code> si el diccionario es null.
	 *
	 * @param dict
	 */
	public Levenshtein(Dictionary dict) {
        if (dict == null) {
            throw new IllegalArgumentException();
        }
		this.dict = dict;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a erase distance uno
	 */
	Set<String> getDeletions(String s) {
        Set<String> deletions = new HashSet<>(); 
        for (int i = 0; i < s.length(); i++) { 
            // totamos todas las combinaciones de la palabra
            // eliminando un letra
            // p.e casa -> asa, csa, caa, cas
            String comb = s.substring(0, i) + s.substring(i+1); 
            if (dict.isWord(comb)) {
                deletions.add(comb);
            } 
        } 
        return deletions;
	}

	/**
	 * @param s palabra
	 * @return todas las palabras a substitution distance uno
	 */
	public Set<String> getSubstitutions(String s) {
        Set<String> subs = new HashSet<>(); 
        for (int i = 0; i < s.length(); i++) { 
            //  para cada posición en i del string, reemplazamos 
            //  con todos los caracteres (menos el que ya esta)
            //  si se forma una palabra reemplazando solo una vez
            // p.e trio -> reemplazo en i=0 por c -> crio
        
            for (char c = 'a'; c <= 'z'; c++) {
                if (c == s.charAt(i)) continue;

                String comb = s.substring(0, i) + c + s.substring(i + 1);
                if (dict.isWord(comb)) {
                    subs.add(comb);
                }
			}


        } 
        return subs;
	}


	/**
	 * @param s palabra
	 * @return todas las palabras a insert distance uno
	 */
	public Set<String> getInsertions(String s) {
        Set<String> inserts = new HashSet<>(); 
        for (int i = 0; i <= s.length(); i++) {  // <= porque sino no se puede inserta en lo último
            //  para cada posición en i del string, hay que probar
            //  insertar todos los caracteres y chequear si se forma una palabra 
            // p.e ata -> inserto en i=0, c=b, bata
            for (char c = 'a'; c <= 'z'; c++) {
                String comb = s.substring(0, i) + c + s.substring(i); // substring(i) -> desde i hasta el fin de la cadena
                if (dict.isWord(comb)) {
                    inserts.add(comb);
                }
			}

        } 
        return inserts;
	}

    public Set<String> getCorrections(String wrong) {
        if (!TokenScanner.isWord(wrong))
            throw new IllegalArgumentException();
        Set<String> result = new HashSet<>();
        result.addAll(getDeletions(wrong));
        result.addAll(getSubstitutions(wrong));
        result.addAll(getInsertions(wrong));
        return super.matchCase(wrong, result);
    }

}
