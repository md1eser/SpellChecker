package edu.isistan.spellchecker.benchmark;

import java.io.*;
import java.util.concurrent.TimeUnit;

import edu.isistan.spellchecker.corrector.DictionaryTrie;
import org.openjdk.jmh.annotations.*;

import edu.isistan.spellchecker.corrector.impl.TrieLevenshtein;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

@State(Scope.Benchmark)
public class TrieLevenshteinBenchmark {
    private TrieLevenshtein small, big;


    @Param({"teh", "ay", "evangelint", "ream", "americanizatio", "atencion", "gemma", "civilin", "pollo", "theow"})
    private String input;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("smallDictionary.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo '" + "smallDictionary.txt" + "' no encontrado en resources.");
            }
            small = new TrieLevenshtein(new DictionaryTrie(new TokenScanner(new InputStreamReader(inputStream))));
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo '" + "dictionary.txt" + "' no encontrado en resources.");
            }
            big = new TrieLevenshtein(new DictionaryTrie(new TokenScanner(new InputStreamReader(inputStream))));
        }
    }

    @Benchmark
    public void bmLevenshteinTrieCorrectionSmall() {
        small.getCorrections(input);
    }

    @Benchmark
    public void bmLevenshteinTrieCorrectionBig() {
        big.getCorrections(input);
    }
}
