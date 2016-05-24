package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.visual.*;

import java.util.*;

public class GuiTextButton extends GuiComponent {
	private static final float CHANGE_TIME = 0.15f;
	private static final float MAX_SCALE = 1.1f;
	private static final Sound MOUSE_DEFAULT_CLICK_SOUND = Sound.loadSoundInBackground(new MyFile(DeviceSound.SOUND_FOLDER, "button2.wav"), 0.2f);
	private static final Sound MOUSE_OVER_SOUND = Sound.loadSoundInBackground(new MyFile(DeviceSound.SOUND_FOLDER, "button1.wav"), 0.2f);

	private Text text;
	private boolean mouseOver;
	private Listener listenerLeft;
	private Listener listenerRight;

	private Sound mouseLeftClickSound;
	private Sound mouseRightClickSound;

	public GuiTextButton(final Text text) {
		this.text = text;
		mouseOver = false;

		mouseLeftClickSound = MOUSE_DEFAULT_CLICK_SOUND;
		mouseRightClickSound = MOUSE_DEFAULT_CLICK_SOUND;

		super.addText(text, 0.0f, 0.0f, 1.0f);
	}

	public void setMouseLeftClickSound(final Sound mouseLeftClickSound) {
		this.mouseLeftClickSound = mouseLeftClickSound;
	}

	public void setMouseRightClickSound(final Sound mouseRightClickSound) {
		this.mouseRightClickSound = mouseRightClickSound;
	}

	public void addLeftListener(final Listener listener) {
		listenerLeft = listener;
	}

	public void addRightListener(final Listener listener) {
		listenerRight = listener;
	}

	@Override
	protected void updateSelf() {
		if (isMouseOver() && !mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), MAX_SCALE, CHANGE_TIME));
			mouseOver = true;
			FlounderDevices.getSound().playSystemSound(MOUSE_OVER_SOUND);
		} else if (!isMouseOver() && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), 1.0f, CHANGE_TIME));
			mouseOver = false;
		}

		if (isMouseOver() && GuiManager.getSelector().wasLeftClick() && listenerLeft != null) {
			FlounderDevices.getSound().playSystemSound(mouseLeftClickSound);
			listenerLeft.eventOccurred();
			GuiManager.getSelector().cancelWasEvent();
		}

		if (isMouseOver() && GuiManager.getSelector().wasRightClick() && listenerRight != null) {
			FlounderDevices.getSound().playSystemSound(mouseRightClickSound);
			listenerRight.eventOccurred();
			GuiManager.getSelector().cancelWasEvent();
		}
	}

	@Override
	protected void getGuiTextures(final List<GuiTexture> guiTextures) {
	}
}
