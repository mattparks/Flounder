package flounder.guis;

import flounder.fonts.*;
import flounder.resources.*;

import java.util.*;

public class GuiManager {
	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private static final GuiScreenContainer container = new GuiScreenContainer();
	private static final List<GuiTexture> guiTextures = new ArrayList<>();

	public static void updateGuis() {
		guiTextures.clear();
		FontManager.getTexts().clear();
		container.update(guiTextures, FontManager.getTexts());
	}

	public static void addComponent(final GuiComponent component, final float relX, final float relY, final float relScaleX, final float relScaleY) {
		container.addComponent(component, relX, relY, relScaleX, relScaleY);
	}

	public static List<GuiTexture> getGuiTextures() {
		return guiTextures;
	}
}
