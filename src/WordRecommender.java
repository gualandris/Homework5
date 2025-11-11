import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class WordRecommender {

    private final ArrayList<String> dictionary;
    private final HashSet<String> dictSet;

    public WordRecommender(String dictionaryFileName) {
        this.dictionary = new ArrayList<>();
        this.dictSet = new HashSet<>();
        loadDictionary(dictionaryFileName);
    }

    private void loadDictionary(String filename) {
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextLine()) {
                String w = sc.nextLine().trim().toLowerCase();
                if (!w.isEmpty()) {
                    dictionary.add(w);
                    dictSet.add(w);
                }
            }
        } catch (FileNotFoundException e) {
            // Let caller handle invalid path earlier; here we just keep empty dict if constructed incorrectly.
        }
    }

    /** Fast exact check: whether word is in the dictionary. */
    public boolean contains(String word) {
        return dictSet.contains(word);
    }

    /** Left-right similarity: average of left and right aligned character matches. */
    public double getSimilarity(String a, String b) {
        int min = Math.min(a.length(), b.length());

        int left = 0;
        for (int i = 0; i < min; i++) {
            if (a.charAt(i) == b.charAt(i)) left++;
        }

        int right = 0;
        for (int k = 1; k <= min; k++) {
            if (a.charAt(a.length() - k) == b.charAt(b.length() - k)) right++;
        }

        return (left + right) / 2.0;
    }

    /** Percentage of unique character overlap (|A∩B| / |A∪B|) using sets (no duplicates). */
    private double commonPercent(String a, String b) {
        HashSet<Character> A = new HashSet<>();
        HashSet<Character> B = new HashSet<>();
        for (char c : a.toCharArray()) A.add(c);
        for (char c : b.toCharArray()) B.add(c);

        HashSet<Character> union = new HashSet<>(A);
        union.addAll(B);
        if (union.isEmpty()) return 1.0;

        HashSet<Character> inter = new HashSet<>(A);
        inter.retainAll(B);
        return (double) inter.size() / union.size();
    }

    /**
     * Return up to topN candidates from the dictionary for replacement of 'word',
     * filtered by length tolerance and minimum commonPercent, and ranked by left-right similarity.
     *
     * Must NOT use Collections.sort over entire dictionary. We maintain a size-N min-heap.
     */
    public ArrayList<String> getWordSuggestions(String word, int tolerance, double commonPercent, int topN) {
        if (topN <= 0) return new ArrayList<>();

        // Min-heap keyed by similarity so we can keep only the best topN.
        PriorityQueue<String> pq = new PriorityQueue<>(topN, (x, y) -> {
            double sx = getSimilarity(word, x);
            double sy = getSimilarity(word, y);
            return Double.compare(sx, sy); // smallest similarity at head
        });

        for (String cand : dictionary) {
            if (Math.abs(cand.length() - word.length()) > tolerance) continue;
            if (commonPercent(word, cand) < commonPercent) continue;

            pq.offer(cand);
            if (pq.size() > topN) pq.poll();
        }

        // Extract from min-heap to list in descending similarity
        ArrayList<String> out = new ArrayList<>();
        while (!pq.isEmpty()) out.add(0, pq.poll()); // reverse order as we pop smallest first
        // If ties in similarity exist, this order is acceptable.
        return out;
    }
}