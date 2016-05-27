package flounder.fonts;

import flounder.engine.*;
import flounder.loaders.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class TextLoader {
	protected static final double LINE_HEIGHT = 0.04f;
	protected static final int SPACE_ASCII = 32;

	private final Texture fontTexture;
	private final MetaFile metaData;

	public TextLoader(final MyFile fontSheet, final MyFile metaFile) {
		this.fontTexture = Texture.newTexture(fontSheet).noFiltering().create();
		this.metaData = new MetaFile(metaFile);
	}

	public int getFontTextureAtlas() {
		return fontTexture.getTextureID();
	}

	public void loadTextIntoMemory(final Text text) {
		List<Line> lines = createStructure(text);
		loadStructureToOpenGL(text, lines);
	}

	private List<Line> createStructure(final Text text) {
		final char[] chars = text.getTextString().toCharArray();
		final List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());

		for (final char c : chars) {
			if (c == SPACE_ASCII) {
				final boolean added = currentLine.attemptToAddWord(currentWord);

				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}

				currentWord = new Word(text.getFontSize());
				continue;
			}

			final Character character = metaData.getCharacter(c);

			if (character == null) {
				FlounderLogger.error("Could not find font char for: " + c);
			}

			currentWord.addCharacter(character);
		}

		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(final List<Line> lines, Line currentLine, final Word currentWord, final Text text) {
		if (!currentLine.attemptToAddWord(currentWord)) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}

		lines.add(currentLine);
	}

	private void loadStructureToOpenGL(final Text text, final List<Line> lines) {
		setTextSettings(text, lines);
		double cursorX = 0f;
		double cursorY = 0f;
		final List<Float> vertices = new ArrayList<>();
		final List<Float> textureCoords = new ArrayList<>();

		for (final Line line : lines) {
			if (text.isCentered()) {
				cursorX = (line.getMaxLength() - line.getLineLength()) / 2;
			}

			for (final Word word : line.getWords()) {
				for (final Character letter : word.getCharacters()) {
					addVerticesForCharacter(cursorX, cursorY, letter, text.getFontSize(), vertices);
					addTextCoords(textureCoords, letter.getXTextureCoord(), letter.getYTextureCoord(), letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					cursorX += letter.getXAdvance() * text.getFontSize();
				}

				cursorX += metaData.getSpaceWidth() * text.getFontSize();
			}

			cursorX = 0;
			cursorY += LINE_HEIGHT * text.getFontSize();
		}

		final float[] verticesArray = listToArray(vertices);
		final float[] textureArray = listToArray(textureCoords);
		final int vao = Loader.createInterleavedVAO(vertices.size() / 2, verticesArray, textureArray);
		text.setMeshInfo(vao, vertices.size() / 2);
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

	private void setTextSettings(final Text text, final List<Line> lines) {
		text.setNumberOfLines(lines.size());

		if (text.isCentered() || lines.size() > 1) {
			text.setOriginalWidth((float) lines.get(0).getMaxLength());
		} else {
			text.setOriginalWidth((float) lines.get(0).getLineLength());
		}
	}

	private void addVerticesForCharacter(final double cursorX, final double cursorY, final Character character, final double fontSize, final List<Float> vertices) {
		final double x = cursorX + character.getXOffset() * fontSize;
		final double y = cursorY + character.getYOffset() * fontSize;
		final double maxX = x + character.getSizeX() * fontSize;
		final double maxY = y + character.getSizeY() * fontSize;
		addVertices(vertices, x, y, maxX, maxY);
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

	private float[] listToArray(final List<Float> list) {
		final float[] array = new float[list.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return array;
	}
}
