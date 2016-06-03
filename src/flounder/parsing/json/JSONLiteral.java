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

	private String string;

	private JSONLiteral(String string) {
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
	public static JSONValue parse(TokenReader tokens, String currentToken) throws IOException, ParseException {
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

	private static JSONValue parseFalse(TokenReader tokens, String token) throws ParseException {
		tokens.parseAssert(token.equals("false"), "'false' expected");
		return FALSE;
	}

	private static JSONValue parseTrue(TokenReader tokens, String token) throws ParseException {
		tokens.parseAssert(token.equals("true"), "'true' expected");
		return TRUE;
	}

	private static JSONValue parseNull(TokenReader tokens, String token) throws ParseException {
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
	public static JSONLiteral create(boolean value) {
		return value ? TRUE : FALSE;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public void write(Writer writer) throws IOException {
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
