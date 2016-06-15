package flounder.fonts;

import flounder.resources.*;

import java.util.*;

public class FontManager {
	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");

	public static final FontType BUNGEE = new FontType(new MyFile(FONTS_LOC, "bungee.png"), new MyFile(FONTS_LOC, "bungee.fnt"));
	public static final FontType COMIC_SANS = new FontType(new MyFile(FONTS_LOC, "comicSans.png"), new MyFile(FONTS_LOC, "comicSans.fnt"));
	public static final FontType FFF_FORWARD = new FontType(new MyFile(FONTS_LOC, "fffForward.png"), new MyFile(FONTS_LOC, "fffForward.fnt"));
	public static final FontType FORTE = new FontType(new MyFile(FONTS_LOC, "forte.png"), new MyFile(FONTS_LOC, "forte.fnt"));
	public static final FontType NEXA_BOLD = new FontType(new MyFile(FONTS_LOC, "nexaBold.png"), new MyFile(FONTS_LOC, "nexaBold.fnt"));
	public static final FontType SEGOE_UI = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));
	public static final FontType SEGOE_UI_BLACK = new FontType(new MyFile(FONTS_LOC, "segoeUIBlack.png"), new MyFile(FONTS_LOC, "segoeUIBlack.fnt"));
	public static final FontType SERIF = new FontType(new MyFile(FONTS_LOC, "serif.png"), new MyFile(FONTS_LOC, "serif.fnt"));
	public static final FontType TREBUCHET = new FontType(new MyFile(FONTS_LOC, "trebuchet.png"), new MyFile(FONTS_LOC, "trebuchet.fnt"));

	private static Map<FontType, List<Text>> texts = new HashMap<>();

	public static Map<FontType, List<Text>> getTexts() {
		return texts;
	}
}
