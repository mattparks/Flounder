package flounder.fonts;

import flounder.devices.*;
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
		final int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		close();
	}

	private void openFile(final MyFile file) {
		try {
			reader = file.getReader();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't read font meta file!");
		}
	}

	private void loadPaddingData() {
		processNextLine();
		padding = getValuesOfVariable("padding");
		paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextLoader.LINE_HEIGHT / lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / ManagerDevices.getDisplay().getDisplayAspectRatio(); // TODO: Move aspect ratio out.
	}

	private void loadCharacterData(final int imageWidth) {
		processNextLine();
		processNextLine();

		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);

			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
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

	/**
	 * Closes the font file after finishing reading.
	 */
	private void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
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
			System.err.println("Failed to read the next line!");
			e.printStackTrace();
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
	private int[] getValuesOfVariable(final String variable) {
		String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];

		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}

		return actualValues;
	}

	private Character loadCharacter(final int imageSize) {
		int id = getValueOfVariable("id");

		if (id == TextLoader.SPACE_ASCII) {
			spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}

		double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (paddingWidth - 2 * DESIRED_PADDING);
		int height = getValueOfVariable("height") - (paddingHeight - 2 * DESIRED_PADDING);
		double quadWidth = width * horizontalPerPixelSize;
		double quadHeight = height * verticalPerPixelSize;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
		double yOff = (getValueOfVariable("yoffset") + padding[PAD_TOP] - DESIRED_PADDING) * verticalPerPixelSize;
		double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}

	protected Character getCharacter(final int ascii) {
		return metaData.get(ascii);
	}

	protected double getSpaceWidth() {
		return spaceWidth;
	}
}
