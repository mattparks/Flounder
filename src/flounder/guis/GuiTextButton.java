package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.visual.*;

import java.util.*;

public class GuiTextButton extends GuiComponent {
	public static final float CHANGE_TIME = 0.15f;
	public static final float MAX_SCALE = 1.1f;

	public static final Sound SOUND_MOUSE_HOVER = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button1.wav"), 0.8f, 1.0f);
	public static final Sound SOUND_MOUSE_LEFT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button2.wav"), 0.8f, 1.0f);
	public static final Sound SOUND_MOUSE_RIGHT = Sound.loadSoundInBackground(new MyFile(FlounderSound.SOUND_FOLDER, "button3.wav"), 0.8f, 1.0f);

	private Text text;
	private boolean mouseOver;

	private ListenerBasic listenerLeft;
	private ListenerBasic listenerRight;

	public GuiTextButton(Text text) {
		this.text = text;
		this.mouseOver = false;

		this.listenerLeft = null;
		this.listenerRight = null;

		addText(text, 0.0f, 0.0f, 1.0f);
	}

	public Text getText() {
		return text;
	}

	public void addLeftListener(ListenerBasic guiListener) {
		listenerLeft = guiListener;
	}

	public void addRightListener(ListenerBasic guiListener) {
		listenerRight = guiListener;
	}

	@Override
	protected void updateSelf() {
		// Mouse over updates.
		if (text.isMouseOver() && !mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), MAX_SCALE, CHANGE_TIME));
			mouseOver = true;
			FlounderSound.playSystemSound(GuiTextButton.SOUND_MOUSE_HOVER);
		} else if (!text.isMouseOver() && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), 1.0f, CHANGE_TIME));
			mouseOver = false;
		}

		// Click updates.
		if (text.isMouseOver() && FlounderGuis.getSelector().wasLeftClick()) {
			FlounderSound.playSystemSound(GuiTextButton.SOUND_MOUSE_LEFT);

			if (listenerLeft != null) {
				listenerLeft.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}

		if (text.isMouseOver() && FlounderGuis.getSelector().wasRightClick()) {
			FlounderSound.playSystemSound(GuiTextButton.SOUND_MOUSE_RIGHT);

			if (listenerRight != null) {
				listenerRight.eventOccurred();
			}

			FlounderGuis.getSelector().cancelWasEvent();
		}

	//	setRelativeX(FlounderDisplay.getAspectRatio() * 0.5f);
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
