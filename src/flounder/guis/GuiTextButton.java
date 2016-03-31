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

	private Text m_text;
	private boolean m_mouseOver;
	private Listener m_listenerLeft;
	private Listener m_listenerRight;
	private MouseButton m_mouseLeft;
	private MouseButton m_mouseRight;

	private Sound m_mouseLeftClickSound;
	private Sound m_mouseRightClickSound;

	public GuiTextButton(Text text) {
		m_text = text;
		m_mouseOver = false;
		m_mouseLeft = new MouseButton(GLFW_MOUSE_BUTTON_LEFT);
		m_mouseRight = new MouseButton(GLFW_MOUSE_BUTTON_RIGHT);

		m_mouseLeftClickSound = MOUSE_DEFAULT_CLICK_SOUND;
		m_mouseRightClickSound = MOUSE_DEFAULT_CLICK_SOUND;

		super.addText(text, 0, 0, 1);
	}

	public void setMouseLeftClickSound(Sound mouseLeftClickSound) {
		m_mouseLeftClickSound = mouseLeftClickSound;
	}

	public void setMouseRightClickSound(Sound mouseRightClickSound) {
		m_mouseRightClickSound = mouseRightClickSound;
	}

	public void addLeftListener(Listener listener) {
		m_listenerLeft = listener;
	}

	public void addRightListener(Listener listener) {
		m_listenerRight = listener;
	}

	@Override
	protected void updateSelf() {
		if (isMouseOver() && !m_mouseOver) {
			m_text.setScaleDriver(new SlideDriver(m_text.getScale(), MAX_SCALE, CHANGE_TIME));
			m_mouseOver = true;
			ManagerDevices.getSound().playSystemSound(MOUSE_OVER_SOUND);
		} else if (!isMouseOver() && m_mouseOver) {
			m_text.setScaleDriver(new SlideDriver(m_text.getScale(), 1f, CHANGE_TIME));
			m_mouseOver = false;
		}

		if (isMouseOver() && m_mouseLeft.wasDown() && m_listenerLeft != null) {
			ManagerDevices.getSound().playSystemSound(m_mouseLeftClickSound);
			m_listenerLeft.eventOccurred();
		}

		if (isMouseOver() && m_mouseRight.wasDown() && m_listenerRight != null) {
			ManagerDevices.getSound().playSystemSound(m_mouseRightClickSound);
			m_listenerRight.eventOccurred();
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
