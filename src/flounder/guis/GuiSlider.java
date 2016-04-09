package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.fonts.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.sounds.*;
import flounder.textures.*;
import flounder.visual.*;

import java.util.*;

public class GuiSlider extends GuiComponent {
	private static final float CHANGE_TIME = 0.15f;
	private static final float MAX_SCALE = 1.1f;
	private static final Sound MOUSE_DEFAULT_CLICK_SOUND = Sound.loadSoundInBackground(new MyFile(DeviceSound.SOUND_FOLDER, "button2.wav"), 0.2f);
	private static Texture TEXTURE_EMPTY_BAR = Texture.newTexture(new MyFile(GuiManager.GUIS_LOC, "emptySlider.png")).noFiltering().createInBackground();
	private static Texture TEXTURE_FULL_BAR = Texture.newTexture(new MyFile(GuiManager.GUIS_LOC, "fullSlider.png")).noFiltering().createInBackground();
	private final Colour fullColour;
	private GuiTexture emptyBarGUI;
	private GuiTexture fullBarGUI;
	private GuiTextButton textButton;
	private boolean mouseOver;
	private ValueDriver scaleDriver;
	private float value;
	private Listener listenerLeft;
	private Listener listenerRight;
	private Sound mouseLeftClickSound;
	private Sound mouseRightClickSound;

	public GuiSlider() {
		emptyBarGUI = new GuiTexture(TEXTURE_EMPTY_BAR, false);
		fullBarGUI = new GuiTexture(TEXTURE_FULL_BAR, false);
		this.textButton = null;
		this.mouseOver = false;
		this.scaleDriver = new ConstantDriver(1.0f);
		this.value = 0.0f;
		this.fullColour = new Colour();

		mouseLeftClickSound = MOUSE_DEFAULT_CLICK_SOUND;
		mouseRightClickSound = MOUSE_DEFAULT_CLICK_SOUND;
	}

	public void addText(final Text text) {
		this.textButton = new GuiTextButton(text);
		super.addComponent(textButton, super.getPosition().x - 0.5f, super.getPosition().y, 1.0f, 1.0f);
	}

	public float getValue() {
		return value;
	}

	public void setValue(final float value) {
		this.value = (value > 1.0f ? 1.0f : value);
	}

	public void setMouseLeftClickSound(final Sound mouseLeftClickSound) {
		this.mouseLeftClickSound = mouseLeftClickSound;
	}

	public void setMouseRightClickSound(final Sound mouseRightClickSound) {
		this.mouseRightClickSound = mouseRightClickSound;
	}

	public void addLeftListener(final Listener listener) {
		listenerLeft = listener;

		if (textButton != null) {
			textButton.addLeftListener(() -> {
				listenerLeft.eventOccurred();
			});
		}
	}

	public void addRightListener(final Listener listener) {
		listenerRight = listener;

		if (textButton != null) {
			textButton.addRightListener(() -> {
				listenerRight.eventOccurred();
			});
		}
	}

	public Colour getFullColour() {
		return fullColour;
	}

	public void setFullColour(final Colour fullColour) {
		this.fullColour.set(fullColour);
	}

	@Override
	protected void updateSelf() {
		if ((isMouseOver() || (textButton != null && textButton.isMouseOver())) && !mouseOver) {
			scaleDriver = new SlideDriver(1.0f, MAX_SCALE, CHANGE_TIME);
			mouseOver = true;
		} else if (!(isMouseOver() || (textButton != null && textButton.isMouseOver())) && mouseOver) {
			scaleDriver = new SlideDriver(scaleDriver.update(FlounderEngine.getDelta()), 1.0f, CHANGE_TIME);
			mouseOver = false;
		}

		if (isMouseOver() && GuiManager.getSelector().wasLeftClick() && listenerLeft != null) {
			ManagerDevices.getSound().playSystemSound(mouseLeftClickSound);
			listenerLeft.eventOccurred();
			GuiManager.getSelector().cancelWasEvent();
		}

		if (isMouseOver() && GuiManager.getSelector().wasRightClick() && listenerRight != null) {
			ManagerDevices.getSound().playSystemSound(mouseRightClickSound);
			listenerRight.eventOccurred();
			GuiManager.getSelector().cancelWasEvent();
		}

		float scale = scaleDriver.update(FlounderEngine.getDelta());
		float width = scale * 0.3f;
		float height = scale * 0.0325f * ManagerDevices.getDisplay().getAspectRatio();

		if (textButton != null) {
			textButton.setScreenSpacePosition(super.getPosition().x - (width / 2.0f), super.getPosition().y, textButton.getScale().x, textButton.getScale().y);
		}

		emptyBarGUI.setPosition(super.getPosition().x - (width / 2.0f), super.getPosition().y, width, height);
		emptyBarGUI.update();

		fullBarGUI.setPosition(super.getPosition().x - (width / 2.0f), super.getPosition().y, width * value, height);
		fullBarGUI.setColourOffset(fullColour);
		fullBarGUI.update();
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		guiTextures.add(emptyBarGUI);
		guiTextures.add(fullBarGUI);
	}
}
