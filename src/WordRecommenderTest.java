import java.util.ArrayList;

public class WordRecommenderTest {
    public static void main(String[] args) {
        WordRecommenderTest tester = new WordRecommenderTest();
        tester.runTests();
    }

    private void runTests() {
        System.out.println("Running WordRecommender.java tests:");
        testDictionaryLoading();
        testContains();
        testSimilarity();
        testCommonPercent();
        testGetWordSuggestions1();
        System.out.println("All WordRecommender.java tests finished.");
    }

    // Test if the dictionary loads correctly
    private void testDictionaryLoading() {
        System.out.println("\n testDictionaryLoading");
        WordRecommender wr = new WordRecommender("test_dictionary.txt");
        if (wr.contains("apple")) { // Test if WordRecommender can correctly read a word from the dictionary file
            System.out.println("testDictionaryLoading: PASSED"); // Passed if "apple" is read
        } else {
            System.out.println("testDictionaryLoading: FAILED"); // Failed if "apple" isn't read
        }
    }

    // Tests if the contains() function works
    private void testContains() {
        System.out.println("\n testContains");
        WordRecommender wr = new WordRecommender("test_dictionary.txt");
        boolean containsApple = wr.contains("apple"); // Should be true, "apple" is in test dictionary
        boolean containsMango = wr.contains("mango"); // Should be false, "mango" isn't in test dictionary
        System.out.println("apple: " + (containsApple ? "PASSED" : "FAILED")); // Passed if contains() returns true
        System.out.println("mango: " + (!containsMango ? "PASSED" : "FAILED")); // Passed if contains() returns false
    }

    // Test the getSimilarity() function for finding the avg L/R character matches
    private void testSimilarity() {
        System.out.println("\n testSimilarity");
        WordRecommender wr = new WordRecommender("test_dictionary.txt");
        double sim1 = wr.getSimilarity("apple", "apples");
        double sim2 = wr.getSimilarity("boat", "goat");
        double sim3 = wr.getSimilarity("test", "best");
        System.out.println("apple/apples similiarity = " + sim1); // Outputs the similarity value
        System.out.println("boat/goat similiarity = " + sim2); // Outputs the similarity value
        System.out.println("test/best similiarity = " + sim3); // Outputs the similarity value
    }

    // Test 1 on getWordSuggestions(), check if function returns any suggestions
    private void testGetWordSuggestions1() {
        System.out.println("\n testGetWordSuggestions1");
        WordRecommender wr = new WordRecommender("test_dictionary.txt");
        ArrayList<String> suggestions = wr.getWordSuggestions("apple", 2, 0.5, 3);

        // Check if any word suggestions are returned for "apple", test passed if one or more suggestions are returned
        System.out.println("Suggestions for 'apple': " + suggestions);
        if (suggestions != null && !suggestions.isEmpty()) {
            System.out.println("testGetWordSuggestions1: PASSED");
        } else {
            System.out.println("testGetWordSuggestions1: FAILED, no suggestions returned");
        }
    }

    // Test if commonPercent() calculates the percentage of unique character overlap correctly
    private void testCommonPercent() {
        System.out.println("\n testCommonPercent");
        WordRecommender wr = new WordRecommender("test_dictionary.txt");

        // Case 1: low overlap value (0.0), lets almost everything pass
        ArrayList<String> low = wr.getWordSuggestions("apple", 2, 0.0, 10);
        System.out.println("Low (0.0) overlap value: " + low.size() + " suggestions.");

        // Case 2: medium overlap value (0.5)
        ArrayList<String> medium = wr.getWordSuggestions("apple", 2, 0.5, 10);
        System.out.println("Medium (0.5) overlap value: " + medium.size() + " suggestions.");

        // Case 3: high overlap value (0.9)
        ArrayList<String> high = wr.getWordSuggestions("apple", 2, 0.9, 10);
        System.out.println("High (0.9) overlap value: " + high.size() + " suggestions.");

        // Check if the number of suggestions decreases as the overlap value rises, test passed if true
        if (low.size() >= medium.size() && medium.size() >= high.size()) {
            System.out.println("testCommonPercent: PASSED");
        } else {
            System.out.println("testCommonPercent: FAILED, check function for correct filtering");
        }
    }

    // Test 2 on getWordSuggestions(), check if the correct number of suggestions is returned given topN
    private void testGetWordSuggestions2() {
        System.out.println("\n testGetWordSuggestions2");
        WordRecommender wr = new WordRecommender("test_dictionary.txt");

        // Check if 2 or fewer suggestions are returned after a topN input of 2
        ArrayList<String> suggestions = wr.getWordSuggestions("boat", 2, 0.5, 2);
        System.out.println("Top 2 suggestions for 'boat': " + suggestions);
        if (suggestions.size() <= 2) {
            System.out.println("testGetWordSuggestions2: PASSED"); // 2 or fewer suggestions returned. test passed
        } else { // More than 2 suggestions returned, test failed
            System.out.println("testGetWordSuggestions2: FAILED, more than 2 suggestions returned.");
        }
    }
}
