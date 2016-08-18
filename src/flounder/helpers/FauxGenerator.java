package flounder.helpers;

import flounder.maths.*;

public class FauxGenerator {
	public static final char[] CONSONANTS = new char[]{'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};
	public static final char[] VOWELS = new char[]{'a', 'e', 'i', 'o', 'u'};

	public static String getFauxSentance(int maxWordCount, int minWordLength, int maxWordLength) {
		String fauxLine = "";

		for (int w = 0; w < Maths.RANDOM.nextInt((maxWordCount - 1) + 1) + 1; w++) {
			String word = "";
			boolean consonant = true;

			while (word.length() < Maths.RANDOM.nextInt((maxWordLength - minWordLength) + 1) + minWordLength) {
				if (consonant) {
					word += CONSONANTS[Maths.RANDOM.nextInt(CONSONANTS.length)];
					consonant = false;
				} else {
					word += VOWELS[Maths.RANDOM.nextInt(VOWELS.length)];
					consonant = true;
				}
			}

			fauxLine += capitalize(word) + " ";
		}

		return fauxLine.trim();
	}

	public static String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
}
