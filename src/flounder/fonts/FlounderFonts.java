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

	public FontType bungee = new FontType(new MyFile(FONTS_LOC, "bungee.png"), new MyFile(FONTS_LOC, "bungee.fnt"));
	public FontType comicSans = new FontType(new MyFile(FONTS_LOC, "comicSans.png"), new MyFile(FONTS_LOC, "comicSans.fnt"));
	public FontType fffForward = new FontType(new MyFile(FONTS_LOC, "fffForward.png"), new MyFile(FONTS_LOC, "fffForward.fnt"));
	public FontType forte = new FontType(new MyFile(FONTS_LOC, "forte.png"), new MyFile(FONTS_LOC, "forte.fnt"));
	public FontType nexaBold = new FontType(new MyFile(FONTS_LOC, "nexaBold.png"), new MyFile(FONTS_LOC, "nexaBold.fnt"));
	public FontType segoeUi = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));
	public FontType segoeUiBlack = new FontType(new MyFile(FONTS_LOC, "segoeUIBlack.png"), new MyFile(FONTS_LOC, "segoeUIBlack.fnt"));
	public FontType serif = new FontType(new MyFile(FONTS_LOC, "serif.png"), new MyFile(FONTS_LOC, "serif.fnt"));
	public FontType trebuchet = new FontType(new MyFile(FONTS_LOC, "trebuchet.png"), new MyFile(FONTS_LOC, "trebuchet.fnt"));
	public FontType brushScript = new FontType(new MyFile(FONTS_LOC, "brushScript.png"), new MyFile(FONTS_LOC, "brushScript.fnt"));

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
