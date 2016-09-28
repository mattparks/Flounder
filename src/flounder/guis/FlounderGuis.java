package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.events.*;
import flounder.fonts.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import java.util.*;

/**
 * A manager that manages GUI textures in a container.
 */
public class FlounderGuis extends IModule {
	private static final FlounderGuis instance = new FlounderGuis();

	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private GuiScreenContainer container;
	private List<GuiTexture> guiTextures;
	private GuiSelector selector;

	/**
	 * Creates a new GUI manager.
	 */
	public FlounderGuis() {
		super(ModuleUpdate.BEFORE_ENTRANCE, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderSound.class, FlounderEvents.class, FlounderLoader.class, FlounderShaders.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.container = new GuiScreenContainer();
		this.guiTextures = new ArrayList<>();
		this.selector = new GuiSelector();
	}

	@Override
	public void run() {
		guiTextures.clear();
		selector.update();
		container.update(guiTextures, FlounderFonts.getTexts());
	}

	@Override
	public void profile() {
		FlounderProfiler.add("GUIs", "Textures Count", guiTextures.size());
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
	public static void addComponent(GuiComponent component, float relX, float relY, float relScaleX, float relScaleY) {
		instance.container.addComponent(component, relX, relY, relScaleX, relScaleY);
	}

	/**
	 * Gets a list of all the renerable GUI textures.
	 *
	 * @return List of GUI textures.
	 */
	public static List<GuiTexture> getGuiTextures() {
		return instance.guiTextures;
	}

	/**
	 * Gets the main GUI selector.
	 *
	 * @return The GUI selector.
	 */
	public static GuiSelector getSelector() {
		return instance.selector;
	}

	@Override
	public IModule getInstance() {
		return instance;
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
