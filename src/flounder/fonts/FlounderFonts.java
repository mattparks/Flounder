package flounder.fonts;

import flounder.engine.*;
import flounder.helpers.*;
import flounder.resources.*;

import java.util.*;

/**
 * A class that holds a list of available engine fonts and texts currently on the screen.
 */
public class FlounderFonts implements IModule {
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

	private Map<FontType, List<Text>> texts;

	/**
	 * Creates a new font manager.
	 */
	public FlounderFonts() {
		texts = new HashMap<>();
	}

	@Override
	public void init() {
		// Creates all font family's that have not been loaded.
		if (FontType.NEEDS_TO_BE_CREATED.size() > 0) {
			FontType.NEEDS_TO_BE_CREATED.forEach(FontType::createLoader);
			FontType.NEEDS_TO_BE_CREATED.clear();
		}
	}

	@Override
	public void update() {
		texts.clear();
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Fonts", "Used Family's", ArrayUtils.totalSecondaryCount(texts));
	}

	/**
	 * Gets a list of the current texts.
	 *
	 * @return The current texts.
	 */
	public Map<FontType, List<Text>> getTexts() {
		return texts;
	}

	@Override
	public void dispose() {
	}
}
