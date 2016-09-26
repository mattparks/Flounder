package flounder.resources;

import flounder.logger.*;

import java.io.*;

/**
 * A engine file that can be read from within the jar file.
 */
public class MyFile {
	public static final String FILE_SEPARATOR = "/";
	public static final MyFile RES_FOLDER = new MyFile("res");

	private String path;
	private String name;

	/**
	 * Constructor for MyFile.
	 *
	 * @param path The path for this file to represent.
	 */
	public MyFile(String path) {
		this.path = FILE_SEPARATOR + path;
		String[] dirs = path.split(FILE_SEPARATOR);
		name = dirs[dirs.length - 1];
	}

	/**
	 * Constructor for MyFile.
	 *
	 * @param paths Paths for this file to represent.
	 */
	public MyFile(String... paths) {
		String path = "";

		for (String part : paths) {
			path += FILE_SEPARATOR + part;
		}

		this.path = path;
		String[] dirs = this.path.split(FILE_SEPARATOR);
		name = dirs[dirs.length - 1];
	}

	/**
	 * Constructor for MyFile.
	 *
	 * @param file The file for this file to represent.
	 * @param subFile The name of the represented sub file.
	 */
	public MyFile(MyFile file, String subFile) {
		path = file.path + FILE_SEPARATOR + subFile;
		name = subFile;
	}

	/**
	 * Constructor for MyFile.
	 *
	 * @param file The file for this file to represent.
	 * @param subFiles Names of the represented sub file.
	 */
	public MyFile(MyFile file, String... subFiles) {
		String path = file.path;

		for (String part : subFiles) {
			path += FILE_SEPARATOR + part;
		}

		this.path = path;
		String[] dirs = this.path.split(FILE_SEPARATOR);
		name = dirs[dirs.length - 1];
	}

	/**
	 * Creates a reader for the file.
	 *
	 * @return A buffered reader for the file.
	 */
	public BufferedReader getReader() {
		try {
			InputStreamReader isr = new InputStreamReader(getInputStream());
			return new BufferedReader(isr);
		} catch (Exception e) {
			FlounderLogger.error("Couldn't get reader for " + path);
			FlounderLogger.exception(e);
			return null;
		}
	}

	/**
	 * @return Returns a input steam to the file path.
	 */
	public InputStream getInputStream() {
		return Class.class.getResourceAsStream(path);
	}

	/**
	 * @return The name / subfile of this file.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "MyFile{" + "path=" + getPath() + "}";
	}

	/**
	 * @return The path to the represented file(s).
	 */
	public String getPath() {
		return path;
	}
}
