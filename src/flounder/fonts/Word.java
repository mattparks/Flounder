package flounder.fonts;

import java.util.*;

/**
 * During the loading of a text this represents one word in the text.
 */
public class Word {
	private final List<Character> m_characters;
	private double m_width;
	private final double m_fontSize;

	/**
	 * Create a new empty word.
	 *
	 * @param fontSize The font size of the text which this word is in.
	 */
	protected Word(final double fontSize) {
		m_characters = new ArrayList<>();
		m_width = 0;
		m_fontSize = fontSize;
	}

	/**
	 * Adds a character to the end of the current word and increases the screen-space width of the word.
	 *
	 * @param character The character to be added.
	 */
	protected void addCharacter(final Character character) {
		m_characters.add(character);
		m_width += character.getXAdvance() * m_fontSize;
	}

	/**
	 * @return The list of characters in the word.
	 */
	protected List<Character> getCharacters() {
		return m_characters;
	}

	/**
	 * @return The width of the word in terms of screen size.
	 */
	protected double getWordWidth() {
		return m_width;
	}
}
