package flounder.fonts;

import flounder.devices.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

/**
 * A module used for holding a list of available engine fonts and texts currently on the screen.
 */
public class FlounderFonts extends Module {
	private static final FlounderFonts INSTANCE = new FlounderFonts();
	public static final String PROFILE_TAB_NAME = "Fonts";

	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");

	public static final FontType ARIAL = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "arial.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "arial.fnt"));
	public static final FontType BERLIN_SANS = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "berlinSans.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "berlinSans.fnt"));
	public static final FontType CANDARA = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "candara.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "candara.fnt"));
	public static final FontType SEGOE = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "segoe.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "segoe.fnt"));

	/**
	 * Creates a new font manager.
	 */
	public FlounderFonts() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderLoader.class, FlounderShaders.class, FlounderTextures.class);
	}

	@Override
	public void init() {
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
