package flounder.fonts;

import java.util.*;

/**
 * During the loading of a text this represents one word in the text.
 */
public class Word {
	private List<Character> characters;
	private double width;
	private double fontSize;

	/**
	 * Creates a new empty word.
	 *
	 * @param fontSize The font size of the text which this word is in.
	 */
	protected Word(double fontSize) {
		this.characters = new ArrayList<>();
		this.width = 0.0;
		this.fontSize = fontSize;
	}

	/**
	 * Adds a character to the end of the current word and increases the screen-space width of the word.
	 *
	 * @param character The character to be added.
	 */
	protected void addCharacter(Character character) {
		characters.add(character);
		width += character.xAdvance * fontSize;
	}

	/**
	 * Gets the list of characters in the word.
	 *
	 * @return The list of characters in the word.
	 */
	protected List<Character> getCharacters() {
		return characters;
	}

	/**
	 * Gets the width of the word in terms of screen size.
	 *
	 * @return The width of the word.
	 */
	protected double getWordWidth() {
		return width;
	}
}
