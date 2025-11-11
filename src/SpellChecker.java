import java.io.*;
import java.util.*;

public class SpellChecker {

    // Per spec: Do not change these lines (a Scanner field called inputReader, initialized in constructor
    // and closed at the end of start()).
    private final Scanner inputReader;

    public SpellChecker() {
        inputReader = new Scanner(System.in);
    }

    public void start() {
        try {
            // 1) Ask repeatedly for a valid dictionary file
            String dictionaryFile = promptForExistingFile(Util.DICTIONARY_PROMPT);
            System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, dictionaryFile);

            WordRecommender recommender = new WordRecommender(dictionaryFile);

            // 2) Ask repeatedly for a valid input file to spell check
            String inputFile = promptForExistingFile(Util.FILENAME_PROMPT);
            String outputFile = computeOutputFileName(inputFile);
            System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, inputFile, outputFile);

            // 3) Spell check the input and write output
            processFile(inputFile, outputFile, recommender);

        } finally {
            // Per spec: close the inputReader as the last line of start()
            inputReader.close();
        }
    }

    private String promptForExistingFile(String prompt) {
        while (true) {
            System.out.print(prompt);
            String path = safeReadLine().trim();
            File f = new File(path);
            if (f.exists() && f.isFile() && f.canRead()) {
                return path;
            }
            System.out.print(Util.FILE_OPENING_ERROR);
        }
    }

    private String computeOutputFileName(String inputFile) {
        // For "fileName.txt" -> "fileName_chk.txt"; if no dot, append "_chk.txt"
        int dot = inputFile.lastIndexOf('.');
        if (dot > 0 && dot < inputFile.length() - 1) {
            String stem = inputFile.substring(0, dot);
            String ext  = inputFile.substring(dot); // includes dot
            return stem + "_chk" + ext;
        }
        return inputFile + "_chk.txt";
    }

    private void processFile(String inputFile, String outputFile, WordRecommender recommender) {
        try (Scanner fileScanner = new Scanner(new File(inputFile));
             PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)))) {

            boolean first = true;
            while (fileScanner.hasNext()) {
                String word = fileScanner.next().trim().toLowerCase();

                String finalWord;
                if (recommender.contains(word)) {
                    finalWord = word;
                } else {
                    finalWord = handleMisspelling(word, recommender);
                }

                if (!first) out.print(' ');
                out.print(finalWord);
                first = false;
            }
        } catch (IOException e) {
            // If writing fails, surface a simple message to stderr.
            System.err.println("I/O error while processing files: " + e.getMessage());
        }
    }

    private String handleMisspelling(String misspelled, WordRecommender recommender) {
        System.out.printf(Util.MISSPELL_NOTIFICATION, misspelled);

        // Build suggestions with (tolerance=2, commonPercent=0.50, topN=4)
        ArrayList<String> suggestions = recommender.getWordSuggestions(misspelled, 2, 0.50, 4);

        if (suggestions.isEmpty()) {
            System.out.print(Util.NO_SUGGESTIONS);
            // Only 'a' or 't'
            while (true) {
                System.out.print(Util.TWO_OPTION_PROMPT);
                String choice = safeReadLine().trim();
                if (choice.equals("a")) {
                    return misspelled;
                } else if (choice.equals("t")) {
                    System.out.print(Util.MANUAL_REPLACEMENT_PROMPT);
                    String repl = nextWordFromLine();
                    return repl.toLowerCase();
                } else {
                    System.out.print(Util.INVALID_RESPONSE);
                }
            }
        } else {
            System.out.print(Util.FOLLOWING_SUGGESTIONS);
            for (int i = 0; i < suggestions.size(); i++) {
                System.out.printf(Util.SUGGESTION_ENTRY, (i + 1), suggestions.get(i));
            }

            while (true) {
                System.out.print(Util.THREE_OPTION_PROMPT);
                String choice = safeReadLine().trim();
                if (choice.equals("a")) {
                    return misspelled;
                } else if (choice.equals("t")) {
                    System.out.print(Util.MANUAL_REPLACEMENT_PROMPT);
                    String repl = nextWordFromLine();
                    return repl.toLowerCase();
                } else if (choice.equals("r")) {
                    System.out.print(Util.AUTOMATIC_REPLACEMENT_INFO);
                    int idx = readValidIndex(suggestions.size());
                    return suggestions.get(idx - 1);
                } else {
                    System.out.print(Util.INVALID_RESPONSE);
                }
            }
        }
    }

    private int readValidIndex(int max) {
        // Loop until a valid integer 1..max is entered.
        while (true) {
            System.out.print(Util.AUTOMATIC_REPLACEMENT_NUMBER_PROMPT);
            String line = safeReadLine().trim();
            try {
                int n = Integer.parseInt(line);
                if (n >= 1 && n <= max) return n;
            } catch (NumberFormatException ignored) {}
            System.out.print(Util.INVALID_RESPONSE);
        }
    }

    /** Reads a full line safely; if stdin closes, returns empty string. */
    private String safeReadLine() {
        try {
            if (inputReader.hasNextLine()) {
                return inputReader.nextLine();
            }
        } catch (NoSuchElementException ignored) {}
        return "";
    }

    /**
     * After prompting for manual replacement, we want the next token (word).
     * If the user types multiple words, we take the first non-empty token.
     */
    private String nextWordFromLine() {
        String line = safeReadLine();
        if (line == null) return "";
        String[] parts = line.trim().split("\\s+");
        for (String p : parts) {
            if (!p.isEmpty()) return p;
        }
        return "";
    }
}