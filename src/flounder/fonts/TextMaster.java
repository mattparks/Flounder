package flounder.fonts;

import flounder.loaders.*;

import java.util.*;

public class TextMaster {
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();

	public static void init(){
	}

	public static void loadText(GUIText text){
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);


		int vao = FlounderLoader.createInterleavedVAO(data.vertexPositions.length / 2, data.vertexPositions, data.textureCoords);
		text.setMeshInfo(vao, data.vertexPositions.length / 2);
		//int vao = FlounderLoader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		//text.setMeshInfo(vao, data.getVertexCount());

		List<GUIText> textBatch = texts.get(font);
		if(textBatch == null){
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public static void removeText(GUIText text){
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);

		if(textBatch.isEmpty()){
			texts.remove(texts.get(text.getFont()));
		}
	}
}
