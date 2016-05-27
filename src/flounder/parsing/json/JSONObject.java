package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * An Object in a JSON file.
 */
public class JSONObject extends JSONValue {
	private final Map<String, JSONValue> map;

	/**
	 * Creates a new JSONObject.
	 */
	public JSONObject() {
		map = new HashMap<>();
	}

	/**
	 * Parses a value from a token source.
	 *
	 * @param tokens The tokens to parse.
	 * @param token The current token of interest.
	 *
	 * @return A JSONValue parsed from the tokens.
	 *
	 * @throws IOException If a token cannot be read.
	 * @throws ParseException If the tokens cannot be parsed into a JSONValue.
	 */
	public static JSONValue parse(final TokenReader tokens, String token) throws IOException, ParseException {
		final JSONObject result = new JSONObject();

		if ((token = tokens.next()).equals("}")) {
			return result;
		}

		do {
			final String key = JSONString.parse(tokens, token).toString();
			tokens.parseAssert((token = tokens.next()).equals(":"), "Separating ':' expected!");
			result.put(key, JSONValue.parse(tokens, tokens.next()));

			if (!(token = tokens.next()).equals(",")) {
				break;
			}

			token = tokens.next();
		} while (true);

		tokens.parseAssert(token.equals("}"), "Closing '}' expected!");
		return result;
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final JSONValue value) {
		if (key == null) {
			throw new NullPointerException("Key cannot be null");
		}

		if (value == null) {
			throw new NullPointerException("Value cannot be null");
		}

		map.put(key, value);
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final int value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final byte value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final short value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final long value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final float value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final double value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final boolean value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(final String key, final char value) {
		put(key, create(value));
	}

	/**
	 * Adds a new value to the JSONObject.
	 *
	 * @param key The name for the value.
	 * @param value The actual value.
	 */
	public void put(String key, String value) {
		put(key, create(value));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
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

		final JSONObject other = (JSONObject) obj;

		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public void write(final Writer writer) throws IOException {
		write(writer, 1);
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public Map<String, JSONValue> asObject() {
		return Collections.unmodifiableMap(map);
	}

	private void writeNewLine(final Writer writer, final int tabLevel) throws IOException {
		writer.write('\n');

		for (int i = 0; i < tabLevel; i++) {
			writer.write('\t');
		}
	}

	private void write(final Writer writer, final int tabLevel) throws IOException {
		writer.write('{');
		writeNewLine(writer, tabLevel);

		Iterator<Map.Entry<String, JSONValue>> it = map.entrySet().iterator();

		while (it.hasNext()) {
			final Map.Entry<String, JSONValue> current = it.next();
			new JSONString(current.getKey()).write(writer);
			writer.write(':');
			writer.write(' ');

			final JSONValue value = current.getValue();

			if (value.isObject()) {
				((JSONObject) value).write(writer, tabLevel + 1);
			} else {
				value.write(writer);
			}

			if (it.hasNext()) {
				writer.write(", ");
				writeNewLine(writer, tabLevel);
			}
		}

		writeNewLine(writer, tabLevel - 1);
		writer.write('}');
	}
}
