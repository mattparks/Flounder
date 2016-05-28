package flounder.guis;

import flounder.devices.*;
import flounder.engine.*;
import flounder.fonts.*;
import flounder.maths.vectors.*;

import java.util.*;

/**
 * An component of a GUI. Implementations of this range from a whole GUI frame to a single button. All components can have subtract-components, and the parent component has the responsibility of keeping the subtract-components updated.
 */
public abstract class GuiComponent {
	private final Vector2f position;
	private final Vector2f scale;

	private final Vector2f relativePosition;
	private final Vector2f relativeScale;
	private final List<GuiComponent> childComponents;
	private final Map<Text, Vector3f> componentTexts;
	private final List<GuiComponent> componentsToDelete;
	private final List<GuiComponent> componentsToRemove;
	private final List<GuiComponent> componentsToAdd;
	private final List<GuiListenerAdvanced> actionEvents;
	private GuiComponent parent;
	private boolean visible;
	private boolean initialized;

	public GuiComponent() {
		position = new Vector2f();
		scale = new Vector2f();
		relativePosition = new Vector2f();
		relativeScale = new Vector2f();
		visible = true;
		childComponents = new ArrayList<>();
		componentTexts = new HashMap<>();
		componentsToDelete = new ArrayList<>();
		componentsToRemove = new ArrayList<>();
		componentsToAdd = new ArrayList<>();
		actionEvents = new ArrayList<>();
		initialized = false;
	}

	/**
	 * Determines whether the component (and all subtract-components) should be visible or not. Non-visible components are not updated.
	 *
	 * @param visible Whether the component should be visible or not.
	 */
	public void show(final boolean visible) {
		this.visible = visible;
	}

	/**
	 * Adds a action listener to run when event has occurred.
	 *
	 * @param actionListener The action listener.
	 */
	public void addActionListener(final GuiListenerAdvanced actionListener) {
		actionEvents.add(actionListener);
	}

	/**
	 * Removes a action listener.
	 *
	 * @param actionListener The action listener to remove.
	 */
	public void removeActionListener(final GuiListenerAdvanced actionListener) {
		actionEvents.remove(actionListener);
	}

	/**
	 * Add a subtract-component to this component.
	 *
	 * @param component The subtract=component.
	 * @param relX The x position of the top-left corner of the child component, in relation to this component (0 is the left edge of this component, 1 is the right edge).
	 * @param relY The y position of the top-left corner of the child component, in relation to this component (0 is the top edge of this component, 1 is the bottom edge).
	 * @param relScaleX The x scale of the child component, in relation to the x scale of this component.
	 * @param relScaleY The y scale of the child component, in relation to the y scale of this component.
	 */
	public void addComponent(final GuiComponent component, final float relX, final float relY, final float relScaleX, final float relScaleY) {
		component.relativePosition.set(relX, relY);
		component.relativeScale.set(relScaleX, relScaleY);
		component.parent = this;
		componentsToAdd.add(component);
	}

	/**
	 * Indicates that a child component should be removed from this component and deleted.
	 *
	 * @param component The child component to be removed.
	 * @param deleteObject If true the object will be cleared and deleted.
	 */
	public void removeComponent(final GuiComponent component, final boolean deleteObject) {
		if (deleteObject) {
			componentsToDelete.add(component);
		} else {
			componentsToRemove.add(component);
		}
	}

	/**
	 * Adds some text to the component.
	 *
	 * @param text The text to be added.
	 * @param relX The x position of the left edge of the text, relative to this component's size and position (0 = far left edge, 1 = far right).
	 * @param relY The y position of the top edge of the text, relative to this component's size and position (0 = top edge, 1 = bottom edge).
	 * @param relLineWidth The width of the line of text, relative to the width of the component.
	 */
	public void addText(final Text text, final float relX, final float relY, final float relLineWidth) {
		Vector3f relativePosition = new Vector3f(relX, relY, relLineWidth);
		componentTexts.put(text, relativePosition);

		if (initialized) {
			setTextScreenSpacePosition(text, relativePosition);
		}
	}

	/**
	 * Calculate the screen-space position and screen-space line width of a text based on their relative position and the screen space position of this component.
	 *
	 * @param text The text whose screen-space position needs to be set.
	 * @param relativePosition The position of the text relative to this component's top-left corner. The z component is the line width, specified relative to the width of this component.
	 */
	private void setTextScreenSpacePosition(final Text text, final Vector3f relativePosition) {
		float x = position.x + scale.x * relativePosition.x;
		float y = position.y + scale.y * relativePosition.y;
		float lineWidth = relativePosition.z * scale.x;
		text.initialize(x, y, lineWidth);
	}

	/**
	 * @return {@code true} if this component isn't currently hidden.
	 */
	public boolean isShown() {
		return visible;
	}

	/**
	 * Removes some text from the component and deletes the text.
	 *
	 * @param text The text currently in the component that needs to be removed.
	 */
	public void deleteText(final Text text) {
		if (text != null) {
			componentTexts.remove(text);
			text.deleteFromMemory();
		}
	}

	/**
	 * @return The x position of the top-left corner of the component, relative to the parent component.
	 */
	public float getRelativeX() {
		return relativePosition.x;
	}

	public void setRelativeX(final float x) {
		relativePosition.x = x;
		updateScreenSpacePosition();
	}

