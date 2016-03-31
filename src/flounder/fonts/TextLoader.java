package flounder.fonts;

import flounder.loaders.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class TextLoader {
	protected static final double LINE_HEIGHT = 0.04f;
	protected static final int SPACE_ASCII = 32;

	private final Texture m_fontTexture;
	private final MetaFile m_metaData;

	public TextLoader(MyFile fontSheet, MyFile metaFile) {
		m_fontTexture = Texture.newTexture(fontSheet).noFiltering().create();
		m_metaData = new MetaFile(metaFile);
	}

	private static void addTextCoords(final List<Float> texCoords, final double x, final double y, final double maxX, final double maxY) {
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

	private static void addVertices(final List<Float> vertices, final double x, final double y, final double maxX, final double maxY) {
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

	public int getFontTextureAtlas() {
		return m_fontTexture.getTextureID();
	}

	public void loadTextIntoMemory(final Text text) {
		List<Line> lines = createStructure(text);
		loadStructureToOpenGL(text, lines);
		// GUIManager.addNewText(text);
	}

	private List<Line> createStructure(final Text text) {
		char[] chars = text.getTextString().toCharArray();
		List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(m_metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());

		for (char c : chars) {
			if (c == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);

				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(m_metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}

				currentWord = new Word(text.getFontSize());
				continue;
			}

			Character character = m_metaData.getCharacter(c);
			currentWord.addCharacter(character);
		}

		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void loadStructureToOpenGL(final Text text, final List<Line> lines) {
		setTextSettings(text, lines);
		double cursorX = 0f;
		double cursorY = 0f;
		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoords = new ArrayList<>();

		for (Line line : lines) {
			if (text.isCentered()) {
				cursorX = (line.getMaxLength() - line.getLineLength()) / 2;
			}

			for (Word word : line.getWords()) {
				for (Character letter : word.getCharacters()) {
					addVerticesForCharacter(cursorX, cursorY, letter, text.getFontSize(), vertices);
					addTextCoords(textureCoords, letter.getXTextureCoord(), letter.getYTextureCoord(), letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					cursorX += letter.getXAdvance() * text.getFontSize();
				}

				cursorX += m_metaData.getSpaceWidth() * text.getFontSize();
			}

			cursorX = 0;
			cursorY += LINE_HEIGHT * text.getFontSize();
		}

		float[] verticesArray = listToArray(vertices);
		float[] textureArray = listToArray(textureCoords);
		int vao = Loader.createInterleavedVAO(vertices.size() / 2, verticesArray, textureArray);
		text.setMeshInfo(vao, vertices.size() / 2);
	}

	private void completeStructure(final List<Line> lines, Line currentLine, final Word currentWord, final Text text) {
		boolean added = currentLine.attemptToAddWord(currentWord);

		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(m_metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}

		lines.add(currentLine);
	}

	private void setTextSettings(final Text text, final List<Line> lines) {
		text.setNumberOfLines(lines.size());

		if (text.isCentered() || lines.size() > 1) {
			text.setOriginalWidth((float) lines.get(0).getMaxLength());
		} else {
			text.setOriginalWidth((float) lines.get(0).getLineLength());
		}
	}

	private void addVerticesForCharacter(final double cursorX, final double cursorY, final Character character, final double fontSize, final List<Float> vertices) {
		double x = cursorX + character.getXOffset() * fontSize;
		double y = cursorY + character.getYOffset() * fontSize;
		double maxX = x + character.getSizeX() * fontSize;
		double maxY = y + character.getSizeY() * fontSize;
		addVertices(vertices, x, y, maxX, maxY);
	}

	private float[] listToArray(final List<Float> list) {
		float[] array = new float[list.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return array;
	}
}
