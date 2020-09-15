package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            var file = new File(args[0]);
            var scanner = new Scanner(file);

            final var builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            final var text = builder.toString();

            var wordsCount = 0;
            var syllablesCount = 0;
            var polysyllablesCount = 0;
            var sentences = text.split("[.!?]");

            final var vowels = Pattern.compile("[\\w]?[AEIOUYaeiouy][\\w]?");
            var charactersCount = text.replaceAll("\\s", "").length();
            for (var sentence : sentences) {
                var words = sentence.trim().split("[\\s]+");
                wordsCount += words.length;

                for (var word : words) {
                    var syllables = vowels.matcher(word).results().count();

                    if (word.charAt(word.length() - 1) == 'e') {
                        syllables--;
                    }

                    if (syllables > 2) {
                        polysyllablesCount++;
                    }

                    syllablesCount += syllables == 0 ? 1 : syllables;
                }
            }

            System.out.println("Words: " + wordsCount);
            System.out.println("Sentences: " + sentences.length);
            System.out.println("Characters: " + charactersCount);
            System.out.println("Syllables: " + syllablesCount);
            System.out.println("Polysyllables: " + polysyllablesCount);

            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            System.out.println();

            scanner = new Scanner(System.in);
            var option = scanner.nextLine();

            switch (option) {
                case "ARI":
                    var score = calculateARIScore(charactersCount, wordsCount, sentences.length);
                    printScore(score, "Automated Readability Index");
                    break;
                case "FK":
                    score = calculateFKScore(wordsCount, sentences.length, syllablesCount);
                    printScore(score, "Flesch–Kincaid readability tests");
                    break;
                case "SMOG":
                    score = calculateSMOGScore(polysyllablesCount, sentences.length);
                    printScore(score, "Simple Measure of Gobbledygook");
                    break;
                case "CL":
                    score = calculateCLScore(charactersCount, wordsCount, sentences.length);
                    printScore(score, "Coleman–Liau index");
                    break;
                case "all":
                    score = calculateARIScore(charactersCount, wordsCount, sentences.length);
                    printScore(score, "Automated Readability Index");

                    score = calculateFKScore(wordsCount, sentences.length, syllablesCount);
                    printScore(score, "Flesch–Kincaid readability tests");

                    score = calculateSMOGScore(polysyllablesCount, sentences.length);
                    printScore(score, "Simple Measure of Gobbledygook");

                    score = calculateCLScore(charactersCount, wordsCount, sentences.length);
                    printScore(score, "Coleman–Liau index");
                    break;
                default:
                    break;
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR");
        }
    }

    private static double calculateCLScore(int charactersCount, int wordsCount, int sentencesCount) {
        return 0.0588 * (double) charactersCount / wordsCount * 100 - 0.296 * (double) sentencesCount / wordsCount * 100 - 15.8;
    }

    private static double calculateFKScore(int wordsCount, int sentencesCount, int syllablesCount) {
        return 0.39 * (double) wordsCount / sentencesCount + 11.8 * syllablesCount / wordsCount - 15.59;
    }

    private static double calculateSMOGScore(int polysyllablesCount, int sentencesCount) {
        return 1.043 * Math.sqrt(polysyllablesCount * 30D / sentencesCount) + 3.1291;
    }

    private static double calculateARIScore(int charactersCount, int wordsCount, int sentencesCount) {
        return 4.71 * (double) charactersCount / wordsCount + 0.5 * (double) wordsCount / sentencesCount - 21.43;
    }

    private static void printScore(double score, String algorithmName) {
        var scoreRounded = (int) Math.ceil(score);
        var year = 5;
        if (scoreRounded < 3) {
            year += scoreRounded;
        } else if (scoreRounded == 3) {
            year += scoreRounded + 1;
        } else if (scoreRounded < 13) {
            year += scoreRounded + 1;
        } else if (scoreRounded == 13) {
            year += scoreRounded + 6;
        } else {
            year = 24;
        }

        System.out.printf("%s: %.2f (about %d%s year olds).\n",
                algorithmName, score, year, year >= 24 ? "+" : "");
    }
}