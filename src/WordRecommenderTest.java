import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WordRecommenderTest {

    /** Helper: create a tiny temp dictionary file and return its path. */
    private String makeTempDict(String... words) throws IOException {
        File f = File.createTempFile("dict", ".txt");
        f.deleteOnExit();
        try (FileWriter w = new FileWriter(f)) {
            for (String s : words) {
                w.write(s.toLowerCase());
                w.write("\n");
            }
        }
        return f.getAbsolutePath();
    }

    @Test
    public void testSimilarity_exampleFromSpec() throws IOException {
        String path = makeTempDict("oblige", "oblivion");
        WordRecommender wr = new WordRecommender(path);
        double sim = wr.getSimilarity("oblige", "oblivion");
        assertEquals(2.5, sim, 1e-9);
    }

    @Test
    public void testContains() throws IOException {
        String path = makeTempDict("cat", "dog");
        WordRecommender wr = new WordRecommender(path);
        assertTrue(wr.contains("cat"));
        assertFalse(wr.contains("cot"));
    }

    @Test
    public void testSuggestions_FilterByToleranceAndCommonPct() throws IOException {
        String path = makeTempDict("morbid","hobbit","sorbet","forbid","automatically","axiomatically");
        WordRecommender wr = new WordRecommender(path);

        ArrayList<String> s = wr.getWordSuggestions("morbit", 2, 0.50, 4);
        // Should produce non-empty suggestions, all length within tolerance
        assertFalse(s.isEmpty());
        for (String cand : s) {
            assertTrue(Math.abs(cand.length() - "morbit".length()) <= 2);
        }
    }

    @Test
    public void testSuggestions_TopNDescendingBySimilarity() throws IOException {
        // Craft dictionary to control similarity order to "abcxxx"
        String path = makeTempDict("abczzz", "abcxxx", "abcyyy", "zzzabc", "xxxabc");
        WordRecommender wr = new WordRecommender(path);
        ArrayList<String> s = wr.getWordSuggestions("abcxxx", 2, 0.34, 3);
        // The perfect match should appear if not excluded by common% or tolerance
        assertFalse(s.isEmpty());
        assertTrue(s.contains("abcxxx"));
        // Top-3 in descending similarity: "abcxxx" should be first or among the first
        assertEquals("abcxxx", s.get(0));
        assertTrue(s.size() <= 3);
    }

    @Test
    public void testSuggestions_None() throws IOException {
        String path = makeTempDict("qwerty", "asdf", "zxcv");
        WordRecommender wr = new WordRecommender(path);
        ArrayList<String> s = wr.getWordSuggestions("sleepyyyyyyyyyyyyyyyy", 2, 0.50, 4);
        assertTrue(s.isEmpty());
    }
}