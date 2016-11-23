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
 * A manager that manages GUI textures in a container.
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
		super(ModuleUpdate.UPDATE_PRE, FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class, FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderSound.class, FlounderEvents.class, FlounderLoader.class, FlounderFonts.class, FlounderShaders.class, FlounderTextures.class);
		guiMaster = null;
	}

	@Override
	public void init() {
		if (guiMaster != null) {
			guiMaster.init();
			((IExtension) guiMaster).setInitialized(true);
		}

		this.container = new GuiScreenContainer();
		this.guiTextures = new ArrayList<>();
		this.selector = new GuiSelector();
	}

	@Override
	public void run() {
		List<IExtension> guiExtensions = null;

		for (IExtension extension : FlounderFramework.getExtensions()) {
			if (extension instanceof IGuiMaster) {
				guiExtensions = new ArrayList<>();
				guiExtensions.add(extension);
			}
		}

		if (guiExtensions != null && !guiExtensions.isEmpty()) {
			for (IExtension extension : guiExtensions) {
				IGuiMaster newManager = (IGuiMaster) extension;

				if (newManager.isActive() && !newManager.equals(guiMaster)) {
					if (guiMaster != null) {
						((IExtension) guiMaster).setInitialized(false);
					}

					guiMaster = newManager;

					if (!extension.isInitialized()) {
						guiMaster.init();
						((IExtension) guiMaster).setInitialized(true);
					}

					break;
				}
			}
		}

		guiTextures.clear();
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
			((IExtension) guiMaster).setInitialized(false);
		}

		guiTextures.forEach(guiTexture -> {
			if (guiTexture != null) {
				guiTexture.getTexture().delete();
			}
		});
		guiTextures.clear();
	}
}
