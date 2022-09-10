package com.flounder.fonts;

import com.flounder.devices.*;
import com.flounder.framework.*;
import com.flounder.loaders.*;
import com.flounder.resources.*;
import com.flounder.textures.*;

/**
 * A module used for holding a list of available engine fonts and texts currently on the screen.
 */
public class FlounderFonts extends com.flounder.framework.Module {
	public static final MyFile FONTS_LOC = new MyFile(MyFile.RES_FOLDER, "fonts");

	public static final FontType ARIAL = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "arial.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "arial.fnt"));
	public static final FontType BERLIN_SANS = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "berlinSans.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "berlinSans.fnt"));
	public static final FontType CAFE_FRANCOISE = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "cafefrancoise.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "cafefrancoise.fnt"));
	public static final FontType CANDARA = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "candara.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "candara.fnt"));
	public static final FontType SEGOE = new FontType(new MyFile(MyFile.RES_FOLDER, "fonts", "segoe.png"), new MyFile(MyFile.RES_FOLDER, "fonts", "segoe.fnt"));

	/**
	 * Creates a new font manager.
	 */
	public FlounderFonts() {
		super(FlounderDisplay.class, FlounderLoader.class, FlounderTextures.class);
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
	}

	@com.flounder.framework.Module.Instance
	public static FlounderFonts get() {
		return (FlounderFonts) Framework.get().getModule(FlounderFonts.class);
	}
}
