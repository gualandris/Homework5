import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
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

    public ArrayList<String> getWordSuggestions(String word, int tolerance, double commonPercent, int topN) {
        // Step 1: Filter candidates by length and character overlap
        ArrayList<String> candidates = new ArrayList<>();

        for (String dictWord : dictionary) {
            // Check length difference
            int lengthDiff = Math.abs(dictWord.length() - word.length());
            if (lengthDiff <= tolerance) {
                // Check common character percentage
                if (commonPercent(word, dictWord) >= commonPercent) {
                    candidates.add(dictWord);
                }
            }
        }

        // Step 2: Find top N by similarity WITHOUT any sorting library
        ArrayList<String> topSuggestions = new ArrayList<>();

        for (int i = 0; i < topN && !candidates.isEmpty(); i++) {
            // Find the BEST candidate remaining
            String bestCandidate = candidates.get(0);
            double bestSimilarity = getSimilarity(word, bestCandidate);

            for (int j = 1; j < candidates.size(); j++) {
                String candidate = candidates.get(j);
                double similarity = getSimilarity(word, candidate);

                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestCandidate = candidate;
                }
            }

            // Add best to results and remove from candidates
            topSuggestions.add(bestCandidate);
            candidates.remove(bestCandidate);
        }

        return topSuggestions;
    }
}