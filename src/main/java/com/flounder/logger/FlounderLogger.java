package com.flounder.logger;

import com.flounder.framework.*;

import java.io.*;
import java.util.*;

/**
 * A module used for logging outputs, errors, and exceptions to files and the console.
 */
public class FlounderLogger extends com.flounder.framework.Module {
	public static final boolean DETAILED = false;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private List<String> saveData;
	private int linesPrinted;

	/**
	 * Creates a new logger manager.
	 */
	public FlounderLogger() {
		super();
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.saveData = new ArrayList<>();
		this.linesPrinted = 0;

		// Logs all registered modules.
		Framework.get().logModules();
	}

	@Handler.Function(Handler.FLAG_UPDATE_ALWAYS)
	public void update() {
	}

	/**
	 * Logs registration info strings sent into a .log file, and if {@code LOG_TO_CONSOLE} is enabled it will also be logged to the IDEs console.
	 *
	 * @param value Text or numbers being added to the log file and possibly to the IDEs console.
	 * @param <T> The object type to be logged.
	 */
	public <T> void init(T value) {
		if (getString(value).isEmpty()) {
			System.out.println();
		} else {
			System.out.println(ANSI_GREEN + "INIT [" + getDateString() + "]: " + ANSI_RESET + getString(value));
		}

		if (getString(value).isEmpty()) {
			saveData.add("");
		} else {
			saveData.add("INIT [" + getDateString() + "]: " + getString(value).replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
		}

		linesPrinted += getString(value).split("\n").length;
	}

	/**
	 * Logs strings sent into a .log file, and if {@code LOG_TO_CONSOLE} is enabled it will also be logged to the IDEs console.
	 *
	 * @param value Text or numbers being added to the log file and possibly to the IDEs console.
	 * @param <T> The object type to be logged.
	 */
	public <T> void log(T value) {
		if (getString(value).isEmpty()) {
			System.out.println();
		} else {
			System.out.println(ANSI_YELLOW + "LOG [" + getDateString() + "]: " + ANSI_RESET + getString(value));
		}

		if (getString(value).isEmpty()) {
			saveData.add("");
		} else {
			saveData.add("LOG [" + getDateString() + "]: " + getString(value).replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
		}

		linesPrinted += getString(value).split("\n").length;
	}

	/**
	 * Warning logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param value Warnings being added to the log file and possibly to your IDES console.
	 * @param <T> The object type to be logged.
	 */
	public <T> void warning(T value) {
		if (getString(value).isEmpty()) {
			System.out.println();
		} else {
			System.out.println(ANSI_PURPLE + "WARNING [" + getDateString() + "]: " + ANSI_RESET + getString(value));
		}

		if (getString(value).isEmpty()) {
			saveData.add("");
		} else {
			saveData.add("WARNING [" + getDateString() + "]: " + getString(value).replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
		}

		linesPrinted += getString(value).split("\n").length;
	}

	/**
	 * Error logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param value Errors being added to the log file and possibly to your IDES console.
	 * @param <T> The object type to be logged.
	 */
	public <T> void error(T value) {
		if (getString(value).isEmpty()) {
			System.out.println();
		} else {
			System.out.println(ANSI_RED + "ERROR [" + getDateString() + "]: " + ANSI_RESET + getString(value));
		}

		if (getString(value).isEmpty()) {
			saveData.add("");
		} else {
			saveData.add("ERROR [" + getDateString() + "]: " + getString(value).replaceAll("\u001B\\[[\\d;]*[^\\d;]", ""));
		}

		linesPrinted += getString(value).split("\n").length;
	}

	/**
	 * Exception logs strings sent into javas console, and if {@code LOG_TO_FILE} is enabled it will also be logged to a log file.
	 *
	 * @param exception The exception added to the log file and possibly to your IDES console.
	 */
	public void exception(Exception exception) {
		System.err.println(ANSI_PURPLE + "EXCEPTION [" + getDateString() + "]: " + ANSI_RESET + getString(exception));
		exception.printStackTrace();

		if (getString(exception).isEmpty()) {
			saveData.add("");
		} else {
			saveData.add("EXCEPTION [" + getDateString() + "]: " + getString(exception));

			for (StackTraceElement element : exception.getStackTrace()) {
				saveData.add("    " + element);
			}
		}

		linesPrinted += getString(exception).split("\n").length;
	}

	/**
	 * Gets a string from a generic.
	 *
	 * @param value The value to get the string from.
	 * @param <T> The generic type.
	 *
	 * @return The string found.
	 */
	private <T> String getString(T value) {
		if (value == null) {
			return "NULL";
		}

		return value.toString();
	}

	/**
	 * Gets the string of the current date.
	 *
	 * @return Returns the string of the current date as [hour:minute:second | day/month/year].
	 */
	private String getDateString() {
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int minute = Calendar.getInstance().get(Calendar.MINUTE);
		int second = Calendar.getInstance().get(Calendar.SECOND) + 1;
		return hour + "." + minute + "." + second;
	}

	/**
	 * Finds / Generates the logs save folder.
	 *
	 * @return The path to the folder.
	 *
	 * @throws IOException Failed to create / find folder.
	 */
	private String getLogsSave() throws IOException {
		File saveDirectory = new File(Framework.get().getRoamingFolder().getPath(), "logs");

		if (!saveDirectory.exists()) {
			System.out.println("Creating directory: " + saveDirectory);

			try {
				saveDirectory.mkdir();
			} catch (SecurityException e) {
				error("Filed to create logging folder: " + saveDirectory.getAbsolutePath());
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

	protected List<String> getSaveData() {
		return saveData;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		try (PrintWriter out = new PrintWriter(getLogsSave())) {
			for (String line : saveData) {
				out.println(line);
			}
		} catch (IOException e) {
			System.err.println("Could not save logs!");
			e.printStackTrace();
		}
	}

	@com.flounder.framework.Module.Instance
	public static FlounderLogger get() {
		return (FlounderLogger) Framework.get().getModule(FlounderLogger.class);
	}
}
