package flounder.fonts;

import flounder.engine.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class TextLoader {
	protected static final double LINE_HEIGHT = 0.04f;
	protected static final int SPACE_ASCII = 32;

	private Texture fontTexture;
	private MetaFile metaData;

	public TextLoader(MyFile fontSheet, MyFile metaFile) {
		this.fontTexture = Texture.newTexture(fontSheet).noFiltering().create();
		this.metaData = new MetaFile(metaFile);
	}

	public int getFontTextureAtlas() {
		return fontTexture.getTextureID();
	}

	public void loadTextIntoMemory(Text text) {
		List<Line> lines = createStructure(text);
		loadStructureToOpenGL(text, lines);
	}

	private List<Line> createStructure(Text text) {
		char[] chars = text.getTextString().toCharArray();
		List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());

		for (char c : chars) {
			if (c == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);

				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}

				currentWord = new Word(text.getFontSize());
				continue;
			}

			Character character = metaData.getCharacter(c);

			if (character == null) {
				FlounderEngine.getLogger().error("Could not find font char for: " + c);
			} else {
				currentWord.addCharacter(character);
			}
		}

		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, Text text) {
		if (!currentLine.attemptToAddWord(currentWord)) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}

		lines.add(currentLine);
	}

	private void loadStructureToOpenGL(Text text, List<Line> lines) {
		setTextSettings(text, lines);
		double cursorX = 0f;
		double cursorY = 0f;
		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoords = new ArrayList<>();

		for (Line line : lines) {
			if (text.isCentred()) {
				cursorX = (line.getMaxLength() - line.getLineLength()) / 2;
			}

			for (Word word : line.getWords()) {
				for (Character letter : word.getCharacters()) {
					addVerticesForCharacter(cursorX, cursorY, letter, text.getFontSize(), vertices);
					addTextCoords(textureCoords, letter.getXTextureCoord(), letter.getYTextureCoord(), letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					cursorX += letter.getXAdvance() * text.getFontSize();
				}

				cursorX += metaData.getSpaceWidth() * text.getFontSize();
			}

			cursorX = 0;
			cursorY += LINE_HEIGHT * text.getFontSize();
		}

		float[] verticesArray = listToArray(vertices);
		float[] textureArray = listToArray(textureCoords);
		int vao = FlounderEngine.getLoader().createInterleavedVAO(vertices.size() / 2, verticesArray, textureArray);
		text.setMeshInfo(vao, vertices.size() / 2);
	}

	private static void addTextCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) x);
		texCoords.add((float) y);
	}

	private void setTextSettings(Text text, List<Line> lines) {
		text.setNumberOfLines(lines.size());

		if (text.isCentred() || lines.size() > 1) {
			text.setOriginalWidth((float) lines.get(0).getMaxLength());
		} else {
			text.setOriginalWidth((float) lines.get(0).getLineLength());
		}
	}

	private void addVerticesForCharacter(double cursorX, double cursorY, Character character, double fontSize, List<Float> vertices) {
		double x = cursorX + character.getXOffset() * fontSize;
		double y = cursorY + character.getYOffset() * fontSize;
		double maxX = x + character.getSizeX() * fontSize;
		double maxY = y + character.getSizeY() * fontSize;
		addVertices(vertices, x, y, maxX, maxY);
	}

	private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) x);
		vertices.add((float) y);
	}

	private float[] listToArray(List<Float> list) {
		float[] array = new float[list.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return array;
	}
}
