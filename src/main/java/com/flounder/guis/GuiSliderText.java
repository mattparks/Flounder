package com.flounder.guis;

import com.flounder.devices.*;
import com.flounder.fonts.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.resources.*;
import com.flounder.textures.*;
import com.flounder.visual.*;

public class GuiSliderText extends ScreenObject {
	private static final TextureObject TEXTURE_BACKGROUND = TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "buttonText.png")).create();

	private TextObject text;
	private GuiObject background;
	private GuiObject slider;

	private boolean updating;
	private float minProgress;
	private float maxProgress;
	private float value;

	private boolean mouseOver;

	private boolean hasChange;
	private Timer changeTimeout;
	private ScreenListener listenerChange;

	public GuiSliderText(ScreenObject parent, Vector2f position, String string, float minProgress, float maxProgress, float value, GuiAlign align) {
		super(parent, position, new Vector2f());

		this.text = new TextObject(this, this.getPosition(), string, GuiButtonText.SCALE_NORMAL, FlounderFonts.CANDARA, 0.36f, align);
		this.text.setInScreenCoords(true);
		this.text.setColour(new Colour(1.0f, 1.0f, 1.0f));

		this.background = new GuiObject(this, this.getPosition(), new Vector2f(), TEXTURE_BACKGROUND, 1);
		this.background.setInScreenCoords(true);
		this.background.setColourOffset(new Colour());

		this.slider = new GuiObject(this, this.getPosition(), new Vector2f(), TEXTURE_BACKGROUND, 1);
		this.slider.setInScreenCoords(true);
		this.slider.setColourOffset(new Colour());

		this.updating = false;
		this.minProgress = minProgress;
		this.maxProgress = maxProgress;
		setValue(value);

		this.mouseOver = false;

		this.hasChange = false;
		this.changeTimeout = new Timer(0.2f);
		this.listenerChange = null;
	}

	public void addChangeListener(ScreenListener listenerChange) {
		this.listenerChange = listenerChange;
	}

	public float getValue() {
		return (value * (maxProgress - minProgress)) + minProgress;
	}

	public void setValue(float value) {
		this.value = (value - minProgress) / (maxProgress - minProgress);
	}

	@Override
	public void updateObject() {
		if (!isVisible() || getAlpha() == 0.0f) {
			return;
		}

		// Click updates.
		if (FlounderGuis.get().getSelector().isSelected(text) && getAlpha() == 1.0f && ((updating && FlounderGuis.get().getSelector().isLeftClick()) || FlounderGuis.get().getSelector().wasLeftClick())) {
			if (!updating) {
				FlounderSound.get().playSystemSound(GuiButtonText.SOUND_MOUSE_LEFT);
				updating = true;
			}

			hasChange = true;

			float width = 2.0f * background.getMeshSize().x * background.getScreenDimensions().x / FlounderDisplay.get().getAspectRatio();
			float positionX = background.getPosition().x;
			float cursorX = FlounderGuis.get().getSelector().getCursorX() - positionX;
			value = 2.0f * cursorX / width;
			value = (value + 1.0f) * 0.5f;

			FlounderGuis.get().getSelector().cancelWasEvent();
		} else {
			updating = false;
		}

		// Updates the listener.
		if (hasChange && changeTimeout.isPassedTime()) {
			if (listenerChange != null) {
				listenerChange.eventOccurred();
			}

			hasChange = false;
			changeTimeout.resetStartTime();
		}

		// Mouse over updates.
		if (FlounderGuis.get().getSelector().isSelected(text) && !mouseOver) {
			FlounderSound.get().playSystemSound(GuiButtonText.SOUND_MOUSE_HOVER);
			text.setScaleDriver(new SlideDriver(text.getScale(), GuiButtonText.SCALE_SELECTED, GuiButtonText.CHANGE_TIME));
			mouseOver = true;
		} else if (!FlounderGuis.get().getSelector().isSelected(text) && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), GuiButtonText.SCALE_NORMAL, GuiButtonText.CHANGE_TIME));
			mouseOver = false;
		}

		// Update the background colour.
		Colour primary = FlounderGuis.get().getGuiMaster().getPrimaryColour();
		Colour.interpolate(GuiButtonText.COLOUR_NORMAL, primary, (text.getScale() - GuiButtonText.SCALE_NORMAL) / (GuiButtonText.SCALE_SELECTED - GuiButtonText.SCALE_NORMAL), background.getColourOffset());
		this.slider.getColourOffset().set(1.0f - primary.r, 1.0f - primary.g, 1.0f - primary.b);

		// Update background size.
		background.getDimensions().set(text.getMeshSize());
		background.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
		Vector2f.multiply(text.getDimensions(), background.getDimensions(), background.getDimensions());
		background.getDimensions().scale(2.0f * text.getScale());
		background.getPositionOffsets().set(text.getPositionOffsets());
		background.getPosition().set(text.getPosition());

		// Update slider size. (This is about the worst looking GUI code, but works well.)
		slider.getDimensions().set(text.getMeshSize());
		slider.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
		Vector2f.multiply(text.getDimensions(), slider.getDimensions(), slider.getDimensions());
		slider.getDimensions().scale(2.0f * text.getScale());
		slider.getPositionOffsets().set(text.getPositionOffsets());
		slider.getPosition().set(text.getPosition());
		slider.getPositionOffsets().x -= (slider.getDimensions().x / 2.0f);
		slider.getDimensions().x *= value;
		slider.getPositionOffsets().x += (slider.getDimensions().x / 2.0f);
	}

	public void setText(String string) {
		this.text.setText(string);
	}

	@Override
	public void deleteObject() {
	}
}
