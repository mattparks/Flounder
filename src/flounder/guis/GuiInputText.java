package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.textures.*;
import flounder.visual.*;

import static flounder.platform.Constants.*;

public class GuiInputText extends ScreenObject {
	protected static final float CHANGE_TIME = 0.1f;

	protected static final float SCALE_NORMAL = 1.6f;
	protected static final float SCALE_SELECTED = 1.8f;

	protected final static Colour COLOUR_NORMAL = new Colour(0.0f, 0.0f, 0.0f);

	protected final static Sound SOUND_MOUSE_HOVER = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button1.wav"), 0.8f, 1.0f);
	protected final static Sound SOUND_MOUSE_LEFT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button2.wav"), 0.8f, 1.0f);

	private static final TextureObject TEXTURE_BACKGROUND = TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "buttonText.png")).create();

	private TextObject text;
	private GuiObject background;

	private String prefix;
	private String value;

	private InputDelay inputDelay;
	private int lastKey;

	private boolean selected;
	private boolean mouseOver;
	private ScreenListener listenerChange;

	public GuiInputText(ScreenObject parent, Vector2f position, String prefix, String value, GuiAlign align) {
		super(parent, position, new Vector2f());

		this.text = new TextObject(this, this.getPosition(), prefix + value, SCALE_NORMAL, FlounderFonts.CANDARA, 0.36f, align);
		this.text.setInScreenCoords(true);
		this.text.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.background = new GuiObject(this, this.getPosition(), new Vector2f(), TEXTURE_BACKGROUND, 1);
		this.background.setInScreenCoords(true);
		this.background.setColourOffset(new Colour());

		this.prefix = prefix;
		this.value = value;

		this.inputDelay = new InputDelay();
		this.lastKey = 0;

		this.selected = false;
		this.mouseOver = false;
		this.listenerChange = null;
	}

	public void addChangeListener(ScreenListener listenerChange) {
		this.listenerChange = listenerChange;
	}

	@Override
	public void updateObject() {
		if (!isVisible() || getAlpha() == 0.0f) {
			return;
		}

		if (selected) {
			int key = FlounderKeyboard.get().getKeyboardChar();

			// TODO: Fix inputs that are not GLFW defined.
			if (key != 0 && FlounderKeyboard.get().getKey(java.lang.Character.toUpperCase(key))) {
				inputDelay.update(true);

				if (lastKey != key || inputDelay.canInput()) {
					value += ((char) key);
					text.setText(prefix + value);

					if (listenerChange != null) {
						listenerChange.eventOccurred();
					}

					lastKey = key;
				}
			} else if (FlounderKeyboard.get().getKey(GLFW_KEY_BACKSPACE)) {
				inputDelay.update(true);

				if (lastKey != 8 || inputDelay.canInput()) {
					if (value.length() - 1 >= 0) {
						value = value.substring(0, value.length() - 1);
						text.setText(prefix + value);

						if (listenerChange != null) {
							listenerChange.eventOccurred();
						}

						lastKey = 8;
					}
				}
			} else if (FlounderKeyboard.get().getKey(GLFW_KEY_ENTER) && lastKey != 13) {
				inputDelay.update(true);

				selected = false;
				text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_NORMAL, CHANGE_TIME));
			} else {
				inputDelay.update(false);
				lastKey = 0;
			}
		}

		// Click updates.
		if (FlounderGuis.get().getSelector().isSelected(text) && getAlpha() == 1.0f && FlounderGuis.get().getSelector().wasLeftClick()) {
			FlounderSound.get().playSystemSound(SOUND_MOUSE_LEFT);

			text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_SELECTED, CHANGE_TIME));
			selected = true;

			FlounderGuis.get().getSelector().cancelWasEvent();
		} else if (FlounderGuis.get().getSelector().wasLeftClick() && selected) {
			text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_NORMAL, CHANGE_TIME));
			selected = false;
		}

		// Mouse over updates.
		if (FlounderGuis.get().getSelector().isSelected(text) && !mouseOver && !selected) {
			FlounderSound.get().playSystemSound(SOUND_MOUSE_HOVER);
			text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_SELECTED, CHANGE_TIME));
			mouseOver = true;
		} else if (!FlounderGuis.get().getSelector().isSelected(text) && mouseOver && !selected) {
			text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_NORMAL, CHANGE_TIME));
			mouseOver = false;
		}

		// Update the background colour.
		Colour.interpolate(COLOUR_NORMAL, FlounderGuis.get().getGuiMaster().getPrimaryColour(), (text.getScale() - SCALE_NORMAL) / (SCALE_SELECTED - SCALE_NORMAL), background.getColourOffset());

		// Update background size.
		background.getDimensions().set(text.getMeshSize());
		background.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
		Vector2f.multiply(text.getDimensions(), background.getDimensions(), background.getDimensions());
		background.getDimensions().scale(2.0f * text.getScale());
		background.getPositionOffsets().set(text.getPositionOffsets());
		background.getPosition().set(text.getPosition());
	}

	@Override
	public void deleteObject() {
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
		this.text.setText(prefix + value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		this.text.setText(prefix + value);
	}
}
