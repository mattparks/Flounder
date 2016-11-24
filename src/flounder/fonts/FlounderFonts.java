package flounder.fonts;

import flounder.devices.*;
import flounder.framework.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import java.util.*;

/**
 * A class that holds a list of available engine fonts and texts currently on the screen.
 */
public class FlounderFonts extends IModule {
	private static final FlounderFonts instance = new FlounderFonts();

	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");

	public static final FontType BUNGEE = new FontType(new MyFile(FONTS_LOC, "bungee.png"), new MyFile(FONTS_LOC, "bungee.fnt"));
	public static final FontType COMIC_SANS = new FontType(new MyFile(FONTS_LOC, "comicSans.png"), new MyFile(FONTS_LOC, "comicSans.fnt"));
	public static final FontType FFF_FORWARD = new FontType(new MyFile(FONTS_LOC, "fffForward.png"), new MyFile(FONTS_LOC, "fffForward.fnt"));
	public static final FontType FORTE = new FontType(new MyFile(FONTS_LOC, "forte.png"), new MyFile(FONTS_LOC, "forte.fnt"));
	public static final FontType NEXA_BOLD = new FontType(new MyFile(FONTS_LOC, "nexaBold.png"), new MyFile(FONTS_LOC, "nexaBold.fnt"));
	public static final FontType SEGO_UI = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));
	public static final FontType SEGOE_UI_BLACK = new FontType(new MyFile(FONTS_LOC, "segoeUIBlack.png"), new MyFile(FONTS_LOC, "segoeUIBlack.fnt"));
	public static final FontType SERIF = new FontType(new MyFile(FONTS_LOC, "serif.png"), new MyFile(FONTS_LOC, "serif.fnt"));
	public static final FontType TREBUCHET = new FontType(new MyFile(FONTS_LOC, "trebuchet.png"), new MyFile(FONTS_LOC, "trebuchet.fnt"));
	public static final FontType BRUSH_SCRIPT = new FontType(new MyFile(FONTS_LOC, "brushScript.png"), new MyFile(FONTS_LOC, "brushScript.fnt"));

	private Map<FontType, List<Text>> texts;

	/**
	 * Creates a new font manager.
	 */
	public FlounderFonts() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderLoader.class, FlounderShaders.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.texts = new HashMap<>();

		// Creates all font family's that have not been loaded.
		if (FontType.NEEDS_TO_BE_CREATED.size() > 0) {
			FontType.NEEDS_TO_BE_CREATED.forEach(FontType::createLoader);
			FontType.NEEDS_TO_BE_CREATED.clear();
		}
	}

	@Override
	public void run() {
		if (!FlounderModules.containsModule(FlounderGuis.class)) {
			texts.clear();
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("Fonts", "Used Family's", ArrayUtils.totalSecondaryCount(texts));
	}

	/**
	 * Gets a list of the current texts.
	 *
	 * @return The current texts.
	 */
	public static Map<FontType, List<Text>> getTexts() {
		return instance.texts;
	}

	@Override
	public IModule getInstance() {
		return instance;
	}

	@Override
	public void dispose() {
		texts.clear();
		FontType.NEEDS_TO_BE_CREATED.clear();
		FontType.NEEDS_TO_BE_CREATED.addAll(FontType.ALL_FONT_TYPES);
	}
}
