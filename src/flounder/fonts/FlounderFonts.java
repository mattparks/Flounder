package flounder.fonts;

import flounder.devices.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import java.util.*;

/**
 * A module used for holding a list of available engine fonts and texts currently on the screen.
 */
public class FlounderFonts extends Module {
	private static final FlounderFonts INSTANCE = new FlounderFonts();
	public static final String PROFILE_TAB_NAME = "Fonts";

	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");

	public static final FontType BUNGEE = new FontType(new MyFile(FONTS_LOC, "bungee.png"), new MyFile(FONTS_LOC, "bungee.fnt"));
	public static final FontType COMIC_SANS = new FontType(new MyFile(FONTS_LOC, "comicSans.png"), new MyFile(FONTS_LOC, "comicSans.fnt"));
	public static final FontType FFF_FORWARD = new FontType(new MyFile(FONTS_LOC, "fffForward.png"), new MyFile(FONTS_LOC, "fffForward.fnt"));
	public static final FontType FORTE = new FontType(new MyFile(FONTS_LOC, "forte.png"), new MyFile(FONTS_LOC, "forte.fnt"));
	public static final FontType NEXA_BOLD = new FontType(new MyFile(FONTS_LOC, "nexaBold.png"), new MyFile(FONTS_LOC, "nexaBold.fnt"));

	public static final FontType OPEN_SANS = new FontType(new MyFile(FONTS_LOC, "openSans.png"), new MyFile(FONTS_LOC, "openSans.fnt"));
	public static final FontType OPEN_SANS_EXTRABOLD = new FontType(new MyFile(FONTS_LOC, "openSansExtrabold.png"), new MyFile(FONTS_LOC, "openSansExtrabold.fnt"));
	public static final FontType OPEN_SANS_LIGHT = new FontType(new MyFile(FONTS_LOC, "openSansLight.png"), new MyFile(FONTS_LOC, "openSansLight.fnt"));
	public static final FontType OPEN_SANS_SEMIBOLD = new FontType(new MyFile(FONTS_LOC, "openSansSemibold.png"), new MyFile(FONTS_LOC, "openSansSemibold.fnt"));

	public static final FontType SEGOE_UI = new FontType(new MyFile(FONTS_LOC, "segoeUI.png"), new MyFile(FONTS_LOC, "segoeUI.fnt"));
	public static final FontType SEGOE_UI_BLACK = new FontType(new MyFile(FONTS_LOC, "segoeUIBlack.png"), new MyFile(FONTS_LOC, "segoeUIBlack.fnt"));
	public static final FontType SEGOE_UI_SEMIBOLD = new FontType(new MyFile(FONTS_LOC, "segoeUISemibold.png"), new MyFile(FONTS_LOC, "segoeUISemibold.fnt"));

	public static final FontType SERIF = new FontType(new MyFile(FONTS_LOC, "serif.png"), new MyFile(FONTS_LOC, "serif.fnt"));
	public static final FontType TREBUCHET = new FontType(new MyFile(FONTS_LOC, "trebuchet.png"), new MyFile(FONTS_LOC, "trebuchet.fnt"));
	public static final FontType BRUSH_SCRIPT = new FontType(new MyFile(FONTS_LOC, "brushScript.png"), new MyFile(FONTS_LOC, "brushScript.fnt"));

	private Map<FontType, List<Text>> texts;

	/**
	 * Creates a new font manager.
	 */
	public FlounderFonts() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderLoader.class, FlounderShaders.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.texts = new HashMap<>();
	}

	@Override
	public void update() {
		// Creates all font family's that have not been loaded.
		if (FontType.NEEDS_TO_BE_CREATED.size() > 0) {
			FontType.NEEDS_TO_BE_CREATED.forEach(FontType::createLoader);
			FontType.NEEDS_TO_BE_CREATED.clear();
		}

		texts.clear();
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Used Family's", ArrayUtils.totalSecondaryCount(texts));
	}

	/**
	 * Gets a list of the current texts.
	 *
	 * @return The current texts.
	 */
	public static Map<FontType, List<Text>> getTexts() {
		return INSTANCE.texts;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		texts.clear();
		FontType.NEEDS_TO_BE_CREATED.clear();
		FontType.NEEDS_TO_BE_CREATED.addAll(FontType.ALL_FONT_TYPES);
	}
}
