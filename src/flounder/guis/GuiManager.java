package flounder.guis;

import flounder.fonts.*;
import flounder.resources.*;

import java.util.*;

public class GuiManager {
	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private static final GuiScreenContainer m_container = new GuiScreenContainer();
	private static final List<GuiTexture> m_guiTextures = new ArrayList<>();
	private static final Map<FontType, List<Text>> m_texts = new HashMap<>();

	public static void updateGuis() {
		m_guiTextures.clear();
		m_texts.clear();
		m_container.update(m_guiTextures, m_texts);
	}

	public static void addComponent(GuiComponent component, float relX, float relY, float relScaleX, float relScaleY) {
		m_container.addComponent(component, relX, relY, relScaleX, relScaleY);
	}

	public static Map<FontType, List<Text>> getTexts() {
		return m_texts;
	}

	public static List<GuiTexture> getGuiTextures() {
		return m_guiTextures;
	}
}
