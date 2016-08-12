package flounder.logger;

import flounder.engine.*;

import java.io.*;
import java.util.*;

/**
 * Various utility functions for debugging.
 */
public class FlounderLogger implements IModule {
	public static final boolean LOG_TO_CONSOLE = true;
	public static final boolean LOG_TO_FILE = true;
	public static final boolean ALLOW_LOUD_LOGS = FlounderLogger.class.getResource("/" + FlounderLogger.class.getName().replace('.', '/') + ".class").toString().startsWith("jar:");

	private StringBuilder saveData;
	private int linesRecorded;

	/**
	 * Creates a new instance of the logger.
	 */
	public FlounderLogger() {
		saveData = new StringBuilder();
		linesRecorded = 0;
	}

	@Override
	public void init() {
		log("Flounder Engine Version: " + FlounderEngine.getVersion().version);
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Logger", "Log To Console", LOG_TO_CONSOLE);
		FlounderEngine.getProfiler().add("Logger", "Log To Files", LOG_TO_FILE);
		FlounderEngine.getProfiler().add("Logger", "Loud Logs", ALLOW_LOUD_LOGS);
		FlounderEngine.getProfiler().add("Logger", "Lines Recorded", linesRecorded);
	}

	public boolean inJar() {
		return ALLOW_LOUD_LOGS;
	}

	/**
	 * Log logs strings sent into a .log file, and if {@code LOG_TO_CONSOLE} is enabled it will also be logged to the IDE's console.
	 *
	 * @param value Text or numbers being added to the log file and possibly to the IDES console.
	 * @param <T> The object type to be logged.
	 */
	public <T> void log(T value) {
		log(value, false);
	}

	/**
	 * Log logs strings sent into a .log file, and if {@code LOG_TO_CONSOLE} is enabled it will also be logged to the IDE's console.
	 *
	 * @param value Text or numbers being added to the log file and possibly to the IDES console.
	 * @param loud If the logged value is not useful, it may or may not be logged because of this.
	 * @param <T> The object type to be logged.
	 */
	public <T> void log(T value, boolean loud) {
		if (loud && !ALLOW_LOUD_LOGS) {
			return;
		}

		if (LOG_TO_CONSOLE) {
			System.out.println("LOG: " + "[" + getDateString() + "]: " + value.toString());
		}

		if (LOG_TO_FILE) {
			saveData.append("LOG: " + "[" + getDateString() + "]: " + value.toString() + "\n");
			linesRecorded++;
		}
	}

	/**
	 * Error logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param value Errors being added to the log file and possibly to your IDES console.
	 * @param <T> The object type to be logged.
	 */
	public <T> void error(T value) {
		error(value, false);
	}

	/**
	 * Error logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param value Errors being added to the log file and possibly to your IDES console.
	 * @param loud If the logged value is not useful, it may or may not be logged because of this.
	 * @param <T> The object type to be logged.
	 */
	public <T> void error(T value, boolean loud) {
		if (loud && !ALLOW_LOUD_LOGS) {
			return;
		}

		System.err.println("ERROR: " + "[" + getDateString() + "]: " + value.toString());

		if (LOG_TO_FILE) {
			saveData.append("ERROR: " + "[" + getDateString() + "]: " + value.toString() + "\n");
			linesRecorded++;
		}
	}

	/**
	 * Exception logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param exception The exception added to the log file and possibly to your IDES console.
	 */
	public void exception(Exception exception) {
		if (!ALLOW_LOUD_LOGS) {
			return;
		}

		if (LOG_TO_CONSOLE) {
			System.err.println("EXCEPTION: " + "[" + getDateString() + "]: " + exception.toString());
		}

		if (LOG_TO_FILE) {
			saveData.append("EXCEPTION: " + "[" + getDateString() + "]: " + exception.toString() + "\n");
			linesRecorded += exception.toString().split("\n").length;
		}
	}

	/**
	 * @return Returns the string of the current date as [hour:minute:second | day/month/year].
	 */
	public String getDateString() {
		return Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1);
	}

	/**
	 * Finds / Generates the logs save folder.
	 *
	 * @return The path to the folder.
	 *
	 * @throws IOException Failed to create / find folder.
	 */
	private String getLogsSave() throws IOException {
		File saveDirectory = new File("logs");

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				error("Filed to create logging folder.");
				exception(e);
			}
		}

		String result = saveDirectory + "/" + Calendar.getInstance().get(Calendar.HOUR) + "." + Calendar.getInstance().get(Calendar.MINUTE) + "." + (Calendar.getInstance().get(Calendar.SECOND) + 1) + "-" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + Calendar.getInstance().get(Calendar.YEAR) + ".log";
		File resultingFile = new File(result);

		if (!resultingFile.exists()) {
			resultingFile.createNewFile();
		}

		FileOutputStream outputFile = new FileOutputStream(resultingFile, false);
		outputFile.close();

		return result;
	}

	@Override
	public void dispose() {
		if (LOG_TO_FILE) {
			try (PrintWriter out = new PrintWriter(getLogsSave())) {
				for (String line : saveData.toString().split("\n")) {
					out.println(line);
				}
			} catch (IOException e) {
				System.err.println("Could not save logs!");
				e.printStackTrace();
			}
		}
	}
}
