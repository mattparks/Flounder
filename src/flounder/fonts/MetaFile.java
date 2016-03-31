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

	private double m_verticalPerPixelSize;
	private double m_horizontalPerPixelSize;
	private double m_spaceWidth;
	private int[] m_padding;
	private int m_paddingWidth;
	private int m_paddingHeight;

	private final Map<Integer, Character> m_metaData;
	private final Map<String, String> m_values;
	private BufferedReader m_reader;

	/**
	 * Opens a font file in preparation for reading.
	 *
	 * @param file The font file.
	 */
	protected MetaFile(final MyFile file) {
		m_metaData = new HashMap<>();
		m_values = new HashMap<>();

		openFile(file);
		loadPaddingData();
		loadLineSizes();
		final int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		close();
	}

	private void openFile(final MyFile file) {
		try {
			m_reader = file.getReader();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't read font meta file!");
		}
	}

	private void loadPaddingData() {
		processNextLine();
		m_padding = getValuesOfVariable("padding");
		m_paddingWidth = m_padding[PAD_LEFT] + m_padding[PAD_RIGHT];
		m_paddingHeight = m_padding[PAD_TOP] + m_padding[PAD_BOTTOM];
	}

	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - m_paddingHeight;
		m_verticalPerPixelSize = TextLoader.LINE_HEIGHT / lineHeightPixels;
		m_horizontalPerPixelSize = m_verticalPerPixelSize / ManagerDevices.getDisplay().getDisplayAspectRatio(); // TODO: Move aspect ratio out.
	}

	private void loadCharacterData(final int imageWidth) {
		processNextLine();
		processNextLine();

		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);

			if (c != null) {
				m_metaData.put(c.getId(), c);
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
		return Integer.parseInt(m_values.get(variable));
	}

	/**
	 * Closes the font file after finishing reading.
	 */
	private void close() {
		try {
			m_reader.close();
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
		m_values.clear();
		String line = null;

		try {
			line = m_reader.readLine();
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
				m_values.put(valuePairs[0], valuePairs[1]);
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
		String[] numbers = m_values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];

		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}

		return actualValues;
	}

	private Character loadCharacter(final int imageSize) {
		int id = getValueOfVariable("id");

		if (id == TextLoader.SPACE_ASCII) {
			m_spaceWidth = (getValueOfVariable("xadvance") - m_paddingWidth) * m_horizontalPerPixelSize;
			return null;
		}

		double xTex = ((double) getValueOfVariable("x") + (m_padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		double yTex = ((double) getValueOfVariable("y") + (m_padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		int width = getValueOfVariable("width") - (m_paddingWidth - 2 * DESIRED_PADDING);
		int height = getValueOfVariable("height") - (m_paddingHeight - 2 * DESIRED_PADDING);
		double quadWidth = width * m_horizontalPerPixelSize;
		double quadHeight = height * m_verticalPerPixelSize;
		double xTexSize = (double) width / imageSize;
		double yTexSize = (double) height / imageSize;
		double xOff = (getValueOfVariable("xoffset") + m_padding[PAD_LEFT] - DESIRED_PADDING) * m_horizontalPerPixelSize;
		double yOff = (getValueOfVariable("yoffset") + m_padding[PAD_TOP] - DESIRED_PADDING) * m_verticalPerPixelSize;
		double xAdvance = (getValueOfVariable("xadvance") - m_paddingWidth) * m_horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}

	protected Character getCharacter(final int ascii) {
		return m_metaData.get(ascii);
	}

	protected double getSpaceWidth() {
		return m_spaceWidth;
	}
}
