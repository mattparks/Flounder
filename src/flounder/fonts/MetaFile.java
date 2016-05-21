package flounder.fonts;

import flounder.devices.*;
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

	private final Map<Integer, Character> metaData;
	private final Map<String, String> values;
	private double verticalPerPixelSize;
	private double horizontalPerPixelSize;
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
	protected MetaFile(final MyFile file) {
		metaData = new HashMap<>();
		values = new HashMap<>();

		openFile(file);
		loadPaddingData();
		loadLineSizes();
		loadCharacterData(getValueOfVariable("scaleW"));
		close();
	}

	private void openFile(final MyFile file) {
		try {
			reader = file.getReader();
		} catch (Exception e) {
			FlounderLogger.error("Couldn't read font meta file " + file.getPath());
			FlounderLogger.exception(e);
		}
	}

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
			FlounderLogger.error("Failed to read the next line!");
			FlounderLogger.exception(e);
		}

		if (line == null) {
			return false;
		}

		for (final String part : line.split(SPLITTER)) {
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
	private int[] getValuesOfVariable(final String variable) {
		final String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		final int[] actualValues = new int[numbers.length];

		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}

		return actualValues;
	}

	private void loadLineSizes() {
		processNextLine();
		final int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextLoader.LINE_HEIGHT / lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / ManagerDevices.getDisplay().getAspectRatio(); // TODO
	}

	/**
	 * Gets the {@code int} value of the variable with a certain name on the current line.
	 *
	 * @param variable The name of the variable.
	 *
	 * @return The value of the variable.
	 */
	private int getValueOfVariable(final String variable) {
		return Integer.parseInt(values.get(variable));
	}

	private void loadCharacterData(final int imageWidth) {
		processNextLine();
		processNextLine();

		while (processNextLine()) {
			final Character c = loadCharacter(imageWidth);

			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
	}

	private Character loadCharacter(final int imageSize) {
		final int id = getValueOfVariable("id");

		if (id == TextLoader.SPACE_ASCII) {
			spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}

		final double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		final double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		final int width = getValueOfVariable("width") - (paddingWidth - 2 * DESIRED_PADDING);
		final int height = getValueOfVariable("height") - (paddingHeight - 2 * DESIRED_PADDING);
		final double quadWidth = width * horizontalPerPixelSize;
		final double quadHeight = height * verticalPerPixelSize;
		final double xTexSize = (double) width / imageSize;
		final double yTexSize = (double) height / imageSize;
		final double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
		final double yOff = (getValueOfVariable("yoffset") + padding[PAD_TOP] - DESIRED_PADDING) * verticalPerPixelSize;
		final double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}

	/**
	 * Closes the font file after finishing reading.
	 */
	private void close() {
		try {
			reader.close();
		} catch (IOException e) {
			FlounderLogger.error("Could not close Font MetaFile.");
			FlounderLogger.exception(e);
		}
	}

	protected Character getCharacter(final int ascii) {
		return metaData.get(ascii);
	}

	protected double getSpaceWidth() {
		return spaceWidth;
	}
}
