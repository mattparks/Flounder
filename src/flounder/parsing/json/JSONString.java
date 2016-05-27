package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;

/**
 * A string in a JSON file.
 */
public class JSONString extends JSONValue {
	private final String value;

	/**
	 * Creates a JSONString from a Java character.
	 *
	 * @param value The character to create from.
	 */
	public JSONString(final char value) {
		this(Character.toString(value));
	}

	/**
	 * Creates a JSONString from a Java String.
	 *
	 * @param value the String to create from.
	 */
	public JSONString(final String value) {
		if (value == null) {
			throw new NullPointerException("String cannot have a null value");
		}

		this.value = value;
	}

	/**
	 * Parses a value from a token source.
	 *
	 * @param tokens The tokens to parse.
	 * @param currentToken The current token of interest.
	 *
	 * @return A JSONValue parsed from the tokens.
	 *
	 * @throws IOException If a token cannot be read.
	 * @throws ParseException If the tokens cannot be parsed into a JSONValue.
	 */
	public static JSONValue parse(final TokenReader tokens, final String currentToken) throws IOException, ParseException {
		return new JSONString(currentToken.substring(1, currentToken.length() - 1));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final JSONString other = (JSONString) obj;

		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public void write(final Writer writer) throws IOException {
		writer.write('\"' + value + '\"');
	}

	@Override
	public boolean isString() {
		return true;
	}

	@Override
	public String asString() {
		return value;
	}
}
