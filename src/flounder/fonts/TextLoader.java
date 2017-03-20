package flounder.fonts;

import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class TextLoader {
	protected static final double LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private TextureObject fontTexture;
	private MetaFile metaData;

	protected TextLoader(MyFile fontSheet, MyFile metaFile) {
		this.fontTexture = TextureFactory.newBuilder().setFile(fontSheet).noFiltering().clampEdges().create();
		this.metaData = new MetaFile(metaFile);
	}

	public TextureObject getFontTexture() {
		return fontTexture;
	}

	protected TextMeshData createTextMesh(Text text) {
		List<Line> lines = createStructure(text);
		TextMeshData data = createQuadVertices(text, lines);
		return data;
	}

	private List<Line> createStructure(Text text) {
		char[] chars = text.getTextString().toCharArray();
		List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());

		for (char c : chars) {
			int ascii = (int) c;

			if (ascii == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);

				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}

				currentWord = new Word(text.getFontSize());
				continue;
			}

			Character character = metaData.getCharacter(ascii);
			currentWord.addCharacter(character);
		}

		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, Text text) {
		boolean added = currentLine.attemptToAddWord(currentWord);

		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}

		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(Text text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double curserX = 0f;
		double curserY = 0f;

		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoords = new ArrayList<>();

		for (Line line : lines) {
			if (text.isCentered()) {
				curserX = (line.maxLength - line.currentLineLength) / 2;
			}

			for (Word word : line.words) {
				for (Character letter : word.characters) {
					addVerticesForCharacter(curserX, curserY, letter, text.getFontSize(), vertices);
					addTexCoords(textureCoords, letter.xTextureCoord, letter.yTextureCoord, letter.xMaxTextureCoord, letter.yMaxTextureCoord);
					curserX += letter.xAdvance * text.getFontSize();
				}

				curserX += metaData.getSpaceWidth() * text.getFontSize();
			}

			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSize();
		}

		return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
	}

	private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize, List<Float> vertices) {
		double x = curserX + (character.xOffset * fontSize);
		double y = curserY + (character.yOffset * fontSize);
		double maxX = x + (character.sizeX * fontSize);
		double maxY = y + (character.sizeY * fontSize);
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
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

	private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {
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


	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}

		return array;
	}
}
