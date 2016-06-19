package flounder.guis;

import flounder.engine.*;
import flounder.fonts.*;
import flounder.sounds.*;
import flounder.visual.*;

import java.util.*;

public class GuiTextButton extends GuiComponent {
	private static final float CHANGE_TIME = 0.15f;
	private static final float MAX_SCALE = 1.1f;

	private Text text;
	private boolean mouseOver;
	private GuiListener guiListenerLeft;
	private GuiListener guiListenerRight;

	private Sound mouseHoverOverSound;
	private Sound mouseLeftClickSound;
	private Sound mouseRightClickSound;

	public GuiTextButton(Text text) {
		this.text = text;
		mouseOver = false;
		super.addText(text, 0.0f, 0.0f, 1.0f);
	}

	public void setSounds(Sound mouseHoverOverSound, Sound mouseLeftClickSound, Sound mouseRightClickSound) {
		this.mouseHoverOverSound = mouseHoverOverSound;
		this.mouseLeftClickSound = mouseLeftClickSound;
		this.mouseRightClickSound = mouseRightClickSound;
	}

	public Text getText() {
		return text;
	}

	public void addLeftListener(GuiListener guiListener) {
		guiListenerLeft = guiListener;
	}

	public void addRightListener(GuiListener guiListener) {
		guiListenerRight = guiListener;
	}

	@Override
	protected void updateSelf() {
		if (isMouseOver() && !mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), MAX_SCALE, CHANGE_TIME));
			mouseOver = true;
			FlounderEngine.getDevices().getSound().playSystemSound(mouseHoverOverSound);
		} else if (!isMouseOver() && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), 1.0f, CHANGE_TIME));
			mouseOver = false;
		}

		if (isMouseOver() && GuiManager.getSelector().wasLeftClick() && guiListenerLeft != null) {
			FlounderEngine.getDevices().getSound().playSystemSound(mouseLeftClickSound);
			guiListenerLeft.eventOccurred();
			GuiManager.getSelector().cancelWasEvent();
		}

		if (isMouseOver() && GuiManager.getSelector().wasRightClick() && guiListenerRight != null) {
			FlounderEngine.getDevices().getSound().playSystemSound(mouseRightClickSound);
			guiListenerRight.eventOccurred();
			GuiManager.getSelector().cancelWasEvent();
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}
}
