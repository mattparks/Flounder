package flounder.fonts;

import java.util.*;

/**
 * Represents a line of text during the loading of a text.
 */
public class Line {
	protected double maxLength;
	protected double spaceSize;

	protected List<Word> words;
	protected double currentLineLength;

	/**
	 * Creates an empty line.
	 *
	 * @param spaceWidth The screen-space width of a space character.
	 * @param fontSize The size of font being used.
	 * @param maxLength The screen-space maximum length of a line.
	 */
	protected Line(double spaceWidth, double fontSize, double maxLength) {
		this.maxLength = maxLength;
		this.spaceSize = spaceWidth * fontSize;

		this.words = new ArrayList<>();
		this.currentLineLength = 0.0;
	}

	/**
	 * Attempt to add a word to the line. If the line can fit the word in without reaching the maximum line length then the word is added and the line length increased.
	 *
	 * @param word The word to try to add.
	 *
	 * @return {@code true} if the word has successfully been added to the line.
	 */
	protected boolean attemptToAddWord(Word word) {
		double additionalLength = word.width;
		additionalLength += !words.isEmpty() ? spaceSize : 0.0;

		if (currentLineLength + additionalLength <= maxLength) {
			words.add(word);
			currentLineLength += additionalLength;
			return true;
		} else {
			return false;
		}
	}
}
