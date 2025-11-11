import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class SpellChecker {
    private Scanner inputReader;
    private HashSet<String> dictionarySet;
    private WordRecommender recommender;

    public SpellChecker() {
        inputReader = new Scanner(System.in);
    }

    public void start() {
        // Step 1: Get valid dictionary filename
        String dictionaryFileName = null;
        while (true) {
            System.out.printf(Util.DICTIONARY_PROMPT);
            String fileName = inputReader.nextLine().trim();

            try {
                File file = new File(fileName);
                Scanner testScanner = new Scanner(file);
                testScanner.close();
                dictionaryFileName = fileName;
                System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, fileName);
                break;
            } catch (Exception e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
            }
        }

        // Step 2: Load dictionary into HashSet
        dictionarySet = new HashSet<>();
        try {
            Scanner dictScanner = new Scanner(new File(dictionaryFileName));
            while (dictScanner.hasNextLine()) {
                dictionarySet.add(dictScanner.nextLine().trim().toLowerCase());
            }
            dictScanner.close();
        } catch (Exception e) {
            // Silent fail - dictionary already validated
        }

        // Step 3: Create recommender
        recommender = new WordRecommender(dictionaryFileName);

        // Step 4: Get valid input filename
        String inputFileName = null;
        String outputFileName = null;
        while (true) {
            System.out.printf(Util.FILENAME_PROMPT);
            String fileName = inputReader.nextLine().trim();

            try {
                File file = new File(fileName);
                Scanner testScanner = new Scanner(file);
                testScanner.close();
                inputFileName = fileName;

                // Generate output filename
                int dot = fileName.lastIndexOf('.');
                if (dot > 0) {
                    outputFileName = fileName.substring(0, dot) + "_chk.txt";
                } else {
                    outputFileName = fileName + "_chk.txt";
                }

                System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, fileName, outputFileName);
                break;
            } catch (Exception e) {
                System.out.printf(Util.FILE_OPENING_ERROR);
            }
        }

        // Step 5: Process file
        processFile(inputFileName, outputFileName);

        inputReader.close(); // DO NOT MODIFY - must be last line
    }

    private void processFile(String inputFileName, String outputFileName) {
        try {
            Scanner fileScanner = new Scanner(new File(inputFileName));
            PrintWriter writer = new PrintWriter(new FileWriter(outputFileName));

            boolean firstWord = true;
            while (fileScanner.hasNext()) {
                String word = fileScanner.next().toLowerCase();

                String finalWord;
                if (dictionarySet.contains(word)) {
                    finalWord = word;
                } else {
                    finalWord = handleMisspelling(word);
                }

                if (!firstWord) {
                    writer.print(" ");
                }
                writer.print(finalWord);
                firstWord = false;
            }

            fileScanner.close();
            writer.close();

        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    private String handleMisspelling(String misspelledWord) {
        System.out.printf(Util.MISSPELL_NOTIFICATION, misspelledWord);

        // Get suggestions
        ArrayList<String> suggestions = recommender.getWordSuggestions(misspelledWord, 2, 0.5, 4);

        if (suggestions.isEmpty()) {
            // No suggestions case
            System.out.printf(Util.NO_SUGGESTIONS);
            System.out.printf(Util.TWO_OPTION_PROMPT);

            while (true) {
                String choice = inputReader.nextLine().trim();

                if (choice.equals("a")) {
                    return misspelledWord;
                } else if (choice.equals("t")) {
                    System.out.printf(Util.MANUAL_REPLACEMENT_PROMPT);
                    return inputReader.nextLine().trim();
                } else {
                    System.out.printf(Util.INVALID_RESPONSE);
                }
            }

        } else {
            // Has suggestions
            System.out.printf(Util.FOLLOWING_SUGGESTIONS);
            for (int i = 0; i < suggestions.size(); i++) {
                System.out.printf(Util.SUGGESTION_ENTRY, (i + 1), suggestions.get(i));
            }
            System.out.printf(Util.THREE_OPTION_PROMPT);

            while (true) {
                String choice = inputReader.nextLine().trim();

                if (choice.equals("a")) {
                    return misspelledWord;
                } else if (choice.equals("t")) {
                    System.out.printf(Util.MANUAL_REPLACEMENT_PROMPT);
                    return inputReader.nextLine().trim();
                } else if (choice.equals("r")) {
                    System.out.printf(Util.AUTOMATIC_REPLACEMENT_PROMPT);

                    while (true) {
                        try {
                            String numStr = inputReader.nextLine().trim();
                            int num = Integer.parseInt(numStr);
                            if (num >= 1 && num <= suggestions.size()) {
                                return suggestions.get(num - 1);
                            }
                            System.out.printf(Util.INVALID_RESPONSE);
                        } catch (Exception e) {
                            System.out.printf(Util.INVALID_RESPONSE);
                        }
                    }
                } else {
                    System.out.printf(Util.INVALID_RESPONSE);
                }
            }
        }
    }
}}