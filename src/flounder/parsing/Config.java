package flounder.parsing;

import flounder.engine.*;
import flounder.helpers.*;
import flounder.resources.*;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.Map.*;

/**
 * Loads and parses a configuration file.
 */
public class Config {
	private MyFile file;
	private Map<String, Pair<String, ConfigReference>> map;

	/**
	 * Loads and parses a configuration file.
	 *
	 * @param file The path to the configuration file.
	 */
	public Config(MyFile file) {
		this.file = file;
		this.map = new HashMap<>();

		File saveDirectory = new File(file.getPath().replaceAll(file.getName(), "").substring(1));
		File sameFile = new File(file.getPath().substring(1));

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				System.out.println("Filed to create " + file.getPath() + " folder.");
				e.printStackTrace();
			}
		}

		if (!sameFile.exists()) {
			System.out.println("Creating file: " + sameFile);

			try {
				sameFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Filed to create " + file.getPath() + " file.");
				e.printStackTrace();
			}
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file.getPath().substring(1)))) {
			String line;
			int lineNumber = 0;

			while ((line = br.readLine()) != null) {
				lineNumber++;

				if (line.isEmpty()) {
					continue;
				}

				char start = line.charAt(0);

				if (start == '[' || start == '#') {
					continue;
				}

				String[] tokens = line.split("=");

				if (tokens.length != 2) {
					throw new ParseException("Only one '=' expected (line " + lineNumber + ")", lineNumber);
				}

				map.put(tokens[0].trim(), new Pair<>(tokens[1].trim(), null));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets a value for a entry in the config.
	 *
	 * @param entry The entry to change.
	 * @param value The value to set to.
	 */
	public void setValue(String entry, String value) {
		map.get(entry).setFirst(value);
	}

	/**
	 * Get a string from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The string assigned to that entry.
	 */
	public String getString(String entry) {
		if (map.get(entry) == null) {
			System.out.println("Config could not find string '" + entry + "' in file: " + file);
			return null;
		}

		String result = map.get(entry).getFirst();

		if (result != null && result.charAt(0) == '$') {
			return getString(result.substring(1));
		}

		return result;
	}

	/**
	 * Get an integer from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The integer assigned to that entry.
	 */
	public int getInt(String entry) {
		return Integer.parseInt(getString(entry));
	}

	/**
	 * Get a double from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The double assigned to that entry.
	 */
	public double getDouble(String entry) {
		return Double.parseDouble(getString(entry));
	}

	/**
	 * Get a boolean from the configuration file.
	 *
	 * @param entry The name of the configuration entry.
	 *
	 * @return The boolean assigned to that entry.
	 */
	public boolean getBoolean(String entry) {
		return Boolean.parseBoolean(getString(entry));
	}

	/**
	 * Get a string from the configuration file, with a default if that string cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The the default configuration entry.
	 *
	 * @return The string assigned to the entry if found, otherwise the string assigned to the default entry.
	 */
	public String getStringWithDefault(String entry, String defaultEntry, ConfigReference reference) {
		String result = getString(entry);

		if (result == null) {
			map.put(entry, new Pair<>(defaultEntry, reference));
			result = defaultEntry;
		} else if (map.get(entry).getSecond() == null) {
			map.remove(entry);
			map.put(entry, new Pair<>(defaultEntry, reference));
		}

		return result;
	}

	/**
	 * Get an integer from the configuration file, with a default if that integer cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The the default configuration entry.
	 *
	 * @return The integer assigned to the entry if found, otherwise the integer assigned to the default entry.
	 */
	public int getIntWithDefault(String entry, int defaultEntry, ConfigReference reference) {
		return Integer.parseInt(getStringWithDefault(entry, "" + defaultEntry, reference));
	}

	/**
	 * Get a double from the configuration file, with a default if that double cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The the default configuration entry.
	 *
	 * @return The double assigned to the entry if found, otherwise the double assigned to the default entry.
	 */
	public double getDoubleWithDefault(String entry, double defaultEntry, ConfigReference reference) {
		return Double.parseDouble(getStringWithDefault(entry, "" + defaultEntry, reference));
	}

	/**
	 * Get a boolean from the configuration file, with a default if that boolean cannot be found.
	 *
	 * @param entry The name of the configuration entry.
	 * @param defaultEntry The the default configuration entry.
	 *
	 * @return The boolean assigned to the entry if found, otherwise the boolean assigned to the default entry.
	 */
	public boolean getBooleanWithDefault(String entry, boolean defaultEntry, ConfigReference reference) {
		return Boolean.parseBoolean(getStringWithDefault(entry, "" + defaultEntry, reference));
	}

	/**
	 * Saves any changes to the configs and closes the config.
	 */
	public void dispose() {
		for (String key : map.keySet()) {
			Pair<String, ConfigReference> pair = map.get(key);

			if (pair.getSecond() != null) {
				setValue(key, pair.getSecond().getReading().toString());
			}
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getPath().substring(1)))) {
			Iterator<Entry<String, Pair<String, ConfigReference>>> it = map.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, Pair<String, ConfigReference>> pair = it.next();
				String line = pair.getKey() + "=" + pair.getValue().getFirst() + "\n";
				bw.write(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A reference to the value that was loaded from the config.
	 */
	public interface ConfigReference<T> {
		/**
		 * Gets the reading from that value.
		 *
		 * @return The value read.
		 */
		T getReading();
	}
}
