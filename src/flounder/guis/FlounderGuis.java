package flounder.guis;

import flounder.devices.*;
import flounder.events.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.logger.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;

import java.util.*;

/**
 * A module used for that manages GUI textures in a container.
 */
public class FlounderGuis extends IModule {
	private static final FlounderGuis instance = new FlounderGuis();

	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	private IGuiMaster guiMaster;

	private GuiScreenContainer container;
	private List<GuiTexture> guiTextures;
	private GuiSelector selector;

	/**
	 * Creates a new GUI manager.
	 */
	public FlounderGuis() {
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProfiler.class, FlounderEvents.class, FlounderDisplay.class, FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderSound.class, FlounderFonts.class, FlounderLoader.class, FlounderShaders.class, FlounderTextures.class);
		guiMaster = null;
	}

	@Override
	public void init() {
		this.container = new GuiScreenContainer();
		this.guiTextures = new ArrayList<>();
		this.selector = new GuiSelector();
	}

	@Override
	public void update() {
		IGuiMaster newManager = (IGuiMaster) getExtensionMatch(guiMaster, IGuiMaster.class, true);

		if (newManager != null) {
			if (guiMaster != null) {
				guiMaster.dispose();
				guiMaster.setInitialized(false);
			}

			guiMaster = newManager;
		}

		guiTextures.clear();

		if (guiMaster != null) {
			if (!guiMaster.isInitialized()) {
				guiMaster.init();
				guiMaster.setInitialized(true);
			}
		}

		selector.update();
		container.update(guiTextures, FlounderFonts.getTexts());

		if (guiMaster != null) {
			guiMaster.update();
		}
	}

	@Override
	public void profile() {
		FlounderProfiler.add("GUIs", "Textures Count", guiTextures.size());
		FlounderProfiler.add("GUIs", "Selected", guiMaster == null ? "NULL" : guiMaster.getClass());
	}

	public static IGuiMaster getGuiMaster() {
		return instance.guiMaster;
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
		if (guiMaster != null) {
			guiMaster.dispose();
			guiMaster.setInitialized(false);
		}

		guiTextures.forEach(guiTexture -> {
			if (guiTexture != null) {
				guiTexture.getTexture().delete();
			}
		});
		guiTextures.clear();
	}
}
