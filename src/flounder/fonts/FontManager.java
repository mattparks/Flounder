package flounder.fonts;

import flounder.resources.*;

import java.util.*;

public class FontManager {
	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");
	public static final FontType NEXA_BOLD = new FontType(new MyFile(FONTS_LOC, "nexaBold.png"), new MyFile(FONTS_LOC, "nexaBold.fnt"));
	public static final FontType SEGOE_UI = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));
	public static final FontType FFF_FORWARD = new FontType(new MyFile(FONTS_LOC, "fffForward.png"), new MyFile(FONTS_LOC, "fffForward.fnt"));

	private static final Map<FontType, List<Text>> texts = new HashMap<>();

	public static Map<FontType, List<Text>> getTexts() {
		return texts;
	}
}
