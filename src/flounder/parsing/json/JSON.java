package flounder.parsing.json;

import flounder.parsing.*;

import java.io.*;
import java.text.*;

/**
 * Reads or writes JSON files.
 */
public class JSON {
	private final JSONValue value;

	/**
	 * Loads a JSON file.
	 *
	 * @param fileName The name and path to the file of interest.
	 *
	 * @throws IOException If the file cannot be loaded.
	 * @throws ParseException If the file cannot be properly parsed.
	 */
	public JSON(final String fileName) throws IOException, ParseException {
		TokenReader tokens;
		tokens = new TokenReader(new FileReader(fileName));
		value = JSONValue.parse(tokens, tokens.next());

		String token;
		tokens.parseAssert((token = tokens.next()) == null, "Expected EOF; instead got " + token);

		tokens.close();
	}

	/**
	 * Wraps a JSON value for writing.
	 *
	 * @param value The value to write to file.
	 */
	public JSON(final JSONValue value) {
		this.value = value;
	}

	/**
	 * Writes a new JSON file.
	 *
	 * @param fileName The name and path to the file to write to.
	 *
	 * @throws IOException If the file cannot be written.
	 */
	public void write(final String fileName) throws IOException {
		final BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		value.write(br);
		br.close();
	}

	/**
	 * Gets the JSONValue for this object.
	 *
	 * @return The JSONValue for this object.
	 */
	public JSONValue get() {
		return value;
	}
}
