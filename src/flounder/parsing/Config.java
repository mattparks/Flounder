package flounder.parsing;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Map.*;

/**
 * Loads and parses a configuration file.
 */
public class Config {
	private final String fileName;
	private final Map<String, String> map;

	/**
	 * Loads and parses a configuration file.
	 *
	 * @param fileName The name and path to the configuration file.
	 *
	 * @throws FileNotFoundException If the file cannot be found.
	 * @throws IOException If the file cannot be loaded.
	 * @throws ParseException If the file cannot be properly parsed.
	 */
	public Config(final String fileName) throws IOException, ParseException {
		this.fileName = fileName;
		map = new HashMap<>();

		try (final BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			int lineNumber = 0;

			while ((line = br.readLine()) != null) {
				lineNumber++;

				if (line.isEmpty()) {
					continue;
				}

				final char start = line.charAt(0);

				if (start == '[' || start == '#') {
					continue;
				}

				final String[] tokens = line.split("=");

				if (tokens.length != 2) {
					throw new ParseException("Only one '=' expected (line " + lineNumber + ")", lineNumber);
				}

				map.put(tokens[0].trim(), tokens[1].trim());
			}
		}
	}

	/**
	 * Saves a new configuration file using this configs fileName.
	 *
	 * @param map The values being written to the config.
	 *
	 * @throws IOException If the file cannot be written.
	 */
	public void write(final Map<String, String> map) throws IOException {
		write(fileName, map);
	}

	/**
	 * Saves a new configuration file using a provided fileName.
	 *
	 * @param fileName The name and path of the output file.
	 * @param map The values being written to the config.
	 *
	 * @throws IOException If the file cannot be written.
	 */
	public void write(final String fileName, final Map<String, String> map) throws IOException {
		try (final BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
			final Iterator<Entry<String, String>> it = map.entrySet().iterator();

			while (it.hasNext()) {
				final Entry<String, String> pair = it.next();
				final String line = pair.getKey() + "=" + pair.getValue() + "\n";
				bw.write(line);
			}
		}
	}

	/**
	 * Get a string from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The string assigned to that entry.
	 */
	public String getString(final String entry) {
		final String result = map.get(entry);

		if (result != null && result.charAt(0) == '$') {
			return getString(result.substring(1));
		}

		// Logger.error("Config could not find string '" + entry + "' in file: " + fileName);

		return result;
	}

	/**
	 * Get an integer from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The integer assigned to that entry.
	 */
	public int getInt(final String entry) {
		return Integer.parseInt(getString(entry));
	}

	/**
	 * Get a double from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The double assigned to that entry.
	 */
	public double getDouble(final String entry) {
		return Double.parseDouble(getString(entry));
	}

	/**
	 * Get a boolean from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The boolean assigned to that entry.
	 */
	public boolean getBoolean(final String entry) {
		return Boolean.parseBoolean(getString(entry));
	}

	/**
	 * Get a string from the configuration file, with a default if that string cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The name of the default configuration entry.
	 *
	 * @return The string assigned to the entry if found, otherwise the string assigned to the default entry.
	 */
	public String getStringWithDefault(final String entry, final String defaultEntry) {
		String result = getString(entry);

		if (result == null) {
			result = getString(defaultEntry);
		}

		return result;
	}

	/**
	 * Get an integer from the configuration file, with a default if that integer cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The name of the default configuration entry.
	 *
	 * @return The integer assigned to the entry if found, otherwise the integer assigned to the default entry.
	 */
	public int getIntWithDefault(final String entry, final String defaultEntry) {
		return Integer.parseInt(getStringWithDefault(entry, defaultEntry));
	}

	/**
	 * Get a double from the configuration file, with a default if that double cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The name of the default configuration entry.
	 *
	 * @return The double assigned to the entry if found, otherwise the double assigned to the default entry.
	 */
	public double getDoubleWithDefault(final String entry, final String defaultEntry) {
		return Double.parseDouble(getStringWithDefault(entry, defaultEntry));
	}

	/**
	 * Get a boolean from the configuration file, with a default if that boolean cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The name of the default configuration entry.
	 *
	 * @return The boolean assigned to the entry if found, otherwise the boolean assigned to the default entry.
	 */
	public boolean getBooleanWithDefault(final String entry, final String defaultEntry) {
		return Boolean.parseBoolean(getStringWithDefault(entry, defaultEntry));
	}
}
