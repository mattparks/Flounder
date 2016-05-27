package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * An array in a JSON file.
 */
public class JSONArray extends JSONValue {
	private final List<JSONValue> values;

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final JSONValue[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray.
	 */
	public JSONArray() {
		values = new ArrayList<>();
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Integer[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final int val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Short[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final short val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Long[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final long val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Byte[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final byte val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Character[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final char val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Float[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final float val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Double[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final double val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final Boolean[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final boolean val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final int[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final long[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final short[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final byte[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final float[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final double[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final char[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final boolean[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(final String[] arr) {
		this();

		for (int i = 0; i < arr.length; i++) {
			add(arr[i]);
		}
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param val The value to add.
	 */
	public void add(final String val) {
		add(create(val));
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
		final JSONArray result = new JSONArray();

		if ((token = tokens.next()).equals("]")) {
			return result;
		}

		do {
			result.add(JSONValue.parse(tokens, token));

			if (!(token = tokens.next()).equals(",")) {
				break;
			}

			token = tokens.next();
		} while (true);

		tokens.parseAssert(token.equals("]"), "Closing ']' expected!");
		return result;
	}

	/**
	 * Adds a new value to the array.
	 *
	 * @param value The value to add.
	 */
	public void add(JSONValue value) {
		if (value == null) {
			value = JSONLiteral.NULL;
		}

		values.add(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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

		final JSONArray other = (JSONArray) obj;

		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return values.toString();
	}

	@Override
	public void write(final Writer writer) throws IOException {
		writer.write('[');
		final Iterator<JSONValue> it = values.iterator();

		while (it.hasNext()) {
			it.next().write(writer);

			if (it.hasNext()) {
				writer.write(", ");
			}
		}

		writer.write(']');
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public List<JSONValue> asArray() {
		return Collections.unmodifiableList(values);
	}
}
