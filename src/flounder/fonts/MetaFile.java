package flounder.fonts;

import flounder.engine.*;
import flounder.resources.*;

import java.io.*;
import java.util.*;

/**
 * Provides functionality for getting the values from a font file.
 */
public class MetaFile {
	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 10;

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private Map<Integer, Character> metaData;
	private Map<String, String> values;
	private double perPixelSize;
	private double spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;
	private BufferedReader reader;

	/**
	 * Opens a font file in preparation for reading.
	 *
	 * @param file The font file.
	 */
	protected MetaFile(MyFile file) {
		metaData = new HashMap<>();
		values = new HashMap<>();

		openFile(file);
		loadPaddingData();
		loadLineSizes();
		loadCharacterData(getValueOfVariable("scaleW"));
		close();
	}

	/**
	 * Opens the font file for reading.
	 *
	 * @param file The font file to open.
	 */
	private void openFile(MyFile file) {
		try {
			reader = file.getReader();
		} catch (Exception e) {
			FlounderEngine.getLogger().error("Couldn't read font meta file " + file.getPath());
			FlounderEngine.getLogger().exception(e);
		}
	}

	/**
	 * Loads all padding data.
	 */
	private void loadPaddingData() {
		processNextLine();
		padding = getValuesOfVariable("padding");
		paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	/**
	 * Read in the next line and store the variable values.
	 *
	 * @return {@code true} if the end of the file hasn't been reached.
	 */
	private boolean processNextLine() {
		values.clear();
		String line = null;

		try {
			line = reader.readLine();
		} catch (IOException e) {
			FlounderEngine.getLogger().error("Failed to read the next line!");
			FlounderEngine.getLogger().exception(e);
		}

		if (line == null) {
			return false;
		}

		for (String part : line.split(SPLITTER)) {
			String[] valuePairs = part.split("=");

			if (valuePairs.length == 2) {
				values.put(valuePairs[0], valuePairs[1]);
			}
		}

		return true;
	}

	/**
	 * Gets the array of ints associated with a variable on the current line.
	 *
	 * @param variable The name of the variable.
	 *
	 * @return The int array of values associated with the variable.
	 */
	private int[] getValuesOfVariable(String variable) {
		String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];

		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}

		return actualValues;
	}

	/**
	 * Loads all line sizes.
	 */
	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		perPixelSize = TextLoader.LINE_HEIGHT / lineHeightPixels;
	}

	/**
	 * Gets the {@code int} value of the variable with a certain name on the current line.
	 *
	 * @param variable The name of the variable.
	 *
	 * @return The value of the variable.
	 */
	private int getValueOfVariable(String variable) {
		String value = values.get(variable);

		if (value == null) {
			//	FlounderEngine.getLogger().error("Could not find font variable for: " + variable, true);
			return 0;
		}

		return Integer.parseInt(value);
	}

	/**
	 * Loads the character data.
	 *
	 * @param imageWidth The images width.
	 */
	private void loadCharacterData(int imageWidth) {
		processNextLine();
		processNextLine();

		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);

			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
	}

	private Character loadCharacter(int imageSize) {
		int id = getValueOfVariable("id");
		int displayWidth = FlounderEngine.getDevices().getDisplay().getWidth();
		int displayHeight = FlounderEngine.getDevices().getDisplay().getHeight();
		double displayAspect = displayWidth / displayHeight;

		if (id == TextLoader.SPACE_ASCII) {
			spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * perPixelSize * (1.0 / displayAspect);
			return null;
		}

		double xTextureCoord = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTextureCoord = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (paddingWidth - 2 * DESIRED_PADDING);
		int height = getValueOfVariable("height") - (paddingHeight - 2 * DESIRED_PADDING);
		double quadWidth = width * perPixelSize;
		double quadHeight = height * perPixelSize;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOffset = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * perPixelSize;
		double yOffset = (getValueOfVariable("yoffset") + padding[PAD_TOP] - DESIRED_PADDING) * perPixelSize;
		double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * perPixelSize;
		return new Character(id, xTextureCoord, yTextureCoord, xTexSize, yTexSize, xOffset, yOffset, quadWidth, quadHeight, xAdvance);
	}

	/**
	 * Closes the font file after finishing reading.
	 */
	private void close() {
		try {
			reader.close();
		} catch (IOException e) {
			FlounderEngine.getLogger().error("Could not close Font MetaFile.");
			FlounderEngine.getLogger().exception(e);
		}
	}

	/**
	 * Gets the character from a ascii id.
	 *
	 * @param ascii The ascii ID.
	 *
	 * @return The character.
	 */
	protected Character getCharacter(int ascii) {
		return metaData.get(ascii);
	}

	/**
	 * Gets the space width.
	 *
	 * @return The space width.
	 */
	protected double getSpaceWidth() {
		return spaceWidth;
	}
}