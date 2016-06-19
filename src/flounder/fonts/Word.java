package flounder.fonts;

import flounder.engine.*;

import java.util.*;

/**
 * During the loading of a text this represents one word in the text.
 */
public class Word {
	private List<Character> characters;
	private double fontSize;
	private double width;

	/**
	 * Create a new empty word.
	 *
	 * @param fontSize The font size of the text which this word is in.
	 */
	protected Word(double fontSize) {
		this.characters = new ArrayList<>();
		this.fontSize = fontSize;
		this.width = 0.0;
	}

	/**
	 * Adds a character to the end of the current word and increases the screen-space width of the word.
	 *
	 * @param character The character to be added.
	 */
	protected void addCharacter(Character character) {
		if (character == null) {
			FlounderEngine.getLogger().error("Invalid character detected!");
			return;
		}

		characters.add(character);
		width += character.getXAdvance() * fontSize;
	}

	/**
	 * @return The list of characters in the word.
	 */
	protected List<Character> getCharacters() {
		return characters;
	}

	/**
	 * @return The width of the word in terms of screen size.
	 */
	protected double getWordWidth() {
		return width;
	}
}
