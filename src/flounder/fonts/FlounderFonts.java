package flounder.fonts;

import flounder.loaders.*;
import flounder.resources.*;

import java.util.*;

public class FlounderFonts {
	private static Map<FontType, List<TextObject>> texts = new HashMap<>();

	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");

	public static void loadText(TextObject text) {
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);


		int vao = FlounderLoader.createInterleavedVAO(data.vertexPositions.length / 2, data.vertexPositions, data.textureCoords);
		text.setMeshInfo(vao, data.vertexPositions.length / 2);
		//int vao = FlounderLoader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		//text.setMeshInfo(vao, data.getVertexCount());

		List<TextObject> textBatch = texts.get(font);

		if (textBatch == null) {
			textBatch = new ArrayList<>();
			texts.put(font, textBatch);
		}

		textBatch.add(text);
	}

	public static void removeText(TextObject text) {
		List<TextObject> textBatch = texts.get(text.getFont());
		textBatch.remove(text);

		if (textBatch.isEmpty()) {
			texts.remove(texts.get(text.getFont()));
		}
	}

	public static Map<FontType, List<TextObject>> getTexts() {
		return texts;
	}
}
