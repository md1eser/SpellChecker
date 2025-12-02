package edu.isistan.spellchecker.benchmark;

import java.io.*;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.impl.SwapCorrector;
import edu.isistan.spellchecker.tokenizer.TokenScanner;

@State(Scope.Benchmark)
public class SwapCorrectorBenchmark {
	private SwapCorrector small, big;
	
	@Setup(Level.Trial)//cambiar a invocation
    public void setUp() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("smallDictionary.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo '" + "smallDictionary.txt" + "' no encontrado en resources.");
            }
            small = new SwapCorrector(new Dictionary(new TokenScanner(new InputStreamReader(inputStream))));
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary.txt")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo '" + "dictionary.txt" + "' no encontrado en resources.");
            }
            big = new SwapCorrector(new Dictionary(new TokenScanner(new InputStreamReader(inputStream))));
        }
    }

    @Param({"ay", "pa", "evangelien", "rceam", "americainzation", "atencion", "gemma", "ciivlian", "pollo", "thorw"})
    private String input;

	@Benchmark
    public void bmSwapCorrectionsSmall() {
		small.getCorrections(input);
    }

    @Benchmark
    public void bmSwapCorrectionsBig() {
        big.getCorrections(input);
    }
}
