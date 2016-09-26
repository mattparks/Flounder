package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.events.*;
import flounder.fonts.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.textures.*;
import flounder.visual.*;

import java.util.*;

public class GuiTextButton extends GuiComponent {
	public static final float CHANGE_TIME = 0.15f;
	public static final float MAX_SCALE = 1.1f;
	public static final float WIDTH_MULTIPLIER = 1.5f;

	public static final Colour DEFAULT_COLOUR = new Colour(0.2f, 0.2f, 0.2f);
	public static final Colour HOVER_COLOUR = new Colour(56, 182, 52, true);

	public static final Sound SOUND_MOUSE_HOVER = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button1.wav"), 0.8f);
	public static final Sound SOUND_MOUSE_LEFT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button2.wav"), 0.8f);
	public static final Sound SOUND_MOUSE_RIGHT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button3.wav"), 0.8f);

	public static final float BACKGROUND_PADDING = 0.018f;

	private Text text;
	private GuiTexture background;
	private boolean mouseOver;

	private ListenerBasic guiListenerLeft;
	private ListenerBasic guiListenerRight;

	private GuiAlign guiAlign;
	private float leftMarginX;

	// A static method that is used to create a easy colour event.
	static {
		FlounderEvents.addEvent(new IEvent() {
			private SinWaveDriver titleColourX = new SinWaveDriver(0.0f, 1.0f, 40.0f);
			private SinWaveDriver titleColourY = new SinWaveDriver(0.0f, 1.0f, 20.0f);

			@Override
			public boolean eventTriggered() {
				return true;
			}

			@Override
			public void onEvent() {
				HOVER_COLOUR.set(titleColourX.update(FlounderEngine.getDelta()), titleColourY.update(FlounderEngine.getDelta()), 0.3f);
			}
		});
	}

	public GuiTextButton(Text text, GuiAlign guiAlign, float leftMarginX) {
		this.text = text;
		this.background = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "button.png")).clampEdges().create());
		this.mouseOver = false;

		this.guiAlign = guiAlign;
		this.leftMarginX = leftMarginX;

		switch (guiAlign) {
			case LEFT:
				addText(text, leftMarginX, 0.0f, 1.0f);
				break;
			case CENTRE:
				addText(text, leftMarginX, 0.0f, 1.0f);
				break;
			case RIGHT:
				addText(text, 1.0f - leftMarginX, 0.0f, 1.0f);
				break;
		}
	}

	public Text getText() {
		return text;
	}

	public void addLeftListener(ListenerBasic guiListener) {
		guiListenerLeft = guiListener;
	}

	public void addRightListener(ListenerBasic guiListener) {
		guiListenerRight = guiListener;
	}

	@Override
	protected void updateSelf() {
		// Background image updates.
		float width = ((text.getMaxLineSize() * WIDTH_MULTIPLIER) / FlounderDisplay.getAspectRatio()) * text.getScale();
		float height = text.getCurrentHeight();
		float positionX;

		switch (guiAlign) {
			case LEFT:
				positionX = super.getPosition().x - (leftMarginX * text.getScale());
				break;
			case CENTRE:
				positionX = super.getPosition().x - (leftMarginX * text.getScale());
				break;
			case RIGHT:
				positionX = super.getPosition().x + (leftMarginX * text.getScale());
				break;
			default:
				positionX = 0.0f;
				break;
		}

		background.getPosition().x = positionX;
		background.getPosition().y = super.getPosition().y - (BACKGROUND_PADDING * text.getScale());
		background.setFlipTexture(guiAlign.equals(GuiAlign.RIGHT));
		background.getScale().set(width, height + ((BACKGROUND_PADDING * 2.0f) / text.getScale()));

		background.getColourOffset().set(isMouseOver() ? HOVER_COLOUR : DEFAULT_COLOUR);
		background.update();

		// Mouse over updates.
		if (isMouseOver() && !mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), MAX_SCALE, CHANGE_TIME));
			mouseOver = true;
			FlounderSound.playSystemSound(GuiTextButton.SOUND_MOUSE_HOVER);
		} else if (!isMouseOver() && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), 1.0f, CHANGE_TIME));
			mouseOver = false;
		}

		// Click updates.
		if (isMouseOver() && FlounderGuis.getSelector().wasLeftClick()) {
			FlounderSound.playSystemSound(GuiTextButton.SOUND_MOUSE_LEFT);

			if (guiListenerLeft != null) {
				guiListenerLeft.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}

		if (isMouseOver() && FlounderGuis.getSelector().wasRightClick()) {
			FlounderSound.playSystemSound(GuiTextButton.SOUND_MOUSE_RIGHT);

			if (guiListenerRight != null) {
				guiListenerRight.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		guiTextures.add(background);
	}
}
