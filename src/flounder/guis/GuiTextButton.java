package flounder.guis;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.inputs.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.visual.*;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class GuiTextButton extends GuiComponent {
	private static final float CHANGE_TIME = 0.15f;
	private static final float MAX_SCALE = 1.1f;
	private static final Sound MOUSE_DEFAULT_CLICK_SOUND = Sound.loadSoundNow(new MyFile(DeviceSound.SOUND_FOLDER, "button2.wav"), 0.2f);
	private static final Sound MOUSE_OVER_SOUND = Sound.loadSoundNow(new MyFile(DeviceSound.SOUND_FOLDER, "button1.wav"), 0.2f);

	private Text text;
	private boolean mouseOver;
	private Listener listenerLeft;
	private Listener listenerRight;
	private MouseButton mouseLeft;
	private MouseButton mouseRight;

	private Sound mouseLeftClickSound;
	private Sound mouseRightClickSound;

	public GuiTextButton(Text text) {
		this.text = text;
		mouseOver = false;
		mouseLeft = new MouseButton(GLFW_MOUSE_BUTTON_LEFT);
		mouseRight = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);

		mouseLeftClickSound = MOUSE_DEFAULT_CLICK_SOUND;
		mouseRightClickSound = MOUSE_DEFAULT_CLICK_SOUND;

		super.addText(text, 0, 0, 1);
	}

	public void setMouseLeftClickSound(Sound mouseLeftClickSound) {
		this.mouseLeftClickSound = mouseLeftClickSound;
	}

	public void setMouseRightClickSound(Sound mouseRightClickSound) {
		this.mouseRightClickSound = mouseRightClickSound;
	}

	public void addLeftListener(Listener listener) {
		listenerLeft = listener;
	}

	public void addRightListener(Listener listener) {
		listenerRight = listener;
	}

	@Override
	protected void updateSelf() {
		if (isMouseOver() && !mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), MAX_SCALE, CHANGE_TIME));
			mouseOver = true;
			ManagerDevices.getSound().playSystemSound(MOUSE_OVER_SOUND);
		} else if (!isMouseOver() && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), 1f, CHANGE_TIME));
			mouseOver = false;
		}

		if (isMouseOver() && mouseLeft.wasDown() && listenerLeft != null) {
			ManagerDevices.getSound().playSystemSound(mouseLeftClickSound);
			listenerLeft.eventOccurred();
		}

		if (isMouseOver() && mouseRight.wasDown() && listenerRight != null) {
			ManagerDevices.getSound().playSystemSound(mouseRightClickSound);
			listenerRight.eventOccurred();
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
