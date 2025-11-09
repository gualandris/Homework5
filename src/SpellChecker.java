import java.io.File;
import java.util.Scanner;

public class SpellChecker {
    // Use this field everytime you need to read user input
    private Scanner inputReader; // DO NOT MODIFY

    public SpellChecker() {
      inputReader = new Scanner(System.in); // DO NOT MODIFY - must be included in this method
      // TODO: Complete the body of this constructor, as necessary.
    }
  
    public void start() {
        // TODO: Complete the body of this method, as necessary.
        // Ask user to input the dictionary file, check if valid
        File dictionaryFile = null;
        Scanner dictionaryScanner = null;

        while (true) {
          System.out.print(Util.DICTIONARY_PROMPT); // Prompt the user to enter the dictionary file
          String fileName = inputReader.nextLine().trim(); // Save user input

          try {
              dictionaryFile = new File(fileName);
              dictionaryScanner = new Scanner(dictionaryFile); // Try to open the dictionary file
              System.out.printf(Util.DICTIONARY_SUCCESS_NOTIFICATION, fileName); // Print success message
              break; // If successful, end loop
          } catch (Exception e) {
              System.out.printf(Util.FILE_OPENING_ERROR); // If invalid, print error message and prompt new input
          }
        }
        dictionaryScanner.close();

        // Ask user to input the text file, check if valid
        File inputFile = null;
        Scanner inputFileScanner = null;

        while (true) {
            System.out.printf(Util.FILENAME_PROMPT); // Prompt the user to enter the text file
            String fileName = inputReader.nextLine().trim(); // Save user input

            try {
                inputFile = new File(fileName);
                inputFileScanner = new Scanner(inputFile); // Try to open the input file

                // Create output file INPUT-FILE-NAME_chk.txt
                String outputFileName;
                if (fileName.contains(".")) {
                    int periodIndex = fileName.lastIndexOf(".");
                    outputFileName = fileName.substring(0, periodIndex) + "_chk.txt"; // Add "_chk.txt" to name of file
                } else {
                    outputFileName = fileName + "_chk.txt";
                }
                System.out.printf(Util.FILE_SUCCESS_NOTIFICATION, fileName, outputFileName); // Print success message and output file name
                break; // If successful, end loop
            } catch (Exception e) {
                System.out.printf(Util.FILE_OPENING_ERROR); // If invalid, print error message and prompt new input
            }
        }
        inputFileScanner.close();
        inputReader.close();  // DO NOT MODIFY - must be the last line of this method!
    }
  
    // You can of course write other methods as well.
  }