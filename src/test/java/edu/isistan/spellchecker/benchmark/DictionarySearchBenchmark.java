package edu.isistan.spellchecker.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import edu.isistan.spellchecker.corrector.Dictionary;
import edu.isistan.spellchecker.corrector.DictionaryTrie;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class DictionarySearchBenchmark {

    private Dictionary hashDictionary;
    private DictionaryTrie trieDictionary;

    @Param({"Photostatting", "shaker", "zzzzzz"})
    private String input;


    @Setup(Level.Trial)
    public void setup() {
        try {
            String dictionaryPath = "src/main/resources/dictionary.txt";
            hashDictionary = Dictionary.make(dictionaryPath);
            trieDictionary = DictionaryTrie.make(dictionaryPath);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar diccionario: " + e.getMessage());
        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }

    @Benchmark
    public boolean testHashSet() {
        return hashDictionary.isWord(input);
    }

    @Benchmark
    public boolean testTrie() {
        return trieDictionary.isWord(input);
    }

}
