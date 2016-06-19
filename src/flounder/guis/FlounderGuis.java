package flounder.guis;

import flounder.engine.*;
import flounder.resources.*;

import java.util.*;

/**
 * A manager that manages GUI textures in a container.
 */
public class FlounderGuis implements IModule {
	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private GuiScreenContainer container;
	private List<GuiTexture> guiTextures;
	private GuiSelector selector;

	/**
	 * Creates a new GUI manager.
	 */
	public FlounderGuis() {
	}

	@Override
	public void init() {
		container = new GuiScreenContainer();
		guiTextures = new ArrayList<>();
		selector = new GuiSelector();
	}

	@Override
	public void update() {
		guiTextures.clear();
		selector.update();
		container.update(guiTextures, FlounderEngine.getFonts().getTexts());
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("GUIs", "Textures Count", guiTextures.size());
	}

	/**
	 * Adds a component to the screen container.
	 *
	 * @param component The component to add,
	 * @param relX The X pos relative to the container.
	 * @param relY The Y pos relative to the container.
	 * @param relScaleX The X scale relative to the container.
	 * @param relScaleY The Y scale relative to the container.
	 */
	public void addComponent(GuiComponent component, float relX, float relY, float relScaleX, float relScaleY) {
		container.addComponent(component, relX, relY, relScaleX, relScaleY);
	}

	/**
	 * Gets a list of all the renerable GUI textures.
	 *
	 * @return List of GUI textures.
	 */
	public List<GuiTexture> getGuiTextures() {
		return guiTextures;
	}

	/**
	 * Gets the main GUI selector.
	 *
	 * @return The GUI selector.
	 */
	public GuiSelector getSelector() {
		return selector;
	}

	@Override
	public void dispose() {
		guiTextures.forEach(guiTexture -> {
			if (guiTexture != null) {
				guiTexture.getTexture().delete();
			}
		});
	}
}
