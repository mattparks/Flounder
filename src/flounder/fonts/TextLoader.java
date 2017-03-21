package flounder.fonts;

import flounder.loaders.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
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

		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		int i = 0;

		for (float v : meshData.vertices) {
			if (i == 0) {
				if (v < minX) {
					minX = v;
				} else if (v > maxX) {
					maxX = v;
				}

				i++;
			} else if (i == 1) {
				if (v < minY) {
					minY = v;
				} else if (v > maxY) {
					maxY = v;
				}

				i = 0;
			}
		}
		FlounderLogger.error("===(" + minX + ", " + minY + "), (" + maxX + ", " + maxY + ")===");

		int vao = FlounderLoader.createInterleavedVAO(meshData.vertices.length / 2, meshData.vertices, meshData.textures);
		text.setMeshInfo(vao, meshData.vertices.length / 2);
		text.setDimensions(new Vector2f(Math.abs(minX) + Math.abs(maxX), Math.abs(minY) + Math.abs(maxY)));
	}

	private List<Line> createStructure(TextObject text) {
		char[] chars = text.getTextString().toCharArray();
		List<Line> lines = new ArrayList<>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getMaxLineSize());
		Word currentWord = new Word();

		for (char c : chars) {
			int ascii = (int) c;

			if (ascii == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);

				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}

				currentWord = new Word();
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
			currentLine = new Line(metaData.getSpaceWidth(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}

		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(TextObject text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double cursorX = 0.0;
		double cursorY = 0.0;

		List<Float> vertices = new ArrayList<>();
		List<Float> textures = new ArrayList<>();

		for (Line line : lines) {
			if (text.isCentered()) {
				cursorX = (line.maxLength - line.currentLineLength) / 2;
			}

			for (Word word : line.words) {
				for (Character letter : word.characters) {
					addVerticesForCharacter(cursorX, cursorY, letter, vertices);
					addTextures(textures, letter.xTextureCoord, letter.yTextureCoord, letter.xMaxTextureCoord, letter.yMaxTextureCoord);
					cursorX += letter.xAdvance;
				}

				cursorX += metaData.getSpaceWidth();
			}

			cursorX = 0;
			cursorY += LINE_HEIGHT;
		}

		return new TextMeshData(listToArray(vertices), listToArray(textures));
	}

	private void addVerticesForCharacter(double cursorX, double cursorY, Character character, List<Float> vertices) {
		double x = cursorX + character.xOffset;
		double y = cursorY + character.yOffset;
		double maxX = x + character.sizeX;
		double maxY = y + character.sizeY;
		double properX = (2.0 * x) - 1.0;
		double properY = (-2.0 * y) + 1.0;
		double properMaxX = (2.0 * maxX) - 1.0;
		double properMaxY = (-2.0 * maxY) + 1.0;
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
