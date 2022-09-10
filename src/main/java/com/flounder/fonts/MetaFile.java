package com.flounder.fonts;

import com.flounder.logger.*;
import com.flounder.resources.*;

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

	private static final int DESIRED_PADDING = 8;

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private Map<Integer, Character> metaData;
	private Map<String, String> values;

	private double verticalPerPixelSize;
	private double horizontalPerPixelSize;
	private double spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;
	private double maxSizeY;

	private BufferedReader reader;

	/**
	 * Opens a font file in preparation for reading.
	 *
	 * @param file The font file to load from.
	 */
	protected MetaFile(MyFile file) {
		this.metaData = new HashMap<>();
		this.values = new HashMap<>();

		openFile(file);
		loadPaddingData();
		loadLineSizes();
		loadCharacterData();
		closeReader();
	}

	/**
	 * Opens the font file, ready for reading.
	 *
	 * @param file The font file to open.
	 */
	private void openFile(MyFile file) {
		try {
			reader = file.getReader();
		} catch (Exception e) {
			FlounderLogger.get().log("Couldn't read font meta file!");
			FlounderLogger.get().exception(e);
		}
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
			FlounderLogger.get().log("Couldn't process the next font line!");
			FlounderLogger.get().exception(e);
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
	 * Loads the data about how much padding is used around each character in the texture atlas.
	 */
	private void loadPaddingData() {
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	/**
	 * Loads information about the line height for this font in pixels,
	 * and uses this as a way to find the conversion rate between pixels in the texture atlas and screen-space.
	 */
	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextLoader.LINE_HEIGHT / (double) lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize;
	}

	/**
	 * Loads in data about each character and stores the data in the {@link Character} class.
	 */
	private void loadCharacterData() {
		// The width of the texture atlas in pixels.
		int imageWidth = getValueOfVariable("scaleW");

		processNextLine();
		processNextLine();

		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);

			if (c != null) {
				metaData.put(c.id, c);
			}
		}
	}

	/**
	 * Loads all the data about one character in the texture atlas and converts it all from 'pixels' to 'screen-space' before storing.
	 * The effects of padding are also removed from the data.
	 *
	 * @param imageSize The size of the texture atlas in pixels.
	 *
	 * @return The data about the character.
	 */
	private Character loadCharacter(int imageSize) {
		int id = getValueOfVariable("id");

		if (id == TextLoader.SPACE_ASCII) {
			this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}

		double xTextureCoord = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTextureCoord = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
		int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
		double quadWidth = width * horizontalPerPixelSize;
		double quadHeight = height * verticalPerPixelSize;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOffset = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
		double yOffset = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
		double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		if (quadHeight > maxSizeY) {
			maxSizeY = quadHeight;
		}
		return new Character(id, xTextureCoord, yTextureCoord, xTexSize, yTexSize, xOffset, yOffset, quadWidth, quadHeight, xAdvance);
	}

	/**
	 * Gets the {@code int} value of the variable with a certain name on the current line.
	 *
	 * @param variable The name of the variable.
	 *
	 * @return The value of the variable.
	 */
	private int getValueOfVariable(String variable) {
		return Integer.parseInt(values.get(variable));
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
	 * Closes the font file after finishing reading.
	 */
	private void closeReader() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected double getSpaceWidth() {
		return spaceWidth;
	}

	protected Character getCharacter(int ascii) {
		return metaData.get(ascii);
	}

	protected double getMaxSizeY() {
		return maxSizeY;
	}
}
