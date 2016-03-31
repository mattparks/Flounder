package flounder.resources;

import java.io.*;

/**
 * A engine file that can be read from within the jar file.
 */
public class MyFile {
	public static final String FILE_SEPARATOR = "/";
	public static final MyFile RES_FOLDER = new MyFile("res");

	private final String m_path;
	private final String m_name;

	/**
	 * Constructor for MyFile.
	 *
	 * @param path The m_path for this file to represent.
	 */
	public MyFile(final String path) {
		this.m_path = FILE_SEPARATOR + path;
		String[] dirs = path.split(FILE_SEPARATOR);
		m_name = dirs[dirs.length - 1];
	}

	/**
	 * Constructor for MyFile.
	 *
	 * @param paths Paths for this file to represent.
	 */
	public MyFile(final String... paths) {
		String path = "";

		for (String part : paths) {
			path += FILE_SEPARATOR + part;
		}

		m_path = path;
		String[] dirs = m_path.split(FILE_SEPARATOR);
		m_name = dirs[dirs.length - 1];
	}

	/**
	 * Constructor for MyFile.
	 *
	 * @param file The file for this file to represent.
	 * @param subFile The m_name of the represented sub file.
	 */
	public MyFile(final MyFile file, final String subFile) {
		m_path = file.m_path + FILE_SEPARATOR + subFile;
		m_name = subFile;
	}

	/**
	 * Constructor for MyFile.
	 *
	 * @param file The file for this file to represent.
	 * @param subFiles Names of the represented sub file.
	 */
	public MyFile(final MyFile file, final String... subFiles) {
		String path = file.m_path;

		for (String part : subFiles) {
			path += FILE_SEPARATOR + part;
		}

		m_path = path;
		String[] dirs = m_path.split(FILE_SEPARATOR);
		m_name = dirs[dirs.length - 1];
	}

	/**
	 * Creates a reader for the file.
	 *
	 * @return A buffered reader for the file.
	 */
	public BufferedReader getReader() {
		try {
			final InputStreamReader isr = new InputStreamReader(getInputStream());
			return new BufferedReader(isr);
		} catch (Exception e) {
			System.err.println("Couldn't get reader for " + m_path);
			throw e;
		}
	}

	/**
	 * @return Returns a input steam to the file m_path.
	 */
	public InputStream getInputStream() {
		return Class.class.getResourceAsStream(m_path);
	}

	/**
	 * @return The m_name / subfile of this file.
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @return The m_path to the represented file(s).
	 */
	public String getPath() {
		return m_path;
	}

	@Override
	public String toString() {
		return getPath();
	}
}
