package flounder.fonts;

import flounder.logger.*;

import java.util.*;

/**
 * During the loading of a text this represents one word in the text.
 */
public class Word {
	protected List<Character> characters;
	protected double width;

	/**
	 * Creates a new empty word.
	 */
	protected Word() {
		this.characters = new ArrayList<>();
		this.width = 0.0;
	}

	/**
	 * Adds a character to the end of the current word and increases the screen-space width of the word.
	 *
	 * @param character The character to be added.
	 */
	protected void addCharacter(Character character) {
		if (character == null) {
			FlounderLogger.error("Invalid character detected!");
			return;
		}

		characters.add(character);
		width += character.xAdvance;
	}
}
