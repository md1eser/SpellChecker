package edu.isistan.spellchecker.benchmark;

import java.io.*;
import java.util.concurrent.TimeUnit;

import edu.isistan.spellchecker.corrector.Trie;
import org.openjdk.jmh.annotations.*;

import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.Levenshtein;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

@State(Scope.Benchmark)
public class LevenshteinBenchmark {
	private Levenshtein small, big, trie;

    @Param({"aa", "pok", "Bakershield", "Basl", "Australoithecus", "cifematic", "Garpot", "citronlla", "Artec", "vior"})
    private String input;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("smallDictionary.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo '" + "smallDictionary.txt" + "' no encontrado en resources.");
            }
            small = new Levenshtein(new Dictionary(new TokenScanner(new InputStreamReader(inputStream))));
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo '" + "dictionary.txt" + "' no encontrado en resources.");
            }
            big = new Levenshtein(new Dictionary(new TokenScanner(new InputStreamReader(inputStream))));
        }
    }
	
	@Benchmark
	public void bmLevenshteinCorrectionSmall() {
        small.getCorrections(input);
	}


    @Benchmark
    public void bmLevenshteinCorrectionBig() {
        big.getCorrections(input);
    }
}
