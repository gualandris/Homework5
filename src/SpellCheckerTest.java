import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.io.*;
import java.util.*;

public class SpellCheckerTest {

    public static void main(String[] args) {
        SpellCheckerTest tester = new SpellCheckerTest();
        tester.runTests();
    }

    private void runTests() {
        System.out.println("Starting SpellChecker Tests...\n");

        processFileTest();
        handleMisspellingTest();

        System.out.println("\nAll tests completed!");
    }

    // Test processFile() method
    private void processFileTest() {
        System.out.println("=== Testing processFile() ===");

        // Test 1: All words spelled correctly
        test1_AllWordsCorrect();

        // Test 2: Mix of correct and misspelled words
        test2_MixedWords();

        // Test 3: Empty file
        test3_EmptyFile();

        // Test 4: Single word file
        test4_SingleWord();
    }

    private void test1_AllWordsCorrect() {
        System.out.println("\nTest 1: All words correct");
        try {
            // Create test dictionary
            createTestFile("test_dict.txt", "hello\nworld\njava\nprogramming\n");

            // Create test input file
            createTestFile("test_input.txt", "hello world java programming");

            // Setup SpellChecker
            SpellChecker checker = new SpellChecker();
            HashSet<String> dictionary = new HashSet<>();
            dictionary.add("hello");
            dictionary.add("world");
            dictionary.add("java");
            dictionary.add("programming");
            checker.setDictionarySet(dictionary);

            // Simulate user input for misspellings (none expected)
            String simulatedInput = "";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Process file using reflection to call private method
            java.lang.reflect.Method method = SpellChecker.class.getDeclaredMethod(
                    "processFile", String.class, String.class);
            method.setAccessible(true);
            method.invoke(checker, "test_input.txt", "test_output.txt");

            // Verify output
            String output = readFile("test_output.txt");
            String expected = "hello world java programming";

            if (output.equals(expected)) {
                System.out.println("PASSED: Output matches expected");
            } else {
                System.out.println("FAILED: Expected '" + expected + "' but got '" + output + "'");
            }

            // Cleanup
            cleanup("test_dict.txt", "test_input.txt", "test_output.txt");

        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void test2_MixedWords() {
        System.out.println("\nTest 2: Mix of correct and misspelled words");
        try {
            // Create test dictionary
            createTestFile("test_dict2.txt", "the\ncat\nsat\non\nmat\n");

            // Create test input file with one misspelled word
            createTestFile("test_input2.txt", "the cat satt on mat");

            // Setup SpellChecker
            SpellChecker checker = new SpellChecker();
            HashSet<String> dictionary = new HashSet<>();
            dictionary.add("the");
            dictionary.add("cat");
            dictionary.add("sat");
            dictionary.add("on");
            dictionary.add("mat");
            checker.setDictionarySet(dictionary);

            // Simulate user choosing to accept misspelling (option 'a')
            String simulatedInput = "a\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Need to reinitialize Scanner in SpellChecker
            // This is a limitation - in production, dependency injection would be better

            System.out.println("Test setup complete (manual verification needed due to Scanner dependency)");

            // Cleanup
            cleanup("test_dict2.txt", "test_input2.txt");

        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }

    private void test3_EmptyFile() {
        System.out.println("\nTest 3: Empty file");
        try {
            createTestFile("test_empty.txt", "");

            SpellChecker checker = new SpellChecker();
            HashSet<String> dictionary = new HashSet<>();
            dictionary.add("test");
            checker.setDictionarySet(dictionary);

            java.lang.reflect.Method method = SpellChecker.class.getDeclaredMethod(
                    "processFile", String.class, String.class);
            method.setAccessible(true);
            method.invoke(checker, "test_empty.txt", "test_empty_output.txt");

            String output = readFile("test_empty_output.txt");

            if (output.isEmpty()) {
                System.out.println("PASSED: Empty file produces empty output");
            } else {
                System.out.println("FAILED: Expected empty output but got '" + output + "'");
            }

            cleanup("test_empty.txt", "test_empty_output.txt");

        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }

    private void test4_SingleWord() {
        System.out.println("\nTest 4: Single word file");
        try {
            createTestFile("test_single.txt", "java");

            SpellChecker checker = new SpellChecker();
            HashSet<String> dictionary = new HashSet<>();
            dictionary.add("java");
            checker.setDictionarySet(dictionary);

            java.lang.reflect.Method method = SpellChecker.class.getDeclaredMethod(
                    "processFile", String.class, String.class);
            method.setAccessible(true);
            method.invoke(checker, "test_single.txt", "test_single_output.txt");

            String output = readFile("test_single_output.txt");

            if (output.equals("java")) {
                System.out.println("PASSED: Single word processed correctly");
            } else {
                System.out.println("FAILED: Expected 'java' but got '" + output + "'");
            }

            cleanup("test_single.txt", "test_single_output.txt");

        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }
    }

    // Test handleMisspelling() - since it's private, we test it indirectly
    private void handleMisspellingTest() {
        System.out.println("\n=== Testing handleMisspelling() (indirect) ===");
        System.out.println("\nNote: handleMisspelling is private and requires user input.");
        System.out.println("Consider refactoring to accept an InputStream parameter for testing,");
        System.out.println("or making it package-private with an overloaded version for testing.");

        System.out.println("\nManual test scenarios to verify:");
        System.out.println("1. Misspelled word with suggestions - choose 'a' (accept)");
        System.out.println("2. Misspelled word with suggestions - choose 'r' (replace with suggestion)");
        System.out.println("3. Misspelled word with suggestions - choose 't' (type replacement)");
        System.out.println("4. Misspelled word with NO suggestions - choose 'a' (accept)");
        System.out.println("5. Misspelled word with NO suggestions - choose 't' (type replacement)");
        System.out.println("6. Invalid responses before valid choice");
    }

    // Helper methods
    private void createTestFile(String filename, String content) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filename));
        writer.print(content);
        writer.close();
    }

    private String readFile(String filename) throws IOException {
        Scanner scanner = new Scanner(new File(filename));
        StringBuilder content = new StringBuilder();

        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine());
        }
        scanner.close();

        return content.toString();
    }

    private void cleanup(String... filenames) {
        for (String filename : filenames) {
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}