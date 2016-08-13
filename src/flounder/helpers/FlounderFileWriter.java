package flounder.helpers;

import java.io.*;

public class FlounderFileWriter {
	public static final int MAX_LINE_LENGTH = 5000;

	private FileWriter fileWriter;
	private int fileNestation;
	private int lineLength;

	public FlounderFileWriter(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
		this.fileNestation = 0;
		this.lineLength = 0;
	}

	public void beginNewSegment(String name) throws IOException {
		name = getIndentations() + name + " {";
		fileWriter.write(name);
		enterBlankLine();
		lineLength = 0;
		fileNestation++;
	}

	public void endSegment(boolean enterTightSpace) throws IOException {
		enterBlankLine();
		fileNestation--;
		fileWriter.write(getIndentations() + "};");

		if (!enterTightSpace) {
			enterBlankLine();
			enterBlankLine();
		}
	}

	public String getIndentations() {
		String data = "";

		for (int i = 0; i < fileNestation; i++) {
			data += "	";
		}

		return data;
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
		fileWriter.write(getIndentations() + "/**");
		enterBlankLine();

		for (String line : lines) {
			fileWriter.write(getIndentations() + " * " + line);
			enterBlankLine();
		}

		fileWriter.write(getIndentations() + " */");
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
