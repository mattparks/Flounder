package flounder.guis;

import flounder.engine.*;
import flounder.fonts.*;
import flounder.maths.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;

import java.util.*;

public class GuiCheckbox extends GuiComponent {
	private Text text;
	private GuiTexture texture;
	private boolean mouseOver;
	private boolean selected;

	private ListenerBasic guiListenerLeft;
	private ListenerBasic guiListenerRight;

	private TextAlign textAlign;
	private float leftMarginX;

	private ValueDriver rowDriver;
	private Colour textboxColour;

	public GuiCheckbox(Text text, TextAlign textAlign, float leftMarginX, boolean selected) {
		this.text = text;
		this.texture = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "checkbox.png")).create());
		this.texture.getTexture().setNumberOfRows(2);
		this.mouseOver = false;
		this.selected = selected;

		this.textAlign = textAlign;
		this.leftMarginX = leftMarginX;

		this.rowDriver = new ConstantDriver(selected ? texture.getTexture().getNumberOfRows() + 1 : 0);
		this.textboxColour = new Colour(GuiTextButton.DEFAULT_COLOUR);

		switch (textAlign) {
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

	public void addLeftListener(ListenerBasic guiListener) {
		guiListenerLeft = guiListener;
	}

	public void addRightListener(ListenerBasic guiListener) {
		guiListenerRight = guiListener;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		float textureRow = rowDriver.update(FlounderEngine.getDelta());
		rowDriver = new SlideDriver(textureRow, selected ? texture.getTexture().getNumberOfRows() + 1 : 0, GuiTextButton.CHANGE_TIME * 1.5f);
	}

	@Override
	protected void updateSelf() {
		float width = text.getCurrentHeight() / FlounderEngine.getDevices().getDisplay().getAspectRatio();
		float height = text.getCurrentHeight();
		float textureRow = rowDriver.update(FlounderEngine.getDelta());
		float positionX;

		switch (textAlign) {
			case LEFT:
				positionX = super.getPosition().x + text.getCurrentWidth() - (height / 2.0f);
				break;
			case CENTRE:
				positionX = super.getPosition().x + text.getCurrentWidth() - (height / 2.0f);
				break;
			case RIGHT:
				positionX = super.getPosition().x - text.getCurrentWidth() - (height / 2.0f);
				break;
			default:
				positionX = 0.0f;
				break;
		}

		texture.setPosition(positionX, super.getPosition().y, width, height);
		texture.setSelectedRow(Math.round(textureRow));
		texture.setColourOffset(Colour.interpolate(GuiTextButton.DEFAULT_COLOUR, GuiTextButton.HOVER_COLOUR, textureRow / texture.getTexture().getNumberOfRows(), textboxColour));
		texture.update();

		// Mouse over updates.
		if (isMouseOver() && !mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), GuiTextButton.MAX_SCALE, GuiTextButton.CHANGE_TIME));
			mouseOver = true;
			FlounderEngine.getDevices().getSound().playSystemSound(GuiTextButton.SOUND_MOUSE_HOVER);
		} else if (!isMouseOver() && mouseOver) {
			text.setScaleDriver(new SlideDriver(text.getScale(), 1.0f, GuiTextButton.CHANGE_TIME));
			mouseOver = false;
		}

		// Click updates.
		if (isMouseOver() && FlounderEngine.getGuis().getSelector().wasLeftClick()) {
			FlounderEngine.getDevices().getSound().playSystemSound(GuiTextButton.SOUND_MOUSE_LEFT);
			setSelected(!selected);

			if (guiListenerLeft != null) {
				guiListenerLeft.eventOccurred();
			}

			FlounderEngine.getGuis().getSelector().cancelWasEvent();
		}

		if (isMouseOver() && FlounderEngine.getGuis().getSelector().wasRightClick()) {
			FlounderEngine.getDevices().getSound().playSystemSound(GuiTextButton.SOUND_MOUSE_RIGHT);
			setSelected(!selected);

			if (guiListenerRight != null) {
				guiListenerRight.eventOccurred();
			}

			FlounderEngine.getGuis().getSelector().cancelWasEvent();
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		guiTextures.add(texture);
	}
}
