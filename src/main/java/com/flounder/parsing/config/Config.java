package com.flounder.parsing.config;

import com.flounder.helpers.*;
import com.flounder.logger.*;
import com.flounder.resources.*;

import java.io.*;
import java.util.*;

/**
 * A class used for loading and parsing a configuration file.
 */
public class Config {
	private Map<ConfigSection, List<ConfigData>> dataMap;
	private MyFile file;

	/**
	 * Loads and parses a configuration file.
	 *
	 * @param file The path to the configuration file.
	 */
	public Config(MyFile file) {
		this.dataMap = new HashMap<>();

		for (ConfigSection section : ConfigSection.values()) {
			this.dataMap.put(section, new ArrayList<>());
		}

		this.file = file;

		load();
	}

	private void load() {
		File saveFile = insureFile();

		try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
			String line;

			ConfigSection currentSection = null;

			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.startsWith("#")) {
					currentSection = null;
					String section = line.substring(1, line.length()).split(":")[0].trim();

					for (ConfigSection s : ConfigSection.values()) {
						if (s.name().equals(section)) {
							currentSection = s;
						}
					}
				} else if (line.startsWith("$")) {
					String key = line.substring(1, line.length()).split(":")[0].trim();
					String data = line.split(":")[1].trim();
					this.dataMap.get(currentSection).add(new ConfigData(key, data, null));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets data from the loaded config, defaults are used to set if not found. These data sets are also used when saving the config.
	 *
	 * @param section The section the key is found under.
	 * @param key The name of the key to find the data under.
	 * @param defaultData The default data that is used if this config is not found.
	 * @param <T> The type of default data.
	 *
	 * @return The data loaded from the config.
	 */
	public <T> ConfigData getData(ConfigSection section, String key, T defaultData) {
		return getData(section, key, defaultData, null);
	}

	/**
	 * Gets data from the loaded config, defaults are used to set if not found. These data sets are also used when saving the config.
	 *
	 * @param section The section the key is found under.
	 * @param key The name of the key to find the data under.
	 * @param defaultData The default data that is used if this config is not found.
	 * @param reference The reference to a code variable used when re-saving the data.
	 * @param <T> The type of default data.
	 *
	 * @return The data loaded from the config.
	 */
	public <T> ConfigData getData(ConfigSection section, String key, T defaultData, ConfigReference reference) {
		for (ConfigData data : dataMap.get(section)) {
			if (data.key.equals(key)) {
				if (data.reference == null) {
					data.reference = reference;
				}

				// The data loaded.
				return data;
			}
		}

		ConfigData configData = new ConfigData(key, defaultData.toString(), reference);
		dataMap.get(section).add(configData);
		return configData;
	}

	/**
	 * Saves the config, data references are used to find new up to data data.
	 */
	public void save() {
		try {
			File saveFile = insureFile();

			FileWriter fileWriter = new FileWriter(saveFile);
			FileWriterHelper fileWriterHelper = new FileWriterHelper(fileWriter);

			for (ConfigSection section : dataMap.keySet()) {
				if (!dataMap.get(section).isEmpty()) {
					fileWriterHelper.beginNewSegment("#" + section.name() + ":", false);

					for (ConfigData data : ArraySorting.insertionSort(dataMap.get(section))) {
						String save = (data.reference == null || data.reference.getReading() == null) ? data.data : data.reference.getReading().toString();
						data.data = save;
						fileWriterHelper.writeSegmentData("$" + data.key + ": " + save, true);
					}

					fileWriterHelper.endSegment(true, false);
				}
			}

			// Closes the file for writing.
			fileWriter.close();
		} catch (IOException e) {
			FlounderLogger.get().error("File saver for config " + file.getName() + " did not save successfully!");
			FlounderLogger.get().exception(e);
		}
	}

	private File insureFile() {
		File saveDirectory = new File(file.getPath().replaceAll(file.getName(), "").substring(1));
		File saveFile = new File(file.getPath().substring(1));

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				System.out.println("Filed to create " + file.getPath() + " folder.");
				e.printStackTrace();
			}
		}

		if (!saveFile.exists()) {
			System.out.println("Creating file: " + saveFile);

			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Filed to create " + file.getPath() + " file.");
				e.printStackTrace();
			}
		}

		return saveFile;
	}

	/**
	 * Gets the file the config is loaded from.
	 *
	 * @return The config file.
	 */
	public MyFile getFile() {
		return file;
	}
}
