package flounder.parsing.config;

/**
 * Config data that has been loaded from data or a config.
 */
public class ConfigData implements Comparable<ConfigData> {
	protected String key;
	protected String data;
	protected ConfigReference reference;

	protected ConfigData(String key, String data, ConfigReference reference) {
		this.key = fixDataString(key);
		this.data = fixDataString(data);
		this.reference = reference;
	}

	private String fixDataString(String string) {
		return string.replace("#", "").replace("$", "").replace(",", "").replace(";", "").replace("{", "").replace("}", "");
	}

	/**
	 * Gets the parsed data (String).
	 *
	 * @return The parsed data.
	 */
	public String getString() {
		return data;
	}

	/**
	 * Gets the parsed data (Boolean).
	 *
	 * @return The parsed data.
	 */
	public boolean getBoolean() {
		return Boolean.parseBoolean(data);
	}

	/**
	 * Gets the parsed data (Integer).
	 *
	 * @return The parsed data.
	 */
	public int getInteger() {
		return Integer.parseInt(data);
	}

	/**
	 * Gets the parsed data (Double).
	 *
	 * @return The parsed data.
	 */
	public double getDouble() {
		return Double.parseDouble(data);
	}

	/**
	 * Gets the parsed data (Float).
	 *
	 * @return The parsed data.
	 */
	public float getFloat() {
		return Float.parseFloat(data);
	}

	/**
	 * Sets the reference to the data, used for saving.
	 *
	 * @param reference The new reference.
	 *
	 * @return this.
	 */
	public ConfigData setReference(ConfigReference reference) {
		this.reference = reference;
		return this;
	}

	@Override
	public int compareTo(ConfigData object) {
		return this.key.compareTo(object.key);
	}
}
