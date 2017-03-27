package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.textures.*;
import flounder.visual.*;

public class GuiButtonText extends ScreenObject {
	private static final float CHANGE_TIME = 0.15f;

	private static final float SCALE_NORMAL = 1.5f;
	private static final float SCALE_SELECTED = 1.75f;

	private final static Colour COLOUR_NORMAL = new Colour(0.0f, 0.0f, 0.0f);

	private final static Sound SOUND_MOUSE_HOVER = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button1.wav"), 0.8f, 1.0f);
	private final static Sound SOUND_MOUSE_LEFT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button2.wav"), 0.8f, 1.0f);
	private final static Sound SOUND_MOUSE_RIGHT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button3.wav"), 0.8f, 1.0f);

	private TextObject textObject;
	private GuiObject guiObject;

	private boolean mouseOver;

	private ListenerBasic listenerLeft;
	private ListenerBasic listenerRight;

	public GuiButtonText(ScreenObject parent, Vector2f position, String text, GuiAlign align) {
		super(parent, position, new Vector2f());

		this.textObject = new TextObject(this, this.getPosition(), text, SCALE_NORMAL, FlounderFonts.CANDARA, 0.36f, align);
		this.textObject.setInScreenCoords(true);
		this.textObject.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.guiObject = new GuiObject(this, this.getPosition(), new Vector2f(), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "buttonText.png")).create(), 1);
		this.guiObject.setInScreenCoords(true);
		this.guiObject.setColourOffset(new Colour());

		this.mouseOver = false;
	}

	public void addLeftListener(ListenerBasic listenerLeft) {
		this.listenerLeft = listenerLeft;
	}

	public void addRightListener(ListenerBasic listenerRight) {
		this.listenerRight = listenerRight;
	}

	@Override
	public void updateObject() {
		if (!isVisible() || getAlpha() == 0.0f) {
			return;
		}

		// Mouse over updates.
		if (FlounderGuis.getSelector().isSelected(textObject) && !mouseOver) {
			FlounderSound.playSystemSound(SOUND_MOUSE_HOVER);
			textObject.setScaleDriver(new SlideDriver(textObject.getScale(), SCALE_SELECTED, CHANGE_TIME));
			mouseOver = true;
		} else if (!FlounderGuis.getSelector().isSelected(textObject) && mouseOver) {
			textObject.setScaleDriver(new SlideDriver(textObject.getScale(), SCALE_NORMAL, CHANGE_TIME));
			mouseOver = false;
		}

		Colour.interpolate(COLOUR_NORMAL, FlounderGuis.getGuiMaster().getPrimaryColour(), (textObject.getScale() - SCALE_NORMAL) / (SCALE_SELECTED - SCALE_NORMAL), guiObject.getColourOffset());

		// Click updates.
		if (FlounderGuis.getSelector().isSelected(textObject) && FlounderGuis.getSelector().wasLeftClick()) {
			FlounderSound.playSystemSound(SOUND_MOUSE_LEFT);

			if (listenerLeft != null) {
				listenerLeft.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}

		if (FlounderGuis.getSelector().isSelected(textObject) && FlounderGuis.getSelector().wasRightClick()) {
			FlounderSound.playSystemSound(SOUND_MOUSE_RIGHT);

			if (listenerRight != null) {
				listenerRight.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}

		// Update background size.
		guiObject.getDimensions().set(textObject.getMeshSize());
		guiObject.getDimensions().y = 0.5f * (float) textObject.getFont().getMaxSizeY();
		Vector2f.multiply(textObject.getDimensions(), guiObject.getDimensions(), guiObject.getDimensions());
		guiObject.getDimensions().scale(2.0f * textObject.getScale());
		guiObject.getPositionOffsets().set(textObject.getPositionOffsets());
		guiObject.getPosition().set(textObject.getPosition());
	}

	public void setText(String text) {
		textObject.setText(text);
	}

	@Override
	public void deleteObject() {
	}

	/**
	 * A simple GUI listener.
	 */
	public interface ListenerBasic {
		/**
		 * Run when a event has occurred.
		 */
		void eventOccurred();
	}
}
