package flounder.guis;

import flounder.fonts.*;
import flounder.resources.*;

import java.util.*;

public class GuiManager {
	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private static GuiScreenContainer container = new GuiScreenContainer();
	private static List<GuiTexture> guiTextures = new ArrayList<>();
	private static GuiSelector selector = new GuiSelector();

	public static void updateGuis() {
		guiTextures.clear();
		FontManager.getTexts().clear();
		selector.update();
		container.update(guiTextures, FontManager.getTexts());
	}

	public static void addComponent(GuiComponent component, float relX, float relY, float relScaleX, float relScaleY) {
		container.addComponent(component, relX, relY, relScaleX, relScaleY);
	}

	public static List<GuiTexture> getGuiTextures() {
		return guiTextures;
	}

	public static GuiSelector getSelector() {
		return selector;
	}
}
