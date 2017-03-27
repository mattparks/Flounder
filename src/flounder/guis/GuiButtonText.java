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
	protected static final float CHANGE_TIME = 0.15f;

	protected static final float SCALE_NORMAL = 1.5f;
	protected static final float SCALE_SELECTED = 1.75f;

	protected final static Colour COLOUR_NORMAL = new Colour(0.0f, 0.0f, 0.0f);

	protected final static Sound SOUND_MOUSE_HOVER = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button1.wav"), 0.8f, 1.0f);
	protected final static Sound SOUND_MOUSE_LEFT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button2.wav"), 0.8f, 1.0f);
	protected final static Sound SOUND_MOUSE_RIGHT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button3.wav"), 0.8f, 1.0f);

	private static final TextureObject TEXTURE_BACKGROUND = TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "buttonText.png")).create();

	private TextObject text;
	private GuiObject background;

	private boolean mouseOver;

	private ScreenListener listenerLeft;
	private ScreenListener listenerRight;

	public GuiButtonText(ScreenObject parent, Vector2f position, String string, GuiAlign align) {
		super(parent, position, new Vector2f());

		this.text = new TextObject(this, this.getPosition(), string, SCALE_NORMAL, FlounderFonts.CANDARA, 0.36f, align);
		this.text.setInScreenCoords(true);
		this.text.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.background = new GuiObject(this, this.getPosition(), new Vector2f(), TEXTURE_BACKGROUND, 1);
		this.background.setInScreenCoords(true);
		this.background.setColourOffset(new Colour());

		this.mouseOver = false;
	}

	public void addLeftListener(ScreenListener listenerLeft) {
		this.listenerLeft = listenerLeft;
	}

	public void addRightListener(ScreenListener listenerRight) {
		this.listenerRight = listenerRight;
	}

	@Override
	public void updateObject() {
		if (!isVisible() || getAlpha() == 0.0f) {
			return;
		}

		// Click updates.
		if (FlounderGuis.getSelector().isSelected(text) && getAlpha() == 1.0f && FlounderGuis.getSelector().wasLeftClick()) {
			FlounderSound.playSystemSound(SOUND_MOUSE_LEFT);

			if (listenerLeft != null) {
				listenerLeft.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		} else if (FlounderGuis.getSelector().isSelected(text) && getAlpha() == 1.0f && FlounderGuis.getSelector().wasRightClick()) {
			FlounderSound.playSystemSound(SOUND_MOUSE_RIGHT);

			if (listenerRight != null) {
				listenerRight.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}

		// Mouse over updates.
		if (FlounderGuis.getSelector().isSelected(text) && !mouseOver) {
			FlounderSound.playSystemSound(SOUND_MOUSE_HOVER);
			text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_SELECTED, CHANGE_TIME));
			mouseOver = true;
		} else if (!FlounderGuis.getSelector().isSelected(text) && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), SCALE_NORMAL, CHANGE_TIME));
			mouseOver = false;
		}

		// Update the background colour.
		Colour.interpolate(COLOUR_NORMAL, FlounderGuis.getGuiMaster().getPrimaryColour(), (text.getScale() - SCALE_NORMAL) / (SCALE_SELECTED - SCALE_NORMAL), background.getColourOffset());

		// Update background size.
		background.getDimensions().set(text.getMeshSize());
		background.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
		Vector2f.multiply(text.getDimensions(), background.getDimensions(), background.getDimensions());
		background.getDimensions().scale(2.0f * text.getScale());
		background.getPositionOffsets().set(text.getPositionOffsets());
		background.getPosition().set(text.getPosition());
	}

	public void setText(String string) {
		this.text.setText(string);
	}

	@Override
	public void deleteObject() {
	}
}
