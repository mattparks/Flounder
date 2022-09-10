package com.flounder.helpers;

import com.flounder.maths.*;

/**
 * A helper for creating randomly generated words, using the Faux method.
 */
public class FauxGenerator {
	public static final char[] CONSONANTS = new char[]{'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};
	public static final char[] VOWELS = new char[]{'a', 'e', 'i', 'o', 'u'};

	/**
	 * Creates a new sentence generated using the Faux method.
	 *
	 * @param maxWordCount The most words to have in the sentence.
	 * @param minWordLength The shortest generated word.
	 * @param maxWordLength The longest generated word.
	 *
	 * @return The generated sentence.
	 */
	public static String getFauxSentence(int maxWordCount, int minWordLength, int maxWordLength) {
		StringBuilder fauxLine = new StringBuilder();

		for (int w = 0; w < Maths.RANDOM.nextInt((maxWordCount - 1) + 1) + 1; w++) {
			StringBuilder word = new StringBuilder();
			boolean consonant = true;

			while (word.length() < Maths.RANDOM.nextInt((maxWordLength - minWordLength) + 1) + minWordLength) {
				if (consonant) {
					word.append(CONSONANTS[Maths.RANDOM.nextInt(CONSONANTS.length)]);
					consonant = false;
				} else {
					word.append(VOWELS[Maths.RANDOM.nextInt(VOWELS.length)]);
					consonant = true;
				}
			}

			fauxLine.append(capitalize(word.toString()));
			fauxLine.append(" ");
		}

		return fauxLine.toString().trim();
	}

	/**
	 * Capitalizes the first letter in the line.
	 *
	 * @param line The line to capitalize.
	 *
	 * @return The capitalized line.
	 */
	public static String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
}
