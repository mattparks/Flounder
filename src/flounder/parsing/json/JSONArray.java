package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * An array in a JSON file.
 */
public class JSONArray extends JSONValue {
	private List<JSONValue> values;

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(JSONValue[] arr) {
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
	public JSONArray(Integer[] arr) {
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
	public void add(int val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Short[] arr) {
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
	public void add(short val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Long[] arr) {
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
	public void add(long val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Byte[] arr) {
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
	public void add(byte val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Character[] arr) {
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
	public void add(char val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Float[] arr) {
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
	public void add(float val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Double[] arr) {
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
	public void add(double val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(Boolean[] arr) {
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
	public void add(boolean val) {
		add(create(val));
	}

	/**
	 * Creates a new JSONArray from an existing array.
	 *
	 * @param arr The array to create from.
	 */
	public JSONArray(int[] arr) {
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
	public JSONArray(long[] arr) {
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
	public JSONArray(short[] arr) {
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
	public JSONArray(byte[] arr) {
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
	public JSONArray(float[] arr) {
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
	public JSONArray(double[] arr) {
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
	public JSONArray(char[] arr) {
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
	public JSONArray(boolean[] arr) {
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
	public JSONArray(String[] arr) {
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
	public void add(String val) {
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
	public static JSONValue parse(TokenReader tokens, String token) throws IOException, ParseException {
		JSONArray result = new JSONArray();

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
		int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		JSONArray other = (JSONArray) obj;

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
	public void write(Writer writer) throws IOException {
		writer.write('[');
		Iterator<JSONValue> it = values.iterator();

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
