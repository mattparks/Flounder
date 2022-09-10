package com.flounder.guis;

import com.flounder.devices.*;
import com.flounder.events.*;
import com.flounder.framework.*;
import com.flounder.maths.vectors.*;
import com.flounder.resources.*;
import com.flounder.tasks.*;
import com.flounder.textures.*;

import java.util.*;

/**
 * A module used for that manages GUI textures in a container.
 */
public class FlounderGuis extends com.flounder.framework.Module {
	public static final MyFile GUIS_LOC = new MyFile(MyFile.RES_FOLDER, "guis");

	public static final float POSITION_MIN = 0.0f;
	public static final float POSITION_MAX = 1.0f;
	public static final float[] POSITIONS = {POSITION_MIN, POSITION_MIN, POSITION_MIN, POSITION_MAX, POSITION_MAX, POSITION_MIN, POSITION_MAX, POSITION_MAX};

	private GuiMaster guiMaster;
	private GuiSelector selector;
	private ScreenObject container;
	private List<ScreenObject> objects;
	private float guiScale;

	/**
	 * Creates a new GUI manager.
	 */
	public FlounderGuis() {
		super(FlounderEvents.class, FlounderTasks.class, FlounderDisplay.class, FlounderJoysticks.class, FlounderKeyboard.class, FlounderMouse.class, FlounderSound.class, FlounderTextures.class);
		guiMaster = null;
	}

	@Handler.Function(Handler.FLAG_INIT)
	public void init() {
		this.selector = new GuiSelector();
		this.container = new ScreenObjectEmpty(null, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f), false);
		this.objects = new ArrayList<>();
		this.guiScale = 1.0f;
		update();
	}

	@Handler.Function(Handler.FLAG_UPDATE_PRE)
	public void update() {
		GuiMaster newManager = (GuiMaster) getExtensionMatch(guiMaster, GuiMaster.class, true);
		cancelChange();

		if (newManager != null) {
			if (guiMaster != null) {
				guiMaster.dispose();
				guiMaster.setInitialized(false);
			}

			guiMaster = newManager;
		}

		if (guiMaster != null) {
			if (!guiMaster.isInitialized()) {
				guiMaster.init();
				guiMaster.setInitialized(true);
			}
		}

		selector.update();
		container.update();

		if (guiMaster != null) {
			guiMaster.update();
		}

		objects.clear();
		container.getAll(objects);
	}

	/**
	 * Gets the GUI master.
	 *
	 * @return The GUI master.
	 */
	public GuiMaster getGuiMaster() {
		return this.guiMaster;
	}

	/**
	 * Gets the screen container.
	 *
	 * @return The screen container.
	 */
	public ScreenObject getContainer() {
		return this.container;
	}

	/**
	 * The rendering objects from the container. Updated each update.
	 *
	 * @return The objects.
	 */
	public List<ScreenObject> getObjects() {
		return objects;
	}

	/**
	 * Gets the main GUI selector.
	 *
	 * @return The GUI selector.
	 */
	public GuiSelector getSelector() {
		return this.selector;
	}

	public float getGuiScale() {
		return guiScale;
	}

	public void setGuiScale(float guiScale) {
		this.guiScale = guiScale;
	}

	@Handler.Function(Handler.FLAG_DISPOSE)
	public void dispose() {
		if (guiMaster != null) {
			guiMaster.dispose();
			guiMaster.setInitialized(false);
		}

		container.delete();
	}

	@com.flounder.framework.Module.Instance
	public static FlounderGuis get() {
		return (FlounderGuis) Framework.get().getModule(FlounderGuis.class);
	}
}
