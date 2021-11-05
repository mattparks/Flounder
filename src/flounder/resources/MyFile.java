package flounder.resources;

import flounder.logger.*;

import java.io.*;

/**
 * A framework file that can be read from within the jar file.
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
		StringBuilder path = new StringBuilder();

		for (String part : paths) {
			path.append(FILE_SEPARATOR);
			path.append(part);
		}

		this.path = path.toString();
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
		StringBuilder path = new StringBuilder(file.path);

		for (String part : subFiles) {
			path.append(FILE_SEPARATOR);
			path.append(part);
		}

		this.path = path.toString();
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
			return new BufferedReader(new InputStreamReader(getInputStream()));
		} catch (Exception e) {
			FlounderLogger.get().error("Couldn't get reader for: " + path);
			FlounderLogger.get().exception(e);
			return null;
		}
	}

	/**
	 * Gets a input steam to the file path.
	 *
	 * @return Returns a input steam to the file path.
	 */
	public InputStream getInputStream() {
		try {
			InputStream is = this.getClass().getResourceAsStream(path);

			if (is != null) {
				return is;
			}

			return new FileInputStream(new File(path));
		} catch (IOException e) {
			FlounderLogger.get().error("Couldn't get input stream to: " + path);
			FlounderLogger.get().exception(e);
			return null;
		}
	}

	/**
	 * Gets the name / sub-file of this file.
	 *
	 * @return The name / sub-file of this file.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the path to the represented file(s)
	 *
	 * @return The path to the represented file(s).
	 */
	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "MyFile{" + "path=" + getPath() + "}";
	}
}
