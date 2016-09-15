package flounder.guis.cursor;

import flounder.engine.*;
import flounder.guis.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

/**
 * A manager that manages a cursor.
 */
public class FlounderCursor implements IModule {
	private boolean isShown;

	private GuiTexture cursorTexture;
	private Colour inactiveColour;
	private Colour clickLeftColour;
	private Colour clickRightColour;

	/**
	 * Creates a new cursor manager.
	 */
	public FlounderCursor() {
	}

	@Override
	public void init() {
		isShown = false;

		cursorTexture = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "cursor.png")).createInSecondThread(), false);
		cursorTexture.getTexture().setNumberOfRows(1);
		cursorTexture.setSelectedRow(1);
		inactiveColour = new Colour(0.0f, 0.0f, 0.0f);
		clickLeftColour = new Colour(0.0f, 0.0f, 0.8f);
		clickRightColour = new Colour(0.8f, 0.0f, 0.0f);
	}

	@Override
	public void update() {
		if (isShown) {
			float averageArea = (FlounderEngine.getDevices().getDisplay().getWidth() + FlounderEngine.getDevices().getDisplay().getHeight()) / 2.0f;

			cursorTexture.setColourOffset(FlounderEngine.getGuis().getSelector().isLeftClick() ? clickLeftColour : FlounderEngine.getGuis().getSelector().isRightClick() ? clickRightColour : inactiveColour);
			cursorTexture.setPosition(FlounderEngine.getGuis().getSelector().getCursorX(), FlounderEngine.getGuis().getSelector().getCursorY(), (33.75f / averageArea), (33.75f / averageArea) * FlounderEngine.getDevices().getDisplay().getAspectRatio());
			cursorTexture.update();
		}
	}

	@Override
	public void profile() {
		FlounderEngine.getProfiler().add("Cursor", "Position X", FlounderEngine.getGuis().getSelector().getCursorX());
		FlounderEngine.getProfiler().add("Cursor", "Position Y", FlounderEngine.getGuis().getSelector().getCursorY());
	}

	public boolean isShown() {
		return isShown;
	}

	public void show(boolean shown) {
		isShown = shown;
	}

	public GuiTexture getCursorTexture() {
		return cursorTexture;
	}

	public int getTotalRows() {
		return cursorTexture.getTexture().getNumberOfRows();
	}

	public Colour getInactiveColour() {
		return inactiveColour;
	}

	public void setInactiveColour(float r, float g, float b) {
		this.inactiveColour.set(r, g, b);
	}

	public Colour getClickLeftColour() {
		return clickLeftColour;
	}

	public void setClickLeftColour(float r, float g, float b) {
		this.clickLeftColour.set(r, g, b);
	}

	public Colour getClickRightColour() {
		return clickRightColour;
	}

	public void setClickRightColour(float r, float g, float b) {
		this.clickRightColour.set(r, g, b);
	}

	public void setAlphaDriver(ValueDriver alphaDriver) {
		//cursorTexture.setAlphaDriver(alphaDriver);
	}

	@Override
	public void dispose() {
	}
}
