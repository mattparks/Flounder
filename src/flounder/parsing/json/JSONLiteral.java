package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;

/**
 * A literal value in a JSON file.
 */
public class JSONLiteral extends JSONValue {
	/**
	 * The null JSONValue
	 */
	public static final JSONLiteral NULL = new JSONLiteral("null");

	/**
	 * The true JSONValue
	 */
	public static final JSONLiteral TRUE = new JSONLiteral("true");

	/**
	 * The false JSONValue.
	 */
	public static final JSONLiteral FALSE = new JSONLiteral("false");

	private final String string;

	private JSONLiteral(final String string) {
		this.string = string;
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
		switch (currentToken.charAt(0)) {
			case 'n':
				return parseNull(tokens, currentToken);
			case 't':
				return parseTrue(tokens, currentToken);
			case 'f':
				return parseFalse(tokens, currentToken);
		}

		tokens.parseAssert(false, currentToken + " is not a JSONLiteral");
		return null;
	}

	private static JSONValue parseFalse(final TokenReader tokens, final String token) throws ParseException {
		tokens.parseAssert(token.equals("false"), "'false' expected");
		return FALSE;
	}

	private static JSONValue parseTrue(final TokenReader tokens, final String token) throws ParseException {
		tokens.parseAssert(token.equals("true"), "'true' expected");
		return TRUE;
	}

	private static JSONValue parseNull(final TokenReader tokens, final String token) throws ParseException {
		tokens.parseAssert(token.equals("null"), "'null' expected");
		return NULL;
	}

	/**
	 * Creates a JSONLiteral from a boolean.
	 *
	 * @param value the boolean to turn into a JSONLiteral.
	 *
	 * @return A JSONLiteral representing the given value.
	 */
	public static JSONLiteral create(final boolean value) {
		return value ? TRUE : FALSE;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public void write(final Writer writer) throws IOException {
		writer.write(string);
	}

	@Override
	public boolean isBoolean() {
		return this == TRUE || this == FALSE;
	}

	@Override
	public boolean isNull() {
		return this == NULL;
	}

	@Override
	public boolean asBoolean() {
		if (this == TRUE) {
			return true;
		} else if (this == FALSE) {
			return false;
		} else {
			return super.asBoolean();
		}
	}
}
