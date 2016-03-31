package flounder.guis;

import flounder.fonts.*;
import flounder.resources.*;

import java.util.*;

public class GuiManager {
	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private static final GuiScreenContainer container = new GuiScreenContainer();
	private static final List<GuiTexture> guiTextures = new ArrayList<>();
	private static final Map<FontType, List<Text>> texts = new HashMap<>();

	public static void updateGuis() {
		guiTextures.clear();
		texts.clear();
		container.update(guiTextures, texts);
	}

	public static Map<FontType, List<Text>> getTexts() {
		return texts;
	}

	public static List<GuiTexture> getGuiTextures() {
		return guiTextures;
	}

	public static void addComponent(GuiComponent component, float relX, float relY, float relScaleX, float relScaleY) {
		container.addComponent(component, relX, relY, relScaleX, relScaleY);
	}
}
