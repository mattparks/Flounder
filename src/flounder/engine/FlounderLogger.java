package flounder.engine;

import flounder.engine.options.*;

import java.io.*;
import java.util.*;

/**
 * Various utility functions for Debugging.
 */
public class FlounderLogger {
	private static final StringBuilder saveData = new StringBuilder();

	/**
	 * Log logs strings sent into a .log file, and if {@code LOG_TO_CONSOLE} is enabled it will also be logged to the IDE's console.
	 *
	 * @param value Text or numbers being added to the log file and possibly to the IDES console.
	 * @param <T> The object type to be logged.
	 */
	public static <T> void log(final T value) {
		if (OptionsLogger.LOG_TO_CONSOLE) {
			System.out.println("LOG: " + "[" + getDateString() + "]: " + value.toString());
		}

		if (OptionsLogger.LOG_TO_FILE) {
			saveData.append("LOG: " + "[" + getDateString() + "]: " + value.toString() + "\n");
		}
	}

	/**
	 * @return Returns the string of the current date as [hour:minute:second | day/month/year].
	 */
	public static String getDateString() {
		return Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
	}

	/**
	 * Error logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param value Errors being added to the log file and possibly to your IDES console.
	 * @param <T> The object type to be logged.
	 */
	public static <T> void error(final T value) {
		System.err.println("ERROR: " + "[" + getDateString() + "]: " + value.toString());

		if (OptionsLogger.LOG_TO_FILE) {
			saveData.append("ERROR: " + "[" + getDateString() + "]: " + value.toString() + "\n");
		}
	}

	/**
	 * Exception logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param exception The exception added to the log file and possibly to your IDES console.
	 */
	public static void exception(final Exception exception) {
		System.err.println("EXCEPTION: " + "[" + getDateString() + "]: " + exception.toString());

		if (OptionsLogger.LOG_TO_FILE) {
			saveData.append("EXCEPTION: " + "[" + getDateString() + "]: " + exception.toString() + "\n");
		}
	}

	public static void dispose() {
		if (OptionsLogger.LOG_TO_FILE) {
			// getLogsSave(), saveData
			try (PrintWriter out = new PrintWriter(getLogsSave())) {
				for (final String line : saveData.toString().split("\n")) {
					out.println(line);
				}
			} catch (final IOException e) {
				System.err.println("Could not save logs!");
				FlounderLogger.exception(e);
			}
		}
	}

	private static String getLogsSave() throws IOException {
		final File saveDirectory = new File("logs");

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				FlounderLogger.error("Filed to create logging folder.");
				FlounderLogger.exception(e);
			}
		}

		final String result = saveDirectory + "/" + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.YEAR) + ".log";
		final File resultingFile = new File(result);

		if (!resultingFile.exists()) {
			resultingFile.createNewFile();
		}

		FileOutputStream outputFile = new FileOutputStream(resultingFile, false);
		outputFile.close();

		return result;
	}
}