	/**
	 * Calculates the screen space position of the component based on their relative position and the screen-space position of their parent.
	 */
	protected void updateScreenSpacePosition() {
		float x = parent.position.x + parent.scale.x * relativePosition.x;
		float y = parent.position.y + parent.scale.y * relativePosition.y;
		float width = relativeScale.x * parent.scale.x;
		float height = relativeScale.y * parent.scale.y;
		setScreenSpacePosition(x, y, width, height);

		childComponents.forEach(GuiComponent::updateScreenSpacePosition);
		componentTexts.keySet().forEach(text -> setTextScreenSpacePosition(text, componentTexts.get(text)));
	}

	/**
	 * Sets the screen-space position and dimensions of this component. (0, 0) is the top left of the screen and (1, 1) is the bottom right.
	 *
	 * @param x The x position of the top-left of the component in screen-space.
	 * @param y The y position of the top-left of the component in screen-space.
	 * @param width The width of the component in screen-space.
	 * @param height The height of the component in screen-space.
	 */
	protected void setScreenSpacePosition(final float x, final float y, final float width, final float height) {
		position.set(x, y);
		scale.set(width, height);
		initialized = true;
	}

	public void increaseRelativePosition(final float dX, final float dY) {
		relativePosition.x += dX;
		relativePosition.y += dY;
		updateScreenSpacePosition();
	}

	/**
	 * @return The screen-space position of the top-left corner of the component.
	 */
	protected Vector2f getPosition() {
		return position;
	}

	/**
	 * @return The screen-space x and y scales of the component.
	 */
	protected Vector2f getScale() {
		return scale;
	}

	/**
	 * @return {@code true} if the mouse cursor is currently over this component.
	 */
	protected boolean isMouseOver() {
		if (FlounderDevices.getMouse().isDisplaySelected() && FlounderDevices.getDisplay().isFocused()) {
			if (GuiManager.getSelector().getCursorX() >= position.x && GuiManager.getSelector().getCursorX() <= position.x + scale.x) {
				if (GuiManager.getSelector().getCursorY() >= position.y && GuiManager.getSelector().getCursorY() <= position.y + scale.y) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Updates the component and all of its child-components, as well as filling the list of {@link GuiTexture}s with any textures that this component or any of its subtract-components need to be rendered. It also removes any subtract components that have indicated that they need to be removed.
	 *
	 * @param guiTextures The list of {@link GuiTexture}s to be rendered.
	 * @param texts The map of texts to be rendered this frame.
	 */
	public final void update(final List<GuiTexture> guiTextures, final Map<FontType, List<Text>> texts) {
		if (!visible) {
			return;
		}

		for (final GuiListenerAdvanced listener : actionEvents) {
			if (listener.hasOccurred()) {
				listener.run();
			}
		}

		updateSelf();
		updateTexts();
		getGuiTextures(guiTextures);
		addTextsToRenderBatch(texts);
		removeOldComponents();
		childComponents.forEach(childComponent -> childComponent.update(guiTextures, texts));
		updateAndAddNewChildren(guiTextures, texts);
	}

	/**
	 * Updates the functionality of this particular component.
	 */
	protected abstract void updateSelf();

	/**
	 * Adds any of this component's {@link GuiTexture}s.
	 *
	 * @param guiTextures The list of GUI textures that are going to be rendered. This method adds any necessary {@link GuiTexture}s from this component to that list.
	 */
	protected abstract void getGuiTextures(final List<GuiTexture> guiTextures);

	/**
	 * Adds the texts from this component into the map of texts for rendering this frame. There is a list of texts for each font being used.
	 *
	 * @param texts All the lists of texts, each associated with the font that all the texts in that list use.
	 */
	private void addTextsToRenderBatch(final Map<FontType, List<Text>> texts) {
		for (final Text text : componentTexts.keySet()) {
			final FontType font = text.getFontType();
			List<Text> textBatch = texts.get(font);

			if (textBatch == null) {
				textBatch = new ArrayList<>();
				texts.put(font, textBatch);
			}

			textBatch.add(text);
		}
	}

	/**
	 * Update the component's texts.
	 */
	private void updateTexts() {
		componentTexts.keySet().forEach(text -> text.update(FlounderEngine.getDelta()));
	}

	/**
	 * Deletes the component and all subtract-components. Mainly just deletes the texts VAOs from memory.
	 */
	private void delete() {
		componentTexts.keySet().forEach(Text::deleteFromMemory);
		componentsToDelete.forEach(GuiComponent::delete);
		componentsToAdd.forEach(GuiComponent::delete);
		childComponents.forEach(GuiComponent::delete);
	}

	private void removeOldComponents() {
		for (final GuiComponent component : componentsToDelete) {
			childComponents.remove(component);
			component.delete();
		}

		for (final GuiComponent component : componentsToRemove) {
			childComponents.remove(component);
		}

		componentsToDelete.clear();
		componentsToRemove.clear();
	}

	private void updateAndAddNewChildren(final List<GuiTexture> guiTextures, final Map<FontType, List<Text>> texts) {
		for (final GuiComponent component : componentsToAdd) {
			childComponents.add(component);
			component.updateScreenSpacePosition();
			component.update(guiTextures, texts);
		}

		componentsToAdd.clear();
	}
}
