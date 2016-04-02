package flounder.fonts;

import flounder.resources.*;

import java.util.*;

public class FontManager {
	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");
	public static final FontType SEGOE_UI = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));

	private static Map<FontType, List<Text>> texts;

	public static void init() {
		texts = new HashMap<>();
	}

	public static Map<FontType, List<Text>> getTexts() {
		return texts;
	}
}
