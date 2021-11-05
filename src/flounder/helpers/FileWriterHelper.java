package flounder.helpers;

import java.io.*;

/**
 * A helper for helping when writing to a file.
 */
public class FileWriterHelper {
	public static final int MAX_LINE_LENGTH = 512;

	private FileWriter fileWriter;
	private int fileNestation;
	private int lineLength;

	/**
	 * Creates a new file writer helper.
	 *
	 * @param fileWriter The FileWriter to help with.
	 */
	public FileWriterHelper(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
		this.fileNestation = 0;
		this.lineLength = 0;
	}

	public void beginNewSegment(String name) throws IOException {
		beginNewSegment(name, true);
	}

	public void beginNewSegment(String name, boolean brackets) throws IOException {
		name = getIndentations() + name + (brackets ? " {" : "");
		fileWriter.write(name);
		enterBlankLine();
		lineLength = 0;
		fileNestation++;
	}

	public void endSegment(boolean enterTightSpace) throws IOException {
		endSegment(enterTightSpace, true);
	}

	public void endSegment(boolean enterTightSpace, boolean brackets) throws IOException {
		enterBlankLine();
		fileNestation--;
		fileWriter.write(getIndentations() + (brackets ? "}" : ""));

		if (!enterTightSpace) {
			enterBlankLine();
			enterBlankLine();
		}
	}

	public String getIndentations() {
		StringBuilder data = new StringBuilder();

		for (int i = 0; i < fileNestation; i++) {
			data.append("	");
		}

		return data.toString();
	}

	public void writeSegmentData(String data, boolean breakAfter) throws IOException {
		writeSegmentData(data);

		if (breakAfter) {
			lineLength = MAX_LINE_LENGTH;
		}
	}

	public void writeSegmentData(String... data) throws IOException {
		if (lineLength >= MAX_LINE_LENGTH) {
			lineLength = 0;
			enterBlankLine();
		}

		if (lineLength == 0) {
			fileWriter.write(getIndentations());
			lineLength = getIndentations().length();
		}

		for (String s : data) {
			lineLength += s.length();
			fileWriter.write(s);
		}
	}

	public void addComment(String... lines) throws IOException {
		enterBlankLine();

		for (String line : lines) {
			fileWriter.write(getIndentations() + "/// " + line);
			enterBlankLine();
		}

		enterBlankLine();
	}

	public void startFileLine(String data) throws IOException {
		fileWriter.write(data);
	}

	public void writeSingleLine(String data) throws IOException {
		enterBlankLine();
		fileWriter.write(data);
	}

	public void enterBlankLine() throws IOException {
		fileWriter.write("\n");
	}
}
