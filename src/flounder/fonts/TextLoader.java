package flounder.fonts;

import flounder.loaders.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

/**
 * A loader capable of loading font data into a instance of a text mesh.
 */
public class TextLoader {
	protected static final double LINE_HEIGHT = 0.03f;
	protected static final int NEWLINE_ASCII = 10;
	protected static final int SPACE_ASCII = 32;

	private TextureObject fontTexture;
	private MetaFile metaData;

	/**
	 * Creates a new text loader.
	 *
	 * @param textureFile The file for the font atlas texture.
	 * @param fontFile The font file containing information about each character in the texture atlas.
	 */
	protected TextLoader(MyFile textureFile, MyFile fontFile) {
		this.fontTexture = TextureFactory.newBuilder().setFile(textureFile).create(); // noFiltering().clampEdges().
		this.metaData = new MetaFile(fontFile);
	}

	/**
	 * Gets the loaded texture atlas for this font.
	 *
	 * @return The fonts texture atlas.
	 */
	protected TextureObject getFontTexture() {
		return fontTexture;
	}

	/**
	 * Creates a mesh for the provided text object using the meta data for this font. Then takes the data created for the text mesh and stores it in OpenGL.
	 *
	 * @param text The text object to create a mesh for.
	 */
	protected void loadTextMesh(TextObject text) {
		List<Line> lines = createStructure(text);
		TextMeshData meshData = createQuadVertices(text, lines);
		int vao = FlounderLoader.createInterleavedVAO(meshData.vertices.length / 2, meshData.vertices, meshData.textures);
		text.setMeshInfo(vao, meshData.vertices.length / 2);
	}

	private List<Line> createStructure(TextObject text) {
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

	private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, TextObject text) {
		boolean added = currentLine.attemptToAddWord(currentWord);

		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}

		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(TextObject text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double curserX = 0.0;
		double curserY = 0.0;

		List<Float> vertices = new ArrayList<>();
		List<Float> textures = new ArrayList<>();

		for (Line line : lines) {
			if (text.isCentered()) {
				curserX = (line.maxLength - line.currentLineLength) / 2;
			}

			for (Word word : line.words) {
				for (Character letter : word.characters) {
					addVerticesForCharacter(curserX, curserY, letter, text.getFontSize(), vertices);
					addTextures(textures, letter.xTextureCoord, letter.yTextureCoord, letter.xMaxTextureCoord, letter.yMaxTextureCoord);
					curserX += letter.xAdvance * text.getFontSize();
				}

				curserX += metaData.getSpaceWidth() * text.getFontSize();
			}

			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSize();
		}

		return new TextMeshData(listToArray(vertices), listToArray(textures));
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

	private static void addTextures(List<Float> textures, double x, double y, double maxX, double maxY) {
		textures.add((float) x);
		textures.add((float) y);
		textures.add((float) x);
		textures.add((float) maxY);
		textures.add((float) maxX);
		textures.add((float) maxY);
		textures.add((float) maxX);
		textures.add((float) maxY);
		textures.add((float) maxX);
		textures.add((float) y);
		textures.add((float) x);
		textures.add((float) y);
	}


	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}

		return array;
	}

	/**
	 * Stores the vertex data for all the quads on which a text will be rendered.
	 */
	public class TextMeshData {
		protected final float[] vertices;
		protected final float[] textures;

		protected TextMeshData(float[] vertices, float[] textures) {
			this.vertices = vertices;
			this.textures = textures;
		}
	}
}
