package flounder.engine;

import flounder.engine.options.*;

import java.io.*;
import java.util.*;

/**
 * Various utility functions for Debugging.
 */
public class Logger {
	private static final Map<String, String> saveData = new HashMap<>();

	/**
	 * Log logs strings sent into a .log file, and if {@code LOG_TO_CONSOLE} is enabled it will also be logged to the IDE's console.
	 *
	 * @param value Text or numbers being added to the log file and possibly to the IDES console.
	 * @param <T> The object type to be logged.
	 */
	public static <T> void log(final T value) {
		if (OptionsLogger.LOG_TO_CONSOLE) {
			System.out.println("LOG: " + getDateString() + value.toString());
		}

		if (OptionsLogger.LOG_TO_FILE) {
			saveData.put("LOG: " + getDateString(), value.toString());
		}
	}

	/**
	 * @return Returns the string of the current date as [hour:minute:second | day/month/year].
	 */
	public static String getDateString() {
		return "[" + Calendar.getInstance().get(Calendar.HOUR) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + (Calendar.getInstance().get(Calendar.SECOND) + 1) + "]: ";
	}

	/**
	 * Error logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param value Errors being added to the log file and possibly to your IDES console.
	 * @param <T> The object type to be logged.
	 */
	public static <T> void error(final T value) {
		System.err.println("ERROR: " + getDateString() + value.toString());

		if (OptionsLogger.LOG_TO_FILE) {
			saveData.put("ERROR: " + getDateString(), value.toString());
		}
	}

	private static String getLogsFolder() throws IOException {
		File saveDirectory = new File("assets/logs");

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		String result = saveDirectory + "/" + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "-" + (Calendar.getInstance().get(Calendar.SECOND) + 1) + "--" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.YEAR) + ".log";

		File resultingFile = new File(result);

		if (!resultingFile.exists()) {
			resultingFile.createNewFile();
		}

		FileOutputStream outputFile = new FileOutputStream(resultingFile, false);
		outputFile.close();

		return result;
	}

	public static void dispose() {
		if (OptionsLogger.LOG_TO_FILE) {
			//try {
			//	Config.write(getLogsFolder(), saveData); // TODO
			//} catch(IOException e) {
			//	e.printStackTrace();
			//}
		}
	}
}
