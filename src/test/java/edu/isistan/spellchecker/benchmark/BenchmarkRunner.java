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
@BenchmarkMode(Mode.AverageTime) // Mide el tiempo promedio por operación
@OutputTimeUnit(TimeUnit.NANOSECONDS) // La búsqueda es muy rápida, usamos nanosegundos
@Warmup(iterations = 2, time = 1) // Calentamiento de la JVM
@Measurement(iterations = 3, time = 1) // Mediciones reales
@Fork(1)
public class BenchmarkRunner {

    private Dictionary hashDictionary;
    private DictionaryTrie trieDictionary;

    // Palabras para probar
    private static final String WORD_START = "apple";      // Al inicio del archivo
    private static final String WORD_END = "zebra";        // Al final
    private static final String WORD_MISS = "zzzzzzzz";    // No existe
    

    @Setup(Level.Trial)
    public void setup() {
        try {
            // Ajusta esta ruta si es necesario. En VSCode suele ser relativa a la raíz del proyecto.
            String dictionaryPath = "src/main/resources/dictionary.txt";
            
            // Cargar diccionarios
            hashDictionary = Dictionary.make(dictionaryPath);
            trieDictionary = DictionaryTrie.make(dictionaryPath);

  

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo cargar el diccionario para el benchmark: " + e.getMessage());
        } catch (Exception e) {
             throw new RuntimeException(e);
        }
    }

    // --- COMPARACIÓN 1: BÚSQUEDA (EXISTE) ---

    @Benchmark
    public boolean hashSet_Contains_Start() {
        return hashDictionary.isWord(WORD_START);
    }

    @Benchmark
    public boolean trie_Contains_Start() {
        return trieDictionary.isWord(WORD_START);
    }

    @Benchmark
    public boolean hashSet_Contains_End() {
        return hashDictionary.isWord(WORD_END);
    }

    @Benchmark
    public boolean trie_Contains_End() {
        return trieDictionary.isWord(WORD_END);
    }

    // --- COMPARACIÓN 2: BÚSQUEDA (NO EXISTE - MISS) ---

    @Benchmark
    public boolean hashSet_Miss() {
        return hashDictionary.isWord(WORD_MISS);
    }

    @Benchmark
    public boolean trie_Miss() {
        return trieDictionary.isWord(WORD_MISS);
    }

       // Método Main para ejecutarlo directamente desde VS Code
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkRunner.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
