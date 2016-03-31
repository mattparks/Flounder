package flounder.fonts;

import java.util.*;

/**
 * Represents a line of text during the loading of a text.
 */
public class Line {
	private final double m_maxLength;
	private final double m_spaceSize;

	private final List<Word> m_words;
	private double m_currentLineLength;

	/**
	 * Creates an empty line.
	 *
	 * @param spaceWidth The screen-space width of a space character.
	 * @param fontSize The size of font being used.
	 * @param maxLength The screen-space maximum length of a line.
	 */
	protected Line(final double spaceWidth, final double fontSize, final double maxLength) {
		m_maxLength = maxLength;
		m_spaceSize = spaceWidth * fontSize;
		m_words = new ArrayList<>();
		m_currentLineLength = 0;
	}

	/**
	 * Attempt to add a word to the line. If the line can fit the word in without reaching the maximum line length then the word is added and the line length increased.
	 *
	 * @param word The word to try to add.
	 *
	 * @return {@code true} if the word has successfully been added to the line.
	 */
	protected boolean attemptToAddWord(final Word word) {
		double additionalLength = word.getWordWidth();
		additionalLength += !m_words.isEmpty() ? m_spaceSize : 0;

		if (m_currentLineLength + additionalLength <= m_maxLength) {
			m_words.add(word);
			m_currentLineLength += additionalLength;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return The max length of the line.
	 */
	protected double getMaxLength() {
		return m_maxLength;
	}

	/**
	 * @return The list of words in the line.
	 */
	protected List<Word> getWords() {
		return m_words;
	}

	/**
	 * @return The current screen-space length of the line.
	 */
	protected double getLineLength() {
		return m_currentLineLength;
	}
}
