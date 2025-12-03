package edu.isistan.spellchecker.benchmark;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

import edu.isistan.spellchecker.corrector.impl.FileCorrector;

@State(Scope.Benchmark)
public class FileCorrectorBenchmark {
	private FileCorrector small, big;
	
	@Setup(Level.Trial)//cambiar a invocation
    public void setUp() throws IOException, FileCorrector.FormatException {
        String dir = "src/test/resources/";
		big = FileCorrector.make(dir + "misspellings.txt");
		small = FileCorrector.make(dir + "smallMisspellings2.txt");
	}

	@Param({"agre", "appartments", "acheives", "cruz", "altough", "accomadating" })
	private String input;

	@Benchmark
	public void bmFileCorrectorCorrectionsSmall() {
		small.getCorrections(input);
	}
	
	@Benchmark
	public void bmFileCorrectorCorrectionsBig() { 
        big.getCorrections(input); 
    }
}
