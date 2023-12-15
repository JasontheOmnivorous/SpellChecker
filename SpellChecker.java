import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeSet;

import javax.swing.JFileChooser;

/**
 * SpellChecker class checks a given text file for spelling errors against a dictionary.
 * The program suggests corrections for misspelled words.
 *
 * @author Min Thant Khaing
 */
public class SpellChecker {

    /**
     * Main method to execute the spell-checking process.
     *
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String[] args) {
        // Step 1: Read the dictionary file and store words in a HashSet
        HashSet<String> dictionary = readDictionary();

        // Step 2: Let the user select an input file
        File inputFile = getInputFileNameFromUser();
        if (inputFile == null) {
            System.out.println("No file selected. Exiting program.");
            System.exit(0);
        }

        // Step 3: Check words in the selected file against the dictionary
        checkWordsInFile(inputFile, dictionary);
    }

    /**
     * Reads the dictionary file and stores words in a HashSet.
     *
     * @return HashSet containing words from the dictionary.
     */
    private static HashSet<String> readDictionary() {
        HashSet<String> dictionary = new HashSet<>();

        try (Scanner filein = new Scanner(new File("words.txt"))) {
            while (filein.hasNext()) {
                String word = filein.next().toLowerCase();
                dictionary.add(word);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading dictionary file: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Actual dictionary size: " + dictionary.size());

        // Check if the size of the set is as expected
        if (dictionary.size() != 72875) {
            System.err.println("Error: Dictionary size is not as expected.");
            System.exit(1);
        }

        return dictionary;
    }

    /**
     * Lets the user select an input file using a file dialog.
     *
     * @return Selected input file or null if no file is selected.
     */
    private static File getInputFileNameFromUser() {
        JFileChooser fileDialog = new JFileChooser();
        fileDialog.setDialogTitle("Select File for Input");
        int option = fileDialog.showOpenDialog(null);
        if (option != JFileChooser.APPROVE_OPTION)
            return null;
        else
            return fileDialog.getSelectedFile();
    }

    /**
     * Checks words in the selected file against the dictionary.
     *
     * @param inputFile  The file containing the words to be checked.
     * @param dictionary HashSet containing words from the dictionary.
     */
    private static void checkWordsInFile(File inputFile, HashSet<String> dictionary) {
        try (Scanner in = new Scanner(inputFile)) {
            in.useDelimiter("[^a-zA-Z]+");

            while (in.hasNext()) {
                String word = in.next().toLowerCase();
                System.out.println("Checking: " + word);
                // Check if the word is in the dictionary
                if (!dictionary.contains(word)) {
                    System.out.println(word + ":");
                    // Step 4: Provide a list of possible correct spellings
                    TreeSet<String> suggestions = corrections(word, dictionary);
                    if (suggestions.isEmpty()) {
                        System.out.println("(no suggestions)");
                    } else {
                        suggestions.forEach(System.out::println);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error reading input file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Provides a list of possible correct spellings for a given misspelled word.
     *
     * @param badWord    The misspelled word.
     * @param dictionary HashSet containing words from the dictionary.
     * @return TreeSet containing suggested corrections.
     */
    private static TreeSet<String> corrections(String badWord, HashSet<String> dictionary) {
        TreeSet<String> suggestions = new TreeSet<>();

        // Delete any one of the letters from the misspelled word
        for (int i = 0; i < badWord.length(); i++) {
            String deleted = badWord.substring(0, i) + badWord.substring(i + 1);
            if (dictionary.contains(deleted)) {
                suggestions.add(deleted);
            }
        }

        // Change any letter in the misspelled word to any other letter
        for (int i = 0; i < badWord.length(); i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                String changed = badWord.substring(0, i) + ch + badWord.substring(i + 1);
                if (dictionary.contains(changed)) {
                    suggestions.add(changed);
                }
            }
        }

        // Insert any letter at any point in the misspelled word
        for (int i = 0; i <= badWord.length(); i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                String inserted = badWord.substring(0, i) + ch + badWord.substring(i);
                if (dictionary.contains(inserted)) {
                    suggestions.add(inserted);
                }
            }
        }

        // Swap any two neighboring characters in the misspelled word
        for (int i = 0; i < badWord.length() - 1; i++) {
            String swapped = badWord.substring(0, i) + badWord.charAt(i + 1) + badWord.charAt(i)
                    + badWord.substring(i + 2);
            if (dictionary.contains(swapped)) {
                suggestions.add(swapped);
            }
        }

        // Insert a space at any point in the misspelled word
        for (int i = 0; i <= badWord.length(); i++) {
            String withSpace = badWord.substring(0, i) + " " + badWord.substring(i);
            String[] words = withSpace.split("\\s+");
            if (words.length == 2 && dictionary.contains(words[0]) && dictionary.contains(words[1])) {
                suggestions.add(withSpace);
            }
        }

        return suggestions;
    }
}
